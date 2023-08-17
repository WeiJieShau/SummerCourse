<?php
header('Access-Control-Allow-Origin: *');
$conn = mysqli_connect("localhost", "root", "1234", "web");
mysqli_query($conn, "set names utf8");

$name = $_POST['name'];
$title = $_POST['title'];
$text = $_POST['text'];

date_default_timezone_set('Asia/Taipei');
$current_time = date('Y-m-d H:i:s');




// 注册用户
$insert_sql = "INSERT INTO `reply_record`(`name`,`title`,`text`,`time`) VALUES ('$name','$title','$text','$current_time')";
if (!$insert_result = mysqli_query($conn, $insert_sql)) {
    echo mysqli_error($conn); // 注册失败
} else {
    echo "success"; // 注册成功
}


mysqli_close($conn);
?>