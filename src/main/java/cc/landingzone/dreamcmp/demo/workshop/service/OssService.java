package cc.landingzone.dreamcmp.demo.workshop.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * @author yicheng.fyc
 * @date 2025/1/14
 */
@Service
public class OssService {

    @Value("${dreamcmp.workshop.oss_bucket}")
    private String bucket;

    @Autowired
    private OSS ossClient;

    /**
     * 列举文件
     * @param prefix 文件前缀(目录)
     */
    public ListObjectsV2Result listObjects(String prefix) {
        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request();
        listObjectsRequest.setBucketName(bucket);
        listObjectsRequest.setDelimiter("/");
        listObjectsRequest.setPrefix(prefix);

        return ossClient.listObjectsV2(listObjectsRequest);
    }

    /**
     * 下载文件
     * @param key 文件名
     */
    public byte[] getObject(String key) throws IOException {
        GetObjectRequest request = new GetObjectRequest(bucket, key);
        return ossClient.getObject(request).getObjectContent().readAllBytes();
    }

}
