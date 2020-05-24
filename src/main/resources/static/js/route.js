function goBaseHome() {
    window.location.href="/";

}


function goGitHome(directory) {
    window.location.href="/" + directory;
}

function getPath(folders) {
    var str= folders;
    var n = str.split("*");
    var r = '';
    for(i=0;i<n.length;i++){
        r = r  + "/" + n[i];
    }
    return r
}

function goBack(directory,folders) {
    if (folders == ''){
        return;
    }
    var str= folders;
    var l = str.split("*").length;
    if(l == 1){
        goGitHome(directory);
    }
    var n=str.split("*",l-1);
    var r = '';
    for(i=0;i<l-1;i++){
        r = r + n[i];
    }
    window.location.href="/" + directory + "?folders=" + r;
}

function goToFolder(directory,folders) {
    window.location.href="/" + directory + "?folders=" + folders;
}

