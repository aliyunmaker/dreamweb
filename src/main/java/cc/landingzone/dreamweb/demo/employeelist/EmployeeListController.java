package cc.landingzone.dreamweb.demo.employeelist;

import cc.landingzone.dreamweb.common.BaseController;
import cc.landingzone.dreamweb.common.model.WebResult;
import cc.landingzone.dreamweb.common.utils.JsonUtils;
import cc.landingzone.dreamweb.demo.employeelist.model.ScimGroup;
import cc.landingzone.dreamweb.demo.employeelist.model.ScimUser;
import cc.landingzone.dreamweb.demo.employeelist.model.SyncRequest;
import cc.landingzone.dreamweb.demo.employeelist.service.LdapService;
import cc.landingzone.dreamweb.demo.employeelist.service.ScimGroupService;
import cc.landingzone.dreamweb.demo.employeelist.service.ScimUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/employeeList")
public class EmployeeListController extends BaseController {

    @RequestMapping("/addUser.do")
    public void addUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            ScimUser scimUser = JsonUtils.parseObject(formString, ScimUser.class);
            String userId = ScimUserService.addUser(scimUser);
            result.setData(userId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/updateUser.do")
    public void updateUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            ScimUser scimUser = JsonUtils.parseObject(formString, ScimUser.class);
            ScimUserService.updateUser(scimUser);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/deleteUser.do")
    public void deleteUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String idArray = request.getParameter("idArray");
            List<String> idList = JsonUtils.parseArray(idArray, String.class);
            // String id = request.getParameter("id");
            if (idList.size() <= 20) {
                for (String id : idList) {
                    ScimUserService.deleteUser(id);
                }
                result.setData("删除成功!");
            } else {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        int deleteCount = 0;
                        try {
                            for (String id : idList) {
                                Thread.sleep(10);
                                ScimUserService.deleteUser(id);
                                deleteCount++;
                                logger.info("delete success[" + deleteCount + "]:" + id);
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        } finally {
                            logger.info("expect delete count: " + idList.size());
                            logger.info("actual delete count: " + deleteCount);
                        }

                    }
                }, "deleteSCIMUser" + System.currentTimeMillis()).start();
                result.setData("后台删除中,请查看后台日志!");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getAllUser.do")
    public void getAllUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            long startTime = System.currentTimeMillis();
            List<ScimUser> scimUserList = ScimUserService.getAllScimUser();
            result.setData(scimUserList);
            logger.info("getAllUser cost: " + (System.currentTimeMillis() - startTime) + "ms");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/addGroup.do")
    public void addGroup(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            ScimGroup scimGroup = JsonUtils.parseObject(formString, ScimGroup.class);
            String userGroupId = ScimGroupService.addGroup(scimGroup);
            result.setData(userGroupId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/updateGroup.do")
    public void updateGroup(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            ScimGroup scimGroup = JsonUtils.parseObject(formString, ScimGroup.class);
            ScimGroupService.updateGroup(scimGroup);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/deleteGroup.do")
    public void deleteGroup(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String idArray = request.getParameter("idArray");
            List<String> idList = JsonUtils.parseArray(idArray, String.class);
            // String id = request.getParameter("id");
            for (String id : idList) {
                ScimGroupService.deleteGroup(id);
            }
            result.setData("删除成功!");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getAllGroup.do")
    public void getAllGroup(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            long startTime = System.currentTimeMillis();
            List<ScimGroup> scimGroupList = ScimGroupService.getAllScimGroup();
            result.setData(scimGroupList);
            logger.info("getAllGroup cost: " + (System.currentTimeMillis() - startTime) + "ms");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/sync.do")
    public void sync(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            List<SyncRequest> syncRequestList = JsonUtils.parseArray(formString, SyncRequest.class);
            boolean removeUnselected = Boolean.parseBoolean(request.getParameter("removeUnselected"));
            assert syncRequestList != null;
            String resultData = LdapService.syncLdaptoScim(syncRequestList, removeUnselected);
            result.setData(resultData);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

}
