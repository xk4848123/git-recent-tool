package com.wanke.gitcloud.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    @RequestMapping(value = "",method= RequestMethod.GET)
    public String file(){
        return "/index/index";
    }

    @RequestMapping(value = "{directory}",method= RequestMethod.GET)
    public String fileDetail(@PathVariable("directory") String directory){
        return "/index/filedetail";
    }
}
