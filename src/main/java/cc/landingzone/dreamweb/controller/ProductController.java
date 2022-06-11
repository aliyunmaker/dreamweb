package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.Product;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.ProductService;
import cc.landingzone.dreamweb.utils.JsonUtils;

@Controller
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;

    @RequestMapping("/searchProduct.do")
    public void searchProduct(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer start = Integer.valueOf(request.getParameter("start"));
            Integer limit = Integer.valueOf(request.getParameter("limit"));
            Page page = new Page(start, limit);
            List<Product> list = productService.searchProduct(page);
            result.setTotal(page.getTotal());
            result.setData(list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/addProduct.do")
    public void addProduct(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            Product product = JsonUtils.parseObject(formString, Product.class);
            productService.addProduct(product);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/updateProduct.do")
    public void updateProduct(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            Product updateProduct = JsonUtils.parseObject(formString, Product.class);
            Product dbProduct = productService.getProductById(updateProduct.getId());
            dbProduct.setProductid(updateProduct.getProductid());
            dbProduct.setApplication(updateProduct.getApplication());
            dbProduct.setScenes(updateProduct.getScenes());
            dbProduct.setProductname(updateProduct.getProductname());
            productService.updateProduct(dbProduct);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

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
