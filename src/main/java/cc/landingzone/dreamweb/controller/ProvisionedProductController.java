package cc.landingzone.dreamweb.controller;

import cc.landingzone.dreamweb.model.*;
import cc.landingzone.dreamweb.service.ProductService;
import cc.landingzone.dreamweb.service.ProvisionedProductService;
import cc.landingzone.dreamweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 产品实例相关请求的处理
 *
 * @author: laodou
 * @createDate: 2022/6/21
 */
@Controller
@RequestMapping("/provisionedProduct")
public class ProvisionedProductController extends BaseController{

    @Autowired
    private ProvisionedProductService provisionedProductService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;
    /**
     * 获取所有产品实例信息
     *
     *
     * @return 产品实例列表
     * @throws Exception
     */
    @GetMapping("/searchProvisionedProduct.do")
    public void searchProvisionedProduct(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer start = Integer.valueOf(request.getParameter("start"));
            Integer limit = Integer.valueOf(request.getParameter("limit"));
            Page page = new Page(start, limit);
            List<ProvisionedProduct> list = provisionedProductService.listProvisionedProducts(page);
            List<ProvisionedProductVO> list1 = new ArrayList<>();
            for (ProvisionedProduct provisionedProduct : list) {
                ProvisionedProductVO provisionedProductVO = new ProvisionedProductVO();
                provisionedProductVO.setId(provisionedProduct.getId());
                provisionedProductVO.setServicecatalogProvisionedProductId(provisionedProduct.getServicecatalogProvisionedProductId());
                provisionedProductVO.setProvisionedProductName(provisionedProduct.getProvisionedProductName());

                Product product = productService.getProductById(provisionedProduct.getProductId());
                provisionedProductVO.setServicecatalogProductId(Optional.ofNullable(product).map(Product::getServicecatalogProductId).orElse(null));
                provisionedProductVO.setProductName(Optional.ofNullable(product).map(Product::getProductName).orElse(null));

                provisionedProductVO.setRoleId(provisionedProduct.getRoleId());

                User user = userService.getUserById(provisionedProduct.getStarterId());
                provisionedProductVO.setStarterName(Optional.ofNullable(user).map(User::getLoginName).orElse(null));

                provisionedProductVO.setStatus(provisionedProduct.getStatus());
                provisionedProductVO.setParameter(provisionedProduct.getParameter());
                provisionedProductVO.setOutputs(provisionedProduct.getOutputs());
                provisionedProductVO.setCreateTime(provisionedProduct.getCreateTime());
                list1.add(provisionedProductVO);
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

    @RequestMapping("/updateProvisionedProduct.do")
    public void updateProvisionedProduct(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String servicecatalogProvisionedProductId = request.getParameter("servicecatalogProvisionedProductId");
        String flag = provisionedProductService.searchStatus(servicecatalogProvisionedProductId);
        result.setSuccess(true);
        result.setData(flag);
        outputToJSON(response, result);
    }

    @RequestMapping("/getRole.do")
    public void getRole(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByLoginName(userName);
//        Map<String, String> map1 = new HashMap<>();
//        map1.put("role", "我的");
//        Map<String, String> map2 = new HashMap<>();
//        map2.put("role", "所有");
        if(user.getRole().equals("ROLE_ADMIN")) {
            List<Permission> roles = new ArrayList<>();
            Permission permission1 = new Permission();
            permission1.setRole("我的");
            Permission permission2 = new Permission();
            permission2.setRole("所有");
            roles.add(permission1);
            roles.add(permission2);
            result.setData(roles);
        } else {
            List<Permission> roles = new ArrayList<>();
            Permission permission1 = new Permission();
            permission1.setRole("我的");
            roles.add(permission1);
            result.setData(roles);
        }
        outputToJSON(response, result);
    }

//    @RequestMapping("/searchProvisionedProductByRole.do")
//    public void searchProvisionedProductByRole(HttpServletRequest request, HttpServletResponse response) {
//        String role = request.getParameter("role");
//        List<ProvisionedProduct> provisionedProducts;
//        if(role.equals("所有")) {
//            provisionedProducts = provisionedProductService.listProvisionedProducts();
//        }
//        List<ProvisionedProduct> provisionedProducts = provisionedProductService.listProvisionedProductsByRole(role);
//
//    }

    @GetMapping("/searchProvisionedProduct2.do")
    public void searchProvisionedProduct2(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer start = Integer.valueOf(request.getParameter("start"));
            Integer limit = Integer.valueOf(request.getParameter("limit"));
            Page page = new Page(start, limit);
            List<ProvisionedProduct> list = provisionedProductService.listProvisionedProducts2(page);
            List<ProvisionedProductVO> list1 = new ArrayList<>();
            for (ProvisionedProduct provisionedProduct : list) {
                ProvisionedProductVO provisionedProductVO = new ProvisionedProductVO();
                provisionedProductVO.setId(provisionedProduct.getId());
                provisionedProductVO.setServicecatalogProvisionedProductId(provisionedProduct.getServicecatalogProvisionedProductId());
                provisionedProductVO.setProvisionedProductName(provisionedProduct.getProvisionedProductName());

                Product product = productService.getProductById(provisionedProduct.getProductId());
                provisionedProductVO.setServicecatalogProductId(Optional.ofNullable(product).map(Product::getServicecatalogProductId).orElse(null));
                provisionedProductVO.setProductName(Optional.ofNullable(product).map(Product::getProductName).orElse(null));

                provisionedProductVO.setRoleId(provisionedProduct.getRoleId());

                User user = userService.getUserById(provisionedProduct.getStarterId());
                provisionedProductVO.setStarterName(Optional.ofNullable(user).map(User::getLoginName).orElse(null));

                provisionedProductVO.setStatus(provisionedProduct.getStatus());
                provisionedProductVO.setParameter(provisionedProduct.getParameter());
                provisionedProductVO.setOutputs(provisionedProduct.getOutputs());
                provisionedProductVO.setCreateTime(provisionedProduct.getCreateTime());
                list1.add(provisionedProductVO);
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
}
