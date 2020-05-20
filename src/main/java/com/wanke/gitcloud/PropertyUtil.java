//package com.wanke.gitcloud;
//
//import org.eclipse.jgit.api.Git;
//import org.springframework.stereotype.Component;
//
//import java.io.*;
//import java.util.Properties;
//import java.util.concurrent.ConcurrentHashMap;
//
//
//@Component
//public class PropertyUtil {
//
//
//    public static void main(String[] args) {
//        ConcurrentHashMap<String, Git> gitMap = new ConcurrentHashMap<String, Git>();
//        System.out.println(gitMap.get("111"));
//    }
//
//    private void writeApplicationProp(String key ,String value){
//        OutputStream fos = null;
//        Properties properties = new Properties();
//   try {
//       properties.load(PropertyUtil.class.getResourceAsStream("/application.properties"));
//       System.out.println(properties.get("my.rootDirectory"));
//       properties.setProperty(key,value);
//
//       fos = new FileOutputStream(PropertyUtil.class.getResource("/application.properties").getFile());
//       properties.store(fos,"update " + key + value);
//      }catch (Exception e){
//       e.printStackTrace();
//      }finally {
//       if (fos != null){
//           try {
//               fos.close();
//           } catch (IOException e) {
//               e.printStackTrace();
//           }
//       }
//   }
//
//
//    }
//
//    public void setMyRootDirectory(String rootDirectory){
//       writeApplicationProp("my.rootDirectory",rootDirectory);
//    }
//
//}