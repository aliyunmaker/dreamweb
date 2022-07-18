package cc.landingzone.dreamweb.controller;

import cc.landingzone.dreamweb.model.*;
import cc.landingzone.dreamweb.service.ProductService;
import cc.landingzone.dreamweb.service.ProvisionedProductService;
import cc.landingzone.dreamweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
}
