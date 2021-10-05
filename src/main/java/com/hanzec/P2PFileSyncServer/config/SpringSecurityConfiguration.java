package com.hanzec.P2PFileSyncServer.config;

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
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Order(1)
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    final AccountService accountService;

    final PasswordEncoder passwordEncoder;

    final TokenService tokenService;


    public SpringSecurityConfiguration(AccountService accountService,
                                       PasswordEncoder passwordEncoder,
                                       TokenService tokenService) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        //Provider for session Login
        authenticationManagerBuilder
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
                .antMatchers("/login")
                .antMatchers("/error")
                .antMatchers("/index.html")
                .antMatchers("/api/v1/register_user")
                .antMatchers("/api/v1/register_client")
                .antMatchers("/swagger**/**", "/webjars/**", "/v3/**", "/doc.html");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated();

        //disable csrf protection for post return 403
        http
                .csrf().disable();

        //logout configuration
        http
                .logout();

        //login page configuration
        http
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/api/v1/auth/password");
    }
}
