package com.wanke.gitcloud;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Set;


@Component
public class Cmd {

    @Value("${my.rootDirectory}")
    private String rootDirectory;

    private HashMap<String, Git> gitMap = new HashMap<String, Git>();

    public synchronized void gitInit(String directory) {
        try {
            if (gitMap.get(directory) == null) {
                Git git = Git.init().setDirectory(new File(rootDirectory + directory)).call();
                gitMap.put(directory, git);
            }
        } catch (GitAPIException e) {
        }
    }

    public synchronized boolean gitCommit(String directory) {
        //提交gita
        Git git = null;
        try {
            git = gitMap.get(directory);
            if (git == null) {
                git = Git.open(new File(rootDirectory + directory));
            }
            AddCommand addCommand = git.add();//add操作 add -A操作在jgit不知道怎么用 没有尝试出来 有兴趣的可以看下jgitAPI研究一下 欢迎留言
            addCommand.addFilepattern(".").call();

            RmCommand rm = git.rm();
            Status status = git.status().call();//循环add missing 的文件 没研究出missing和remove的区别 就是删除的文件也要提交到git
            Set<String> missing = status.getMissing();
            for (String m : missing) {
                rm.addFilepattern(m).call(); //每次需重新获取rm status 不然会报错
                rm = git.rm();
                status = git.status().call();
            }
            //循环add remove 的文件
            Set<String> removed = status.getRemoved();
            for (String r : removed) {
                rm.addFilepattern(r).call();
                rm = git.rm();
                status = git.status().call();
            }
            git.commit().setMessage("commit").call();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


}
