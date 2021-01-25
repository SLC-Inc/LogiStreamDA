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
import android.view.View;
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

public class SetInActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private TextView msgText;
    private EditText placeText,locationText,itemCodeText,itemNameText,lotNoText,volText,numText,qrcodeText;
    private Button returnButton,qrcodeReadButton,nextButton;
    private String strQRcode;
    private int intResult = 1;
    private String strMsg;
    private String strC05_name;
//    private String strC05_bank;
//    private String strC05_bay;
//    private String strC05_lebel;
    private String strC05_location;

    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setin);

        //共通クラス取得
        globals = (Globals) this.getApplication();

        msgText = findViewById(R.id.setInmsgText);
        placeText = findViewById(R.id.setInPlaceText);
        locationText = findViewById(R.id.setInLocationText);
        itemCodeText = findViewById(R.id.setInItemCodeText);
        itemNameText = findViewById(R.id.setInItemNameText);
        lotNoText = findViewById(R.id.setInLotNoText);
        volText = findViewById(R.id.setInVolText);
        numText = findViewById(R.id.setInNumText);
        qrcodeText = findViewById(R.id.setInQRcodeText);
        returnButton = findViewById(R.id.returnBtn);
        qrcodeReadButton = findViewById(R.id.setInQRcodeReadBtn);
        nextButton = findViewById(R.id.setInNextBtn);

        placeText.setEnabled(false);
        locationText.setEnabled(false);
        itemCodeText.setEnabled(false);
        itemNameText.setEnabled(false);
        lotNoText.setEnabled(false);
        volText.setEnabled(false);
        numText.setEnabled(false);
        qrcodeText.setEnabled(false);

        //QRコード読取ボタン押下イベント
        qrcodeReadButton.setOnClickListener( v-> {
            IntentIntegrator integrator = new IntentIntegrator(SetInActivity.this);
            //カメラ起動(QLコード読取画面に遷移)
            integrator.initiateScan();
        });

        //確定ボタン押下イベント
        nextButton.setOnClickListener( v-> {
            //次画面遷移
            Intent intent = new Intent(SetInActivity.this, SetInActivity2.class);
            //次画面で使用する値セット
            globals.strPlace = strC05_name;
            globals.strLocation = strC05_location;

            startActivity(intent);
        });

        //戻るボタン押下イベント
        returnButton.setOnClickListener( v-> {
            //画面を閉じる
            finish();
        });

        //QRコードセット検知イベント
        qrcodeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //編集前
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //編集中
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (qrcodeText.getText().toString().substring(0,2).equals(globals.QR_LOCATION)){
                    //ロケーション取得
                    getLocation();
                }else{
                    Context context = getApplicationContext();
                    Toast.makeText(context , "ロケーションラベルではありません。", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    //別画面から戻ってきた場合のイベント
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //スキャン結果判定
        if (scanResult.getContents() != null) {
            //スキャンデータをセット
            qrcodeText.setText(scanResult.getContents());
          } else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    protected void getLocation(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //文字列切り出し
                    //globals.strNo = qrcodeText.getText().toString().substring(2,6);
                    //globals.strIdx = qrcodeText.getText().toString().substring(7,12);
                    String strqr =  qrcodeText.getText().toString();
                    String[] data = strqr.split("-");

                    globals.strNo = data[0].substring(2,6);
                    globals.strIdx = data[1];

                    //連想配列に入力値格納
                    HashMap<String, Object> jsonMap = new HashMap<>();

                    jsonMap.put("termName", globals.strModel);
                    jsonMap.put("version", globals.strAppVersion);
                    jsonMap.put("userName", globals.strUserName);
                    jsonMap.put("no",globals.strNo);
                    jsonMap.put("idx",globals.strIdx);

                    //電文受送信
                    String response = globals.postAPI(globals.IPADRRESS+globals.URL_SETIN_INTO_LOCATION, jsonMap);
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
                            strC05_name = jsonObj.getString("locationName");
                            strC05_location = jsonObj.getString("location");
//                    strC05_bay = jsonObj.getString("C05_BAY");
//                    strC05_lebel = jsonObj.getString("C05_LEBEL");
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Context context = getApplicationContext();
                        if (intResult == 0){
                            //通常棚
                            Toast.makeText(context , "ロケーションラベル読取成功",
                                    Toast.LENGTH_SHORT).show();
                            //確定ボタン有効化
                            nextButton.setEnabled(true);
                        }else{
                            Toast.makeText(context , strMsg, Toast.LENGTH_SHORT).show();
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
