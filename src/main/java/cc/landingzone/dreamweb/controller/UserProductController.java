package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.landingzone.dreamweb.model.*;

import java.util.List;


import cc.landingzone.dreamweb.service.ProductService;
import cc.landingzone.dreamweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cc.landingzone.dreamweb.service.UserProductService;
import cc.landingzone.dreamweb.utils.JsonUtils;


@Controller
@RequestMapping("/userProduct")
public class UserProductController extends BaseController {

    @Autowired
    private UserProductService userProductService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

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
            String productId = request.getParameter("productId");
            Integer product_Id = Integer.valueOf(productId);
            String userId = request.getParameter("userId");
            Integer user_Id = Integer.valueOf(userId);
            String portfolioId = request.getParameter("portfolioId");
            Product product = productService.getProductById(product_Id);
            User user = userService.getUserById(user_Id);
            UserProduct userProduct = new UserProduct();
            userProduct.setProductId(product.getProductId());
            userProduct.setUserName(user.getLoginName());
            userProduct.setPortfolioId(portfolioId);
            userProduct.setProductName(product.getProductName());
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
            String id = request.getParameter("id");
            Integer Id = Integer.valueOf(id);
            String productId = request.getParameter("productId");
            String userName = request.getParameter("userName");
            String portfolioId = request.getParameter("portfolioId");
            String productName = productService.getProductName(productId);
            String test = productService.getPortfolioId(productId, userName);
            if(test == null || (!test.equals(portfolioId))){
                UserProduct dbUserProduct = userProductService.getUserProductById(Id);
                dbUserProduct.setProductId(productId);
                dbUserProduct.setUserName(userName);
                dbUserProduct.setPortfolioId(portfolioId);
                dbUserProduct.setProductName(productName);
                userProductService.updateUserProduct(dbUserProduct);
            }
            else if (test.equals(portfolioId)) {
                result.setSuccess(false);
                result.setErrorMsg("已有相同权限！");
            }
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
