package cc.landingzone.dreamweb.framework;

import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.service.RSAService;
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

import javax.naming.Context;
import javax.naming.directory.InitialDirContext;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Component
public class MyAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;
    @Autowired
    private RSAService rsaService;

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

        // decrypt password
        password = rsaService.decrypt(password);

        User user = userService.getUserByLoginName(username);
        if (null == user) {
            throw new UsernameNotFoundException(username);
        }

        // 如果是@landingzone.cc的用户就用AD的LDAP验证
        if (username.endsWith("@landingzone.cc")) {
            Hashtable<String, String> env = new Hashtable<>();
            String LDAP_URL = "ldap://121.199.62.9:389"; // LDAP 访问地址
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, LDAP_URL);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, username);
            env.put(Context.SECURITY_CREDENTIALS, password);
            try {
                InitialDirContext dc = new InitialDirContext(env);
                List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
                grantedAuths.add(new SimpleGrantedAuthority(user.getRole()));
                return new UsernamePasswordAuthenticationToken(username, user.getPassword(), grantedAuths);
            } catch (Exception e) {
                throw new BadCredentialsException(e.getMessage());
            }
        }

        // 密码策略: md5(salt+password) equals user.getAuthkey()
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
