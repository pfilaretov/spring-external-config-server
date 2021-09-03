package pro.filaretov.spring.config.clientapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PropertiesFetcher implements ApplicationRunner {

    @Value("${my.aws.param}")
    private String myAwsParam;

    @Value("${my.config.server.param}")
    private String myConfigServerParam;

    @Value("${my.shared.param}")
    private String mySharedParam;

    @Override
    public void run(ApplicationArguments args) {
        log.info("my.aws.param={}", myAwsParam);
        log.info("my.config.server.param={}", myConfigServerParam);
        log.info("my.shared.param={}", mySharedParam);
    }
}
