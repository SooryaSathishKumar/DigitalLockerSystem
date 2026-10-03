package com.examly.springapp.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    private String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private long expiration = 86400000L; // 24 hours in milliseconds

    public JwtConfig() {
    }

    public JwtConfig(String secret, long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}
