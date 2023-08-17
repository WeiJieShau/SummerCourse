package com.example.bmrcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Result_page extends AppCompatActivity {
    Button cancel_btn,save_button;
    TextView nameResultTextView,bmiResultTextView,bmrResultTextView;
    String genderValue ;
    String user_name ;
    String user_age ;
    String user_height ;
    String user_weight ;
    String DataMode;
    int bmrValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);
        //要定義 cancel_btn 不然頁面無法正常跳轉
        cancel_btn=findViewById(R.id.cancel);
        save_button=findViewById(R.id.save);

        nameResultTextView = findViewById(R.id.name_result);
        bmiResultTextView = findViewById(R.id.bmi_result);
        bmrResultTextView = findViewById(R.id.bmr_result);
        //取得CreateRecord傳來資料
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            genderValue = bundle.getString("genderValue");
            user_name = bundle.getString("user_name");
            user_age = bundle.getString("user_age");
            user_height = bundle.getString("user_height");
            user_weight = bundle.getString("user_weight");
            DataMode = bundle.getString("Mode");

            int bmiValue = calculateBMI(user_height, user_weight);
            bmrValue = calculateBMR(genderValue,user_age,user_height,user_weight);

            // 將結果顯示到對應的 TextView 中
            nameResultTextView.setText(user_name);
            bmrResultTextView.setText(String.valueOf(bmrValue));

            bmiResultTextView.setText(String.valueOf(bmiValue));
        }



        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Result_page.RESULT_CANCELED);
                finish();
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> dataMap = new HashMap<>();
                dataMap.put("name", user_name);
                dataMap.put("gender", genderValue);
                dataMap.put("age", user_age);
                dataMap.put("height", user_height);
                dataMap.put("weight", user_weight);
                dataMap.put("bmr", String.valueOf(bmrValue));

                insertRecord(dataMap,DataMode);

                Intent it = new Intent();
                it.setClass(Result_page.this,MainActivity.class);
                startActivity(it);
            }

            private void insertRecord(HashMap<String, String> map,String mode) {
                // 將傳入的 map 參數轉換為 JSON 字符串
                String jsonString = new JSONObject(map).toString();
                Log.d("Mode: ", mode);
                if(mode.equals("Create")){
                    // 在後臺線程中執行 HTTP POST 請求
                    Thread internetThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String path = "./insert.php";
                            Log.d("jsonString", jsonString);
                            executeHttpPost(path, jsonString);
                            Log.d("Internet thread", "END");
                        }
                    });

                    internetThread.start();

                    try {
                        // 等待新執行緒完成
                        internetThread.join();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else if(mode.equals("Modify")){//modify.php

                    Thread internetThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String path = "./modify.php";
                            Log.d("jsonString", jsonString);
                            executeHttpPost(path, jsonString);
                            Log.d("Internet thread", "END");
                        }
                    });

                    internetThread.start();

                    try {
                        // 等待新執行緒完成
                        internetThread.join();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Log.d("insertRecord()", "End");
            }

            private void executeHttpPost(String path, String json) {
                try {
                    URL urlObj = new URL(path);
                    Log.d("json", json);
                    HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");//注意型別

                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.connect();

                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.writeBytes(json);
                    wr.flush();
                    wr.close();

                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("Save Record", "result: " + result.toString());
                    conn.disconnect();

                } catch (IOException e) {
                    Log.v("Save Record", "Record saved failed");
                    e.printStackTrace();
                }
            }
        });

        }
    private int calculateBMI(String height, String weight) {
        // 假設 height 和 weight 分別是身高和體重的字符串表示，需要將它們轉換為浮點數或整數進行計算
        float heightValue = Float.parseFloat(height);
        float weightValue = Float.parseFloat(weight);

        Log.d("Result_Page", String.valueOf(heightValue));
        Log.d("Result_Page", String.valueOf(weightValue));
        Log.d("Result_Page", String.valueOf(weightValue / (heightValue * heightValue)));

        // 計算方法是 BMI = 體重(kg) / 身高(m)的平方
        return (int)(weightValue / (heightValue * heightValue)*10000);
    }

    public static int calculateBMR(String gender, String age_string, String height_string, String weight_string) {
        float bmr=0.0f;
        int age = Integer.parseInt(age_string);
        float height = Float.parseFloat(height_string);
        float weight = Float.parseFloat(weight_string);
        if ("Male".equals(gender)) {
            // 使用男性的 BMR 计算公式
            bmr = 66 + (13.7f * weight) + (5 * height) - (6.8f * age);
        } else if ("Female".equals(gender)) {
            // 使用女性的 BMR 计算公式
            bmr = 655 + (9.6f * weight) + (1.8f * height) - (4.7f * age);
        }

        return (int)bmr;
    }

}
