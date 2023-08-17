package com.example.bmrcalculator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Button create_btn;
    ListView listview;

    ArrayList<String> nameList = new ArrayList<>();
    ArrayList<String> genderList = new ArrayList<>();
    ArrayList<String> bmrList = new ArrayList<>();
    ArrayList<String> ageList = new ArrayList<>();
    ArrayList<String> heightList = new ArrayList<>();
    ArrayList<String> weightList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        create_btn=findViewById(R.id.create);
        listview=(ListView) findViewById(R.id.List);

        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(MainActivity.this, CreateRecord.class);
                startActivity(it);
            }
        });

        //ListView

        MyAdapter adapter=new MyAdapter(MainActivity.this);
        listview.setAdapter(adapter);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> AdapterView, View view, int position, long id) {
                String name = nameList.get(position);
                String gender = genderList.get(position);
                String age = ageList.get(position);
                String height = heightList.get(position);
                String weight = weightList.get(position);
                String bmr = bmrList.get(position);



                // 建立 AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("刪除資料");
                builder.setMessage("確定要刪除 " + name + " 的資料嗎？");

                // 設定確定按鈕的點擊事件
                builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // 使用 HTTP POST 方法刪除資料
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // 使用 HTTP POST 方法將資料傳送至後端 PHP
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                URL url = new URL("http://IP/bmrphp/delete.php");
                                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                                connection.setRequestMethod("POST");
                                                connection.setDoOutput(true);
                                                connection.setDoInput(true);
                                                connection.setUseCaches(false);

                                                // 建立 JSON 物件來包裝要傳送的資料
                                                JSONObject data = new JSONObject();
                                                data.put("name", name);

                                                // 將 JSON 物件轉換為字串
                                                String jsonString = data.toString();

                                                // 設定 Content-Type 為 application/json
                                                connection.setRequestProperty("Content-Type", "application/json");

                                                // 寫入要傳送的資料
                                                OutputStream outputStream = connection.getOutputStream();
                                                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                                                writer.write(jsonString);
                                                writer.flush();
                                                writer.close();
                                                outputStream.close();

                                                // 取得伺服器回傳的結果
                                                int responseCode = connection.getResponseCode();
                                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                                    InputStream inputStream = connection.getInputStream();
                                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                                                    String line;
                                                    StringBuilder response = new StringBuilder();
                                                    while ((line = bufferedReader.readLine()) != null) {
                                                        response.append(line);
                                                    }
                                                    bufferedReader.close();
                                                    inputStream.close();

                                                    // 將伺服器回傳的結果顯示出來
                                                    String result = response.toString();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                                                            // 在成功刪除資料後，重新載入資料
                                                            Log.d("postion","Postion"+ String.valueOf(position));

                                                            ((BaseAdapter) listview.getAdapter()).notifyDataSetChanged();
                                                        }
                                                    });
                                                } else {
                                                    // 若連接失敗，顯示錯誤訊息
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(MainActivity.this, "Failed to connect to the server.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();

                                    // 在成功刪除資料後，重新載入資料

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            nameList.remove(position);
                                            genderList.remove(position);
                                            ageList.remove(position);
                                            heightList.remove(position);
                                            weightList.remove(position);
                                            bmrList.remove(position);
                                            ((BaseAdapter) listview.getAdapter()).notifyDataSetChanged();
                                            Toast.makeText(MainActivity.this, "成功刪除資料", Toast.LENGTH_SHORT).show();
                                            loadData();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });

                // 設定取消按鈕的點擊事件
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // 顯示 AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }



        });


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> AdapterView, View view, int position, long id) {
                String name = nameList.get(position);
                String gender = genderList.get(position);
                String bmr = bmrList.get(position);
                String age = ageList.get(position);
                String height = heightList.get(position);
                String weight = weightList.get(position);

                // 創建一個新的 Intent
                Intent intent = new Intent(MainActivity.this, ModifyRecord.class);

                // 創建 Bundle 並將資料存入
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("gender", gender);
                bundle.putString("bmr", bmr);
                bundle.putString("age", age);
                bundle.putString("height", height);
                bundle.putString("weight", weight);

                // 將 Bundle 設置到 Intent 中
                intent.putExtras(bundle);

                // 啟動 ModifyRecord Activity
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("On", "onResume");
        loadData(); // 加載最新資料
    }

    public void loadData() {

        nameList.clear();
        genderList.clear();
        bmrList.clear();
        ageList.clear();
        heightList.clear();
        weightList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://IP/bmrphp/read.php");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setUseCaches(false);
                    connection.connect();

                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                        String line = null;

                        while ((line = bufferedReader.readLine()) != null) {
                            JSONArray jsonArray = new JSONArray(line);
                            Log.d("loadData","laod json :"+jsonArray.toString());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                // 從 JSON 物件中取得對應的值
                                String name = jsonObject.getString("Name");
                                String gender = jsonObject.getString("Gender");
                                String age = jsonObject.getString("Age");
                                String height = jsonObject.getString("Height");
                                String weight = jsonObject.getString("Weight");
                                String bmr = jsonObject.getString("BMR");

                                // 將取得的值加入對應的 ArrayList
                                nameList.add(name);
                                genderList.add(gender);
                                ageList.add(age);
                                heightList.add(height);
                                weightList.add(weight);
                                bmrList.add(bmr);
                            }
                        }

                        inputStream.close();
                    } else {
                        Log.d("nameList", "Error");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ((BaseAdapter) listview.getAdapter()).notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    public class MyAdapter extends BaseAdapter{
        public MyAdapter(Context mContext) {
            this.mContext = mContext;
            layoutInflater=LayoutInflater.from(mContext);
        }

        Context mContext;
        LayoutInflater layoutInflater;

        @Override
        public int getCount() {

            //return names.length;
            return nameList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textName,textBmr;

            convertView = layoutInflater.inflate(R.layout.listlayout,null);

            textName=convertView.findViewById(R.id.name);
            textBmr=convertView.findViewById(R.id.bmr);

            Log.d("Adapter", nameList.get(position)+"setting" );
            textName.setText(nameList.get(position));
            textBmr.setText(bmrList.get(position));

            return convertView;
        }
    }


}