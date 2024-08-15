package com.limemojito.test.lambda;

import com.limemojito.aws.localstack.lambda.LocalstackLambdaConfig;
import com.limemojito.test.jackson.JacksonSupportConfiguration;
import com.limemojito.test.s3.S3SupportConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({LocalstackLambdaConfig.class, JacksonSupportConfiguration.class, S3SupportConfig.class})
@ComponentScan(basePackageClasses = {LambdaSupport.class})
public class LambdaSupportConfig {
}
