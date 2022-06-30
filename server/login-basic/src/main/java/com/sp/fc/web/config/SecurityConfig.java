package com.sp.fc.web.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;

@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomAuthDetails customAuthDetails;

    public SecurityConfig(CustomAuthDetails customAuthDetails){
        this.customAuthDetails = customAuthDetails;
    }



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //USER, ADMIN 사용자 등록 -> 이렇게만 하면 에러 발생  왜? csrf토큰 때문 이를 위해 html th:action 타임리프를 이용하면 타임리프에서 히든으로 csrf토큰을 만들어줌
        auth.inMemoryAuthentication()
            .withUser(
                User.withDefaultPasswordEncoder()
                    .username("user1")
                    .password("1111")
                    .roles("USER")   
            ).withUser(
                User.withDefaultPasswordEncoder()
                .username("admin")
                .password("2222")
                .roles("ADMIN")
            );
    }


   


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(request->{
                    request
                            .antMatchers("/**").permitAll()   //root 페이지에서는 모두가 접근할 수 있도록 
                            .anyRequest().authenticated() //어떤 요청이 들어왔을때는 허락을 받고 들어 올 수 있도록 -> 이러면 css도 영향을 받음
                            ;
                })
                .formLogin(
                    login->login.loginPage("/login") //로그인 페이지를 특정해주지않으면 default로 제공되는 로그인 페이지가 나온다
                    .permitAll()  //permitAll을 해줘야 페이지에 접근할 수 있음, 아니면 무한루프에 빠지게됨
                    .defaultSuccessUrl("/", false) //로그인 성공하면 갈 페이지
                    .failureUrl("/login-error") //로그인 실패했을때 갈 페이지
                    .authenticationDetailsSource(customAuthDetails)

                )
                .logout(logout->logout.logoutSuccessUrl("/")) //로그아웃 시 갈 페이지
                .exceptionHandling(exception->exception.accessDeniedPage("/access-denied")) //exception날 경우 갈 페이지
                ;
    }

    //web 리소스들이(css, js, imges, html..) 스프링 시큐리티 영향을 받지 않게
    public void configure(WebSecurity web) throws Exception{
        web.ignoring()
            .requestMatchers(
                PathRequest.toStaticResources().atCommonLocations()
            );
    }

     //보통 ADMIN이면 USER권한의 페이지들도 접근 할 수 있어야한다. 이럴때
     @Bean
     RoleHierarchy roleHierarchy(){
         RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
         roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
         return roleHierarchy;
     }
}
