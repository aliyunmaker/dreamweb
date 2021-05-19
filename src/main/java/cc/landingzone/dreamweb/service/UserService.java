package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.dao.UserDao;
import cc.landingzone.dreamweb.framework.MyAuthenticationProvider;
import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserService {

    @Autowired
    private UserDao userDao;

    public static final String User_Role_Admin = "ROLE_ADMIN";
    public static final String User_Role_Guest = "ROLE_GUEST";

    public static final String WX_UNION_LOGIN_NAME_PREFIX = "weixin_";

    /**
     * 添加
     *
     * @param user
     */
    @Transactional
    public void addUser(User user) {
        Assert.notNull(user, "数据不能为空!");
        Assert.hasText(user.getLoginName(), "工号不能为空!");
        if (StringUtils.isBlank(user.getRole())) {
            user.setRole(User_Role_Guest);
        }
        if (StringUtils.isNotBlank(user.getPassword())) {
            user.setPassword(MyAuthenticationProvider.buildMd5Password(user.getPassword()));
        }
        // user.setAuthkey(GoogleAuthUtils.generateAuthkey());

        User userDB = getUserByLoginName(user.getLoginName());
        if (userDB != null) {
            throw new IllegalArgumentException("用户不能重名:" + user.getLoginName());
        }

        userDao.addUser(user);
    }

    /**
     * 给指定用户分配角色
     *
     * @param loginName
     * @param role
     */
    public void assignRole(String loginName, String role) {
        Assert.hasText(loginName, "登录名不能为空!");
        Assert.hasText(role, "role can not be blank!");
        User user = getUserByLoginName(loginName);
        Assert.notNull(user, "can not find user by loginName:" + loginName);
        user.setRole(role);
        updateUser(user);
    }

    /**
     * 删除用户
     *
     * @param id
     */
    @Transactional
    public void deleteUser(Integer id) {
        Assert.notNull(id, "id can not be null!");
        userDao.deleteUser(id);
    }

    /**
     * 更新用户信息
     *
     * @param user
     */
    public void updateUser(User user) {
        Assert.notNull(user, "数据不能为空!");
        Assert.hasText(user.getLoginName(), "登录名不能为空!");
        userDao.updateUser(user);
    }

    /**
     * 根据登录名获取用户
     *
     * @param loginName
     * @return
     */
    public User getUserByLoginName(String loginName) {
        Assert.hasText(loginName, "登录名不能为空!");
        return userDao.getUserByLoginName(loginName);
    }

    public User getUserByUnionid(String unionid) {
        Assert.hasText(unionid, "unionid不能为空!");
        return userDao.getUserByUnionid(unionid);
    }

    /**
     * 根据ID获取用户
     *
     * @param id
     * @return
     */
    public User getUserById(Integer id) {
        Assert.notNull(id, "id不能为空!");
        return userDao.getUserById(id);
    }

    /**
     * 根据登录名数组获取用户列表
     *
     * @param loginNames
     * @return
     */
    public List<User> getUsersByLoginNames(List<String> loginNames) {
        Assert.notEmpty(loginNames, "登录名不能为空!");
        List<User> users = userDao.getUsersByLoginNames(loginNames);
        if (users.size() != loginNames.size()) {
            List<String> diff = new ArrayList<String>();
            Map<String, Integer> map = new HashMap<String, Integer>(loginNames.size());
            for (User user : users) {
                map.put(user.getLoginName(), 1);
            }
            for (String name : loginNames) {
                if (map.get(name) == null) {
                    diff.add(name);
                }
            }
            Assert.isTrue(users.size() == loginNames.size(), "操作失败！以下用户不存在：" + String.join(",", diff));
        }
        return users;
    }

    /**
     * 搜索用户
     *
     * @return
     */
    public List<User> searchUser(String simpleSearch, Page page) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(simpleSearch)) {
            // id精确搜索的特殊处理
            if (simpleSearch.startsWith("id_")) {
                map.put("userId", Integer.valueOf(simpleSearch.substring(3)));
            } else if (simpleSearch.startsWith(" ")) {
                map.put("userId", Integer.valueOf(StringUtils.trim(simpleSearch)));
            } else if (simpleSearch.equals("=admin")) {
                map.put("role", "ROLE_ADMIN");
            } else {
                map.put("simpleSearch", simpleSearch);
            }
        }
        map.put("page", page);
        List<User> list = userDao.searchUser(map);
        if (null != page) {
            if (null != page.getStart() && null != page.getLimit()) {
                Integer total = userDao.searchUserTotal(map);
                page.setTotal(total);
            } else {
                page.setTotal(list.size());
            }
        }
        return list;
    }

}
