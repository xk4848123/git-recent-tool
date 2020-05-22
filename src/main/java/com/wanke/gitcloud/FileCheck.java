package com.wanke.gitcloud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileCheck {

    @Value("${my.rootDirectory}")
    private String checkDirectory;

    public boolean init0() {
        if (checkDirectory == null || "".equals(checkDirectory)) {
            return false;
        }
        File fileDirectory = new File(checkDirectory);
        if (fileDirectory.exists()) {
            File _gitDirectory = new File(checkDirectory + ".gitDirectory");
            if (_gitDirectory.exists()) {
                return true;
            } else {
                return false;
            }
        } else {
            return creatFileDir(checkDirectory);
        }
    }

    /**
     * 创建目录
     *
     * @param path
     */
    public boolean creatFileDir(String path) {
            File file = new File(path + File.separator + ".gitDirectory");
            boolean isSuccess = file.mkdirs();
            return  isSuccess;

    }





}
