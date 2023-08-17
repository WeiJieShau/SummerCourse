<?php
header('Access-Control-Allow-Origin: *');

$loginuser = $_POST['loginuser']; // 获取登录用户
$filename = $_POST['oldFilename']; // 获取要重命名的文件名
$newFilename = $_POST['newFilename']; // 获取新的文件名

$ext = pathinfo($filename, PATHINFO_EXTENSION); // 获取原始文件的扩展名
$newFilenameWithExt = $newFilename . '.' . $ext; // 添加原始扩展名到新文件名中

$oldFilePath = "./uploads/" . $loginuser . "/" . $filename;
$newFilePath = "./uploads/" . $loginuser . "/" . $newFilenameWithExt;

if (file_exists($oldFilePath)) {
    if (rename($oldFilePath, $newFilePath)) {
        echo "success";
    } else {
        echo "重命名文件失败。";
    }
} else {
    echo "文件不存在。";
}
?>