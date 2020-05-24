package com.wanke.gitcloud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileService {

    @Value("${my.rootDirectory}")
    private String rootDirectory;

    public List<String> showDirectroys() {
        File dir = new File(rootDirectory);
        List<String> filesList = new ArrayList<>();
        if (!dir.exists() || !dir.isDirectory()) {// 判断是否存在目录
            return filesList;
        }
        String[] files = dir.list();
        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            if (!file.getName().equals(".gitDirectory")) {
                filesList.add(file.getName());
            }
        }
        return  filesList;
    }


    public List<Map<String,String>> showFileDetails(String directory,String folders) {
        String basePath =rootDirectory + File.separator + directory;
        String path = "";
        if (folders == null || "".equals(folders)){
           path = basePath;
        }else {
            path = basePath;
            String[] folderArray = folders.split("\\*");
            for(String folder : folderArray){
                path = path + File.separator + folder;
            }
        }
        File dir = new File(path);
        List<Map<String,String>> filesList = new ArrayList<>();
        if (!dir.exists() || !dir.isDirectory()) {// 判断是否存在目录
            HashMap<String,String> map = new HashMap<>();
            map.put("data","notroot");
            filesList.add(map);
            return filesList;
        }
        String[] files = dir.list();
        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            if(file.getName().equals(".git")){
                continue;
            }
            String fileType = "file";
            if (file.isDirectory()){
                fileType = "directory";
            }
            HashMap<String,String> map = new HashMap<>();
            map.put("type",fileType);
            map.put("fileName",file.getName());
            map.put("size",String.valueOf(file.length()) + "B");
            DateTimeFormatter df = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
            map.put("modifiedTime" ,  df.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.of("Asia/Shanghai"))));
            filesList.add(map);
        }
        return  filesList;
    }


    public boolean createFolder(String directory,String folders,String newfolder) {
        if (newfolder.contains("*")){
            return  false;
        }
        String basePath =rootDirectory + File.separator + directory;
        String path = "";
        if (folders == null || "".equals(folders)){
            path = basePath;
        }else {
            path = basePath;
            String[] folderArray = folders.split("\\*");
            for(String folder : folderArray){
                path = path + File.separator + folder;
            }
        }
        File dir = new File(path + File.separator + newfolder);
        if (!dir.exists()){
            return dir.mkdir();
        }

        return true;
    }

    /**
     * 获取物理路径
     */

    public String getPath(String directory,String folders,String fileName,String spliFlag){
        String basePath = rootDirectory + spliFlag + directory;
        String path = "";
        if (folders == null || "".equals(folders)){
            path = basePath;
        }else {
            path = basePath;
            String[] folderArray = folders.split("\\*");
            for(String folder : folderArray){
                path = path + spliFlag + folder;
            }
        }
        if (fileName != null){
            return path + spliFlag + fileName;
        }
        return path;

    }


    /**
     * 获取仓库下相对路径
     */

    public String getPath(String folders,String fileName,String spliFlag){
        String path = "";
        if (folders == null || "".equals(folders)){
            path = fileName;
        }else {
            String[] folderArray = folders.split("\\*");
            for(String folder : folderArray){
                if ("".equals(path)){
                    path = folder;
                }else {
                    path = path + spliFlag + folder;
                }

            }
            path =path + spliFlag + fileName;
        }

        return path;

    }

    public boolean deleteFile(String path){
        boolean success = (new File(path)).delete();
        return  success;
    }

    public  boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
               //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

}
