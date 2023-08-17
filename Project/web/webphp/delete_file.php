<?php
header('Access-Control-Allow-Origin: *');

$filename = $_POST['filename']; // 获取要删除的文件名
$loginuser = $_POST['loginuser']; // 获取登录用户
$filePath = "./uploads/" . $loginuser . "/" . $filename; // 设置文件的完整路径

if (file_exists($filePath)) {
    if (unlink($filePath)) {
        echo "success";
    } else {
        echo "删除文件失败。";
    }
} else {
    echo "文件不存在。";
}
?>