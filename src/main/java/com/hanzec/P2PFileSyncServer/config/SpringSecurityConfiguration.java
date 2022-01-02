package com.hanzec.P2PFileSyncServer.config;

import ch.qos.logback.core.net.server.Client;
import com.google.gson.Gson;
import com.hanzec.P2PFileSyncServer.security.filter.ClientLoginFilter;
import com.hanzec.P2PFileSyncServer.security.provider.ClientAuthenticationProvider;
import com.hanzec.P2PFileSyncServer.service.AccountService;
import com.hanzec.P2PFileSyncServer.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final Gson gson;

    private final TokenService tokenService;

    private final AccountService accountService;

    private final PasswordEncoder passwordEncoder;

    private final ClientAuthenticationProvider clientAuthenticationProvider;

    public SpringSecurityConfiguration(Gson gson,
                                       AccountService accountService,
                                       PasswordEncoder passwordEncoder,
                                       ClientAuthenticationProvider clientAuthenticationProvider,
                                       TokenService tokenService) {
        this.gson = gson;
        this.tokenService = tokenService;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.clientAuthenticationProvider = clientAuthenticationProvider;
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        //Provider for session Login
        authenticationManagerBuilder
                .authenticationProvider(clientAuthenticationProvider)
                .userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public HttpSessionEventPublisher httpSessoinEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Override
    public void configure(WebSecurity web) {
        //ignoring static objects
        web.ignoring()
                .antMatchers("/error")
                .antMatchers("/index.html")
                .antMatchers("/swagger-ui/**")
                .antMatchers("/v3/api-docs/**")
                .antMatchers("/swagger-ui.html")
                .antMatchers("/client_sign_root.crt");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/api/v1/login").permitAll()
                .antMatchers("/api/v1/password_login").permitAll()
                .antMatchers("/api/v1/register_user").permitAll()
                .antMatchers("/api/v1/register_client").permitAll()
                .anyRequest().authenticated();

        //disable csrf protection for post return 403
        http
                .csrf().disable();

        //logout configuration
        http
                .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID");

//        // remember me configuration
//        http
//                .rememberMe()
//                .key("remember-me-key")
//                .rememberMeParameter("remember-me")
//                .tokenValiditySeconds(60 * 60 * 24 * 7)
//                .userDetailsService(accountService)
//                .rememberMeCookieName("remember-me-cookie");
//
//        // session configuration
//        http
//                .sessionManagement()
//                .invalidSessionUrl("/session/invalid");


        // add custom filters
        http.addFilterAt(new ClientLoginFilter(gson, authenticationManagerBean()), UsernamePasswordAuthenticationFilter.class);

        //login page configuration
        http
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/api/v1/login")
                .usernameParameter("email")
                .passwordParameter("password")
                // switch back to original page
                .successHandler((request, response, authentication) -> {
                    response.setContentType("application/json;charset=utf-8");
                    RequestCache cache = new HttpSessionRequestCache();
                    SavedRequest savedRequest = cache.getRequest(request, response);
                    if (savedRequest != null) {
                        response.sendRedirect(savedRequest.getRedirectUrl());
                    } else {
                        response.sendRedirect("/loginSuccess");
                    }
                })
                .permitAll();
    }
}
