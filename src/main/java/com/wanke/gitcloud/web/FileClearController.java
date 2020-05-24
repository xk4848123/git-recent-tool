package com.wanke.gitcloud.web;

import com.wanke.gitcloud.Cmd;
import com.wanke.gitcloud.FileCheck;
import com.wanke.gitcloud.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;

@Controller
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

    @RequestMapping(value = "file/{directory}/{folders}", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    public String deleteFile(@PathVariable("directory") String directory, @PathVariable("folders") String filePath,
                             @RequestParam("file") String file) {

        if (fileCheck.isFile(directory, filePath)) {
            if (fileService.deleteFile(rootDirectory + File.separator + directory + File.separator + filePath)) {
                cmd.gitCommit(directory);
                return "true";
            }
        }
        return "fail";
    }

    @RequestMapping(value = "dir/{directory}/{folders}", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    public String deleteFolder(@PathVariable("directory") String directory, @PathVariable("folders") String folders) {
        if (fileCheck.isDirectory(directory, folders)) {
            boolean isSuccess = fileService.deleteDir(new File(rootDirectory + File.separator + directory + File.separator + folders));
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
