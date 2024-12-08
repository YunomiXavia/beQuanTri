package com.example.beQuanTri.configuration.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.image.storage.path}")
    private String imageStoragePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourcePath = "file:///" + imageStoragePath.replace("\\", "/").replace(" ", "%20") + "/";

        registry.addResourceHandler("/products/**")
                .addResourceLocations(resourcePath)
                .setCachePeriod(3600)
                .resourceChain(true);
    }
}
