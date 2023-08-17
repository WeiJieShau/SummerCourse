<?php
    $ip = $_SERVER["REMOTE_ADDR"];
    $link = mysqli_connect("localhost", "root", "", "bmr_data") or die("Error with MySQL connection");
    $link -> set_charset("UTF8");
    
    $postData = file_get_contents('php://input');
    $jsonData = json_decode($postData, true);
    
    $name = $jsonData["name"];
    $gender = $jsonData["gender"];
    $age = $jsonData["age"];
    $height = $jsonData["height"];
    $weight = $jsonData["weight"];
    $bmr = $jsonData["bmr"];
    
    if (!empty($name) && !empty($gender) && !empty($age) && !empty($height) && !empty($weight) && !empty($bmr)) {
        $result = $link->query("INSERT INTO user_data (Name, Gender, Age, Height, Weight, BMR) VALUES ('$name', '$gender', $age, $height, $weight, $bmr)");
        if ($result) {
            echo "Data inserted successfully.";
        } else {
            echo "Error inserting data: " . mysqli_error($link);
        }
    } else {
        echo "Some fields are empty, skipping INSERT.";
    }
    
    $link -> close();
?>
