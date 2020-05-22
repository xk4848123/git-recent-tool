package com.wanke.gitcloud.web;

import com.wanke.gitcloud.Cmd;
import com.wanke.gitcloud.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "commit")
public class CommitController {

    @Autowired
    private Cmd cmd;

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "commits" ,method= RequestMethod.GET)
    @ResponseBody
    public List<Map<String, String>> commitLog(@RequestParam(value = "directory",required=false,defaultValue="default") String directory){
        return cmd.getCommitLog(directory);
    }

    @RequestMapping(value = "historycontent" ,method= RequestMethod.GET,produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String historyContent(@RequestParam(value = "directory",required=false,defaultValue="default") String directory,@RequestParam(value = "filename") String fileName,
                                 @RequestParam(value = "folders",required = false,defaultValue = "") String folders,
                                 @RequestParam(value = "commitid")  String commitId){

        return cmd.gitFileHistoryContent(directory, fileService.getPath(folders,fileName,"/"), commitId);
    }

    @RequestMapping(value = "filecommits" ,method= RequestMethod.GET)
    @ResponseBody
    public List<Map<String,String>>  fileHistoryContent(@RequestParam(value = "directory",required=false,defaultValue="default") String directory,
                                                        @RequestParam(value = "folders",required = false,defaultValue = "") String folders,
                                                        @RequestParam(value = "filename") String fileName){

        return cmd.getCommitLog(directory,fileService.getPath(folders,fileName,"/"));
    }
}
