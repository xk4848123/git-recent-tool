package com.wanke.gitcloud.web;

import com.wanke.gitcloud.Cmd;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Api(value = "版本回滚", tags = {"用于版本回滚的相关接口"})
@RequestMapping(value = "reset")
public class RestController {

    @Autowired
    private Cmd cmd;

    @ApiOperation(value = "版本回滚", notes = "版本回滚", httpMethod = "POST")
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
