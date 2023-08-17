<?php

$ip = $_SERVER["REMOTE_ADDR"];
$link = mysqli_connect("localhost", "root", "", "bmr_data") or die("Error with MySQL connection");
$link->set_charset("UTF8");

$result = $link->query("SELECT * FROM `user_data`");

$output = array(); // Create an empty array to store all records

while ($row = $result->fetch_assoc()) {
    $output[] = $row; // Add each record to the output array
}

$link->close();
echo json_encode($output); // Encode the output array as JSON and echo it
?>
