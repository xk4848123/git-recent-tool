package com.wanke.gitcloud.web;

import com.wanke.gitcloud.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;

@Controller
public class HomeController {

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "",method= RequestMethod.GET)
    public String file(){
        return "index/index";
    }

    @RequestMapping(value = "{directory}",method= RequestMethod.GET)
    public String fileDetail(@PathVariable("directory") String directory){
        return "index/filedetail";
    }

    @RequestMapping(value = "{directory}/singlefile",method= RequestMethod.GET)
    public String singleFile(@PathVariable("directory") String directory,
                             @RequestParam(value = "folders",required = false,defaultValue = "") String folders
    , @RequestParam(value = "filename") String fileName, Model model, RedirectAttributes attr)
    {
        String path = fileService.getPath(directory, folders, fileName,File.separator);
        if (path == null){
            return  "redirect:/";
        }
        File file = new File(path);
        if (file.exists()){
            model.addAttribute("directory",directory);
            model.addAttribute("folders",folders);
            model.addAttribute("filename",fileName);
            model.addAttribute("filefullname",fileName+ "(" + String.valueOf(file.length()) + "B)");
            return "index/singlefile";
        }
        //重定向到原目录
        attr.addAttribute("folders",folders);
        String redirectUrl = "/" + directory;
        return  "redirect:" + redirectUrl;

    }
}
