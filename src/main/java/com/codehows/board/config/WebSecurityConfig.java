package com.codehows.board.config;

import com.codehows.board.config.oauth.MyOAuth2UserService;
import com.codehows.board.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailService userService;
    private final MyOAuth2UserService myOAuth2UserService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setMatchingRequestParameterName("null");

        http.requestCache((cache) -> cache
                .requestCache(requestCache));

        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(it -> it
                .loginPage("/login")
                .defaultSuccessUrl("/boardList")
                .usernameParameter("uid")
                .failureUrl("/login/error")
        );

        http.logout(it -> it
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/boardList")
                .invalidateHttpSession(true)
        );

        http.authorizeHttpRequests(req -> {
            req
                    .requestMatchers(antMatcher("/login")).permitAll()
                    .requestMatchers(antMatcher("/error")).permitAll()
                    .requestMatchers(antMatcher("/newUser")).permitAll()
                    .requestMatchers(antMatcher("/user/**")).permitAll()
                    .requestMatchers(antMatcher("/boardList")).permitAll()
                    //.requestMatchers(antMatcher("/board/**")).permitAll()  //추가함
                    .requestMatchers(antMatcher("/board/boardRegistForm")).authenticated()
                    .requestMatchers(antMatcher("/board/boardUpdateForm")).authenticated()
                    .anyRequest().authenticated();
        });

        http.exceptionHandling(it ->
                it.authenticationEntryPoint(new CustomAuthenticationEntryPoint("/login")));

        http.oauth2Login(oauth2 -> oauth2
                .redirectionEndpoint(endpoint -> endpoint.baseUri("/kakao/callback"))
                .userInfoEndpoint(endpoint -> endpoint.userService(myOAuth2UserService))
                .loginPage("/login")
                .defaultSuccessUrl("/boardList")
                .failureUrl("/login?error=true")
        );

        // X-Frame-Options 헤더 설정
        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin()) // 또는 .disable()로 완전히 비활성화
        );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
