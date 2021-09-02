package pro.filaretov.spring.config.clientapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PropertiesFetcher implements ApplicationRunner {

    @Value("${my.useful.param}")
    private String myUsefulParam;

    @Override
    public void run(ApplicationArguments args) {
        log.info("myUsefulParam={}", myUsefulParam);
    }
}
