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
@RequestMapping(value = "diff")
public class DiffController {

    @Autowired
    private Cmd cmd;

    /**
     * firstcommitid相对于该commitid
     * */
    @RequestMapping(value = "diffvshead", method = RequestMethod.GET)
    @ResponseBody
    public String diffVshead(@RequestParam(value = "directory", required = false, defaultValue = "default") String directory,
                                               @RequestParam(value = "commitid") String commitId) {
        return cmd.gitDiffVsHead(directory,commitId);
    }

    /**
     * firstcommitid相对于secondcommitid
     * */
    @RequestMapping(value = "diffcustomize", method = RequestMethod.GET)
    @ResponseBody
    public String diffCustomize(@RequestParam(value = "directory", required = false, defaultValue = "default") String directory,
                            @RequestParam(value = "firstcommitid") String fCommitId,@RequestParam(value = "secondcommitid") String sCommitId) {
        return cmd.gitDiffCustomize(directory,fCommitId,sCommitId);
    }
}
