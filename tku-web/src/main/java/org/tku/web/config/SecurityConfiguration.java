package org.tku.web.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.tku.web.service.UserDetailsServiceImpl;

import java.util.Collection;

@Configuration
@EnableWebSecurity  //置 Spring Boot 或 Spring MVC 項目的安全性，以確保應用程式的資料和資源受到適當的保護。
@Log4j2
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filter(HttpSecurity http)throws Exception{

        http.authorizeHttpRequests(registry ->{
            //定義那些URL 需要被保護、那些不需要被保護
            //做登入授權 把網頁保護起來
            //正向表列
            registry.requestMatchers("/web/**").authenticated()
                    .anyRequest().permitAll();
            //負向表列: /js /css 靜態是permitall
            //其他是anyrequest
            //Error 403 沒有該資源的權限
        });


        http.formLogin(httpSecurityFormLoginConfigurer -> {
            httpSecurityFormLoginConfigurer.loginPage("/login")
                    .defaultSuccessUrl("/web/index")
                    .usernameParameter("userName")
                    .passwordParameter("password")
                    .failureHandler((request, response, exception) -> {
                log.error("密碼錯誤");
                response.sendRedirect("/login?error=failed");
            });
        });

        http.exceptionHandling(configurer ->{
            configurer.authenticationEntryPoint((request, response, authException) -> {
                log.error("未登入: "+ authException.getMessage());
                response.sendRedirect("/login?error=unauth");
            });
        });

        http.logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                .logoutUrl("/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    log.info("登出成功");
                    response.sendRedirect("/login?logout");
                })
                .deleteCookies("JESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsServiceImpl userDetailsService() {
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
        userDetailsService.setPasswordEncoder(bCryptPasswordEncoder());
        return userDetailsService;
    }



    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        //daoAuthenticationProvider.setUserDetailsService(userDetailsService());

        //lambda語法 下面程式碼和70行 連userDetailsService() 及sessiondata一樣
        daoAuthenticationProvider.setUserDetailsService(username-> {
            return new UserDetails() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return AuthorityUtils.commaSeparatedStringToAuthorityList("USER");
                }

                public String getUsername() {
                    return username;
                }

                public String getPassword() {
                    //目前沒有資料庫 假設
                    return bCryptPasswordEncoder().encode("12345");
                }

                @Override
                public boolean isAccountNonExpired() {
                    return true;
                }

                @Override
                public boolean isAccountNonLocked() {
                    return true;
                }

                @Override
                public boolean isCredentialsNonExpired() {
                    return true;
                }

                @Override
                public boolean isEnabled() {
                    return true;
                }
            };
        });
        return daoAuthenticationProvider;
    }
}
