package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SampleController {

    private Logger logger = LoggerFactory.getLogger(SampleController.class);

    public static final String lineBreak = "\n";

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    @ResponseBody
    String root() {
        return "Hello World! @" + new Date();
    }

    @RequestMapping("/hello")
    public void hello(HttpServletRequest request, HttpServletResponse response) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(new Date());
            sb.append(lineBreak);
            response.getWriter().write(sb.toString());
            response.flushBuffer();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @RequestMapping("/home")
    public void test(HttpServletRequest request, HttpServletResponse response) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(Thread.currentThread());
            User user = userService.getUserByLoginName("kidccc@gmail.com");
            response.getWriter().write(user.getLoginName() + user.getId());
            response.flushBuffer();
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }


    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/test")
    public String test(Model model) {
        model.addAttribute("name", "charles");
        return "test";
    }
}