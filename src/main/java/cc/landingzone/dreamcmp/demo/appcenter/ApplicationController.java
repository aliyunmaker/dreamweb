package cc.landingzone.dreamcmp.demo.appcenter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cc.landingzone.dreamcmp.common.ApplicationEnum;
import cc.landingzone.dreamcmp.common.BaseController;
import cc.landingzone.dreamcmp.common.ResourceUtil;
import cc.landingzone.dreamcmp.common.model.WebResult;
import cc.landingzone.dreamcmp.demo.appcenter.model.Application;
import cc.landingzone.dreamcmp.demo.appcenter.model.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Controller
@RequestMapping("/apps")
public class ApplicationController extends BaseController {

    @RequestMapping("/listApps.do")
    public void listApps(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            ApplicationEnum[] applicationEnums = ApplicationEnum.values();
            List<Application> applications = new ArrayList<>();

            /* parallelize tasks */
            int numThreads = ApplicationEnum.values().length;
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            List<Future<Application>> futures = new ArrayList<>();

            // 提交并行任务
            for (ApplicationEnum applicationEnum : applicationEnums) {
                Future<Application> future = executor.submit(() -> ApplicationUtil.getApplicationInfo(applicationEnum));
                futures.add(future);
            }

            // 获取并行任务的结果
            for (Future<Application> future : futures) {
                Application application = future.get();
                applications.add(application);
            }

            // 关闭线程池
            executor.shutdown();

            result.setTotal(applications.size());
            result.setData(applications);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getAppDetail.do")
    public void getAppDetail(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String appName = request.getParameter("appName");

            List<Resource> resources = ResourceUtil.listResourcesByApplication(appName);

            // 获取环境类型
            ResourceUtil.setEnvironmentType(resources);

            Map<String, List<Resource>> servicesResources = ResourceUtil.getResourcesByServices(resources);

            // 设置操作，设置resourceType
            ResourceUtil.setOperations(servicesResources);

//            for (ServiceEnum serviceEnum: ServiceEnum.values()) {
//                Service service = ApplicationUtil.getServiceByApplication(appName, serviceEnum);
//                resources.addAll(ApplicationUtil.getResourcesByService(service, appName));
//            }

            result.setTotal(servicesResources.size());
            result.setData(servicesResources);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}