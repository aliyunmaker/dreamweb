package cc.landingzone.dreamcmp.demo.workshop.service;

import com.aliyun.sts20150401.Client;
import com.aliyun.sts20150401.models.AssumeRoleRequest;
import com.aliyun.sts20150401.models.AssumeRoleResponse;
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

    // @Autowired(required = false)
    // private Client stsClientEcsRole;

    private Client getClient() {
        // 如果是本地环境 使用 AK/SK 测试
        // return stsClientEcsRole == null ? stsClient : stsClientEcsRole;

        return stsClient;
    }

    public GetCallerIdentityResponse getCallerIdentity() throws Exception {
        return getClient().getCallerIdentity();
    }

    public AssumeRoleResponse assumeRole(AssumeRoleRequest request) throws Exception {
        return getClient().assumeRole(request);
    }
}
