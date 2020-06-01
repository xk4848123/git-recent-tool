package com.wanke.gitcloud.web;

import com.wanke.gitcloud.Cmd;
import com.wanke.gitcloud.FileCheck;
import com.wanke.gitcloud.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@Controller
@Api(value = "清理", tags = {"清理"})
@RequestMapping(value = "clear")
public class FileClearController {

    @Autowired
    private FileService fileService;

    @Value("${my.rootDirectory}")
    private String rootDirectory;

    @Autowired
    private FileCheck fileCheck;

    @Autowired
    private Cmd cmd;

    @ApiOperation(value = "清理文件", notes = "清理文件", httpMethod = "POST")
    @RequestMapping(value = "file/{directory}", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String deleteFile(@PathVariable("directory") String directory, @RequestParam(value = "folders",required = false,defaultValue = "") String folders,
                             @RequestParam("file") String file) {
        String path = fileService.getPath(directory, folders, file, File.separator);
        if (path == null){
            return "fail";
        }
        if (fileCheck.isFile(path)) {
            if (fileService.deleteFile(path)) {
                cmd.gitCommit(directory);
                return "true";
            }
        }
        return "fail";
    }

    @ApiOperation(value = "清理目录", notes = "清理目录", httpMethod = "POST")
    @RequestMapping(value = "dir/{directory}", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String deleteFolder(@PathVariable("directory") String directory, @RequestParam("folders") String folders) {
        String path = fileService.getPath(directory, folders, null, File.separator);
        if (path == null){
            return "fail";
        }
        if (fileCheck.isDirectory(path)) {
            boolean isSuccess = fileService.deleteDir(new File(path));
            cmd.gitCommit(directory);
            if (isSuccess){
                return "true";
            }else {
                return "fail";
            }

        }
        return "fail";
    }

}
