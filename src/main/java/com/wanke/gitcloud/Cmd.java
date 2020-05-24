package com.wanke.gitcloud;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
public class Cmd {

    @Value("${my.rootDirectory}")
    private String rootDirectory;

    private HashMap<String, Git> gitMap = new HashMap<String, Git>();

    public void initDirectory(String directory) {
        gitInit(directory,true);
    }

    private Git gitInit(String directory,boolean force) {
        Git git = null;
        try {
            git = gitMap.get(directory);
            if (git == null) {
                synchronized (this) {
                    if (gitMap.get(directory) == null) {
                        if (new File(rootDirectory + directory + File.separator + ".git").exists()) {
                            git = Git.open(new File(rootDirectory + directory));
                        } else {
                            if (force){
                                git = Git.init().setDirectory(new File(rootDirectory + directory)).call();
                            }else {
                                return  null;
                            }

                        }
                        gitMap.put(directory, git);
                        return git;
                    }
                }
            }
            return git;
        } catch (Exception e) {
            return git;
        }
    }

    public synchronized boolean gitCommit(String directory) {
        //提交git
        Git git = null;
        try {
            git = gitInit(directory,false);
            if (git == null) {
                return false;
            }
            AddCommand addCommand = git.add();//add操作
            addCommand.addFilepattern(".").call();

            RmCommand rm = git.rm();
            Status status = git.status().call();//循环add missing 的文件
            Set<String> missing = status.getMissing();
            for (String m : missing) {
                rm.addFilepattern(m).call(); //每次需重新获取rm status 不然会报错
                rm = git.rm();
            }
            status = git.status().call();
            //循环add remove 的文件
            Set<String> removed = status.getRemoved();
            for (String r : removed) {
                rm.addFilepattern(r).call();
                rm = git.rm();
            }
            git.commit().setMessage("commit").call();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    public List<Map<String, String>> getCommitLog(String directory,Integer maxCount,Integer skip) {
        Git git = null;
        git = gitInit(directory,false);
        if (git == null){
            return null;
        }
        Iterable<RevCommit> iterable = null;
        try {
            iterable = git.log().setMaxCount(maxCount).setSkip(skip).call();
        } catch (GitAPIException e) {
            return null;
        }
        List<Map<String, String>> logs = new ArrayList<>();
        Iterator<RevCommit> iter = iterable.iterator();
        while (iter.hasNext()) {
            HashMap<String, String> log = new HashMap<>();
            RevCommit commit = iter.next();
            long time = commit.getCommitTime() * 1000L;
            String commitID = commit.getName();  //这个应该就是提交的版本号
            DateTimeFormatter df = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
            log.put("time", df.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("Asia/Shanghai"))));
            log.put("commitID", commitID);
            logs.add(log);
        }
        return logs;

    }

