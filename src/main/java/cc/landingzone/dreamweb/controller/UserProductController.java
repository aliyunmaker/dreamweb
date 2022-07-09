package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.landingzone.dreamweb.model.*;

import java.util.ArrayList;
import java.util.List;


import cc.landingzone.dreamweb.service.ProductService;
import cc.landingzone.dreamweb.service.UserService;
import org.checkerframework.checker.units.qual.A;
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
            List<Product> list = userProductService.listProduct(userName);
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
//    @RequestMapping("/addUserProduct.do")
//    public void addUserProduct(HttpServletRequest request, HttpServletResponse response) {
//        WebResult result = new WebResult();
//        try {
//            String productId = request.getParameter("productId");
//            Integer product_Id = Integer.valueOf(productId);
//            String userId = request.getParameter("userId");
//            Integer user_Id = Integer.valueOf(userId);
//            String portfolioId = request.getParameter("portfolioId");
//            Product product = productService.getProductById(product_Id);
//            User user = userService.getUserById(user_Id);
//            UserProductAssociate userProductAssociate = new UserProductAssociate();
//            userProductAssociate.setProductId(product.getProductId());
//            userProductAssociate.setUserName(user.getLoginName());
//            userProductAssociate.setPortfolioId(portfolioId);
//            userProductAssociate.setProductName(product.getProductName());
//            userProductService.saveUserProduct(userProductAssociate);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            result.setSuccess(false);
//            result.setErrorMsg(e.getMessage());
//        }
//        outputToJSON(response, result);
//    }

    /**
         * 更新权限
         *
         * @param:
         *
         * @throws Exception
         */
//    @RequestMapping("/updateUserProduct.do")
//    public void updateUserProduct(HttpServletRequest request, HttpServletResponse response) {
//        WebResult result = new WebResult();
//        try {
//            String id = request.getParameter("id");
//            Integer Id = Integer.valueOf(id);
//            String productId = request.getParameter("productId");
//            String userName = request.getParameter("userName");
//            String portfolioId = request.getParameter("portfolioId");
//            String productName = productService.getProductName(productId);
//            String test = userProductService.getPortfolioId(productId, userName);
//            if(test == null || (!test.equals(portfolioId))){
//                UserProductAssociate dbUserProductAssociate = userProductService.getUserProductById(Id);
//                dbUserProductAssociate.setProductId(productId);
//                dbUserProductAssociate.setUserName(userName);
//                dbUserProductAssociate.setPortfolioId(portfolioId);
//                dbUserProductAssociate.setProductName(productName);
//                userProductService.updateUserProduct(dbUserProductAssociate);
//            }
//            else if (test.equals(portfolioId)) {
//                result.setSuccess(false);
//                result.setErrorMsg("已有相同权限！");
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            result.setSuccess(false);
//            result.setErrorMsg(e.getMessage());
//        }
//        outputToJSON(response, result);
//    }

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
