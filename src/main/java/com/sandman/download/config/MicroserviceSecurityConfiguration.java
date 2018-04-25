package com.sandman.download.config;

import com.sandman.download.security.AuthoritiesConstants;
import com.sandman.download.security.jwt.JWTConfigurer;
import com.sandman.download.security.jwt.TokenProvider;

import com.sandman.download.service.AuthService;
import io.github.jhipster.security.AjaxAuthenticationFailureHandler;
import io.github.jhipster.security.AjaxAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@Configuration
@Import(SecurityProblemSupport.class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MicroserviceSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthService authService;

    private final TokenProvider tokenProvider;

    private final SecurityProblemSupport problemSupport;

    public MicroserviceSecurityConfiguration(TokenProvider tokenProvider, SecurityProblemSupport problemSupport) {

        this.tokenProvider = tokenProvider;
        this.problemSupport = problemSupport;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/app/**/*.{js,html}")
            .antMatchers("/bower_components/**")
            .antMatchers("/i18n/**")
            .antMatchers("/content/**")
            .antMatchers("/swagger-ui/index.html")
            .antMatchers("/test/**")
            .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
            .disable()
            .exceptionHandling()
            .authenticationEntryPoint(problemSupport)
            .accessDeniedHandler(problemSupport)
        .and()
            .headers()
            .frameOptions()
            .disable()
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
            .antMatchers("/api/sandman/v1/user/createUser").permitAll()//用户注册->开放接口
            .antMatchers(" /api/sandman/v1/uploadRecord/getAllRecords").authenticated()//用户查看上传记录，只能查看自己的
            .antMatchers(" /api/sandman/v1/downloadRecord/getAllDownloadRecords").authenticated()//用户查看下载记录，只能查看自己的
            .antMatchers(" /api/sandman/v1/resourceRecord/getAllResourceRecord").authenticated()//用户查看积分明细，只能查看自己的
            .antMatchers(" /api/sandman/v1/resource/updateResource").authenticated()//用户更新自己的资源信息，所以要登录
            .antMatchers("/api/sandman/v1/resource/uploadResource").authenticated()//用户上传资源，需要登录
            .antMatchers("/api/sandman/v1/resource/downloadResource").authenticated()//用户下载资源，需要登录
            .antMatchers(" /api/sandman/v1/resource/getAllMyResources").permitAll()//查看资源列表，开放接口无需登录
            .antMatchers("/api/sandman/v1/resource/delResource").authenticated()//资源假删，需要登录，自己上传的资源才有权删除
            .antMatchers("/api/sandman/v1/resource/getManyResourcesByFuzzy").permitAll()//资源检索，输入框查询，无需登录
            .antMatchers("/api/sandman/v1/resource/getOneResource").permitAll()//查看资源详情，无需登录
            .antMatchers("/management/health").permitAll()
            .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/swagger-resources/configuration/ui").permitAll()

        .and()
            .formLogin()
            .loginProcessingUrl("/api/sandman/v1/user/login")//登录接口
            .successForwardUrl("/api/sandman/v1/user/success")
            //.successHandler()
            //.defaultSuccessUrl("/api/sandman/v1/user/success",true)
            .failureForwardUrl("/api/sandman/v1/user/error")//登录失败页面
            //.defaultSuccessUrl("/api/sandman/v1/user/login")//登录成功页面
        .and()
            .logout()
            .logoutUrl("/api/sandman/v1/user/logout").permitAll()//登出接口
            .logoutSuccessUrl("/api/sandman/v1/user/logoutSuccess")//登出成功页面
        .and()/**/
            .rememberMe()
            .rememberMeParameter("rememberMe")
            .tokenValiditySeconds(60*60*24*7)//一周时间，记住我功能
            .rememberMeCookieName("rememberMe")
        .and()
            .apply(securityConfigurerAdapter());
        //.antMatchers("/api/**").authenticated()
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
