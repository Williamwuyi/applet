package com.jiebao.platfrom.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jiebao")
public class JiebaoProperties {

    private ShiroProperties shiro = new ShiroProperties();

    private boolean openAopLog = true;

}
