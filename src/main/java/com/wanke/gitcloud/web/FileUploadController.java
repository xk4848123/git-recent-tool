package com.wanke.gitcloud.web;

import com.wanke.gitcloud.Cmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class FileUploadController {


    @Autowired
    private Cmd cmd;

    @Value("${my.rootDirectory}")
    private String rootDirectory;
    /*
     * 获取file.html页面
     */
    @RequestMapping(value = "file",method= RequestMethod.GET)
    public String file(){
        return "/file";
    }

    /**
     * 实现文件上传
     * */
    @RequestMapping(value="fileUpload",method= RequestMethod.POST)
    @ResponseBody
    public String fileUpload(@RequestParam(value="directory",required=false,defaultValue="default") String directory,@RequestParam("fileName") MultipartFile file){
        if(file.isEmpty()){
            return "false";
        }
        cmd.initDirectory(directory);
        String fileName = file.getOriginalFilename();
        int size = (int) file.getSize();
        System.out.println(fileName + "-->" + size);
        
        String path = rootDirectory + File.separator + directory;
        File dest = new File(path + File.separator + fileName);
        if(!dest.getParentFile().exists()){ //判断文件父目录是否存在
            return "false";
        }
        try {
            file.transferTo(dest); //保存文件
            if (cmd.gitCommit(directory)){
                return  "true";
            }else {
                return  "false";
            }
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "false";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "false";
        }
    }

    /*
     * 获取multifile.html页面
     */
    @RequestMapping(value = "multifile",method= RequestMethod.GET)
    public String multifile(){
        return "/multifile";
    }
    
    /**
     * 实现多文件上传
     * */
    @RequestMapping(value="multifileUpload",method= RequestMethod.POST)
  /**public @ResponseBody String multifileUpload(@RequestParam("fileName")List<MultipartFile> files) */
    public @ResponseBody String multifileUpload(HttpServletRequest request){
        Map<String,String[]> map = request.getParameterMap();
        Set<String> keys = map.keySet();
        String[] values = request.getParameterValues("directory");
        String directory = values[0];
        if (directory == null || "".equals(directory)){
            directory = "default";
        }
        List<MultipartFile> files = ((MultipartHttpServletRequest)request).getFiles("fileName");
        
        if(files.isEmpty()){
            return "false";
        }
        cmd.initDirectory(directory);
        String path = rootDirectory + File.separator + directory;
        
        for(MultipartFile file:files){
            String fileName = file.getOriginalFilename();
            int size = (int) file.getSize();
            System.out.println(fileName + "-->" + size);
            
            if(file.isEmpty()){
                continue;
            }else{        
                File dest = new File(path + "/" + fileName);
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
        if (cmd.gitCommit(directory)){
            return  "true";
        }else {
            return  "false";
        }
    }
}