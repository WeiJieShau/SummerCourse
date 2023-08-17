<?php
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

$conn = mysqli_connect("localhost", "root", "1234", "web");
mysqli_query($conn, "set names utf8");

$account = $_POST['account'];
$password = $_POST['password'];

$sql = "SELECT * FROM `user_data` WHERE `account` = '$account'";
$result = mysqli_query($conn, $sql);

if (mysqli_num_rows($result) > 0) {
    $row = mysqli_fetch_assoc($result);
    if ($row['password'] === $password) {
        echo json_encode("success"); // 密码正确
    } else {
        echo json_encode("incorrect_password"); // 密码不正确
    }
} else {
    echo json_encode("account_not_found"); // 账户不存在
}

mysqli_close($conn);
?>