package com.kinan.customerapp.controllers;

import com.kinan.customerapp.models.Product;
import com.kinan.customerapp.repositories.ICustomerRepository;
import com.nimbusds.jose.crypto.impl.ECDH;
import groovyjarjarpicocli.CommandLine;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eren
 **/
@Controller
public class CustomerController {
    private ICustomerRepository customerRepository;
    private ClientRegistrationRepository clientRegistrationRepository;

    public CustomerController(ICustomerRepository customerRepository, ClientRegistrationRepository clientRegistrationRepository) {
        this.customerRepository = customerRepository;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Value("${inventory.service.base.uri}")
    private String inventoryServiceBaseUri;
    @GetMapping("/customers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getCustomers(Model model){
        model.addAttribute("customers", customerRepository.findAll());
        return "getCustomers";
    }
    @GetMapping("/auth")
    @ResponseBody
    public Authentication authentication(Authentication authentication){
        return authentication;
    }
    @GetMapping("/")
    public String home(){
        return "index";
    }
    @GetMapping("/notAuthorized")
    public String notAuthorized(){
        return "notAuthorized";
    }
    @GetMapping("/oauthLogin")
    public String oauthLogin(Model model){
        String authorizationRequestBaseUri = "oauth2/authorization";
        Map<String, String> oauth2AuthenticationUrls = new HashMap<>();
        Iterable<ClientRegistration> clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        clientRegistrations.forEach(registration -> {
            oauth2AuthenticationUrls.put(registration.getClientName(), authorizationRequestBaseUri + "/" + registration.getRegistrationId());
        });
        model.addAttribute("urls", oauth2AuthenticationUrls);
        return "oauthLogin";
    }
    @GetMapping("/products")
    public String getProducts(Model model){
        try{
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = context.getAuthentication();
            OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            DefaultOidcUser oidcUser = (DefaultOidcUser) auth2AuthenticationToken.getPrincipal();
            String jwtTokenValue = oidcUser.getIdToken().getTokenValue();
            RestClient restClient = RestClient.create(inventoryServiceBaseUri);
            List<Product> products = restClient.get()
                    .uri("/products")
                    .headers(h -> h.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenValue))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Product>>() {
                    });
            model.addAttribute("products", products);
            System.out.println(products);
            return "getProducts";
        }catch(Exception e){
            return "notAuthorized";
        }
    }
}
