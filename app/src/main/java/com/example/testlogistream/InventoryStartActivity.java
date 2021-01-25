package com.example.testlogistream;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class InventoryStartActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private Globals globals;
    private Button NewstartButton,WorkstartButton,returnButton;

    private int intResult = 1;
    private String strMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_start);

        //共通クラス取得
        globals = (Globals) this.getApplication();

        NewstartButton = findViewById(R.id.setNewBtn);
        WorkstartButton = findViewById(R.id.setWorkBtn);
        returnButton = findViewById(R.id.returnBtn);



        //新規ボタンイベント
        NewstartButton.setOnClickListener( v-> {
            //画面を閉じる
            StartInv("1");
        });

        //作業再開ボタンイベント
        WorkstartButton.setOnClickListener( v-> {
            //画面を閉じる
            StartInv("2");
        });
        //戻るボタン押下イベント
        returnButton.setOnClickListener( v-> {
            //画面を閉じる
            finish();
        });

    }


    protected void StartInv(String Stat){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //連想配列に入力値格納
                    HashMap<String, Object> jsonMap = new HashMap<>();
                    jsonMap.put("termName", globals.strModel);
                    jsonMap.put("version", globals.strAppVersion);
                    jsonMap.put("userName", globals.strUserName);
                    jsonMap.put("strage",globals.strStrageID);
                    jsonMap.put("stat",Stat);

                    //電文受送信
                    String response = globals.postAPI(globals.IPADRRESS+globals.URL_INVENTORY_START, jsonMap);
                    if (response.equals("")){
                        //応答なしの場合
                        intResult = 2;
                        strMsg = "サーバー接続失敗";
                    }else{
                        //応答ありの場合
                        //応答データをJSON形式に変換
                        JSONObject jsonObj = new JSONObject(response);
                        //応答データ移送
                        intResult = Integer.parseInt(jsonObj.getString("result"));
                        strMsg = jsonObj.getString("msg");
                        globals.strInvID = jsonObj.getString("InveID");

                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Context context = getApplicationContext();
                        if (intResult == 0){
                            Toast.makeText(context , "棚卸し作業開始",
                                    Toast.LENGTH_SHORT).show();
                            //次画面遷移
                            Intent intent = new Intent(InventoryStartActivity.this,
                                    Inventory_LocActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        thread.start();
    }
    // メニューをActivity上に設置する
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // メニューが選択されたときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menuMain:
                //
                intent = new Intent(this, MainMenuActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //ハードウェアの戻るボタン押下時の制御
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction()==KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    // ダイアログ表示など特定の処理を行いたい場合はここに記述
                    // 親クラスのdispatchKeyEvent()を呼び出さずにtrueを返す
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
