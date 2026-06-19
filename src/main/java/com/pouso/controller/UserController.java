package com.pouso.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    
    @GetMapping("/user") //service user controller edit
     public String editUser(HttpSession session, Model model) {
        return "edit-user";
    }

}
