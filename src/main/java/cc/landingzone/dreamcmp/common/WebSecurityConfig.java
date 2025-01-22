package cc.landingzone.dreamcmp.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import cc.landingzone.dreamcmp.common.framework.MyAuthenticationProvider;

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
            // 无AK扫码体验小程序放行
            .antMatchers("/workshop/demo_token_vending_mobile.html").permitAll()
            // TVM演示获取token放行，通过cookie中的密钥 + session policy 保证安全性
            .antMatchers("/workshop/ak/getStsToken.do").permitAll()
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
            .logoutSuccessUrl("/login.html")
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
