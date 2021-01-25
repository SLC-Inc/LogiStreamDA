package com.example.testlogistream;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PickActivity extends AppCompatActivity {
    private Globals globals;
    private Handler handler = new Handler();
    private int intResult = 1;
    private String strMsg;
    private String strOrderID;
    private String strTrkNo;
    private Button returnButton,qrcodeReadButton,nextButton;
    private EditText qrcodeText;

    //アクティビティー生成時、初回実行イベント
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);
        //共通クラス取得
        globals = (Globals) this.getApplication();

        qrcodeText = findViewById(R.id.pickQRcodeText);
        returnButton = findViewById(R.id.returnBtn);
        qrcodeReadButton = findViewById(R.id.setInQRcodeReadBtn);
        nextButton = findViewById(R.id.setInNextBtn);

        qrcodeText.setEnabled(false);

        //QRコード読取ボタン押下イベント
        qrcodeReadButton.setOnClickListener( v-> {
            IntentIntegrator integrator = new IntentIntegrator(PickActivity.this);
            //カメラ起動(QLコード読取画面に遷移)
            integrator.initiateScan();
        });
        //確定ボタン押下イベント
        nextButton.setOnClickListener( v-> {
            //次画面遷移
            Intent intent = new Intent(PickActivity.this, Pick_GenpinActivity.class);
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
                if (qrcodeText.getText().toString().substring(0,2).equals(globals.QR_PLIST)){
                    checkOrderPick();

                }else{
                    Context context = getApplicationContext();
                    Toast.makeText(context , "ピッキングリストではありません。", Toast.LENGTH_LONG).show();
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

    //製品出荷ピッキング指示テーブルのチェック
    private void checkOrderPick(){
        //読み取ったオーダーが製品出荷ピッキング指示テーブルに存在するかチェック
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //文字列切り出し
                    globals.strOrderID = qrcodeText.getText().toString().substring(2,16);
                    globals.strNo = qrcodeText.getText().toString().substring(16,20);

                    //連想配列に入力値格納
                    HashMap<String, Object> jsonMap = new HashMap<>();
                    //
                    jsonMap.put("termName", globals.strModel);
                    jsonMap.put("version", globals.strAppVersion);
                    jsonMap.put("userName", globals.strUserName);
                    jsonMap.put("orderPickID", globals.strOrderID);
                    jsonMap.put("trkNo", globals.strNo);
                    //電文受送信
                    String response = globals.postAPI(globals.IPADRRESS+globals.URL_PICK_ORDER_CHK, jsonMap);
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
                            globals.strpickSum = jsonObj.getString("pickTargetSum");
                            globals.strPlace = jsonObj.getString("locationName");
                            globals.strpickCnt=globals.strpickSum;
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
                            //ピッキングリスト読取成功
                            Toast.makeText(context, "ピッキングリスト読取成功", Toast.LENGTH_SHORT).show();
                            //確定ボタン有効化
                            nextButton.setEnabled(true);

                        } else if(intResult == 9) {
                            //作業完了済みピッキングリスト
                            Toast.makeText(context , "読み取ったピッキング指示は既に作業完了済みです。", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context , "読み取ったピッキング指示は存在しません。", Toast.LENGTH_SHORT).show();
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