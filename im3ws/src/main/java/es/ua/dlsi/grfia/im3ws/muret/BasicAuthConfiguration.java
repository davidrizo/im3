package es.ua.dlsi.grfia.im3ws.muret;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class BasicAuthConfiguration
        extends WebSecurityConfigurerAdapter {


    //TODO
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
                auth
                .inMemoryAuthentication()
                .withUser("username")
                .password("{noop}password")
                .roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http)
            throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/muret/auth/login").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }
}