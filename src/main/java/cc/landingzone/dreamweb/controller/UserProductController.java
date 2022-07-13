package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.landingzone.dreamweb.model.*;

import java.util.ArrayList;
import java.util.List;


import cc.landingzone.dreamweb.service.ProductService;
import cc.landingzone.dreamweb.service.UserService;
import cc.landingzone.dreamweb.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cc.landingzone.dreamweb.service.UserProductService;


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
    @RequestMapping("/searchUserProductAssociate.do")
    public void searchUserProductAssociate(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer start = Integer.valueOf(request.getParameter("start"));
            Integer limit = Integer.valueOf(request.getParameter("limit"));
            Page page = new Page(start, limit);
            List<UserProductAssociate> list = userProductService.listUserProductAssociate(page);
            List<UserProductAssociateVO> list1 = new ArrayList<>();
            for (UserProductAssociate userProductAssociate : list ) {
                Product product = productService.getProductById(userProductAssociate.getProductId());
                User user = userService.getUserById(userProductAssociate.getUserId());
                UserProductAssociateVO userProductAssociateVO = new UserProductAssociateVO();
                userProductAssociateVO.setId(userProductAssociate.getId());
                userProductAssociateVO.setLoginName(user.getLoginName());
                userProductAssociateVO.setProductName(product.getProductName());
                userProductAssociateVO.setServicecatalogProductId(product.getServicecatalogProductId());
                userProductAssociateVO.setServicecatalogPortfolioId(userProductAssociate.getServicecatalogPortfolioId());
                list1.add(userProductAssociateVO);
            }
            result.setTotal(page.getTotal());
            result.setData(list1);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    /**
     * 元资源申请页面按照用户名搜索产品列表
     *
     * @param:
     * @return
     * @throws Exception
     */
    @RequestMapping("/searchUserProductByUserName.do")
    public void searchUserProductByUserName(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByLoginName(userName);
            List<Product> list = userProductService.listProductByUserId(user.getId());
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
            UserProductAssociate userProductAssociate = JsonUtils.parseObject(formString, UserProductAssociate.class);
            userProductService.saveUserProductAssociate(userProductAssociate);
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
            String productName = request.getParameter("productName");
            String loginName = request.getParameter("loginName");
            String servicecatalogPortfolioId = request.getParameter("servicecatalogPortfolioId");
            UserProductAssociate userProductAssociate = userProductService.getUserProductAssociateById(Integer.valueOf(id));
            User user = userService.getUserByLoginName(loginName);
            Product product = productService.getProductByProductName(productName);
            userProductAssociate.setProductId(product.getId());
            userProductAssociate.setServicecatalogPortfolioId(servicecatalogPortfolioId);
            userProductAssociate.setUserId(user.getId());
            userProductService.updateUserProductAssociate(userProductAssociate);
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
            userProductService.deleteUserProductAssociate(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
