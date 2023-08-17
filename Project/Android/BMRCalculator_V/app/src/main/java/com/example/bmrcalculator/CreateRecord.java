package com.example.bmrcalculator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CreateRecord extends AppCompatActivity {
    Button send_data_btn,cancel_btn;
    EditText name_bar,age_bar,weight_bar,height_bar;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_record);

        send_data_btn = (Button) findViewById(R.id.Send_data);
        cancel_btn =(Button) findViewById(R.id.cancel);

        name_bar = findViewById(R.id.editTextTextPersonName);
        age_bar = findViewById(R.id.age);
        height_bar = findViewById(R.id.height);
        weight_bar = findViewById(R.id.weight);
        radioGroup = findViewById(R.id.radioGroup);


        send_data_btn.setOnClickListener(v -> {

            String user_name=name_bar.getText().toString();
            String user_age = age_bar.getText().toString();
            String user_height = height_bar.getText().toString();
            String user_weight = weight_bar.getText().toString();

            Log.d("CreateRecord", user_name);
            Log.d("CreateRecord", user_height);
            Log.d("CreateRecord", user_weight);


            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            String user_gender;
            if (selectedRadioButtonId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                user_gender = selectedRadioButton.getText().toString();

            } else {
                // 如果没有選中任何 RadioButton
                user_gender = "未輸入性別";

            }
            String finalUser_gender = user_gender;
            Bundle bundle = new Bundle();
            bundle.putString("genderValue", finalUser_gender);
            bundle.putString("user_name", user_name);
            bundle.putString("user_age", user_age);
            bundle.putString("user_height", user_height);
            bundle.putString("user_weight", user_weight);
            bundle.putString("Mode","Create");
            Intent intent = new Intent();
            intent.setClass(CreateRecord.this,  Result_page.class);
            intent.putExtras(bundle);

            if (finalUser_gender.isEmpty() || user_name.isEmpty() || user_age.isEmpty() || user_height.isEmpty() || user_weight.isEmpty()) {
                // If any of the fields is empty, show AlertDialog
                showAlertDialog();
            } else {
                // All fields are filled, start Result_page activity

                // ... Put data into intent extras ...
                startActivity(intent);
            }

        });

        cancel_btn.setOnClickListener(v -> {
            Intent it = new Intent();
            it.setClass(CreateRecord.this,MainActivity.class);
            startActivity(it);
        });

    }

    private void showAlertDialog() {AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Missing Information");
        builder.setMessage("請填入完整資料.");
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}