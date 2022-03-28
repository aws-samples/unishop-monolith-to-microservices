const AWS = require('aws-sdk');
const apiGWClient = new AWS.APIGateway();

const REQUIRED_PARAMS = ['ApiGatewayId', 'ResourcePath', 'Method'];

const missingRequiredParams = (params, required) => {
    if (!params) {
        return required;
    }
    return required.reduce((miss, param) => (!params[param] ? [...miss, (param)] : miss), []);
};

const promisify = async (awsCall, params, ignoreErrors) => {
    return new Promise((res, rej) => {
        awsCall.bind(apiGWClient)(params, (err, data) => {
            if (err) {
                if (ignoreErrors && ignoreErrors.indexOf(err.code) >= 0) {
                    res(undefined);
                }
                else {
                    console.log(`Throwing error [${err.code} - not in ${ignoreErrors ? ignoreErrors : 'excluded codes'}]:\n`, err);
                    rej(err);
                }
            }
            res(data);
        });
    });
};

const getApiResources = async (restApiId, position) => {
    return promisify(apiGWClient.getResources, { restApiId, position });
};

const getApiResource = async (restApiId, resourcePath) => {
    try {
        let resourcesRes = await getApiResources(restApiId);
        console.log('Resources:\n', JSON.stringify(resourcesRes.items));
        while (resourcesRes.position && !resourcesRes.items.find((res) => res.path === resourcePath)) {
            resourcesRes = await getApiResources(restApiId, resourcesRes.position);
            console.log('Resources:\n', JSON.stringify(resourcesRes.items));
        }
        return resourcesRes.items.find((res) => res.path === resourcePath) || null;
    }
    catch (ex) {
        console.log('Failed to fetch resource', ex);
        return null;
    }
};

const getMethod = async (restApiId, resourceId, httpMethod) => {
    return promisify(apiGWClient.getMethod, { restApiId, resourceId, httpMethod }, ['NotFoundException']);
};

const deleteMethod = async (restApiId, resourceId, httpMethod) => {
    return promisify(apiGWClient.deleteMethod, { restApiId, resourceId, httpMethod }, ['NotFoundException']);
};

const getIntegration = async (restApiId, resourceId, httpMethod) => {
    return promisify(apiGWClient.getIntegration, { restApiId, resourceId, httpMethod }, ['NotFoundException']);
};

const getIntegrationResponse = async (restApiId, resourceId, httpMethod, statusCode) => {
    return promisify(apiGWClient.getIntegrationResponse, { restApiId, resourceId, httpMethod, statusCode }, ['NotFoundException']);
};

const enableCors = async (restApiId, resourceId, httpMethod, responseTemplates) => {
    const methods = httpMethod != 'OPTIONS' ? `${httpMethod},OPTIONS` : 'DELETE,GET,HEAD,OPTIONS,PATCH,POST,PUT';

    var params = {
        httpMethod,
        resourceId,
        restApiId,
        statusCode: '200',
        responseTemplates: responseTemplates || {
            'application/json': '',
        },
        responseParameters: {
            'method.response.header.Access-Control-Allow-Headers': "\'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token\'",
            'method.response.header.Access-Control-Allow-Methods': `\'${methods}\'`,
            'method.response.header.Access-Control-Allow-Origin': "\'*\'"
        }
    };

    const existing = await getIntegrationResponse(restApiId, resourceId, httpMethod, '200');

    if (existing) {
        console.log('Existing Integration Response:\n', JSON.stringify(existing, null, 2));
    }

    if (!existing) {
        console.log('Update Integration Response:\n', JSON.stringify(params, null, 2));
        return promisify(apiGWClient.putIntegrationResponse, params);
    }
    else {
        return Promise.resolve(existing);
    }
};

const createIntegration = async (params) => {
    return promisify(apiGWClient.putIntegration, params);
};

const createMethod = async (params) => {
    return promisify(apiGWClient.putMethod, params);
};

