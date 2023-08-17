<?php
header('Access-Control-Allow-Origin: *');
$conn = mysqli_connect("localhost", "root", "1234", "web");
mysqli_query($conn, "set names utf8");

$searchName = isset($_GET['searchName']) ? $_GET['searchName'] : '';
$query = "SELECT * FROM `mem_log`";
if (!empty($searchName)) {
    $query .= " WHERE name LIKE '%$searchName%'";
}
$query .= " ORDER BY time DESC";

$result = mysqli_query($conn, $query);

$logs = array();
while ($row = mysqli_fetch_assoc($result)) {
    $logs[] = array(
        'member_name' => $row['name'],
        // 將 'name' 改成 'member_name'
        'execution_time' => $row['time'],
        // 將 'time' 改成 'execution_time'
        'record' => $row['log']
    );

}

echo json_encode($logs);
?>