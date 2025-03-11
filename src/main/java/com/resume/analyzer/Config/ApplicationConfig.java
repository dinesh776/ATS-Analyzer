package com.resume.analyzer.Config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration(exclude = {OpenAiAutoConfiguration.class})
public class ApplicationConfig {

}