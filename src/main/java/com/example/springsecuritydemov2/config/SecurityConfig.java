package com.example.springsecuritydemov2.config;

import com.example.springsecuritydemov2.security.JwtConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
// tas vajadzīgs lai controllierī norādītu authorizācijas (piekļuves) līmeņus ar @PreAuthorize
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public SecurityConfig(JwtConfigurer jwtConfigurer) {
        this.jwtConfigurer = jwtConfigurer;
    }

    private final JwtConfigurer jwtConfigurer;

    @Override // šo izvēlējāmies no overraide metodēm. Un tā kā strdājam ar HHTP tad izvēlamies tieši šo.
    protected void configure(HttpSecurity http) throws Exception {
        http
                // nākošā rindiņa nosaka aizsardzību no csrf (spring security piedāvā to defoltā
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)// norādam jo vairāk neizmantosim sesijsas bet piekļuvi ar jwt token
                .and()
                .authorizeRequests()
                //visām lapām kas atrakstītas controller izmantojam @PreAuthorize. Tā kā lapas level2 un index nav nevienā kontrolierī tad aprakstām šādi
                .antMatchers("/").permitAll() // pamatlapai varēs piekļūt visi
                .antMatchers("/api/v1/auth/login").permitAll()
                // nākošās divas rindiņas nosaka ka katram pieprasījumam jābut autorizētam
                .anyRequest()
                .authenticated()
                .and()
                .apply(jwtConfigurer); // nosaku ka autentifikācija notiks arjwtConfigurer klases palidzību
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // nosaka cik "spēcīgai" jābūt šifrēšanai
    }

}
