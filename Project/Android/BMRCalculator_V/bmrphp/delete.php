<?php
    $ip = $_SERVER["REMOTE_ADDR"];
    $link = mysqli_connect("localhost", "root", "", "bmr_data") or die("Error with MySQL connection");
    $link->set_charset("UTF8");

    $postData = file_get_contents('php://input');
    $jsonData = json_decode($postData, true);

    $name = $jsonData["name"];

    $result = $link->query("DELETE FROM user_data WHERE name='$name'");

    if ($result) {
        echo "Data deleted successfully.";
    } else {
        echo "Failed to delete data.";
    }

    $link->close();
?>
