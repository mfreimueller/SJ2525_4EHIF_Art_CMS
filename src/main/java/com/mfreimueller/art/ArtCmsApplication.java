package com.mfreimueller.art;

import com.mfreimueller.art.foundation.UploadProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@EnableConfigurationProperties(UploadProperties.class)
public class ArtCmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArtCmsApplication.class, args);
    }

    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

}
