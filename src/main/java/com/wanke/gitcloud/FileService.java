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


    public List<Map<String,String>> showFileDetails(String directory) {
        File dir = new File(rootDirectory + File.separator + directory);
        List<Map<String,String>> filesList = new ArrayList<>();
        if (!dir.exists() || !dir.isDirectory()) {// 判断是否存在目录
            return filesList;
        }
        String[] files = dir.list();
        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            if (file.isDirectory()){
                continue;
            }
            HashMap<String,String> map = new HashMap<>();
            map.put("fileName",file.getName());
            map.put("size",String.valueOf(file.length()) + "B");
            DateTimeFormatter df = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
            map.put("modifiedTime" ,  df.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.of("Asia/Shanghai"))));
            filesList.add(map);
        }
        return  filesList;
    }
}
