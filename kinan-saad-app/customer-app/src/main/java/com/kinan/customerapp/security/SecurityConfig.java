package com.kinan.customerapp.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;

/**
 * @author Eren
 **/
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class SecurityConfig {
    private ClientRegistrationRepository clientRegistrationRepository;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(ar -> ar.requestMatchers( "/","/oauthLogin", "/webjars/**", "/h2-console/**").permitAll())
                .authorizeHttpRequests(ar -> ar.anyRequest().authenticated())
                .headers(h -> h.frameOptions(fo -> fo.disable()))
                .csrf(crsf -> crsf.ignoringRequestMatchers("/h2-console/**"))
                .oauth2Login(al -> al.loginPage("/oauthLogin").defaultSuccessUrl("/"))
                .logout((logout) -> logout
                        .logoutSuccessHandler(oidcClientInitiatedLogoutSuccessHandler())
                        .logoutSuccessUrl("/").permitAll()
                        .deleteCookies("JSESSIONID")
                ).exceptionHandling(eh -> eh.accessDeniedPage("/notAuthorized"))
                .build();
    }
    private OidcClientInitiatedLogoutSuccessHandler oidcClientInitiatedLogoutSuccessHandler(){
        final OidcClientInitiatedLogoutSuccessHandler oidcClientInitiatedLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(this.clientRegistrationRepository);
        oidcClientInitiatedLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}?logoutsuccess=true");
        return oidcClientInitiatedLogoutSuccessHandler;
    }
    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper(){
        return (authorities -> {
            final Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            authorities.forEach((authority) -> {
                if(authority instanceof OidcUserAuthority oidcUserAuthority){
                    mappedAuthorities.addAll(mapAuthorities(oidcUserAuthority.getIdToken().getClaims()));
                    System.out.println(oidcUserAuthority.getAttributes());
                }
                else if(authority instanceof OAuth2UserAuthority oAuth2UserAuthority){
                    mappedAuthorities.addAll(mapAuthorities(oAuth2UserAuthority.getAttributes()));
                }
            });
            return mappedAuthorities;
        });
    }
    private List<SimpleGrantedAuthority> mapAuthorities(final Map<String, Object> attributes){
        final Map<String, Object> realmAccess = ((Map<String, Object>) attributes.getOrDefault("realm_access", Collections.emptyMap()));
        Collection<String> roles = ((Collection<String>)realmAccess.getOrDefault("roles", Collections.emptyList()));
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
