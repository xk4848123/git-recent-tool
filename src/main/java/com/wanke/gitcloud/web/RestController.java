package com.wanke.gitcloud.web;

import com.wanke.gitcloud.Cmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "reset")
public class RestController {

    @Autowired
    private Cmd cmd;

    @RequestMapping(value = "{directory}",method= RequestMethod.POST,produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String restToRevision(@PathVariable("directory") String directory,@RequestParam("revision") String revision){
        if (cmd.rollBackPreRevision(directory,revision)){
            return  "success";
        }else {
            return  "fail";
        }
    }

}
