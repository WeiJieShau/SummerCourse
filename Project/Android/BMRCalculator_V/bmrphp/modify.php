<?php
    $ip = $_SERVER["REMOTE_ADDR"];
    $link = mysqli_connect("localhost", "root", "", "bmr_data") or die("Error with MySQL connection");
    $link->set_charset("UTF8");

    $postData = file_get_contents('php://input');
    $jsonData = json_decode($postData, true);

    $name = $jsonData["name"];
    $gender = $jsonData["gender"];
    $age = $jsonData["age"];
    $height = $jsonData["height"];
    $weight = $jsonData["weight"];
    $bmr = $jsonData["bmr"];

    $result = $link->query("UPDATE user_data SET gender='$gender', age='$age', height='$height', weight='$weight', bmr='$bmr' WHERE name='$name'");

    if ($result) {
        echo "Data modified successfully.";
    } else {
        echo "Failed to modify data.";
    }

    $link->close();
?>
