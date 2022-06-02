package cc.landingzone.dreamweb;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ServletComponentScan("cc.landingzone.dreamweb")
@MapperScan("cc.landingzone.dreamweb.dao")
public class DreamwebApplication {

    public static void main(String[] args) {
        SpringApplication.run(DreamwebApplication.class, args);
    }

}
