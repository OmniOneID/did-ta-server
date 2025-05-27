package org.omnione.did.base.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ta")
public class TaAuthProperty {

    private Auth auth = new Auth();

    @Getter @Setter
    public static class Auth {
        private String registrationPassword;
    }
}
