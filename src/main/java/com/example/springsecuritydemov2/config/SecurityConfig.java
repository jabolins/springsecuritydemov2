package com.example.springsecuritydemov2.config;

import com.example.springsecuritydemov2.model.Permission;
import com.example.springsecuritydemov2.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override // šo izvēlējāmies no overraide metodēm. Un tā kā strdājam ar HHTP tad izvēlamies tieši šo.
    protected void configure(HttpSecurity http) throws Exception {
        http
                // nākošā rindiņa nosaka aizsardzību no csrf (spring security piedāvā to defoltā
                .csrf().disable()
                .authorizeRequests()
               // .antMatchers("/").permitAll() // antMatchers noska konkrēti kādām lapām kādas piekļuves. Šobrīd izņēmu ārā lai pie level2 var piekļūt tikai admin

                // šādi (nākošās 3 rindiņas) bija uzrakstīts līdz izveidojām Permissions.
//                .antMatchers(HttpMethod.GET, "/api/**").hasAnyRole(Role.ADMIN.name(), Role.USER.name())
//                .antMatchers(HttpMethod.POST, "/api/**").hasRole(Role.ADMIN.name())
//                .antMatchers(HttpMethod.DELETE, "/api/**").hasRole(Role.ADMIN.name())
                // pārtaisām iepriekšējās 3 rindiņas šādi izmantojot izveidotos permission
                .antMatchers(HttpMethod.GET, "/api/**").hasAuthority(Permission.DEVELOPERS_READ.getPermission())
                .antMatchers(HttpMethod.POST, "/api/**").hasAuthority(Permission.DEVELOPERS_WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE, "/api/**").hasAuthority(Permission.DEVELOPERS_WRITE.getPermission())


                .antMatchers("/level2.html").hasAuthority(Permission.DEVELOPERS_WRITE.getPermission())// šo pieliku es tikai pārbaudei par piekļuves tiesībām
                .antMatchers("/index.html").hasAuthority(Permission.DEVELOPERS_READ.getPermission())// šo pieliku es pārbaudei lai nodalītu index un level2
                // nākošās divas rindiņas nosaka ka katram pieprasījumam jābut autorizētam
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }

    @Bean // norādam lai būtu piekļuve ne dažādām vietām
    @Override // arī šo mēs pievinojam no overraide metodēm lai varētu izmantot nevis standarta security user/password bet mūsu norādītos.
    protected UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager( // ar šādas rindas palīdzību mēs izveidojam jaunu lietotāju ar paroli, lietotājvārdu un tiesībām (tiesības defoltās)
                User.builder()
                        .username("admin")
                        .password(passwordEncoder().encode("admin"))
                        //šādi (nākošā rindiņa) bija pirms izveidojām Permissions
//                        .roles(Role.ADMIN.name())
                        //pārveidojām iepriekšējo rindiņu izmantojot permissions
                        .authorities(Role.ADMIN.getAuthorities())
                        .build(),
                User.builder()
                        .username("user")
                        .password(passwordEncoder().encode("user"))
                        .authorities((Role.USER.getAuthorities()))
                        .build()
        );
    }

    @Bean
    protected PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12); // nosaka cik "spēcīgai" jābūt šifrēšanai
    }
}
