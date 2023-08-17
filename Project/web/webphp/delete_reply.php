<?php
header('Access-Control-Allow-Origin: *');
$conn = mysqli_connect("localhost", "root", "1234", "web");
mysqli_query($conn, "set names utf8");

$title = $_POST['title'];
$name = $_POST['name'];
$time = $_POST['time'];

// 檢查是否存在符合條件的回覆
$check_sql = "SELECT * FROM `reply_record` WHERE `name` = '$name' AND `time` = '$time'";
$check_result = mysqli_query($conn, $check_sql);

if (mysqli_num_rows($check_result) > 0) {
    // 刪除回覆資料
    $delete_sql = "DELETE FROM `reply_record` WHERE `name`='$name' AND `time`='$time'";

    if (mysqli_query($conn, $delete_sql)) {
        echo "success"; // 回覆刪除成功
    } else {
        echo "error"; // 刪除失敗
    }
} else {
    echo "not_found"; // 找不到符合的回覆記錄
}

mysqli_close($conn);
?>