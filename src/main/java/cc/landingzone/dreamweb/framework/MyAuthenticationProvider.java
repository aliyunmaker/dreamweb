package cc.landingzone.dreamweb.framework;

import java.util.ArrayList;
import java.util.List;

import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.service.UserService;
import cc.landingzone.dreamweb.utils.Md5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@Component
public class MyAuthenticationProvider implements AuthenticationProvider {


    @Autowired
    private UserService userService;

    private static final String salt = "dream";

    public static void main(String[] args) {
        System.out.println(buildMd5Password("admin"));
    }

    public static String buildMd5Password(String password) {
        Assert.hasText(password, "password can not be blank!");
        return Md5Utils.getMD5(salt + password);
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userService.getUserByLoginName(username);
        if (null == user) {
            throw new UsernameNotFoundException(username);
        }

        // 密码策略: md5(salt+password)  equals  user.getAuthkey()
        if (buildMd5Password(password).equals(user.getPassword())) {
            List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
            grantedAuths.add(new SimpleGrantedAuthority(user.getRole()));
            return new UsernamePasswordAuthenticationToken(username, user.getPassword(), grantedAuths);
        }
        throw new BadCredentialsException(username);

    }

    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
