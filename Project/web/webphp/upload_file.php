<?php
$account = $_POST['loginuser'];
$uploadDir = "./uploads/" . $account . "/";
header('Access-Control-Allow-Origin: *');
if (!file_exists($uploadDir)) {
    mkdir($uploadDir, 0777, true); // 创建目录，并设置适当的权限
}
if ($_FILES["file"]["error"] == UPLOAD_ERR_OK) {
    $tmpName = $_FILES["file"]["tmp_name"];
    $newFileName = $uploadDir . $_FILES["file"]["name"];

    if (move_uploaded_file($tmpName, $newFileName)) {
        echo "success";
    } else {
        echo "上传失败";
    }
} else {
    echo "上传失败：" . $_FILES["file"]["error"];
}
?>