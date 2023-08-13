package com.hk.entity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    @Value("${project.folder:}")
    private String projectFolder;

    @Value(("${isDev:}"))
    private Boolean isDev;

    public Boolean getIsDev() {
        return isDev;
    }

    public String getProjectFolder() {
        return projectFolder;
    }

}
