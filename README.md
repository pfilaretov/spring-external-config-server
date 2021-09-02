# spring-external-config-server

Sample spring app with a configuration externalised to Spring Cloud Config Server.

## How to run

1. Create git repository as a backend for config server at `${user.home}/spring-cloud-config-repo`
   and add two properties files for `springConfigServerClientApp` application (`dev` and `prod` profiles):
   ```
   cd ${HOME}
   mkdir spring-cloud-config-repo
   cd spring-cloud-config-repo
   git init .
   echo my.useful.param=dev-value > springConfigServerClientApp-dev.properties
   echo my.useful.param=PROD-value > springConfigServerClientApp-prod.properties
   git add -A .
   git commit -m "Add application.properties"
   ```
2. Start `config-server` module's spring boot app - this is the Config Server serving requests at
   http://localhost:8888. Try it out with 
   ```
   GET http://user:Passw0rd@localhost:8888/springConfigServerClientApp/dev
   ```
3. Start `client-app` module's spring boot app - this is the client. It should read properties from the 
   config server and print something like
   ```
   PropertiesFetcher: myUsefulParam=dev-value
   ```

## Links

[Spring Cloud Config Server](https://cloud.spring.io/spring-cloud-config/reference/html/#_spring_cloud_config_server)

[Quick Intro to Spring Cloud Configuration](https://www.baeldung.com/spring-cloud-configuration) (by baeldung)