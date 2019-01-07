package com.leo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liang on 2017/6/6.
 */
@Controller
public class LeoStaticController {

    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }

}
