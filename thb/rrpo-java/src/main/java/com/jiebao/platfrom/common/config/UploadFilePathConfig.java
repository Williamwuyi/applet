package com.jiebao.platfrom.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UploadFilePathConfig implements WebMvcConfigurer {

    @Value("${jiebao.upload.staticImagePath}")
    private String staticImagePath;

    @Value("${jiebao.upload.staticFilePath}")
    private String staticFilePath;

    @Value("${jiebao.upload.imagePath}")
    private String imagePath;

    @Value("${jiebao.upload.filePath}")
    private String filePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(staticImagePath).addResourceLocations("file:" + imagePath);
        registry.addResourceHandler(staticFilePath).addResourceLocations("file:" + filePath);
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }
}
