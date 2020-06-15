# git-recent-tool
===========================

## 环境依赖
jdk8+
maven3.5.4+

## 部署步骤
1. 配置git对应本地根目录
    application.properties中my.rootDirectory
    如:windows:my.rootDirectory=E:\\git_002\\
    如:linux:my.rootDirectory=/home/


2. mvn clean package -Dmaven.test.skip=true  //打jar包

3. java -jar xxx.jar


## 目录结构描述
├── Readme.md                      // help
├── src/main/java
     └── com.wanke.gitcloud
                ├── configuration  //文件上传配置，mvc静态资源配置
                ├── web            //控制器
                ├── Cmd.java       //核心版本管理类
                ├── FileCheck.java //目录检测
                ├── FileService    //目录参数和物理路径映射
                ├── GitCloudApplication.java //启动类
├── src/main
     └── resources
              ├── static  //静态资源
              ├── templates  //themleaf模板
              ├── application.properties  //spring boot配置

