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
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
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

    @RequestMapping(value = "newgit", method = RequestMethod.POST)
    @ResponseBody
    public String newGit(@RequestParam(value = "directory") String directory) {
        cmd.initDirectory(directory);
        return "success";
    }

    @RequestMapping(value = "createfolder/{directory}", method = RequestMethod.POST)
    @ResponseBody
    public String newFolder(@PathVariable("directory") String directory,
                            @RequestParam(value = "folders", required = false, defaultValue = "") String folders,
                            @RequestParam("foldername") String folderName) {
        if (fileService.createFolder(directory, folders, folderName)) {
            return "success";
        }
        return "fail";
    }


    /**
     * 实现多文件上传
     */
    @RequestMapping(value = "multifileUpload", method = RequestMethod.POST)
    public String multifileUpload(HttpServletRequest request, RedirectAttributes attr) {
        Map<String, String[]> map = request.getParameterMap();
        Set<String> keys = map.keySet();
        String[] values = request.getParameterValues("directory");
        String directory = values[0];
        if (directory == null || "".equals(directory)) {
            directory = "default";
        }
        String[] folderValues = request.getParameterValues("folders");
        String folders = folderValues[0];

        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("fileName");
        if (files.isEmpty()) {
            return "false";
        }
        String path = fileService.getPath(directory, folders, null, File.separator);
        if (path == null){
            return "false";
        }
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            int size = (int) file.getSize();
            System.out.println(fileName + "-->" + size);

            if (file.isEmpty()) {
                continue;
            } else {
                File dest = new File(path + File.separator + fileName);
                if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
                    return "false";
                }
                try {
                    file.transferTo(dest);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    continue;
                }
            }
        }
        cmd.gitCommit(directory);
        attr.addAttribute("folders",folders);
        String redirectUrl = "/" + URLEncoder.encode(directory);;
        return "redirect:" + redirectUrl;

    }

    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
    private void downloadFile(HttpServletResponse response, HttpServletRequest request) {
        //获取仓库
        String[] values = request.getParameterValues("directory");
        String directory = values[0];
        if (directory == null || "".equals(directory)) {
            directory = "default";
        }
        String[] folderValues = request.getParameterValues("folders");

        String folders = folderValues[0];
        String[] filenameValues = request.getParameterValues("filename");
        String fileName = filenameValues[0];
        String[] commitIdValues = request.getParameterValues("commitid");
        String commitId = commitIdValues[0];
        OutputStream outputStream = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            outputStream = response.getOutputStream();
            response.setContentType("application/force-download");// 设置强制下载不打开            
            response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName,"utf-8"));
            if (commitId.equals("head")) {

                //下载最新的本地文件即可
                String path = fileService.getPath(directory, folders, fileName, File.separator);
                if (path == null){
                    return;
                }
                File file = new File(path);
                if (!file.exists()) {
                    return;
                }
                byte[] buffer = new byte[1024];

                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while (i != -1) {
                    outputStream.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } else {
                ByteArrayOutputStream out = cmd.gitFileHistoryContentByte(directory, fileService.getPath(folders, fileName, "/"), commitId);
                if (out == null) {
                    return;
                }
                outputStream.write(out.toByteArray());
            }
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return;
    }

}