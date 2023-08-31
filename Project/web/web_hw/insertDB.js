//var loginuser = $("#loginuser").text();
function files_refresh() {
    $.ajax({
        type: "GET",
        url: "http://192.168.0.115/webphp/read_files.php",
        data: {
            loginuser: localStorage.getItem("loginuser")
        },
        dataType: "json",
        success: function (fileList) {
            var table = "";
            for (var i = 0; i < fileList.length; i++) {
                table += "<tr>";
                table += "<td style='text-align: center;'>" + fileList[i].name + "</td>";
                table += "<td style='text-align: center;'>" + formatSizeUnits(fileList[i].size) + "</td>";
                table += "<td style='text-align: center;'>" + fileList[i].time + "</td>";


                table += "<td style='text-align: center;'>";
                // 原本的按鈕功能
                table += "<button onclick='downloadFile(\"" + fileList[i].name + "\", \"download\"); log_insert(\"file_download\")'>下載</button>";
                table += "<button onclick='deleteFile(\"" + fileList[i].name + "\"); log_insert(\"file_delete\")'>刪除</button>";
                table += "<button onclick='renameFile(\"" + fileList[i].name + "\"); log_insert(\"file_rename\")'>改檔名</button>";

                table += "</td>";


                table += "</tr>";
            }
            $("#userfiles_table").html(table);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            console.log(xhr.status);
            console.log(thrownError);
        }
    });

}
function formatSizeUnits(bytes) {
    if (bytes >= 1073741824) {
        return (bytes / 1073741824).toFixed(2) + ' GB';
    } else if (bytes >= 1048576) {
        return (bytes / 1048576).toFixed(2) + ' MB';
    } else if (bytes >= 1024) {
        return (bytes / 1024).toFixed(2) + ' KB';
    } else if (bytes > 1) {
        return bytes + ' bytes';
    } else if (bytes == 1) {
        return bytes + ' byte';
    } else {
        return '0 bytes';
    }

}
// 下載檔案
function downloadFile(filename) {

    var link = document.createElement("a");
    link.href = "http://192.168.0.115/webphp/download.php?loginuser=" + localStorage.getItem("loginuser") + "&filename=" + filename;
    link.target = "_blank";
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    alert("檔案下載成功");
}

// 刪除檔案
function deleteFile(filename) {
    alert(localStorage.getItem("loginuser"));
    $.ajax({
        url: "http://192.168.0.115/webphp/delete_file.php",
        type: "POST",

        data: { filename: filename, loginuser: localStorage.getItem("loginuser") },
        success: function (response) {
            alert("檔案已刪除");

            files_refresh();
        },
        error: function (xhr, ajaxOptions, thrownError) {
            alert("刪除檔案失敗：" + thrownError);
        }
    });
}

// 改檔名
function renameFile(filename) {
    var newFilename = prompt("請輸入新的檔名：", filename);
    if (newFilename !== null && newFilename.trim() !== "") {
        $.ajax({
            url: "http://192.168.0.115/webphp/rename_file.php",
            type: "POST",
            data: { oldFilename: filename, newFilename: newFilename, loginuser: localStorage.getItem("loginuser") },
            success: function (response) {
                alert("檔名已修改");

                files_refresh();
            },
            error: function (xhr, ajaxOptions, thrownError) {
                alert("修改檔名失敗：" + thrownError);
            }
        });
    }
}
function log_insert(extractedId) {

    $.ajax({
        type: "POST",
        url: "http://192.168.0.115/webphp/insert_log.php",
        data: {
            log: extractedId,
            user: localStorage.getItem("loginuser")
        },
        success: function (response) {
            // 在成功回調中處理返回的結果
            console.log("PHP 請求成功：" + response);
        },
        error: function (xhr, status, error) {
            // 在錯誤回調中處理錯誤
            console.log("PHP 請求失敗：" + error);
        }
    });
}