const updateMethodIntegration = async (
    restApiId,
    resourceId,
    httpMethod,
    requestTemplates,
    responseTemplates,
    responses,
    existingIntegration,
    enableMethodCors,
    isMock
) => {

    const params = {
        ...(existingIntegration || {}),
        restApiId,
        resourceId,
        httpMethod,
        integrationHttpMethod: isMock ? undefined : 'POST',
        type: isMock ? 'MOCK' : 'AWS',
        passthroughBehavior: 'WHEN_NO_TEMPLATES',
        contentHandling: 'CONVERT_TO_TEXT',
        requestTemplates,
        requestParameters: {},
        integrationResponses: undefined,
    };

    if (!existingIntegration) {
        console.log(`Creating Method: ${httpMethod}`); 
        const newMethodParams = {
            authorizationType: "NONE",
            requestParameters: {},
            restApiId,
            resourceId,
            httpMethod
        };
        const newMethod = await createMethod(newMethodParams);
        console.log(`Method: \n`, JSON.stringify(newMethod, null, 2));
    }

    console.log('Integration Parameters:\n', JSON.stringify(params, null, 2));

    const integration = await createIntegration(params);

    console.log('Integration:\n', JSON.stringify(integration, null, 2));

    if (responses) {
        console.log(`Adding responses: ${responses}`);
        for (const codeIdx in responses) {
            try {
                await addResponse(restApiId, resourceId, httpMethod, responses[codeIdx], isMock ? {
                    'application/json': 'Empty'
                } : undefined);
            }
            catch (ex) {
                return failedCreation(event, context, `Failed to add response ${responses[codeIdx]} for method ${httpMethod} for ${resourceId}`);
            }
        }
    }


    if (enableMethodCors) {
        try {
            console.log(`Enabling CORS for ${httpMethod} [${resourceId}]`);
            await enableCors(restApiId, resourceId, httpMethod, responseTemplates);
        }
        catch (ex) {
            console.log(`Enabling CORS Failed ${ex}`);
            console.log(ex);
        }
    }

    return integration;
};

const addResponse = async (restApiId, resourceId, httpMethod, statusCode, responseModels) => {
    return promisify(apiGWClient.putMethodResponse, {
        restApiId,
        resourceId,
        httpMethod,
        statusCode: `${statusCode}`,
        responseModels,
        responseParameters: {
            'method.response.header.Access-Control-Allow-Headers': false,
            'method.response.header.Access-Control-Allow-Methods': false,
            'method.response.header.Access-Control-Allow-Origin': false
        }
    }, ['ConflictException']);
};


const SUCCESS = "SUCCESS";
const FAILED = "FAILED";

const cfnSend = async (event, context, responseStatus, responseData, physicalResourceId, noEcho) => {
    return new Promise((res, rej) => {
        var responseBody = JSON.stringify({
            Status: responseStatus,
            Reason: "See the details in CloudWatch Log Stream: " + context.logStreamName,
            PhysicalResourceId: physicalResourceId || context.logStreamName,
            StackId: event.StackId,
            RequestId: event.RequestId,
            LogicalResourceId: event.LogicalResourceId,
            NoEcho: noEcho || false,
            Data: responseData
        });

        console.log("Response body:\n", responseBody);

        var https = require("https");
        var url = require("url");

        var parsedUrl = url.parse(event.ResponseURL);
        var options = {
            hostname: parsedUrl.hostname,
            port: 443,
            path: parsedUrl.path,
            method: "PUT",
            headers: {
                "content-type": "",
                "content-length": responseBody.length
            }
        };

        console.log("Request options:\n", options);

        var request = https.request(options, function(response) {
            console.log("Status code: " + response.statusCode);
            console.log("Status message: " + response.statusMessage);
            context.done();
            res(responseBody);
        });

        request.on("error", function(error) {
            console.log("send(..) failed executing https.request(..): " + error);
            context.done();
            rej(error);
        });

        request.write(responseBody);
        request.end();
    });
};

const physicalId = (event) => {
    return `ApiGWUpdate-${event.ResourceProperties.ApiGatewayId
        }-${event.LogicalResourceId

        }-${event.ResourceProperties.Method || `CR`
        }${event.ResourceProperties.Delete ? '-delete' : ''}`;
};

const failedCreation = async (event, context, message) => {

    if (event.ResponseURL) {
        await cfnSend(
            event,
            context,
            FAILED, { Message: message },
            physicalId(event)
        );
    }

    return {
        statusCode: 400,
        body: {
            message
        },
    };
};

const virtualDelete = async (event, context) => {
    console.log(`Deleting virtual CR`);
    if (event.ResponseURL) {
        await cfnSend(
            event,
            context,
            SUCCESS, { Message: 'Deleted' },
            physicalId(event)
        );
    }
    return {
        statusCode: 200,
        body: 'Deleted'
    };
};

