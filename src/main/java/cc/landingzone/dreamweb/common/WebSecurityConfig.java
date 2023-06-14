package cc.landingzone.dreamweb.common;

import cc.landingzone.dreamweb.common.framework.MyAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyAuthenticationProvider authProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception { // 配置资源和用户权限匹配
        http.csrf().disable();
        // http.anonymous();
        http
            .authorizeRequests()
            .antMatchers("/", "/system/getStartInfo.do").permitAll()
            .antMatchers("/welcome/*", "/user/getUserInfo.do", "/sso/*",
                "/index.html", "/system/getIndexLogoPage.do")
            .hasAnyRole("GUEST", "ADMIN")
            .antMatchers("/**").hasRole("ADMIN")
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/login.html")
            .defaultSuccessUrl("/index.html", true)
            .permitAll()
            .and()
            .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl(CommonConstants.LOGOUT_SUCCESS_URL)
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
