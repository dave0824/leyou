package com.leyou.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("leyou.sms")
@Data
public class SmsProperties {

   private String accessKeyId;
   private String accessKeySecret;
   private String signName;
   private String verifyCodeTemplate;
}
