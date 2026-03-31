package com.loanflow.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;
import java.util.stream.Collectors;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "dotenvProperties";
    private static final String DOTENV_DIR_PROPERTY = "dotenv.directory";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String directory = System.getProperty(DOTENV_DIR_PROPERTY, System.getProperty("user.dir"));

        Dotenv dotenv = Dotenv.configure()
                .directory(directory)
                .ignoreIfMissing()
                .load();

        Map<String, Object> properties = dotenv.entries().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        if (!properties.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
        }
    }
}

