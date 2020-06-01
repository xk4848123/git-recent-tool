package com.wanke.gitcloud.web;

import com.wanke.gitcloud.Cmd;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@Api(value = "版本比较", tags = {"版本比较"})
@RequestMapping(value = "diff")
public class DiffController {

    @Autowired
    private Cmd cmd;

    /**
     * firstcommitid相对于该commitid
     * */
    @ApiOperation(value = "与HEAD版本比较", notes = "与HEAD版本比较", httpMethod = "GET")
    @RequestMapping(value = "diffvshead", method = RequestMethod.GET,produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String diffVshead(@RequestParam(value = "directory", required = false, defaultValue = "default") String directory,
                                               @RequestParam(value = "commitid") String commitId) {
        return cmd.gitDiffVsHead(directory,commitId);
    }

    /**
     * firstcommitid相对于secondcommitid
     * */
    @ApiOperation(value = "两个版本作比较(first相较与second的变化)", notes = "两个版本作比较(first相较与second的变化)", httpMethod = "GET")
    @RequestMapping(value = "diffcustomize", method = RequestMethod.GET,produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String diffCustomize(@RequestParam(value = "directory", required = false, defaultValue = "default") String directory,
                            @RequestParam(value = "firstcommitid") String fCommitId,@RequestParam(value = "secondcommitid") String sCommitId) {
        return cmd.gitDiffCustomize(directory,fCommitId,sCommitId);
    }
}
