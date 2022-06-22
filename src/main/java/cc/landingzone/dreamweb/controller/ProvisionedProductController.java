package cc.landingzone.dreamweb.controller;

import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.ProvisionedProduct;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.ProductService;
import cc.landingzone.dreamweb.service.ProvisionedProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
            List<ProvisionedProduct> list = provisionedProductService.listExample(page);
            result.setTotal(page.getTotal());
            result.setData(list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
