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
                } else {
                    console.log(`Throwing error [${err.code} - not in ${ignoreErrors ? ignoreErrors : 'excluded codes'}]:\n`, err);
                    rej(err);
                }
            }
            res(data);
        });
    });
};

const getApiResources = async (restApiId, position) => {
    return promisify(apiGWClient.getResources, { restApiId, limit: 2, position });
};

const getApiResource = async (restApiId, resourcePath) => {
    try {
        let resourcesRes = await getApiResources(restApiId);
        while (resourcesRes.position && !resourcesRes.items.find((res) => res.path === resourcePath)) {
            resourcesRes = await getApiResources(restApiId, resourcesRes.position);
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
    return promisify(apiGWClient.getIntegration, { restApiId, resourceId, httpMethod });
};

const getIntegrationResponse = async (restApiId, resourceId, httpMethod, statusCode) => {
    return promisify(apiGWClient.getIntegrationResponse, { restApiId, resourceId, httpMethod, statusCode }, ['NotFoundException']);
};

const enableCors = async (restApiId, resourceId, httpMethod) => {
    var params = {
        httpMethod,
        resourceId,
        restApiId,
        statusCode: '200',
        responseTemplates: {
            'application/json': '',  
        },
        responseParameters: {
            'method.response.header.Access-Control-Allow-Headers': "\'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token\'",
            'method.response.header.Access-Control-Allow-Methods': `\'${httpMethod},OPTIONS\'`,
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

const updateMethodIntegration = async (restApiId, resourceId, httpMethod, requestTemplates, existingIntegration, enableMethodCors) => {
    const params = {
        ...existingIntegration,
        restApiId,
        resourceId,
        httpMethod,
        integrationHttpMethod: 'POST',
        type: 'AWS',
        passthroughBehavior: 'WHEN_NO_TEMPLATES',
        contentHandling: 'CONVERT_TO_TEXT',
        requestTemplates,
        requestParameters: undefined,
        integrationResponses: undefined,
    };

    console.log('Integration Parameters:\n', JSON.stringify(params, null, 2));

    const integration = await createIntegration(params);

    
    if (enableMethodCors) {
        try {
            console.log(`Enabling CORS for ${httpMethod} [${resourceId}]`);
            await enableCors(restApiId, resourceId, httpMethod);
        }
        catch (ex) {
            console.log(`Enabling CORS Failed ${ex}`);
            console.log(ex);
        }
    }

    return integration;
};

const addResponse = async (restApiId, resourceId, httpMethod, statusCode) => {
    return promisify(apiGWClient.putMethodResponse, {
        restApiId,
        resourceId,
        httpMethod,
        statusCode: `${statusCode}`,
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

        var request = https.request(options, function (response) {
            console.log("Status code: " + response.statusCode);
            console.log("Status message: " + response.statusMessage);
            context.done();
            res(responseBody);
        });

        request.on("error", function (error) {
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
        } else {
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

    if (event.ResourceProperties.Responses) {
        console.log(`Adding responses: ${event.ResourceProperties.Responses}`);
        for (const codeIdx in event.ResourceProperties.Responses) {
            try {
                await addResponse(apiGwId, resource.id, event.ResourceProperties.Method, event.ResourceProperties.Responses[codeIdx]);
            }
            catch (ex) {
                return failedCreation(event, context, `Failed to add response ${event.ResourceProperties.Responses[codeIdx]} for method ${event.ResourceProperties.Method} for ${resource.id} [${resource.path}]`);
            }
        }
    }
    
    const integration = await getIntegration(apiGwId, resource.id, event.ResourceProperties.Method);

    console.log('Existing Integration:\n', JSON.stringify(integration, null, 2));

    const updated = await updateMethodIntegration(
        apiGwId,
        resource.id,
        event.ResourceProperties.Method,
        event.ResourceProperties.RequestTemplates,
        integration,
        event.ResourceProperties.CORS);

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
    } catch (ex) {
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
    } else {
        const apiGwId = event.ResourceProperties.ApiGatewayId;
        const resource = await getApiResource(apiGwId, event.ResourceProperties.ResourcePath);

        if (resource === null) {
            return await failedCreation(event, context, `Missing resource: '${event.ResourceProperties.ResourcePath}'`);
        }
        else {
            if (event.ResourceProperties.Delete) {
                try {
                    response = await deleteOperation(event, context, apiGwId, resource);
                } catch (ex) {
                    console.log(`Failed to delete method: `, ex);
                    return await failedCreation(event, context, `Failed to delete method: '${ex.message}'`);
                }
            }
            else {
                try {
                    response = await updateOperation(event, context, apiGwId, resource);
                } catch (ex) {
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
