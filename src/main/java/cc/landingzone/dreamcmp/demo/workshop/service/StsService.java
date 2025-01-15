package cc.landingzone.dreamcmp.demo.workshop.service;

import com.aliyun.sts20150401.Client;
import com.aliyun.sts20150401.models.GetCallerIdentityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yicheng.fyc
 * @date 2025/1/14
 */
@Service
@Slf4j
public class StsService {

    @Autowired
    private Client stsClient;

    @Autowired(required = false)
    private Client stsClientEcsRole;

    public GetCallerIdentityResponse getCallerIdentity() throws Exception {
        GetCallerIdentityResponse res;

        if (stsClientEcsRole == null) {
            // 如果是本地环境 使用 AK/SK 测试
            res = stsClient.getCallerIdentity();
        } else {
            res = stsClientEcsRole.getCallerIdentity();
        }

        return res;
    }
}
