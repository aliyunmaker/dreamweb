package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController extends BaseController {

    @RequestMapping("/")
    public void hello(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect("index.html");
        } catch (Exception e) {
        }

    }

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginPageTitle", "DreamCMP");
        return "login";
    }

}