# spring-external-config-server

Sample spring app with a configuration externalised to Spring Cloud Config Server as well as AWS Parameter Store.

It shows how Config Server parameters can be overwritten by AWS Parameter Store property source with a custom
bootstrap configuration.

## How to run

### 1. Config Server

1. Create git repository as a backend for config server at `${user.home}/spring-cloud-config-repo`
   and add three properties files for `springConfigServerClientApp` application (default, `dev` and `prod` profiles):
   ```
   cd ${HOME}
   mkdir spring-cloud-config-repo
   cd spring-cloud-config-repo
   git init .

   my.config.server.param=config-server-value > springConfigServerClientApp.properties
   my.shared.param=config-server-shared-value > springConfigServerClientApp.properties
      
   echo my.config.server.param=config-server-DEV-value > springConfigServerClientApp-dev.properties
   echo my.shared.param=config-server-shared-DEV-value > springConfigServerClientApp-dev.properties
   
   echo my.config.server.param=config-server-PROD-value > springConfigServerClientApp-prod.properties
   echo my.shared.param=config-server-shared-PROD-value > springConfigServerClientApp-prod.properties
   
   git add -A .
   git commit -m "Add application.properties"
   ```
2. Start `config-server` module's spring boot app - this is the Config Server serving requests at
   http://localhost:8888. Try it out with 
   ```
   GET http://user:Passw0rd@localhost:8888/springConfigServerClientApp/dev
   ```

### 2. AWS Parameter Store   

1. Login to AWS Console
2. Go to "My Security Credentials"
3. Create and download new Access Key
4. Install [AWS CLI](https://aws.amazon.com/cli/)
5. Configure AWS CLI with Access Key created earlier:
    ```
    aws configure
    ```
   This will create `<user_home>/.aws/credentials` file with access key details
6. Go to Systems Manager Parameter Store
7. Create standard string parameters for `my.aws.param` and `my.shared.param` properties - 
   both default and profile-specific:
   ```
   /config/springConfigServerClientApp/my.aws.param = AWS-value
   /config/springConfigServerClientApp_dev/my.aws.param = AWS-DEV-value
   /config/springConfigServerClientApp_prod/my.aws.param = AWS-PROD-value
   
   /config/springConfigServerClientApp/my.shared.param = AWS-shared-value
   /config/springConfigServerClientApp_dev/my.shared.param = AWS-shared-DEV-value
   /config/springConfigServerClientApp_prod/my.shared.param = AWS-shared-PROD-value
   ```

### 3. Client app

Start `client-app` module's spring boot app - this is the client. It should read properties from both 
config server and AWS Parameter Store and print something like
```
PropertiesFetcher      : my.aws.param=AWS-DEV-value
PropertiesFetcher      : my.config.server.param=config-server-DEV-value
PropertiesFetcher      : my.shared.param=AWS-shared-DEV-value
```


## Links

[Spring Cloud Config Server](https://cloud.spring.io/spring-cloud-config/reference/html/#_spring_cloud_config_server)

[Quick Intro to Spring Cloud Configuration](https://www.baeldung.com/spring-cloud-configuration) (by baeldung)

[How to externalize spring boot properties to an AWS System Manager Parameter Store](https://towardsaws.com/how-to-externalize-spring-boot-properties-to-an-aws-system-manager-parameter-store-2a945b1e856f).