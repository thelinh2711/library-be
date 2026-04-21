package com.example.library_be.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
        return server -> {
            Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
            connector.setScheme("http");
            String port = System.getenv().getOrDefault("PORT", "8081");
            connector.setPort(Integer.parseInt(port));
            connector.setSecure(false);
            connector.setRedirectPort(8443); // redirect sang HTTPS
            server.addAdditionalTomcatConnectors(connector);
        };
    }
}
