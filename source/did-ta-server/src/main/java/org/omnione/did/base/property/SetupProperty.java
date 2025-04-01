package org.omnione.did.base.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Property class for setup for admin.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "setup")
public class SetupProperty {
    private String path;
    private String baseUrl;
}
