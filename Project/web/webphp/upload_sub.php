<?php
header('Access-Control-Allow-Origin: *');
$conn = mysqli_connect("localhost", "root", "1234", "web");
mysqli_query($conn, "set names utf8");

$account = $_POST['loginuser'];
$sub_status = $_POST['status'];

// 检查是否存在该账户
$check_sql = "SELECT * FROM `user_data` WHERE `account` = '$account'";
$check_result = mysqli_query($conn, $check_sql);

if (mysqli_num_rows($check_result) > 0) {
    // 更新用户数据
    $update_sql = "UPDATE `user_data` SET `subscribe` = '$sub_status' WHERE `account` = '$account'";

    if (!$update_result = mysqli_query($conn, $update_sql)) {
        echo mysqli_error($conn); // 更新失败
    } else {
        echo "success"; // 更新成功
    }
} else {
    echo "account_not_found"; // 账户不存在
}

mysqli_close($conn);
?>