function updateSubscriptionStatus() {

    var status = localStorage.getItem("sub_status");
    if (status === "1") {
        $("#sub_btn").removeClass("btn-outline-info").addClass("btn-info");
        $("#sub_btn").text("取消訂閱");

        $(".image-field").removeClass("d-none");
    } else {
        $("#sub_btn").removeClass("btn-info").addClass("btn-outline-info");
        $("#sub_btn").text("訂閱");
        $(".image-field").addClass("d-none");
    }
}
$(document).ready(function (e) {

    refresh();
    userlist_refresh();
    userdata_refresh();

    loadMemberLogs(localStorage.getItem("loginuser"));
    Mes_refresh();


    $(".account-field, .password-field, .login_btn-field, .signUp-field").removeClass("d-none");
    $(".email-field, .gender-field, .colorpicker-field, .login-field, .signUp_send-field").addClass("d-none");
    //進入註冊
    $("#signUp").click(function () {

        $(".account-field, .password-field, .email-field, .gender-field, .colorpicker-field,.signUp_send-field,.login-field").removeClass("d-none");
        $(".login_btn-field,.signUp-field").addClass("d-none");
    });
    //返回登入
    $("#login_mode").click(function () {
        $(".account-field, .password-field, .login_btn-field, .signUp-field").removeClass("d-none");
        $(".email-field, .gender-field, .colorpicker-field, .login-field, .signUp_send-field").addClass("d-none");
    });
    $("#email").on("input", function () {
        var emailInput = $(this);

        if (!emailInput[0].checkValidity()) {
            emailInput[0].setCustomValidity("Please enter a valid email address.");
        } else {
            emailInput[0].setCustomValidity("");
        }
    });
    //註冊送出
    $("#signUp_send").click(function () {

        var account = $("#account").val();
        var password = $("#password").val();
        var email = $("#email").val();
        var gender = $("input[name='gender']:checked").val();

        var color = $("#colorpicker").val();

        if (account !== "") {

            $.ajax({
                type: "POST",
                url: "http://192.168.0.115/webphp/insert_user_data.php",
                data: {
                    account: account,
                    password: password,
                    email: email,
                    gender: gender,
                    color: color
                },
                success: function (response) {
                    if (response === "already_registered") {
                        $(".login-field,.signUp-field").removeClass("d-none");
                        $(".email-field, .gender-field, .colorpicker-field,.signUp_send-field").addClass("d-none");
                        alert("已註冊 請使用登入");
                    } else if (response === "success") {
                        $(".signUp-field,.login_btn-field").removeClass("d-none");
                        $(".email-field, .gender-field, .colorpicker-field,.signUp_send-field,.login-field").addClass("d-none");
                        alert("註冊成功");
                        /*
                        localStorage.setItem("loginuser", loginuser);


                        window.location.href = "UserList.html";*/
                        refresh();
                    }
                },
                error: function (xhr, ajaxOptions, thrownError) {

                    console.log(xhr.status);
                    console.log(thrownError);
                }
            });
        }
    });


    $("#login").click(function () {
        var account = $("#account").val();
        var password = $("#password").val();


        $.ajax({
            type: "POST",
            url: "http://192.168.0.115/webphp/check_login.php",
            data: {
                account: account,
                password: password
            },

            success: function (response) {
                if (response === "success") {
                    loginuser = account;


                    localStorage.setItem("loginuser", loginuser);

                    window.location.href = "UserList.html";
                } else if (response === "incorrect_password") {
                    alert("Incorrect password. Please try again.");
                } else if (response === "account_not_found") {
                    alert("Account not found. Please register first.");
                }
            },
            error: function (xhr, ajaxOptions, thrownError) {
                alert("Error occurred: " + thrownError);


                console.log(xhr.status);
                console.log(thrownError);
            }
        });
    });
    $("#modify").click(function () {
        var account = $("#ed_account").val();
        var password = $("#ed_password").val();
        var email = $("#ed_email").val();
        var gender = $("input[name='ed_gender']:checked").val();
        var color = $("#ed_colorpicker").val();
        log_insert(this.id);

        if (account !== "") {

            $.ajax({
                type: "POST",
                url: "http://192.168.0.115/webphp/upload_user_data.php",
                data: {
                    account: account,
                    password: password,
                    email: email,
                    gender: gender,
                    color: color
                },
                success: function (response) {

                    alert("修改成功");

                    window.location.href = "UserList.html";
                    refresh();

                },
                error: function (xhr, ajaxOptions, thrownError) {

                    console.log(xhr.status);
                    console.log(thrownError);
                }
            });
        }
    });
    $("#status_btn").click(function () {
        var account = $("#account").val();
        var password = $("#password").val();
        var email = $("#email").val();
        var gender = $("#gender").val();
        var color = $("#colorpicker").val();
        loginuser = null;
        window.location.href = "Login.html";
        log_insert("logout");

    });
    $("#uploadButton").click(function () {
        var formData = new FormData();
        var fileInput = $("#formFile")[0];

        formData.append("file", fileInput.files[0]);
        formData.append("loginuser", localStorage.getItem("loginuser"));
        log_insert(this.id,);

        $.ajax({
            url: "http://192.168.0.115/webphp/upload_file.php",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                alert("文件上傳成功！");
                files_refresh();

            },
            error: function (xhr, ajaxOptions, thrownError) {
                alert("上傳失敗：" + thrownError);
            }
        });
    });

    $("#release_btn").click(function () {
        var title = $("#topic_title").val();
        var content = $("#topic_content").val();
        $.ajax({
            url: "http://192.168.0.115/webphp/releaseMes.php",
            type: "POST",
            data: {
                name: localStorage.getItem("loginuser"),
                title: title,
                content: content,


            },
            success: function (response) {
                alert("訊息發布成功！");
                Mes_refresh();

            },
            error: function (xhr, ajaxOptions, thrownError) {
                alert("發布失敗：" + thrownError);
            }
        });
        Mes_refresh();


    });
    // Event delegation for reply buttons
    $("#Mes_card").on("click", ".reply_btn", function () {
        var title = $(this).attr("id").replace("_reply_btn", ""); // Get the title from the button's ID
        console.log("title:" + title);
        var idstring = "#" + title + "_input";
        console.log("idstring" + idstring)
        var replyContent = $(idstring).val(); // Get the reply content from the input field

        log_insert("reply_mes");
        alert(replyContent);
        // Prepare data to send to the PHP script
        var postData = {
            title: title,
            name: localStorage.getItem("loginuser"),
            text: replyContent
        };

        // Send AJAX request to PHP script
        $.ajax({
            type: "POST",
            url: "http://192.168.0.115/webphp/insert_reply.php", // Replace with your PHP script's URL
            data: postData,
            dataType: "text",
            success: function (response) {
                //alert("訊息回覆成功！");
            },
            error: function (xhr, status, error) {
                //alert("回覆失敗：");
            }
        });

        Mes_refresh();

    });
    $("#Mes_card").on("click", ".mod_btn", function () {
        var cardBody = $(this).closest(".card-body");
        log_insert("mod_mes");
        var title = $(this).attr("id").replace("_mod_btn", "");
        var modifiedContent = cardBody.find("input[name='reply_content']").val();
        var replyTime = cardBody.find(".card-text").data("time");
        //alert(replyTime + " " + title + " " + modifiedContent);
        var postData = {
            title: title,
            name: localStorage.getItem("loginuser"),
            text: modifiedContent,
            time: replyTime
        };

        $.ajax({
            type: "POST",
            url: "http://192.168.0.115/webphp/modify_reply.php",
            data: postData,
            dataType: "text",
            success: function (response) {
                alert("訊息修改成功！");
            },
            error: function (xhr, status, error) {
                //alert("修改失敗：");
                // Handle error
            }
        });
        Mes_refresh();
    });

    $("#Mes_card").on("click", ".delete_btn", function () {
        var cardBody = $(this).closest(".card-body");


        var replyTime = cardBody.find(".card-text").data("time");
        var title = $(this).attr("id").replace("_delete_btn", "");
        if (confirm("確定要刪除這則留言嗎？")) {
            // 確定刪除，發送 AJAX 請求
            var postData = {
                title: title,
                name: localStorage.getItem("loginuser"),
                time: replyTime
            };
            log_insert("delete_mes");
        }
        $.ajax({
            type: "POST",
            url: "http://192.168.0.115/webphp/delete_reply.php",
            data: postData,
            dataType: "text",
            success: function (response) {
                // Handle success
                // You might want to refresh the replies after successful deletion
                //alert("刪除成功！");
            },
            error: function (xhr, status, error) {
                // Handle error
                //alert("刪除失敗：");
            }
        });
        Mes_refresh();
    });
    $("#Mes_card").on("click", ".main_mod_btn", function () {
        var cardBody = $(this).closest(".card-body");
        // 在這裡實現修改留言的邏輯
        var title = $(this).attr("id").replace("main_mod_btn", "");
        var modifiedContent = cardBody.find("input[name='reply_content']").val();
        log_insert("mod_mes");
        var postData = {
            title: title,
            name: localStorage.getItem("loginuser"),
            text: modifiedContent
        };
        $.ajax({
            type: "POST",
            url: "http://192.168.0.115/webphp/main_mod_reply.php",
            data: postData,
            dataType: "text",
            success: function (response) {
                alert("修改成功！");
                Mes_refresh();
            },
            error: function (xhr, status, error) {
                alert("修改失敗：" + error);
            }
        });
        // ...
    });

    // 刪除留言按鈕的點擊事件處理
    $("#Mes_card").on("click", ".main_delete_btn", function () {

        var title = $(this).attr("id").replace("main_delete_btn", "");
        //var replyTime = $(this).data("time");

        if (confirm("確定要刪除這則留言嗎？")) {
            // 確定刪除，發送 AJAX 請求
            log_insert("delete_mes");
            var postData = {
                title: title,
                name: localStorage.getItem("loginuser"),
                //time: replyTime
            };
            alert(postData.title + postData.name);
            $.ajax({
                type: "POST",
                url: "http://192.168.0.115/webphp/main_delete_reply.php",
                data: postData,
                dataType: "text",
                success: function (response) {
                    alert("刪除成功！");
                    Mes_refresh();
                },
                error: function (xhr, status, error) {
                    alert("刪除失敗：" + error);
                }
            });
        }
    });

    $('#searchButton').on('click', function () {
        var searchName = $('#searchName').val();

        loadMemberLogs(searchName);
    });

    $("a[id*='link']").click(function (event) {

        // 取得連結的 ID
        var linkId = $(this).attr("id");

        // 從連結的 ID 中去除 "_link" 字串
        var extractedId = linkId.replace("_link", "");


        // 取得存儲在 localStorage 中的 loginuser
        var loginuser = localStorage.getItem("loginuser");


        // 使用 AJAX 發送請求，將 extractedId 和 loginuser 傳遞給 PHP
        log_insert(extractedId);


    });
    var imagePaths = [
        "./images/NCU.jpg",

        "./images/HTML_Blog.jpeg",
        "./images/Coding.jpg"
        // ...添加更多圖片路徑
    ];

    var currentImageIndex = 0;


    $("#sub_btn").click(function () {
        var sub_btn_status = localStorage.getItem("sub_status");
        if (sub_btn_status === "0") {
            // 訂閱狀態
            $("#sub_btn").removeClass("btn-outline-info").addClass("btn-info");
            $("#sub_btn").text("取消訂閱");
            localStorage.setItem("sub_status", "1");
            updateSubscriptionStatus();
            updateSubscriptionStatusToDB();
            $(".image-field").removeClass("d-none");


            // 呼叫 log_insert(subscribe)
            log_insert("subscribe");
        } else {
            // 取消訂閱狀態
            $("#sub_btn").removeClass("btn-info").addClass("btn-outline-info");
            $("#sub_btn").text("訂閱");
            localStorage.setItem("sub_status", "0");
            updateSubscriptionStatus();
            updateSubscriptionStatusToDB();
            $(".image-field").addClass("d-none");

            // 呼叫 log_insert(cancel_subscribe)
            log_insert("cancel_subscribe");
        }
    });


    function updateSubscriptionStatusToDB() {
        var status = localStorage.getItem("sub_status");

        $.ajax({
            type: "POST",
            url: "http://192.168.0.115/webphp/upload_sub.php",
            data: {
                status: status,
                loginuser: localStorage.getItem("loginuser") // 根據您的需求傳遞用戶信息
            },
            success: function (response) {
                console.log("訂閱狀態已更新到後端");
            },
            error: function (xhr, ajaxOptions, thrownError) {
                console.log("更新訂閱狀態失敗：" + thrownError);
            }
        });
    }
    function Mes_refresh() {

        $.ajax({
            type: "GET",
            url: "http://192.168.0.115/webphp/read_mesRecord.php",
            dataType: "json",
            success: function (fileList) {
                var table = "";
                for (var i = 0; i < fileList.length; i++) {
                    table += "<div class='card'>";
                    table += "<h5 class='card-header'>" + fileList[i].title + "</h5>";
                    table += "<div id='" + i + "reply_card'>";
                    table += "<div class='card-body'>";
                    table += "<h5 class='card-header'>" + fileList[i].content + "</h5>";
                    table += "<p class='card-text'>" + fileList[i].name + " ," + fileList[i].time + "</p>";
                    if (fileList[i].name === localStorage.getItem("loginuser")) {
                        table += "<p>";
                        table += "<button class='btn btn-secondary' type='button' data-bs-toggle='collapse'"
                            + "data-bs-target='#" + fileList[i].title + "_collapseExample" + i + "' aria-expanded='false' "
                            + "aria-controls='" + fileList[i].title + "_collapseExample" + i + "'>修改留言</button>";
                        table += "<button id='" + fileList[i].title + "main_delete_btn' class='btn btn-danger main_delete_btn' data-time='" + fileList[i].time + "'>刪除留言</button>";
                        table += "</p>";
                    }
                    table += "<div class='collapse' id='" + fileList[i].title + "_collapseExample" + i + "'>" +
                        "<input type='text' class='form-control' id='" + "fileList[i].title" + "_topic_content" + "' name='reply_content' placeholder='請輸入內容' value='" + fileList[i].content + "'>" +
                        "<button id='" + fileList[i].title + "main_mod_btn'" + " class='btn btn-outline-secondary main_mod_btn'>修改</button>";
                    table += "</div>";
                    table += "</div>";

                    table += "<div id='" + fileList[i].title + "_reply_card'>";
                    table += "</div>";

                    table += "</div>";

                    table += "<div class='card card-body'>";
                    table += "<div class='mt-3'>";
                    table += "<input id='" + fileList[i].title + "_input" + "' type='text' class='form-control' name='reply_content' placeholder='回覆內容'>";
                    table += "</div>";

                    table += "<div class='mt-3' id='reply_div'>";
                    table += "<button id='" + fileList[i].title + "_reply_btn" + "' class='btn btn-primary reply_btn'>回覆</button>";
                    table += "</div>";
                    table += "</div>";

                    table += "</div>";







                }
                $("#Mes_card").html(table);
                $.ajax({
                    type: "GET",
                    url: "http://192.168.0.115/webphp/read_replyRecord.php",
                    dataType: "json",
                    success: function (fileList) {

                        for (var i = 0; i < fileList.length; i++) {
                            var card = "";
                            card += "<div class='card card-body'>";
                            card += "<h5 class='card-header' id='reply_card'>" + fileList[i].text + "</h5>";
                            card += "<p id='" + fileList[i].name + i + "' class='card-text' data-time='" + fileList[i].time + "'>" + fileList[i].name + " ," + fileList[i].time + "</p>";
                            if (fileList[i].name === localStorage.getItem("loginuser")) {
                                card += "<p>";
                                card += "<button class='btn btn-secondary' type='button' data-bs-toggle='collapse'"
                                    + "data-bs-target='#" + fileList[i].title + "_collapseExample" + i + "' aria-expanded='false' "
                                    + "aria-controls='" + fileList[i].title + "_collapseExample" + i + "'>修改留言</button>";
                                card += "<button id='" + i + "delete_btn' class='btn btn-danger delete_btn' 'data-time=" + fileList[i].time + "'>刪除留言</button>";
                                card += "</p>";
                            }
                            card += "<div class='collapse' id='" + fileList[i].title + "_collapseExample" + i + "'>" +
                                "<input type='text' class='form-control' id='" + "fileList[i].title" + "_topic_content" + i + "' name='reply_content' placeholder='請輸入內容' value='" + fileList[i].text + "'>" +
                                "<button id='" + fileList[i].title + "_mod_btn" + i + "' class='btn btn-outline-secondary mod_btn'>修改</button>";
                            card += "</div>";
                            card += "</div>";




                            $("#" + fileList[i].title + "_reply_card").append(card);

                        }

                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        console.log(xhr.status);
                        console.log(thrownError);
                    }
                });



            },
            error: function (xhr, ajaxOptions, thrownError) {
                console.log(xhr.status);
                console.log(thrownError);
            }
        });



    }



    function userdata_refresh() {


        loginuser = localStorage.getItem("loginuser")


        $.ajax({
            type: "POST",
            url: "http://192.168.0.115/webphp/get_user_profile.php",
            data: { account: loginuser },
            success: function (userData) {
                output = $.parseJSON(userData);
                console.log(output);


                for (var num = 0; num < output.length; num++) {
                    var account = output[num][0];
                    $("#ed_account").val(account);
                    var password = output[num][1];
                    $("#ed_password").val(password);
                    var email = output[num][2];
                    $("#ed_email").val(email);
                    if (output[num][3] === "男生") {
                        $("#ed_gender_male").prop("checked", true);
                    } else {
                        $("#ed_gender_female").prop("checked", true);
                    }
                    $("#ed_colorpicker").val(output[num][4]);
                    var isSubscribed = output[num][5];
                    localStorage.setItem("sub_status", isSubscribed);

                    updateSubscriptionStatus();

                }



            },
            error: function (xhr, ajaxOptions, thrownError) {
                console.log(xhr.status);
                console.log(thrownError);
            }
        });
    }

    function loadMemberLogs(searchName) {

        $.ajax({
            type: 'GET',
            url: 'http://192.168.0.115/webphp/get_member_logs.php',
            data: { searchName: searchName },
            dataType: 'json',
            success: function (logs) {
                var table = "";

                for (var i = 0; i < logs.length; i++) {
                    console.log(logs[i].member_name);
                    table += "<tr style='text-align: center;'><td>" + logs[i].member_name + "</td>";
                    table += "<td style='text-align: center;'>" + logs[i].execution_time + "</td>";
                    table += "<td style='text-align: center;'>" + logs[i].record + "</td>";

                }
                $("#userlog_table").html(table);
            },
            error: function (xhr, status, error) {
                console.log('Error:', error);
            }
        });

    }
    function userlist_refresh() {

        $.ajax({
            type: "POST",
            url: "http://192.168.0.115/webphp/showUserList.php",
            error: function (xhr, ajaxOptions, thrownError) {
                console.log(xhr.status);
                console.log(thrownError);
            },
            success: function (output) {
                output = $.parseJSON(output);
                console.log(output);

                var table = "";
                for (var num = 0; num < output.length; num++) {
                    table += "<tr style='text-align: center;'><td>" + output[num][0] + "</td>";
                    table += "<td style='text-align: center;'>" + output[num][2] + "</td>";
                    table += "<td style='text-align: center;'>" + output[num][3] + "</td>";
                    table += "<td style='background-color:" + output[num][4] + ";'></td></tr>";
                }
                loginuser = localStorage.getItem("loginuser");
                if (loginuser) {
                    $("#loginuser").text(loginuser + " , 您好!");
                }


                $("#userlist_table").html(table);
            }
        });

    }
    function refresh() {
        var loginuser = localStorage.getItem("loginuser");
        var navbarContent = `
    <div class="container-fluid">
        <ul class="navbar-nav">
            <li class="nav-item"><a id="mem_list_page_link" class="nav-link" href="UserList.html">會員列表</a></li>
            <li class="nav-item"><a id="mod_profile_page_link" class="nav-link" href="ModifyPage.html">修改個人資料</a></li>
            <li class="nav-item"><a id="file_page_link" class="nav-link" href="FilesPage.html">檔案總管</a></li>
            <li class="nav-item"><a id="mes_board_page_link" class="nav-link" href="MesBoard.html">留言板</a></li>
            <li class="nav-item"><a id="sub_page_link" class="nav-link" href="Subscribe.html">訂閱</a></li>
            ${localStorage.getItem("loginuser") === "admin"
                ? '<li class="nav-item"><a id="mem_manag_page_link" class="nav-link" href="MemberManagement.html">會員管理</a></li>'
                : ''
            }
        </ul>
        <form class="d-flex">
        <span id="loginuser" class="navbar-text"></span>
        <button id="status_btn" type="button" class="btn">登出</button>
        </form>
    </div>
    
    
`;



        $("#dynamic-navbar").html(navbarContent);

    }
    files_refresh();
});