<?php
header('Access-Control-Allow-Origin: *');
$conn = mysqli_connect("localhost", "root", "1234", "web");
mysqli_query($conn, "set names utf8");

date_default_timezone_set('Asia/Taipei');
$current_time = date('Y-m-d H:i:s');

$name = $_POST['name'];
$title = $_POST['title'];
$content = $_POST['content'];
$time = $current_time;


$insert_sql = "INSERT INTO `mes_record`(`name`,`title`,`content`,`time`) VALUES ('$name','$title','$content','$time')";
if (!$insert_result = mysqli_query($conn, $insert_sql)) {
    echo mysqli_error($conn);
} else {
    echo "success";
}

mysqli_close($conn);
?>