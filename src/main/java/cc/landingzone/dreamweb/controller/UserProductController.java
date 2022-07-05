package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cc.landingzone.dreamweb.model.Page;

import java.util.List;


import cc.landingzone.dreamweb.model.UserProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.UserProductService;
import cc.landingzone.dreamweb.utils.JsonUtils;


@Controller
@RequestMapping("/userProduct")
public class UserProductController extends BaseController {

    @Autowired
    private UserProductService userProductService;

    /**
         * 获取所有权限列表
         *
         *
         * @return 权限列表
         * @throws Exception
         */
    @RequestMapping("/searchUserProduct.do")
    public void searchUserProduct(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer start = Integer.valueOf(request.getParameter("start"));
            Integer limit = Integer.valueOf(request.getParameter("limit"));
            Page page = new Page(start, limit);
            List<UserProduct> list = userProductService.listUserProduct(page);
            result.setTotal(page.getTotal());
            result.setData(list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    /**
         * 增加权限
         *
         * @param:
         *
         * @throws Exception
         */
    @RequestMapping("/addUserProduct.do")
    public void addUserProduct(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            UserProduct userProduct = JsonUtils.parseObject(formString, UserProduct.class);
            userProductService.saveUserProduct(userProduct);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    /**
         * 更新权限
         *
         * @param:
         *
         * @throws Exception
         */
    @RequestMapping("/updateUserProduct.do")
    public void updateUserProduct(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            UserProduct updateUserProduct = JsonUtils.parseObject(formString, UserProduct.class);
            UserProduct dbUserProduct = userProductService.getUserProductById(updateUserProduct.getId());
            dbUserProduct.setProductId(updateUserProduct.getProductId());
            dbUserProduct.setUserName(updateUserProduct.getUserName());
            dbUserProduct.setPortfolioId(updateUserProduct.getPortfolioId());
            dbUserProduct.setProductName(updateUserProduct.getProductName());
            userProductService.updateUserProduct(dbUserProduct);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    /**
         * 删除权限
         *
         * @param:
         *
         * @throws Exception
         */
    @RequestMapping("/deleteUserProduct.do")
    public void deleteUserProduct(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer id = Integer.valueOf(request.getParameter("id"));
            userProductService.deleteUserProduct(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
