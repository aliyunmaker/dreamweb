package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.Product;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.ProductService;
import cc.landingzone.dreamweb.service.UserProductService;
import cc.landingzone.dreamweb.utils.JsonUtils;

/**
 *
 * 管理产品列表
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
@Controller
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserProductService userProductService;

    /**
         * 获取所有产品列表
         *
         *
         * @return 产品列表
         * @throws Exception
         */
    @RequestMapping("/searchProduct.do")
    public void searchProduct(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer start = Integer.valueOf(request.getParameter("start"));
            Integer limit = Integer.valueOf(request.getParameter("limit"));
            Page page = new Page(start, limit);
            List<Product> list = productService.listProduct(page);
            result.setTotal(page.getTotal());
            result.setData(list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/searchUserProduct.do")
    public void searchUserProduct(HttpServletRequest request, HttpServletResponse response) {
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
         * 增加产品
         *
         * @param: 产品ID、应用、场景
         *
         * @throws Exception
         */
    @RequestMapping("/addProduct.do")
    public void addProduct(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            Product product = JsonUtils.parseObject(formString, Product.class);
            productService.saveProduct(product);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    /**
         * 更新产品
         *
         * @param: 产品ID、应用、场景
         *
         * @throws Exception
         */
    @RequestMapping("/updateProduct.do")
    public void updateProduct(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            Product updateProduct = JsonUtils.parseObject(formString, Product.class);
            Product dbProduct = productService.getProductById(updateProduct.getId());
            dbProduct.setProductId(updateProduct.getProductId());
            dbProduct.setApplication(updateProduct.getApplication());
            dbProduct.setScenes(updateProduct.getScenes());
            dbProduct.setProductName(updateProduct.getProductName());
            dbProduct.setProductVersionId(updateProduct.getProductVersionId());
            productService.updateProduct(dbProduct);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    /**
         * 删除产品
         *
         * @param: 产品ID
         *
         * @throws Exception
         */
    @RequestMapping("/deleteProduct.do")
    public void deleteProduct(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer id = Integer.valueOf(request.getParameter("id"));
            productService.deleteProduct(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
