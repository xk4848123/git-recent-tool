package com.wanke.gitcloud.web;

import com.wanke.gitcloud.Cmd;
import com.wanke.gitcloud.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping(value = "newfile")
public class FileNewController {

    @Autowired
    private Cmd cmd;

    @Autowired
    private FileService fileService;

    @Value("${my.rootDirectory}")
    private String rootDirectory;

    @RequestMapping(value = "newgit",method = RequestMethod.POST)
    @ResponseBody
    public String newGit(@RequestParam(value = "directory") String directory){
            cmd.initDirectory(directory);
            return "success";
    }

    @RequestMapping(value = "createfolder/{directory}",method = RequestMethod.POST)
    @ResponseBody
    public String newFolder(@PathVariable("directory") String directory,
                            @RequestParam(value = "folders" ,required=false,defaultValue="") String folders,
                            @RequestParam("foldername") String folderName){
        if (fileService.createFolder(directory,folders,folderName)){
            return "success";
        }
        return "fail";
    }



    /**
     * 实现多文件上传
     * */
    @RequestMapping(value="multifileUpload",method= RequestMethod.POST)
    public  String multifileUpload(HttpServletRequest request, RedirectAttributes attr){
        Map<String,String[]> map = request.getParameterMap();
        Set<String> keys = map.keySet();
        String[] values = request.getParameterValues("directory");
        String directory = values[0];
        if (directory == null || "".equals(directory)){
            directory = "default";
        }
        String[] folderValues = request.getParameterValues("folders");
        String folders = folderValues[0];

        List<MultipartFile> files = ((MultipartHttpServletRequest)request).getFiles("fileName");
        if(files.isEmpty()){
            return "false";
        }
        String basePath = rootDirectory + File.separator + directory;
        String path = "";
        if (folders == null || "".equals(folders)){
            path = basePath;
        }else {
            attr.addAttribute("folders",folders);
            path = basePath;
            String[] folderArray = folders.split("\\*");
            for(String folder : folderArray){
                path = path + File.separator + folder;
            }
        }
        for(MultipartFile file:files){
            String fileName = file.getOriginalFilename();
            int size = (int) file.getSize();
            System.out.println(fileName + "-->" + size);
            
            if(file.isEmpty()){
                continue;
            }else{        
                File dest = new File(path + File.separator + fileName);
                if(!dest.getParentFile().exists()){ //判断文件父目录是否存在
                   return "false";
                }
                try {
                    file.transferTo(dest);
                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    continue;
                } 
            }
        }
        cmd.gitCommit(directory);
        String redirectUrl = "/" + directory;
        return "redirect:" + redirectUrl;

    }
}