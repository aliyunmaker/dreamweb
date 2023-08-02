package cc.landingzone.dreamcmp.demo.costanalysis;

import cc.landingzone.dreamcmp.common.*;
import com.aliyun.bssopenapi20171214.models.DescribeSplitItemBillResponseBody;
import kotlin.Pair;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Utils for Cost Analysis
 */
public class CostAnalysisUtil {
    public static Map<String, Float> getTotalAmount(String billingCycle, String tagKey, String productCode) throws Exception {
        Map<String, Float> totalAmounts = new HashMap<>();
        List<String> tagValues = new ArrayList<>();

        /* 支持获得各应用或者各部门的账单 */
        if ("application".equals(tagKey)) {
            for (ApplicationEnum applicationEnum : ApplicationEnum.values()) {
                tagValues.add(applicationEnum.getName());
            }
        } else if ("department".equals(tagKey)) {
            for (DepartmentEnum departmentEnum: DepartmentEnum.values()) {
                tagValues.add(departmentEnum.name());
            }
        } else {
            throw new IllegalArgumentException("Do not support this tag key!");
        }

        /* parallelize tasks */
        int numThreads = tagValues.size();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<Future<Pair<String, Float>>> futures = new ArrayList<>();

        for (String tagValue: tagValues) {
            Future<Pair<String, Float>> future = executor.submit(() -> getTotalAmountByTagValue(billingCycle, tagKey, tagValue, productCode));
            futures.add(future);
        }

        for (Future<Pair<String, Float>> future: futures) {
            Pair<String, Float> tagValueTotalAmount = future.get();
            totalAmounts.put(tagValueTotalAmount.getFirst(), tagValueTotalAmount.getSecond());
        }

        return totalAmounts;
    }

    public static Pair<String, Float> getTotalAmountByTagValue(String billingCycle, String tagKey, String tagValue, String productCode) throws Exception {
        Float totalAmount = 0.0f;
        com.aliyun.bssopenapi20171214.Client client = ClientHelper.createBSSClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.bssopenapi20171214.models.DescribeSplitItemBillRequest.DescribeSplitItemBillRequestTagFilter tagFilter0 = new com.aliyun.bssopenapi20171214.models.DescribeSplitItemBillRequest.DescribeSplitItemBillRequestTagFilter()
                .setTagKey(tagKey)
                .setTagValues(Collections.singletonList(tagValue));
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        List<DescribeSplitItemBillResponseBody.DescribeSplitItemBillResponseBodyDataItems> items;

        if ("all".equals(productCode)) {
            items = new ArrayList<>();
            for (ServiceEnum serviceEnum : ServiceEnum.values()) {
                String serviceName = serviceEnum.name().toLowerCase();
                com.aliyun.bssopenapi20171214.models.DescribeSplitItemBillRequest describeSplitItemBillRequest = new com.aliyun.bssopenapi20171214.models.DescribeSplitItemBillRequest()
                        .setBillingCycle(billingCycle)
                        .setTagFilter(Collections.singletonList(tagFilter0))
                        .setMaxResults(300)
                        .setProductCode(serviceName);
                List<DescribeSplitItemBillResponseBody.DescribeSplitItemBillResponseBodyDataItems> serviceItems = client.describeSplitItemBillWithOptions(describeSplitItemBillRequest, runtime).getBody().getData().getItems();
                if (serviceItems != null) {
                    items.addAll(serviceItems);
                }
            }
        } else {
            com.aliyun.bssopenapi20171214.models.DescribeSplitItemBillRequest describeSplitItemBillRequest = new com.aliyun.bssopenapi20171214.models.DescribeSplitItemBillRequest()
                    .setBillingCycle(billingCycle)
                    .setTagFilter(Collections.singletonList(tagFilter0))
                    .setMaxResults(300)
                    .setProductCode(productCode);
            items = client.describeSplitItemBillWithOptions(describeSplitItemBillRequest, runtime).getBody().getData().getItems();
        }


        for (DescribeSplitItemBillResponseBody.DescribeSplitItemBillResponseBodyDataItems item : items) {
            totalAmount += item.getPretaxAmount();
        }

        return new Pair<>(tagValue, totalAmount);
    }

    public static Map<String, Map<String, Float>> getPeriodTotalAmounts(String billingCycleStart, String billingCycleEnd, String tagKey, String productCode) throws Exception {
        /* 外层Map的key是应用/部门，内层Map的key是时间 */
        Map<String, Map<String, Float>> periodTotalAmounts = new HashMap<>();
        List<String> billingCycles = new ArrayList<>();

        if ("application".equals(tagKey)) {
            for (ApplicationEnum applicationEnum : ApplicationEnum.values()) {
                periodTotalAmounts.put(applicationEnum.getName(), new HashMap<>());
            }
        } else if ("department".equals(tagKey)) {
            for (DepartmentEnum departmentEnum: DepartmentEnum.values()) {
                periodTotalAmounts.put(departmentEnum.name(), new HashMap<>());
            }
        } else {
            throw new IllegalArgumentException("Do not support this tag key!");
        }

        String curBillingCycle = billingCycleEnd;
        Map<String, Float> totalAmount = getTotalAmount(curBillingCycle, tagKey, productCode);
        for (Map.Entry<String, Float> amount: totalAmount.entrySet()) {
            Map<String, Float> cycleAmount = periodTotalAmounts.get(amount.getKey());
            cycleAmount.put(curBillingCycle, amount.getValue());
            periodTotalAmounts.put(amount.getKey(), cycleAmount);
        }
        while (!curBillingCycle.equals(billingCycleStart)) {
            int curYear = Integer.parseInt(curBillingCycle.split("-")[0]);
            int curMonth = Integer.parseInt(curBillingCycle.split("-")[1]);
            if (curMonth - 1 > 0) {
                curMonth --;
            } else {
                curYear --;
                curMonth = curMonth + 12 - 1;
            }
            curBillingCycle = curYear + "-" + String.format("%02d", curMonth);
            totalAmount = getTotalAmount(curBillingCycle, tagKey, productCode);
            for (Map.Entry<String, Float> amount: totalAmount.entrySet()) {
                Map<String, Float> cycleAmount = periodTotalAmounts.get(amount.getKey());
                cycleAmount.put(curBillingCycle, amount.getValue());
                periodTotalAmounts.put(amount.getKey(), cycleAmount);
            }
        }

        return periodTotalAmounts;
    }
}
