package com.example.testlogistream;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private Button loginButton;
    private EditText idEditText,passEditText;
    private TextView msgTextView;
    private int intResult = 1;
    private String strMsg;
    private Globals globals;
    private Context context;
    private String path;
    private String state;
    private String fileName;
    private String file;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //共通クラス取得
        globals = (Globals) this.getApplication();

        loginButton = findViewById(R.id.loginBtn);
        idEditText = findViewById(R.id.idText);
        passEditText = findViewById(R.id.passText);
        msgTextView = findViewById(R.id.msgText);

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }



        //ログインID、パスワードのセット（テスト用）
        idEditText.setText("");
        passEditText.setText("");

        //ログインボタン押下イベント
        loginButton.setOnClickListener(v -> {
            msgTextView.setText("");
            //ログインID取得
            String strId = idEditText.getText().toString();
            //パスワード取得
            String strPass = passEditText.getText().toString();

            //入力チェック
            if (strId.equals("") || strPass.equals("")) {
                Toast.makeText(this, "ログインID、もしくはパスワードを入力してください。",
                        Toast.LENGTH_SHORT).show();
            } else {
                //グロバルIPセット処理
                fileName = "logistreamip.txt";
                String dir = Environment.getExternalStorageDirectory().getPath();
                file = dir + "/Download/" + fileName;


                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)){
                    String str = readFile(file);

                    //IPセット
                    globals.IPADRRESS = str;

                    //ログイン処理
                    login(strId,strPass);
                }
            }
        });
    }
        protected void login(String strId,String strPass){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //Android端末名取得
                        globals.strModel = android.os.Build.MODEL;
                        globals.strAppVersion = getVersionName(LoginActivity.this);
                        //連想配列に入力値格納
                        HashMap<String, Object> jsonMap = new HashMap<>();
//                        jsonMap.put("WebAPI_ID", 1);
                        jsonMap.put("termName", globals.strModel);
                        jsonMap.put("version", globals.strAppVersion);
                        jsonMap.put("loginID", strId);
                        jsonMap.put("loginPass", strPass);

                        //電文受送信
                        String response = globals.postAPI( globals.IPADRRESS + globals.URL_LOGIN, jsonMap);
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
                            if (intResult == 0){
                                //応答結果が正常の場合
                                globals.strUserName = jsonObj.getString("userName");
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Context context = getApplicationContext();
                            if (intResult == 0) {
                                //ログイン成功
                                Toast.makeText(context, "ログイン成功", Toast.LENGTH_SHORT).show();
                                //次画面遷移
                                Intent intent = new Intent(LoginActivity.this,
                                        MainMenuActivity.class);
                                startActivity(intent);

                                //
//                                finish();

                            }else{
                                Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            thread.start();
        }
    protected String getVersionName(Activity activity) {

        try {
            // Java パッケージ名を取得
            // android.content.Context#getPackageName
            String name = activity.getPackageName();

            // インストールされているアプリケーションパッケージの
            // 情報を取得するためのオブジェクトを取得
            // android.content.Context#getPackageManager
            PackageManager pm = activity.getPackageManager();

            // アプリケーションパッケージの情報を取得
            PackageInfo info = pm.getPackageInfo(name, PackageManager.GET_META_DATA);

            // バージョン番号の文字列を返す
            return info.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String readFile(String file){
        String text = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
            String lineBuffer;
            while (true){
                lineBuffer = reader.readLine();
                if (lineBuffer != null){
                    text = lineBuffer;
                }
                else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
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