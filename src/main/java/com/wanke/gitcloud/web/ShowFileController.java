package com.wanke.gitcloud.web;

import com.wanke.gitcloud.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "files")
public class ShowFileController {

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "showall", method = RequestMethod.GET)
    @ResponseBody
    public List<String> showDirectory(){
        return fileService.showDirectroys();
    }

    @RequestMapping(value = "{directory}", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, String>> showDirectory(@PathVariable("directory") String directory){
        return fileService.showFileDetails(directory);
    }

}
