package com.wanke.gitcloud.web;

import com.wanke.gitcloud.Cmd;
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

    @RequestMapping(value = "commits" ,method= RequestMethod.GET)
    @ResponseBody
    public List<Map<String, String>> commitLog(@RequestParam(value = "directory",required=false,defaultValue="default") String directory){
        return cmd.getCommitLog(directory);
    }

    @RequestMapping(value = "historycontent" ,method= RequestMethod.GET)
    @ResponseBody
    public String historyContent(@RequestParam(value = "directory",required=false,defaultValue="default") String directory,@RequestParam(value = "filename") String fileName,@RequestParam(value = "commitid")  String commitId){
        return cmd.gitFileHistoryContent(directory, fileName, commitId);
    }

    @RequestMapping(value = "filecommits" ,method= RequestMethod.GET)
    @ResponseBody
    public List<Map<String,String>>  fileHistoryContent(@RequestParam(value = "directory",required=false,defaultValue="default") String directory,@RequestParam(value = "filename") String fileName){
        return cmd.getCommitLog(directory,fileName);
    }
}
