package com.example.bmrcalculator;

import static com.example.bmrcalculator.Result_page.calculateBMR;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ModifyRecord extends AppCompatActivity {
    Button cancel_btn,send_btn;
    EditText nameEditText, ageEditText, heightEditText, weightEditText;
    RadioGroup genderRadioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_record);

        cancel_btn=findViewById(R.id.cancel);
        send_btn=findViewById(R.id.send);
        // 取得 MainActivity 傳來的資料
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        String gender = bundle.getString("gender");
        String age = bundle.getString("age");
        String height = bundle.getString("height");
        String weight = bundle.getString("weight");
        String bmr = bundle.getString("bmr");

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(ModifyRecord.this,MainActivity.class);
                startActivity(it);
            }
        });
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 取得使用者輸入的資料
                String name = nameEditText.getText().toString();
                String gender = ((RadioButton) findViewById(genderRadioGroup.getCheckedRadioButtonId())).getText().toString();
                String age = ageEditText.getText().toString();
                String height = heightEditText.getText().toString();
                String weight = weightEditText.getText().toString();

                // 計算 BMR
                float bmr = calculateBMR(gender, weight, height, age);

                Bundle bundle = new Bundle();
                bundle.putString("genderValue", gender);
                bundle.putString("user_name", name);
                bundle.putString("user_age", age);
                bundle.putString("user_height", height);
                bundle.putString("user_weight", weight);
                bundle.putString("Mode","Modify");
                Intent intent = new Intent();
                intent.setClass(ModifyRecord.this,  Result_page.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });


        // 初始化相應的元件
        nameEditText = findViewById(R.id.editTextTextPersonName);
        ageEditText = findViewById(R.id.age);
        heightEditText = findViewById(R.id.height);
        weightEditText = findViewById(R.id.weight);
        genderRadioGroup = findViewById(R.id.radioGroup);

        // 將資料顯示在對應的 EditText 和 RadioGroup 上
        nameEditText.setText(name);
        ageEditText.setText(age);
        heightEditText.setText(height);
        weightEditText.setText(weight);

        if (gender.equals("Male")) {
            genderRadioGroup.check(R.id.radioButton);
        } else if (gender.equals("Female")) {
            genderRadioGroup.check(R.id.radioButton2);
        }

        Button cancelBtn = findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回 MainActivity
                finish();
            }
        });

    }
}