package com.wanke.gitcloud.web;

import com.wanke.gitcloud.Cmd;
import com.wanke.gitcloud.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@Api(value = "查询历史", tags = {"查询历史"})
@RequestMapping(value = "commit")
public class CommitController {

    @Autowired
    private Cmd cmd;

    @Autowired
    private FileService fileService;


    @ApiOperation(value = "查询仓库版本", notes = "查询仓库版本", httpMethod = "GET")
    @RequestMapping(value = "commits" ,method= RequestMethod.GET)
    @ResponseBody
    public List<Map<String, String>> commitLog(@RequestParam(value = "directory",required=false,defaultValue="default") String directory,
                                               @RequestParam(value = "pagenum",required=false,defaultValue= "5") String pageNum,
                                               @RequestParam(value = "page",required=false,defaultValue= "1") String page
                                               ){
        Integer intPageNum = Integer.valueOf(pageNum);
        Integer skip = intPageNum * (Integer.valueOf(page) - 1);
        return cmd.getCommitLog(directory,intPageNum,skip);
    }

    @ApiOperation(value = "查看文件某个版本内容", notes = "查看文件某个版本内容", httpMethod = "GET")
    @RequestMapping(value = "historycontent" ,method= RequestMethod.GET,produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String historyContent(@RequestParam(value = "directory",required=false,defaultValue="default") String directory,@RequestParam(value = "filename") String fileName,
                                 @RequestParam(value = "folders",required = false,defaultValue = "") String folders,
                                 @RequestParam(value = "commitid")  String commitId){

        return cmd.gitFileHistoryContent(directory, fileService.getPath(folders,fileName,"/"), commitId);
    }

    @ApiOperation(value = "查询文件版本", notes = "查询文件版本", httpMethod = "GET")
    @RequestMapping(value = "filecommits" ,method= RequestMethod.GET)
    @ResponseBody
    public List<Map<String,String>>  fileHistoryContent(@RequestParam(value = "directory",required=false,defaultValue="default") String directory,
                                                        @RequestParam(value = "folders",required = false,defaultValue = "") String folders,
                                                        @RequestParam(value = "filename") String fileName,
                                                        @RequestParam(value = "pagenum",required=false,defaultValue= "5") String pageNum,
                                                        @RequestParam(value = "page",required=false,defaultValue= "1") String page){
        Integer intPageNum = Integer.valueOf(pageNum);
        Integer skip = intPageNum * (Integer.valueOf(page) - 1);
        return cmd.getCommitLog(directory,fileService.getPath(folders,fileName,"/"),intPageNum,skip);
    }
}
