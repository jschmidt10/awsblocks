awsblocks dispatcher
====================

The dispatcher utility is a small web framework for building REST endpoints on AWS Lambda. The framework allows us to use a 
single Lambda function to support many REST endpoints simultaneously. 

### Why? 
There are some great reasons to separate your REST endpoints into separate Lambda functions. Doing so allows you to independently 
deploy your endpoints and allows them to scale independently as well. However, when building a small website, I noticed a few 
reasons why it was desirable to pack all my endpoints into a single Lambda.

First, for a small project, I am not gaining any agility by deploying my endpoints separately. My project is small so it is 
easier to only worry about one deployment. Additionally, my endpoints are not highly active and are not facing scaling issues.

A Lambda function on AWS has a "warm up" time. You'll notice that on initial invocations, your code will run much slower. When 
I did some adhoc measuring, a simple "hello world" java endpoint could take over 10 seconds from a "cold" state. Once the Lambda 
was warm, the requests took < 200 milliseconds, which is much more reasonable for an interactive website.

This leads to my second reason for using a single Lambda. By using a single Lambda, all requests hit the same instance of my 
code and keep it warm for longer. If you used separate Lambdas, each function would have to warm up separately. 

### How? 

To use the framework, first we define one or more LambdaHandlers:

```java
public class HelloWorldHandler implements LambdaHandler {

    @Override
    public boolean handlesPath(String path) {
        return path.equals("hello");
    }

    @Override
    public LambdaProxyResponse handle(LambdaProxyRequest request) {
        return new LambdaProxyResponse(Http.OK, "world");
    }
}
```

This handler will process any requests for "https://yourlambda.com/hello" and reply with "world". 

Next, we define our app by extending the Dispatcher and pluggin in our Handlers: 

```java
class HelloWorldApp extends Dispatcher {
    public HelloWorldApp() {
      super(Arrays.asList(new HelloWorldHandler()));
    }
}
```

Now, just build your java project. When configuring your Lambda function, the handler function will be 
`HelloWorldApp#handleRequest`. The library only works with Lambda's configured to use Lambda Proxy in API Gateway.
