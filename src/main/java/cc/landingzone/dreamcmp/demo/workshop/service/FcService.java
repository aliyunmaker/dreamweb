package cc.landingzone.dreamcmp.demo.workshop.service;

import com.aliyun.fc20230330.Client;
import com.aliyun.fc20230330.models.InvokeFunctionHeaders;
import com.aliyun.sdk.service.fc20230330.AsyncClient;
import com.aliyun.sdk.service.fc20230330.models.InvokeFunctionRequest;
import com.aliyun.sdk.service.fc20230330.models.InvokeFunctionResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * @author 恬裕
 * @date 2025/1/20
 */
@Service
public class FcService {

    @Autowired
    private Client fcClient;

    @Autowired
    private AsyncClient asyncClient;

    /**
     * 异步调用FC
     */
    public String invokeFunctionAsync(String functionName, String payload) {
        try {
            InputStream payloadStream = new ByteArrayInputStream(payload.getBytes());
            InvokeFunctionRequest invokeFunctionRequest = InvokeFunctionRequest.builder()
                .functionName(functionName)
                .body(payloadStream)
                .build();
            CompletableFuture<InvokeFunctionResponse> response = asyncClient.invokeFunction(invokeFunctionRequest);
            InvokeFunctionResponse resp = response.get();
            InputStream stream = resp.getBody();
            return IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public com.aliyun.fc20230330.models.InvokeFunctionResponse invokeFunction(String functionName, String payload)
        throws Exception {
        try {
            InputStream payloadStream = new ByteArrayInputStream(payload.getBytes());
            com.aliyun.fc20230330.models.InvokeFunctionRequest invokeFunctionRequest = new com.aliyun.fc20230330.models.InvokeFunctionRequest()
                .setBody(payloadStream);
            com.aliyun.fc20230330.models.InvokeFunctionResponse response = fcClient.invokeFunction(functionName, invokeFunctionRequest);
            return response;
        } catch (Exception e) {
            throw e;
        }
    }
}
