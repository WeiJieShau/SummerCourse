<?php
header('Access-Control-Allow-Origin: *');
$uploadDir = "./uploads/" . $_GET['loginuser'] . "/";
$files = scandir($uploadDir);
$fileList = array();

foreach ($files as $file) {
    if ($file != "." && $file != "..") {
        $filePath = $uploadDir . $file;
        $fileSize = filesize($filePath);
        $fileTime = filemtime($filePath); // 获取文件的修改时间

        date_default_timezone_set("Asia/Taipei");
        $fileTimeTaipei = date("Y-m-d H:i:s", $fileTime);

        // 将文件名、大小和时间信息放入关联数组中
        $fileInfo = array(
            "name" => $file,
            "size" => $fileSize,
            "time" => $fileTimeTaipei
        );

        $fileList[] = $fileInfo;
    }
}

echo json_encode($fileList);
?>