package cn.wenhaha.security.config;

import cn.wenhaha.security.handler.JwtAuthenticationEntryPoint;
import cn.wenhaha.security.LogoutSuccessHandlerWen;
import cn.wenhaha.security.filter.JwtValidateSecurityFilter;
import cn.wenhaha.security.handler.JwtAccessDeniedHandler;
import cn.wenhaha.security.repository.SecurityUserConfig;
import cn.wenhaha.security.service.SpringDataUserDetailsService;
import cn.wenhaha.security.voter.RoleBasedVoter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.ConsensusBased;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: Wyndem
 * @Description: security配置
 * @Date: Created in  2018-10-31 11:45
 * @Modified By:
 */
@EnableWebSecurity
public class SecurityConfig  /*extends WebSecurityConfigurerAdapter*/ {

    @Value("${cn.wenhaha.loginUrl}")
    private String roule;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private JwtValidateSecurityFilter jwtValidateSecurityFilter;

    @Autowired
    private SecurityUserConfig securityUserConfig;




    public AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<? extends Object>> decisionVoters
                = Arrays.asList(
                new RoleBasedVoter(securityUserConfig,roule));

        return new ConsensusBased(decisionVoters);
    }


    //加入自定义用户验证
   /* @Bean
    public SpringDataUserDetailsService springDataUserDetailsService() {
        return new SpringDataUserDetailsService();
    }*/



    //加密方式
    @Bean
    public PasswordEncoder passwordEncoderBean() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public AccessDeniedHandler acessDeniedHandler(){
        return new JwtAccessDeniedHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //设置自定义登录
//                .formLogin()
//                .loginPage(roule)
//                .loginProcessingUrl(roule)
//                .and()
                //放行登录url
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(roule).permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                //其他界面需要认证
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                //禁用session，这会让cn.wenhaha.jwt.status=false失效的，因为开启session后会通过session来获取SecurityContext
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint()).and()
                .exceptionHandling().accessDeniedHandler(acessDeniedHandler()).and()

                //加入权限管理
                .authorizeRequests()
                .accessDecisionManager(accessDecisionManager());


        //加入自定义过滤器
        http
                .addFilterBefore(jwtValidateSecurityFilter, UsernamePasswordAuthenticationFilter.class);


        http.logout()
                .logoutSuccessHandler(new LogoutSuccessHandlerWen());
//                .addLogoutHandler(new LogoutHandlerWen());

        logger.info("安全配置加载完毕!!!");
        return http.build();
    }


    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //设置自定义登录
//                .formLogin()
//                .loginPage(roule)
//                .loginProcessingUrl(roule)
//                .and()
                //放行登录url
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(roule).permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                //其他界面需要认证
                .anyRequest().authenticated()
                .and()
                .csrf().disable()

                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint()).and()
                .exceptionHandling().accessDeniedHandler(acessDeniedHandler()).and()

                //加入权限管理
                .authorizeRequests()
                .accessDecisionManager(accessDecisionManager());


        //加入自定义过滤器
        http
                .addFilterBefore(jwtValidateSecurityFilter, UsernamePasswordAuthenticationFilter.class);


        http.logout()
                .logoutSuccessHandler(new LogoutSuccessHandlerWen());
//                .addLogoutHandler(new LogoutHandlerWen());

        logger.info("安全配置加载完毕!!!");
    }*/




}
