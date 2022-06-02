package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.dao.ProductDao;
import cc.landingzone.dreamweb.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

@Component
public class ProductService {
    @Autowired
    private ProductDao productDao;

    @Transactional
    public List<String> getApplication () {
        return productDao.getApplication();
    }

    @Transactional
    public List<String> getScenes (String application) {
        return productDao.getScenes(application);
    }

    @Transactional
    public String getProductId (String application, String scene) {
        return productDao.getProductId(application, scene);
    }

    @Transactional
    public Integer getExampleId (String productId, String exampleName) {
        return productDao.getExampleId(productId, exampleName);
    }

    @Transactional
    public void addExample (String productId, String exampleName) {
        productDao.addExample(productId, exampleName);
    }

    @Transactional
    public void updateExample(String processid) {

    }

    @Transactional
    public void getExample (String processid) {

    }
}
