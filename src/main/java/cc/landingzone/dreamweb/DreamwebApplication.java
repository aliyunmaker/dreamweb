package cc.landingzone.dreamweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan("cc.landingzone.dreamweb")
@MapperScan("cc.landingzone.dreamweb.dao")
public class DreamwebApplication {

    public static void main(String[] args) {
        SpringApplication.run(DreamwebApplication.class, args);
    }

}
