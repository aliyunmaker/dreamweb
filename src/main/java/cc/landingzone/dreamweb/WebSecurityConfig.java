package cc.landingzone.dreamweb;

import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.framework.ApiLogoutSuccessHandler;
import cc.landingzone.dreamweb.framework.MyAuthenticationProvider;
import cc.landingzone.dreamweb.framework.MyAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyAuthenticationProvider authProvider;

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
//        http.anonymous();
        http
                .authorizeRequests()
                .antMatchers("/", "/weixin/**", "/system/getStartInfo.do", "/apiLogin").permitAll()
                .antMatchers("/welcome/*", "/user/getUserInfo.do", "/sso/*","/tools/*","/aliyunTools/*.do").hasAnyRole("GUEST", "ADMIN")
                .antMatchers("/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .successHandler(myAuthenticationSuccessHandler)
                .and()
                .logout()
                    .logoutRequestMatcher(new OrRequestMatcher(new AntPathRequestMatcher("/logout"),
                        new AntPathRequestMatcher(CommonConstants.API_LOGOUT_URL)))
                    .defaultLogoutSuccessHandlerFor(new ApiLogoutSuccessHandler(CommonConstants.API_LOGOUT_SUCCESS_URL),
                        new AntPathRequestMatcher(CommonConstants.API_LOGOUT_URL))
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/info/403.html")
                .and()
                .headers()
                .frameOptions().sameOrigin()
                .httpStrictTransportSecurity().disable();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider);
    }

}
