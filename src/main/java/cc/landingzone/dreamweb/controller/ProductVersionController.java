package cc.landingzone.dreamweb.controller;

import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.Product;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.ProductService;
import cc.landingzone.dreamweb.service.ProductVersionService;
import cc.landingzone.dreamweb.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 管理产品版本列表
 * @author: laodou
 * @createDate: 2022/7/8
 */
@Controller
@RequestMapping("/productVersion")
public class ProductVersionController extends BaseController{

    @Autowired
    private ProductVersionService productVersionService;

    @Autowired
    private ProductService productService;


    /**
         * 获取所有产品版本列表
         *
         * @param:
         * @return
         * @throws Exception
         */
    @RequestMapping("/searchProductVersion.do")
    public void searchProductVersion(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer start = Integer.valueOf(request.getParameter("start"));
            Integer limit = Integer.valueOf(request.getParameter("limit"));
            Page page = new Page(start, limit);
            List<Product> list = productVersionService.listProductVersion(page);
            for (Product product: list) {
                product.setProductName(productService.getProductName(product.getProductId()));
            }
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
         * 增加产品版本
         *
         * @param:
         * @return
         * @throws Exception
         */
    @RequestMapping("/addProductVersion.do")
    public void addProductVersion(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            Product product = JsonUtils.parseObject(formString, Product.class);
            productVersionService.saveProductVersion(product);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    /**
         *  更新产品版本
         *
         * @param:
         * @return
         * @throws Exception
         */
    @RequestMapping("/updateProductVersion.do")
    public void updateProductVersion(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            Product updateProduct = JsonUtils.parseObject(formString, Product.class);
            Product dbProduct = productVersionService.getProductVersionById(updateProduct.getId());
            dbProduct.setProductId(updateProduct.getProductId());
            dbProduct.setApplication(updateProduct.getApplication());
            dbProduct.setScenes(updateProduct.getScenes());
            dbProduct.setProductVersionId(updateProduct.getProductVersionId());
            productVersionService.updateProductVersion(dbProduct);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    /**
         * 删除产品版本
         *
         * @param:
         * @return
         * @throws Exception
         */
    @RequestMapping("/deleteProductVersion.do")
    public void deleteProductVersion(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer id = Integer.valueOf(request.getParameter("id"));
            productVersionService.deleteProductVersion(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
