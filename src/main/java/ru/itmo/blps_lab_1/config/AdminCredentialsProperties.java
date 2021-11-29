package ru.itmo.blps_lab_1.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="admin")
@Getter
@Setter
public class AdminCredentialsProperties {
    private String username;
    private String password;

    @Override
    public String toString(){
        return "Admin Properties {" +
                "\nusername=" + username +
                "\npassword=" + password +
                "}";
    }
}
