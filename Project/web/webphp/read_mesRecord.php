<?php
header('Access-Control-Allow-Origin: *');

$conn = mysqli_connect("localhost", "root", "1234", "web");
mysqli_query($conn, "set names utf8");

$sql = "SELECT * FROM `mes_record`";
$send = mysqli_query($conn, $sql);

$num = mysqli_num_rows($send);
$json_arr = array();

for ($i = 0; $i < $num; $i++) {
    $rs = mysqli_fetch_assoc($send); // Use mysqli_fetch_assoc instead of mysqli_fetch_row
    $json_arr[$i] = $rs;
}

echo json_encode($json_arr);
?>