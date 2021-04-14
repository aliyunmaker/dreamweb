package cc.landingzone.dreamweb.test;


import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

/**
 * ldap test
 */
public class LdapTest {


    public static void main(String[] args) {
//        testAuth();
        testGetUserInfo();
    }


    public static void testAuth() {
        Hashtable<String, String> env = new Hashtable<>();
        String LDAP_URL = "ldap://121.199.62.9:389"; // LDAP 访问地址
//        String LDAP_URL = "ldap://127.0.0.1:389"; // LDAP 访问地址
        String username = "charles@sunfire.com";
        String password = "test_1234"; // 密码
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, LDAP_URL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);
        try {
            InitialDirContext dc = new InitialDirContext(env);// 初始化上下文
            System.out.println("认证成功");
        } catch (javax.naming.AuthenticationException e) {
            System.out.println("认证失败");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("认证出错：" + e);
        }
    }

    public static void testGetUserInfo() {
        Hashtable<String, String> env = new Hashtable<>();
        String LDAP_URL = "ldap://121.199.62.9:389"; // LDAP 访问地址
        String username = "charles@landingzone.cc";
        String password = "test_1234"; // 密码
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, LDAP_URL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);
        try {
            InitialDirContext dc = new InitialDirContext(env);// 初始化上下文
            System.out.println("认证成功");
            // 创建搜索控件
            SearchControls searchCtls = new SearchControls();
            // 设置搜索范围
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            // 设置搜索过滤条件
            String searchFilter = "(objectClass=user)";
//            String searchFilter = "(objectClass=*)";
            // 设置搜索域节点
//            String searchBase = "dc=landingzone,dc=cc";
            String searchBase = "ou=hangzhou,dc=landingzone,dc=cc";
            // 定制返回属性
            String[] returningAttrs = {"url", "whenChanged", "employeeID", "name", "userPrincipalName",
                    "physicalDeliveryOfficeName", "departmentNumber", "telephoneNumber", "homePhone", "mobile",
                    "department", "sAMAccountName", "whenChanged", "mail"};
            // 不定制属性，返回所有的属性集
            searchCtls.setReturningAttributes(returningAttrs);
            int totalResults = 0;
            try {
                NamingEnumeration answer = dc.search(searchBase, searchFilter, searchCtls);
                while (answer.hasMoreElements()) {
                    SearchResult sr = (SearchResult) answer.next();
                    String dn = sr.getName();
                    System.out.println("\n" + dn);

                    Attributes Attrs = sr.getAttributes();
                    if (Attrs != null) {
                        try {
                            for (NamingEnumeration ne = Attrs.getAll(); ne.hasMore(); ) {
                                Attribute Attr = (Attribute) ne.next();
                                String attrId = Attr.getID();
                                // 读取属性值
                                for (NamingEnumeration e = Attr.getAll(); e.hasMore(); totalResults++) {
                                    // 接受循环遍历读取的userPrincipalName用户属性
                                    String attrValue = e.next().toString();
                                    System.out.println(attrId + "=" + attrValue);
                                }
                            }
                        } catch (NamingException e) {
                            System.err.println("Throw Exception : " + e);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Throw Exception : " + e);
            }
        } catch (javax.naming.AuthenticationException e) {
            System.out.println("认证失败");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("认证出错：" + e);
        }
    }


}
