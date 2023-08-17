<?php

$filename = $_GET['filename']; // 获取要下载的文件名
$loginuser = $_GET['loginuser'];
$filePath = "./uploads/" . $loginuser . "/" . $filename; // 设置文件的完整路径

if (file_exists($filePath)) {
    header('Content-Description: File Transfer');
    header('Content-Type: application/octet-stream');
    header('Content-Disposition: attachment; filename="' . basename($filePath) . '"');
    header('Expires: 0');
    header('Cache-Control: must-revalidate');
    header('Pragma: public');
    header('Content-Length: ' . filesize($filePath));
    readfile($filePath);
    exit;
} else {
    echo "文件不存在。";
}
?>