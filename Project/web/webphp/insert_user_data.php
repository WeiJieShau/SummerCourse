<?php
header('Access-Control-Allow-Origin: *');
$conn = mysqli_connect("localhost", "root", "1234", "web");
mysqli_query($conn, "set names utf8");

$account = $_POST['account'];
$password = $_POST['password'];
$email = $_POST['email'];
$gender = $_POST['gender'];
$color = $_POST['color'];

// 检查是否已注册
$check_sql = "SELECT * FROM `user_data` WHERE `account` = '$account'";
$check_result = mysqli_query($conn, $check_sql);

if (mysqli_num_rows($check_result) > 0) {
    echo "already_registered"; // 账户已注册
} else {
    // 注册用户
    $insert_sql = "INSERT INTO `user_data`(`account`,`password`,`email`,`gender`,`color`) VALUES ('$account','$password','$email','$gender','$color')";
    if (!$insert_result = mysqli_query($conn, $insert_sql)) {
        echo mysqli_error($conn); // 注册失败
    } else {
        echo "success"; // 注册成功
    }
}

mysqli_close($conn);
?>