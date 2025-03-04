package cc.landingzone.dreamcmp.demo.workshop.service;

import com.aliyun.cs20151215.Client;
import com.aliyun.cs20151215.models.DescribeClusterUserKubeconfigRequest;
import com.aliyun.cs20151215.models.DescribeClusterUserKubeconfigResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description:
 * Author: laodouza
 * Date: 2025/2/21
 */
@Service
public class AckService {

    @Autowired
    private Client ackClient;

    public String describeClusterUserKubeconfig(String clusterId) throws Exception {
        DescribeClusterUserKubeconfigRequest describeClusterUserKubeconfigRequest = new DescribeClusterUserKubeconfigRequest()
            .setTemporaryDurationMinutes(1440L)
            .setPrivateIpAddress(true);
        DescribeClusterUserKubeconfigResponse describeClusterUserKubeconfigResponse
            = ackClient.describeClusterUserKubeconfig(clusterId, describeClusterUserKubeconfigRequest);
        return describeClusterUserKubeconfigResponse.getBody().getConfig();
    }
}