const deleteOperation = async (event, context, apiGwId, resource) => {
    let result = {};
    console.log(`Deleting method ${event.ResourceProperties.Method} for ${resource.id} [${resource.path}]`);
    try {
        const method = await getMethod(apiGwId, resource.id, event.ResourceProperties.Method);
        if (method) {
            console.log('Deleting method:\n', JSON.stringify(method, null, 2));
            result = await deleteMethod(apiGwId, resource.id, event.ResourceProperties.Method);
        }
        else {
            console.log(`Skipping delete: ${event.ResourceProperties.Method} for ${resource.id} [${resource.path}] does not exist.`);
            result = {
                message: `Skipping delete: ${event.ResourceProperties.Method} for ${resource.id} [${resource.path}] does not exist.`
            };
        }
    }
    catch (ex) {
        console.log('Failed to delete method ${event.ResourceProperties.Method} for ${resource.id} [${resource.path}]', JSON.stringify(ex, null, 2));
        return failedCreation(event, context, `Failed to delete method ${event.ResourceProperties.Method} for ${resource.id} [${resource.path}]`);
    }
    return {
        statusCode: 200,
        body: result
    };
};

const updateOperation = async (event, context, apiGwId, resource) => {

    const integration = await getIntegration(apiGwId, resource.id, event.ResourceProperties.Method);

    if (integration) {
        console.log('Existing Integration:\n', JSON.stringify(integration, null, 2));
    }
    else {
        console.log(`Creating new Integration:\n`, JSON.stringify(event.ResourceProperties, null, 2));
    }


    const updated = await updateMethodIntegration(
        apiGwId,
        resource.id,
        event.ResourceProperties.Method,
        event.ResourceProperties.RequestTemplates,
        event.ResourceProperties.ResponseTemplates,
        event.ResourceProperties.Responses,
        integration,
        event.ResourceProperties.CORS,
        event.ResourceProperties.Mock
    );

    return {
        statusCode: 200,
        body: updated
    };
};

const deployOperation = async (event, context) => {
    if (!event.ResourceProperties.ApiGatewayId) {
        return await failedCreation(event, context, `Missing required resource properties: 'ApiGatewayId'`);
    }

    const apiGwId = event.ResourceProperties.ApiGatewayId;

    const params = {
        restApiId: apiGwId,
        stageName: event.ResourceProperties.Deploy
    };

    let response = {};

    try {
        response = await promisify(apiGWClient.createDeployment, params);
    }
    catch (ex) {
        console.log(`Failed to deploy ${event.ResourceProperties.ApiGatewayId} - Stage: ${event.ResourceProperties.Deploy}`, ex);
        return await failedCreation(event, context, `Failed to deploy ${event.ResourceProperties.ApiGatewayId} - Stage: ${event.ResourceProperties.Deploy}`);
    }

    if (event.ResponseURL) {
        await cfnSend(
            event,
            context,
            SUCCESS, response,
            physicalId(event)
        );
    }

    return {
        statusCode: 200,
        body: `Deployed ${event.ResourceProperties.ApiGatewayId} - Stage: ${event.ResourceProperties.Deploy}`,
    };
};

exports.handler = async (event, context) => {
    console.log("Event body:\n", JSON.stringify(event));

    if (event.RequestType === "Delete") {
        return await virtualDelete(event, context);
    }

    if (event.ResourceProperties.Deploy) {
        return await deployOperation(event, context);
    }

    let response = {};

    const missingParams = missingRequiredParams(event.ResourceProperties, REQUIRED_PARAMS);

    if (missingParams.length) {
        return await failedCreation(event, context, `Missing required resource properties: '${missingParams}'`);
    }
    else {
        const apiGwId = event.ResourceProperties.ApiGatewayId;
        const resource = await getApiResource(apiGwId, event.ResourceProperties.ResourcePath);

        if (resource === null && !event.ResourceProperties.Mock) {
            return await failedCreation(event, context, `Missing resource: '${event.ResourceProperties.ResourcePath}'`);
        }
        else {
            if (event.ResourceProperties.Delete) {
                try {
                    response = await deleteOperation(event, context, apiGwId, resource);
                }
                catch (ex) {
                    console.log(`Failed to delete method: `, ex);
                    return await failedCreation(event, context, `Failed to delete method: '${ex.message}'`);
                }
            }
            else {
                try {
                    response = await updateOperation(event, context, apiGwId, resource);
                }
                catch (ex) {
                    console.log(`Failed to update integration: `, ex);
                    return await failedCreation(event, context, `Failed to update integration: '${ex.message}'`);
                }
            }
        }
    }

    if (event.ResponseURL) {
        await cfnSend(
            event,
            context,
            SUCCESS,
            response,
            physicalId(event)
        );
    }

    return response;
};
