package com.smartcampus;

import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.ApplicationPath; // Requirement: javax.ws.rs.core.Application subclass 

@ApplicationPath("/api/v1")
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        packages("com.smartcampus");
    }
}