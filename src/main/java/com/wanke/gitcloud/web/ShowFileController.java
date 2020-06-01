package com.wanke.gitcloud.web;

import com.wanke.gitcloud.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@Api(value = "展示仓库目录", tags = {"用于展示仓库目录"})
@RequestMapping(value = "files")
public class ShowFileController {

    @Autowired
    private FileService fileService;

    @ApiOperation(value = "展示仓库", notes = "展示仓库", httpMethod = "GET")
    @RequestMapping(value = "showall", method = RequestMethod.GET)
    @ResponseBody
    public List<String> showDirectory(){
        return fileService.showDirectroys();
    }

    @ApiOperation(value = "展示仓库下目录", notes = "展示仓库下目录", httpMethod = "GET")
    @RequestMapping(value = "{directory}", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, String>> showDirectory(@ApiParam(value = "仓库参数", required = false,example = "default") @PathVariable("directory") String directory ,@ApiParam(value = "目录参数", required = false,example = "default*default") @RequestParam(value = "folders" ,required=false,defaultValue="") String folders){
        return fileService.showFileDetails(directory,folders);
    }

}
