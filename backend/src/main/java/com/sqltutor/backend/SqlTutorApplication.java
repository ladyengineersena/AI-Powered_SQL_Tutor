package com.sqltutor.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.lang.NonNull;

@SpringBootApplication
public class SqlTutorApplication implements WebMvcConfigurer {
    static {
        System.setProperty("file.encoding", "UTF-8");
    }

    public static void main(String[] args) {
        SpringApplication.run(SqlTutorApplication.class, args);
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(false);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converters.add(0, stringConverter);
        
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setDefaultCharset(StandardCharsets.UTF_8);
        converters.add(1, jsonConverter);
    }

    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

    @Bean
    public org.springframework.boot.web.servlet.FilterRegistrationBean<CharacterEncodingFilter> filterRegistrationBean() {
        org.springframework.boot.web.servlet.FilterRegistrationBean<CharacterEncodingFilter> registrationBean = new org.springframework.boot.web.servlet.FilterRegistrationBean<>();
        registrationBean.setFilter(characterEncodingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Integer.MIN_VALUE);
        return registrationBean;
    }

    @Bean
    public CommandLineRunner startupCheck() {
        return args -> {
            System.out.println("\n==========================================");
            System.out.println("   SQL TUTOR BACKEND BASARIYLA CALISTI!");
            System.out.println("   PORT: 9101");
            System.out.println("==========================================\n");
        };
    }
}
