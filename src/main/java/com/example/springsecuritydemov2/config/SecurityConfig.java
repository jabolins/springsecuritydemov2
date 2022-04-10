package com.example.springsecuritydemov2.config;

import com.example.springsecuritydemov2.model.Permission;
import com.example.springsecuritydemov2.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)// tas vajadzīgs lai controllierī norādītu authorizācijas (piekļuves) līmeņus ar @PreAuthorize
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override // šo izvēlējāmies no overraide metodēm. Un tā kā strdājam ar HHTP tad izvēlamies tieši šo.
    protected void configure(HttpSecurity http) throws Exception {
        http
                // nākošā rindiņa nosaka aizsardzību no csrf (spring security piedāvā to defoltā
                .csrf().disable()
                .authorizeRequests()
                //visām lapām kas atrakstītas controller izmantojam @PreAuthorize. Tā kā lapas level2 un index nav nevienā kontrolierī tad aprakstām šādi
                .antMatchers("/level2.html").hasAuthority(Permission.DEVELOPERS_WRITE.getPermission())// šo pieliku es tikai pārbaudei par piekļuves tiesībām
                .antMatchers("/index.html").hasAuthority(Permission.DEVELOPERS_READ.getPermission())// šo pieliku es pārbaudei lai nodalītu index un level2
                // nākošās divas rindiņas nosaka ka katram pieprasījumam jābut autorizētam
                .anyRequest()
                .authenticated()
                .and()
                // nomainām nākošo rindiņu httpBasic (tā pieļauj pamata aturizēšanos) pret speciālizēto formu (formLogin)
//                .httpBasic()
                .formLogin()
                .loginPage("/auth/login").permitAll() // obligāti jānorāda ka visiem ir piekļuve šai lapai
                .defaultSuccessUrl("/auth/index")
                //nākošās 7 rindiņas ir lai izveidotu drošu izlogošanos. Lai metode ir tikai POST. Lai notīra visus datus un atbilstošo coocies. Tās ir svarīgas!!!!
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", "POST"))
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/aut/login")
        ;
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
