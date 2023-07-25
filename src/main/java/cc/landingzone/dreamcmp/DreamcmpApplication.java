package cc.landingzone.dreamcmp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan("cc.landingzone.dreamcmp")
public class DreamcmpApplication {

    public static void main(String[] args) {
        SpringApplication.run(DreamcmpApplication.class, args);
    }

}
