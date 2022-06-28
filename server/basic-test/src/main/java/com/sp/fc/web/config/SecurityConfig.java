package com.sp.fc.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Order(1) //필터를 두개이상 만드는경우 Order 어노테이션을 써서 순서를 정해줘야한다. 
@EnableWebSecurity(debug = true) //리퀘스트가 올 때마다 이 리퀘스트는 어떤 필터 체인을 타고 있는지 확인할 수 있음
@EnableGlobalMethodSecurity(prePostEnabled = true) //권한을 체크하는 @preAuthorize가 작동
public class SecurityConfig extends WebSecurityConfigurerAdapter{
    
    //application.yml에는 유저 정보를 하나만 설정할 수 있기때문에 유저를 추가하 할 경우 AuthenticationManagerBuilder를 사용해서 쉽게 추가할 수 있다
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.inMemoryAuthentication()
            .withUser(User.builder()
            .username("user2")
                .password(passwordEncoder().encode("2222"))
                .roles("USER")
            ) .withUser(User.builder()
                .username("admin")
                .password(passwordEncoder().encode("3333"))
                .roles("ADMIN"));
    }


    //password를 그냥 "2222"이런식으로 세팅하면 에러가 남. 패스워드 인코딩을 해줘야함
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //스프링 시큐리티는 기본적으로 모든 페이지를 막고 시작함
    //만약 특정 페이지의 권한을 풀어주고 싶다면 
    //필터들을 추가해주면 된다.
    @Override
    protected void configure(HttpSecurity http)throws Exception{
        http.authorizeRequests((requests) ->
                requests.antMatchers("/").permitAll()
                        .anyRequest().authenticated());
        http.formLogin();
        http.httpBasic();
    }

}
