package cc.landingzone.dreamweb.task;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.enums.LoginMethodEnum;
import cc.landingzone.dreamweb.service.SystemConfigService;
import cc.landingzone.dreamweb.service.UserService;
import cc.landingzone.dreamweb.utils.JsonUtils;

@Component
public class SyncUserFromLDAPTask {

    @Autowired
    private UserService userService;

//    @Autowired
//    private RedisLockRegistry redisLockRegistry;

    @Autowired
    private SystemConfigService systemConfigService;

    private static Logger logger = LoggerFactory.getLogger(SyncUserFromLDAPTask.class);

    private static Hashtable<String, String> env = new Hashtable<>();

    static {
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://121.199.62.9:389");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "charles@landingzone.cc");
        env.put(Context.SECURITY_CREDENTIALS, "test_1234");
    }

    private static final String ATTR_SAM_ACCOUNT_NAME = "sAMAccountName";
    private static final String ATTR_USER_PRINCIPAL_NAME = "userPrincipalName";
    private static final String ATTR_DISPLAY_NAME = "displayName";
    private static final String ATTR_TELEPHONE_NUMBER = "telephoneNumber";

    private static String[] returningAttrs =
            {ATTR_SAM_ACCOUNT_NAME, ATTR_USER_PRINCIPAL_NAME, ATTR_DISPLAY_NAME, ATTR_TELEPHONE_NUMBER};


    public static void main(String[] args) {
        List<User> list = searchLDAP("");
        System.out.println(JsonUtils.toJsonString(list));
    }

    //    @Scheduled(fixedRate = 5 * 60 * 1000)
    //10分钟
    @Scheduled(cron = "0 0/10 * * * ?")
    public void doTask() {
        if(!systemConfigService.isAllowLDAP()) {
            return;
        }
        Lock lock = new ReentrantLock();
        boolean success = false;
        try {
            success = lock.tryLock(3, TimeUnit.SECONDS);
            logger.info("get lock:" + success);
            if (success) {
                List<User> userList = searchLDAP("");
                for (User user : userList) {
                    User dbUser = userService.getUserByLoginName(user.getLoginName());
                    if (dbUser == null) {
                        user.setLoginMethod(LoginMethodEnum.LDAP_LOGIN);
                        userService.addUser(user);
                    } else {
                        if (!LoginMethodEnum.LDAP_LOGIN.equals(dbUser.getLoginMethod())) {
                            logger.error("sync error:" + dbUser.getLoginName() + ". " + JsonUtils.toJsonString(dbUser));
                        }
                    }
                }
                logger.info("sync success! count:" + userList.size());

                //sleep 10s
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (success) {
                lock.unlock();
            }
        }
    }

    private static List<User> searchLDAP(String filter) {
        List<User> users = new ArrayList<>();
        try {
            // 初始化上下文
            InitialDirContext dc = new InitialDirContext(env);
            logger.info("auth success!");
            String searchFilter = "(&(objectClass=user)" + filter + ")";
            // 创建搜索控件
            SearchControls searchCtls = new SearchControls();
            // 设置搜索范围
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            // 定制返回属性
            searchCtls.setReturningAttributes(returningAttrs);
            // 设置搜索域节点
            String searchBase = "ou=hangzhou,dc=landingzone,dc=cc";

            NamingEnumeration<?> answer = dc.search(searchBase, searchFilter, searchCtls);
            while (answer.hasMoreElements()) {
                User user = new User();
                SearchResult sr = (SearchResult) answer.next();
                Attributes attrs = sr.getAttributes();
                if (attrs != null) {
                    for (NamingEnumeration<?> ne = attrs.getAll(); ne.hasMore(); ) {
                        Attribute attr = (Attribute) ne.next();
                        String attrId = attr.getID();
                        for (NamingEnumeration<?> e = attr.getAll(); e.hasMore(); ) {
                            String attrValue = e.next().toString();
//                            System.out.println(attrId + "====" + attrValue);
//                            if (ATTR_SAM_ACCOUNT_NAME.equals(attrId)) {
//                                user.setLoginName(attrValue);
//                            }
                            if (ATTR_USER_PRINCIPAL_NAME.equals(attrId)) {
                                user.setLoginName(attrValue);
                            }
                            if (ATTR_DISPLAY_NAME.equals(attrId)) {
                                if (null == user.getName()) {
                                    user.setName(attrValue);
                                }
                            }
                            if (ATTR_TELEPHONE_NUMBER.equals(attrId)) {
                                user.setPhone(attrValue);
                            }
                        }
                    }
                    users.add(user);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return users;
    }

}
