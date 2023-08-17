<?php
// 獲取來自 JavaScript 的 ID 和使用者名稱
header('Access-Control-Allow-Origin: *');
$id = $_POST['log'];
$user = $_POST['user'];

// 創建與資料庫的連線
$conn = mysqli_connect("localhost", "root", "1234", "web");

// 檢查連線是否成功
if ($conn->connect_error) {
    die("連線失敗: " . $conn->connect_error);
}

date_default_timezone_set('Asia/Taipei');
$current_time = date('Y-m-d H:i:s');

// 根據 ID 值設定不同的 log 訊息
if ($id === "mem_list_page") {
    $log = "進入會員列表";
} elseif ($id === "mod_profile_page") {
    $log = "進入修改頁面";
} elseif ($id === "file_page") {
    $log = "進入檔案總管";
} elseif ($id === "mes_board_page") {
    $log = "進入留言板";
} elseif ($id === "sub_page") {
    $log = "進入訂閱頁面";
} elseif ($id === "mem_manag_page") {
    $log = "進入會員管理頁面";
} elseif ($id === "modify") {
    $log = "修改會員資料";
} elseif ($id === "uploadButton") {
    $log = "上傳檔案";

} elseif ($id === "file_download") {
    $log = "下載檔案";
} elseif ($id === "file_delete") {
    $log = "刪除檔案";
} elseif ($id === "file_rename") {
    $log = "重新命名檔案";
} elseif ($id === "reply_mes") {
    $log = "回覆留言";
} elseif ($id === "mod_mes") {
    $log = "修改留言";
} elseif ($id === "delete_mes") {
    $log = "刪除留言";
} elseif ($id === "subscribe") {
    $log = "訂閱";
} elseif ($id === "cancel_subscribe") {
    $log = "取消訂閱";
} elseif ($id === "logout") {
    $log = "登出";
} else {
    $log = "未知的頁面";
}

// 插入資料到資料庫
$sql = "INSERT INTO `mem_log` (`name`, `time`,`log`) VALUES ('$user', '$current_time','$log')";

if ($conn->query($sql) === TRUE) {
    echo "資料插入成功";
} else {
    echo "資料插入失敗: " . $conn->error;
}

// 關閉資料庫連線
$conn->close();
?>