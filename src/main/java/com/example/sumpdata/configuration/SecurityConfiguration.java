package com.example.sumpdata.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SecurityConfiguration {

    @Value("${security.allow.ip.list}")
    private List<String > allowIpList;

    Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    private static final List<IpAddressMatcher> matchers = new ArrayList<>();

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        allowIpList.forEach(ip -> matchers.add(new IpAddressMatcher(ip)));
        http
                .exceptionHandling(exceptionHandling())
                .authorizeHttpRequests((auth) -> auth.anyRequest().access(authorizeIpAddress()))
                .csrf(csrf -> csrf.disable());
        http.csrf(csrf -> csrf.disable());
        return http.build();
    }

    // https://stackoverflow.com/a/77181838
    private Customizer<ExceptionHandlingConfigurer<HttpSecurity>> exceptionHandling() {
        return (eh) -> eh.defaultAuthenticationEntryPointFor(
                ((request, response, authException) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)),
                (request) -> request.getRequestURI().startsWith("/devices/")
        );
    }

    // https://stackoverflow.com/questions/72366267/matching-ip-address-with-authorizehttprequests
    private AuthorizationManager<RequestAuthorizationContext> authorizeIpAddress() {
        return (authentication, context) -> {
            HttpServletRequest request = context.getRequest();
            return new AuthorizationDecision(checkIPAddress(request));
        };
    }

    private boolean checkIPAddress(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String url = request.getServletPath();
        boolean matched = matchers.stream().anyMatch(matcher -> matcher.matches(request));
        if (matched) {
            logger.info("Access granted to ip: " + ipAddress + ", url=" + url);
        } else {
            logger.warn("Access denied to ip: " + ipAddress + ", url=" + url);
        }

        return matchers.stream().anyMatch(matcher -> matcher.matches(request));
    }
}
