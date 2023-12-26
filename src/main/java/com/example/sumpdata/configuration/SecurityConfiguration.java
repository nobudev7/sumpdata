package com.example.sumpdata.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SecurityConfiguration {

    // 127.0.0.1 – 127.255.255.255 could be IPv4 local host range.
    @Value("${security.allow.ip.list}")
    private List<String > allowIpList;

    private static final List<IpAddressMatcher> matchers = new ArrayList<>();

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        allowIpList.forEach(ip -> matchers.add(new IpAddressMatcher(ip)));
        http.authorizeHttpRequests((auth) ->
                auth.anyRequest().access(authorizeIpAddress()));
        return http.build();
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
        System.out.println(ipAddress);
        return matchers.stream().anyMatch(matcher -> matcher.matches(request));
    }
}
