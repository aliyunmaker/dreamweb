package cc.landingzone.dreamweb.dao;


import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProductDao {


    List<String> getApplication();

    List<String> getScenes(String application);

    String getProductId(String application, String scene);

    Integer getExampleId(String productId, String exampleName);

    void addExample(String productId, String exampleName);
}