    public List<Map<String, String>> getCommitLog(String directory, String fileName,Integer maxCount,Integer skip) {
        Git git = null;
        git = gitInit(directory,false);
        if (git == null){
            return  null;
        }
        Iterable<RevCommit> iterable = null;
        try {
            iterable = git.log().addPath(fileName).setMaxCount(maxCount).setSkip(skip).call();
        } catch (GitAPIException e) {
            return null;
        }
        List<Map<String, String>> logs = new ArrayList<>();
        Iterator<RevCommit> iter = iterable.iterator();
        while (iter.hasNext()) {
            HashMap<String, String> log = new HashMap<>();
            RevCommit commit = iter.next();
            long time = commit.getCommitTime() * 1000L;
            String commitID = commit.getName();  //这个应该就是提交的版本号
            DateTimeFormatter df = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
            log.put("time", df.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("Asia/Shanghai"))));
            log.put("commitID", commitID);
            logs.add(log);
        }
        return logs;

    }

    public String gitDiffVsHead(String directory, String revision) {
        StringBuilder result = new StringBuilder();
        ByteArrayOutputStream outputStream = null;
        Repository repository = null;
        try {
            Git git = gitInit(directory,false);
            if (git == null){
             return  "";
            }
            repository = git.getRepository();
            Iterable<RevCommit> iterable = git.log().setMaxCount(1).call();
            RevCommit curRevCommit = null;
            for (RevCommit revCommit : iterable) {
                curRevCommit = revCommit;
            }
            RevWalk walk = new RevWalk(repository);
            ObjectId objId = repository.resolve(revision);
            RevCommit historyCommit = walk.parseCommit(objId);
            AbstractTreeIterator newCommit = getAbstractTreeIterator(curRevCommit, repository);
            AbstractTreeIterator oldCommit = getAbstractTreeIterator(historyCommit, repository);
            List<DiffEntry> diff = git.diff().setOldTree(oldCommit).setNewTree(newCommit).call();
            outputStream = new ByteArrayOutputStream();
            DiffFormatter diffFormatter = new DiffFormatter(outputStream);
            //设置比较器为忽略空白字符对比（Ignores all whitespace）
            diffFormatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
            diffFormatter.setRepository(repository);
            for (DiffEntry diffEntry : diff) {
                diffFormatter.format(diffEntry);
                result.append(outputStream.toString());
                outputStream.reset();
            }
        } catch (Exception e) {
            return "";
        } finally {
            if (repository != null){
                repository.close();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result.toString().replaceAll("\n", "<br>");
    }

    public String gitDiffCustomize(String directory, String firstRevision, String secondRevision) {
        StringBuilder result = new StringBuilder();
        ByteArrayOutputStream outputStream = null;
        Repository repository = null;
        try {
            Git git = gitInit(directory,false);
            if (git == null){
                return  "";
            }
            repository = git.getRepository();
            RevWalk walk = new RevWalk(repository);
            ObjectId secondObjId = repository.resolve(secondRevision);
            ObjectId firstObjId = repository.resolve(firstRevision);
            RevCommit secondCommit = walk.parseCommit(secondObjId);
            RevCommit curRevCommit = walk.parseCommit(firstObjId);
            AbstractTreeIterator newCommit = getAbstractTreeIterator(curRevCommit, repository);
            AbstractTreeIterator oldCommit = getAbstractTreeIterator(secondCommit, repository);
            List<DiffEntry> diff = git.diff().setOldTree(oldCommit).setNewTree(newCommit).call();
            outputStream = new ByteArrayOutputStream();
            DiffFormatter diffFormatter = new DiffFormatter(outputStream);
            //设置比较器为忽略空白字符对比（Ignores all whitespace）
            diffFormatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
            diffFormatter.setRepository(repository); // 这里为什么还要设置它
            for (DiffEntry diffEntry : diff) {
                diffFormatter.format(diffEntry);
                result.append(outputStream.toString());
                outputStream.reset();

            }
        } catch (Exception e) {
            return "";
        } finally {
            if (repository != null){
                repository.close();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(result.toString());
        return result.toString().replaceAll("\n", "<br>");
    }


    public static AbstractTreeIterator getAbstractTreeIterator(RevCommit commit, Repository repository) {
        RevWalk revWalk = new RevWalk(repository);
        CanonicalTreeParser treeParser = null;
        try {
            RevTree revTree = revWalk.parseTree(commit.getTree().getId());
            treeParser = new CanonicalTreeParser();
            treeParser.reset(repository.newObjectReader(), revTree.getId());
            revWalk.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return treeParser;
    }

    public String gitFileHistoryContent(String directory, String fileName, String revision) {
        Git git = gitInit(directory,false);
        if (git == null){
            return "";
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Repository repository = null;
        try {
            repository = git.getRepository();
            RevWalk walk = new RevWalk(repository);
            ObjectId objId = repository.resolve(revision);
            RevCommit revCommit = walk.parseCommit(objId);
            RevTree revTree = revCommit.getTree();
            TreeWalk treeWalk = TreeWalk.forPath(repository, fileName, revTree);
            ObjectId blobId = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.open(blobId);
            loader.copyTo(out);
            return out.toString().replaceAll("\n", "<br>");
        } catch (Exception e) {
            return null;
        } finally {
            if (repository != null)
                repository.close();
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public ByteArrayOutputStream gitFileHistoryContentByte(String directory, String fileName, String revision) {
        Git git = gitInit(directory,false);
        if (git == null){
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Repository repository = null;
        try {
            repository = git.getRepository();
            RevWalk walk = new RevWalk(repository);
            ObjectId objId = repository.resolve(revision);
            RevCommit revCommit = walk.parseCommit(objId);
            RevTree revTree = revCommit.getTree();
            TreeWalk treeWalk = TreeWalk.forPath(repository, fileName, revTree);
            ObjectId blobId = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.open(blobId);
            loader.copyTo(out);
            return out;
        } catch (Exception e) {
            return null;
        } finally {
            if (repository != null)
                repository.close();
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean rollBackPreRevision(String directory, String revision){
        Git git = gitInit(directory,false);
        if (git == null){
            return false;
        }
        Repository repository = null;
        try {
            repository = git.getRepository();
            RevWalk walk = new RevWalk(repository);
            ObjectId objId = repository.resolve(revision);
            RevCommit revCommit = walk.parseCommit(objId);
            String preVision = revCommit.getName();
            git.reset().setMode(ResetCommand.ResetType.HARD).setRef(preVision).call();
            return true;
        } catch (Exception e){
            return false;
        }finally {
            repository.close();
        }
    }
}
