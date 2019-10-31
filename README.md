# Unishop MonoToMicro Workshop (2-3 hours)

![](/MonoToMicroAssets/assets1024/unishop_front.png)

## Background

Unishop is THE one-stop-shop for all your Unicorn needs. You can find the best Unicorn selection online at the Unishop
and get your Unicorn delivered in less than 24 hours! 

As a young startup Unishop built a great service which was focused on customers and business outcomes but less on
technology and architecture. After a few years establishing a business model and securing the next round of venture
capital funding, the business is looking to expand to other markets, such as Unicorn-Insurance, Unicorn-Banking and
Unicorn-Ride-Sharing. The CEO asked the CTO to prepare the technology stack and start re-architecting Unishop solution
to ensure that the right foundations are in place for supporting the business plan.

As part of this workshop the CTO would like to explore moving to a microservices-based architecture using the [strangler
pattern](https://martinfowler.com/bliki/StranglerFigApplication.html).

<details>
<summary>  
<b>Prerequisites</b>
</summary>
<br> 
Before we start, we will need to ensure that we have some tools installed.

* **AWS account (mandatory)**: for deploying the resources and application
* **Java (optional)**: If you planning on compiling the code yourself you will need Java installed locally. It is recommended to check if you have Java already installed. Use the `java -version` from command line to check
if you have it on your machine (see output below for how it should look if you have Java installed on your machine. If
you can't find it on your machine then download Java 1.8.0.x or above).

```
sh-3.2# java -version
java version "1.8.0_202"
Java(TM) SE Runtime Environment (build 1.8.0_202-b08)
Java HotSpot(TM) 64-Bit Server VM (build 25.202-b08, mixed mode)
```

* **IDE of your choosing (optional, two popular ones listed below)**:
If you planning on browsing the code locally we recommend you will use an IDE
    * Eclipse IDE for Java Developers: https://www.eclipse.org/downloads/
    * IntelliJ IDEA: https://www.jetbrains.com/idea/download/
* **Gradle (optional)**: If you planning on compiling the code yourself you will need Gardle installed locally. Download Gardle from https://gradle.org/install/

</details>

<details>
<summary>
<b>Agenda</b>
</summary>
<br> 
We've broken the workshop down into easy to follow and digestible chunks, which walks you through the process of
transforming a monolithic application to a microservices-based application.
   
In **Part 1**, we will cover the monolithic application. It is a **traditional** Spring Boot Java application which will be deployed on an EC2 instance and connect to RDS MySQL database. The frontend will be hosted on S3 **Static web hosting**, it is a simple yet powerful hosting solution which auto-scale and meet growing needs automatically. Once deployed, the **Unishop** will be accessible to the outside
world.

In **Part 2-5** we will extract domain-based functionality and build it as a standalone microservice using Lambda and DynamoDB. In this case, that will be the Unishop shopping cart functionality.

In **Part 2** we will front the legacy application with API Gateway which will help switch between old REST API to new Lambda code.

In **Part 3** we will set up a new DynamoDB table which will hold the shopping cart.

In **Part 4** we will deploy Lambda code which will replace the legacy shopping cart functionality.  

**Part 5** will be used to wrap up and clean up.  

As you probably understand by now, one of the major benefits of moving to microservices architecture is that you can
develop each microservice using different technologies stack which is most suitable
for the use case. In this case, we decided to use Lambda and DynamoDB as the compute and database capabilities for the Unishop shopping cart functionality.

</details>

<details>
<summary>  
<b>Architecture: Legacy Monolithic Application</b>
</summary>
<br>
The AS-IS architecture looks as follows:  
![](/MonoToMicroAssets/assets1024/Slide1.png)

For simplicity, we will leverage a single VPC with two public subnets in two availability zones.
The EC2 instance will reside within a single availability zone. Likewise, the RDS instance will reside within a
single availability zone. 

Once the legacy application is deployed we will use API Gateway to front it, which will enable a more seamless transition
to the microservices pattern.

The TO-BE architecture, would not change the AS-IS architecture, however, we will use Lambda and DynamoDB to implement the shopping cart microservice functionality. Note that with the introduction of DynamoDB, data migration from MySQL to DynamoDB will be required.    
 ![](/MonoToMicroAssets/assets1024/Slide2.png)

</details>

<details>
<summary> 
<b>Part 1: Legacy Monolithic Application Deployment (~30 min)</b>
</summary>
<br> 

In this section we will deploy our legacy application using CloudFormation template. 
For this portion, please work through this [exercise](/MonoToMicroLegacy).  
</details>

<details>
<summary> 
<b>Part 1 review</b>
</summary>
 <br>
   
   ```
Now that we've successfully deployed our monolithic application, we're ready to consider how we might peel off
capabilities to be deployed as a separate microservice.

Let's take a moment to inspect the code base of the monolithic application that we just deployed. It is broken up into a
number of primary controllers.
    * CoreController
    * BasketController: basket management
    * UnicornController: inventory management
    * UserController: user management, registration, login
    * HealthController: performs basic health checks

In addition to the controllers, each domain got a number of other key components, e.g. events, models, repository
representations, and services. The database (RDS MySQL) is a reflection of the domains with 3 tables.
* unicorns: holds the inventory of Unicorns
* unicorn_basket: an association table between the Unicorns and the user's selection
* unicorn_user: represents the users in the system

In an e-commerce application, the basket functionality is critical. It needs to be highly available, durable, and
scalble to meet on-going and spiky workloads, e.g. Black Friday surges. One good first step would be to move this
functionality out of the monolith to allow it to scale independently to meet these needs. This also allows the
development teams to respond more rapidly to new business requirements, e.g. the business wanting to add new items
associated with their new insurance or banking initiatives.

So, let's plan the move, we will need a simple yet bulletproof plan which we can follow and replicate in the future for
other microservices. When thinking about breaking a monolith you need to consider the following 
* Microservice stack: Which tools are best for the microservice implementation? 
* Microservice data access: How do you make sure that current consumers of the API's won't break?
* Data migration: How do you move the data from the monolith to the microservice
* Microservice switch over: How do you enable a seamless switch between monolith and microservice
* Microservices and monolith internal data exchange: Performance is key to any solution. When breaking monolith to small
chunks you introduce chatter to the network which can have performance impact. Considering internal communication
between microservices and the monolith is essential for success.

With all of the above in mind, let's try to figure out how we will do it?
```

```diff
+ Participant Task: Break into small groups and plan your own deployment before peeking into the steps we decided to take
```
</details>

<details>
<summary>  
<b>Part 2: API Gateway set up (~45 min)</b>
</summary>
<br>

Once we have the legacy application up and running. Let's front it with API gateway which will help us switch between implementations easily.  
For this portion, please work through this [exercise](/MonoToMicroAPIGateway).
</details>

<details>
<summary> 
<b>Part 2 review</b>
</summary>
<br>
   
   ```
In part 2 we've introduced API gateway to front the legacy application. This is the first step moving 
towards microservice architecture. Fronting your legacy application with API gateway enables more than 
just a move to microservices, you can also introduce GraphQL on top of it to merge schemas and create 
API’s which pulls information from different microservices, giving the business ability to test 
different data consumption patterns and innovate rapidly. 
   ```
</details>


<details>
<summary>  
<b>Part 3: DynamoDB set up (~15 min)</b>
</summary>
<br>
 
In part 3 and 4 we will setup the new Microservice. We start with the data layer, so let's set up DynamoDB table.  
For this portion, please work through this [exercise](/MonoToMicroDynamoDB).
</details>

<details>
<summary> 
<b>Part 3 review</b>
</summary>
<br> 
   
   ```
There are many benefits in moving to microservice architecture, an important one is the ability to use 
different technology stack to compose the service. Another one is the ability to change the stack while 
minimizing the blast radius. Using DynamoDB as database with Lambda as business logic execution layer 
is one example of using different stack for a specific access pattern. We recommend you explore new 
technologies and components using Proof Of Concept (PoC) strategy and selecting the technology which 
makes the logical selection for the specific service
   ```
   
</details>

<details>
<summary>  
<b>Part 4: Microservices Application (~30 min)</b>
</summary>
<br>
 
In this section we will deploy Lambda code and instruct the API gateway to invoke the newly deployed Lambda shopping cart functionality  
For this portion, please work through this [exercise](/MonoToMicroLambda).
</details>

<details>
<summary> 
<b>Part 4 review</b>
</summary>
<br> 
   
   ```
So, what happened here, we’ve deployed a legacy application to the cloud using CloudFormation 
(the app can be an on premises one). Next, we’ve fronted the legacy application with API gateway to enable seamless 
switchover between implementations and redirecting requests to different microservices. Lastly, 
we’ve implemented a new microservice which breaks the legacy monolith to small chunks and 
redirected requests to the new service. et voila, strangler pattern in action. Now, let’s do 
some cleanup and happy hour is just around the corner. 
   ```

</details>

<details>
<summary>  
<b>Part 5: Wrap-up and Clean-up (~10 min)</b>
</summary>
<br>
   
Modernizing legacy applications is a necessity, there are few approaches you can follow; we hope that this workshop highlighted the benefits of breaking the monolith using the **strangler pattern**. Whichever approach you decide to follow we believe that the gradual improvement will benefit your business and increase confidence in delivering application modernization. Good luck with your journey!

```diff
- Before you leave, make sure you delete the below resources so you won’t be charged for on going usage!
```

* **Delete the CloudFormation stacks for the legacy application**
* **Delete the CloudFormation stacks for DynamoDB unless you've created the database manually, in that case you need to manually delete the table**
* **Remove the IAM role created for Lambda/DynamoDB**
* **Remove the keypair if created**
* **Delete S3 bucket with all content**

</details>

<details>
<summary>  
<b>Credits</b>
</summary>
<br>

### Contributers

Thanks for the below team members who worked very hard to get this workshop in place
* **Puneet Agarwal**
* **Mony kiem**
* **Aravind Singirikonda**
* **Heeki Park**
* **Nir Ozeri**

</details>
