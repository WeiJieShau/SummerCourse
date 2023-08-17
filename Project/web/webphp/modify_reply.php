<?php
header('Access-Control-Allow-Origin: *');
$conn = mysqli_connect("localhost", "root", "1234", "web");
mysqli_query($conn, "set names utf8");

$title = $_POST['title'];
$name = $_POST['name'];
$newtext = $_POST['text'];
$time = $_POST['time'];

date_default_timezone_set("Asia/Taipei"); // 設定時區為台北
$current_time = date("Y-m-d H:i:s");

// 检查是否存在符合條件的回覆
$check_sql = "SELECT * FROM `reply_record` WHERE `name` = '$name' AND `time` = '$time'";
$check_result = mysqli_query($conn, $check_sql);

if (mysqli_num_rows($check_result) > 0) {
    // 更新回覆資料，包括時間
    $update_sql = "UPDATE `reply_record` SET `text`='$newtext', `time`='$current_time' WHERE `name`='$name' AND `time`='$time'";

    if (mysqli_query($conn, $update_sql)) {
        echo "success"; // 回覆更新成功
    } else {
        echo "error"; // 更新失敗
    }
} else {
    echo "not_found"; // 找不到符合的回覆記錄
}

mysqli_close($conn);
?>