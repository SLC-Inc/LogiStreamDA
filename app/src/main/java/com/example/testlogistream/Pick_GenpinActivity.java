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
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Pick_GenpinActivity extends AppCompatActivity {
    private Globals globals;
    private Handler handler = new Handler();
    private int intResult =1;
    private String strMsg;
    private String strItemCode="";
    private String strItemName="";
    private String strLotNo="";
    private String strVol="";
    private String strUnit="";
    private String strLocation="";
    private int itemCnt;

    private TextView pickUnitText,titleText;
    private EditText itemCntText, itemMaxText, workClassText, strageText, locationText, itemCodeText,
            itemNameText, lotNoText, volText, qrcodeText;
    private Button returnButton,qrcodeReadButton,nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_genpin);
        pickUnitText = findViewById(R.id.pickUnitText);
        titleText = findViewById(R.id.titleText);
        itemCntText = findViewById(R.id.pickItemCntText);
        itemMaxText = findViewById(R.id.pickItemMaxText);
        workClassText = findViewById(R.id.pickWorkClassText);
        strageText = findViewById(R.id.pickStrageText);
        locationText = findViewById(R.id.pickLocationText);
        itemCodeText = findViewById(R.id.pickItemCodeText);
        itemNameText = findViewById(R.id.pickItemNameText);
        lotNoText = findViewById(R.id.pickLotNoText);
        volText = findViewById(R.id.pickVolText);

        qrcodeText = findViewById(R.id.pickQRcodeText);
        returnButton = findViewById(R.id.returnBtn);
        qrcodeReadButton = findViewById(R.id.setInQRcodeReadBtn);
        nextButton = findViewById(R.id.setInNextBtn);

        //共通クラス取得
        globals = (Globals) this.getApplication();

        //初期セット
        itemCntText.setText(globals.strpickCnt);
        itemMaxText.setText(globals.strpickSum);
        strageText.setText(globals.strPlace);
        workClassText.setText("出荷");
        itemCnt = Integer.parseInt(globals.strpickCnt);
        nextButton.setEnabled(false);
        itemCntText.setEnabled(false);
        itemMaxText.setEnabled(false);
        workClassText.setEnabled(false);
        strageText.setEnabled(false);
        locationText.setEnabled(false);
        itemCodeText.setEnabled(false);
        itemNameText.setEnabled(false);
        lotNoText.setEnabled(false);
        volText.setEnabled(false);
        pickUnitText.setEnabled(false);
        qrcodeText.setEnabled(false);


        //ピック完了ボタン押下イベント
        nextButton.setOnClickListener( v-> {
            pickComp();
        });

        //QRコード読取ボタン押下イベント
        qrcodeReadButton.setOnClickListener( v-> {
            IntentIntegrator integrator = new IntentIntegrator(Pick_GenpinActivity.this);
            //カメラ起動(QLコード読取画面に遷移)
            integrator.initiateScan();
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
                if (qrcodeText.getText().toString().substring(0,2).equals(globals.QR_GENPIN)){
                    getStock2();
                }else{
                    Context context = getApplicationContext();
                    Toast.makeText(context , "正しいラベルではありません。", Toast.LENGTH_SHORT).show();
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
    //品目コード、ロットNoを元に在庫情報取得
    protected void  getStock2() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //文字列切り出し
                    String strItem = qrcodeText.getText().toString().substring(12, 28);
                    String strLot = qrcodeText.getText().toString().substring(28, 44);

                    //連想配列に入力値格納
                    HashMap<String, Object> jsonMap = new HashMap<>();
                    jsonMap.put("termName", globals.strModel);
                    jsonMap.put("version", globals.strAppVersion);
                    jsonMap.put("userName", globals.strUserName);
                    jsonMap.put("orderPickID",globals.strOrderID);
                    jsonMap.put("trkNo",globals.strNo);
                    jsonMap.put("itemCode", strItem.trim());
                    jsonMap.put("lotNo", strLot.trim());


                    //電文受送信
                    String response = globals.postAPI(globals.IPADRRESS+globals.URL_PICK_PROCESS, jsonMap);
                    if (response.equals("")) {
                        //応答なしの場合
                        intResult = 2;
                        strMsg = "サーバー接続失敗";
                    } else {
                        //応答ありの場合
                        //応答データをJSON形式に変換
                        JSONObject jsonObj = new JSONObject(response);
                        //応答データ移送
                        intResult = Integer.parseInt(jsonObj.getString("result"));
                        strMsg = jsonObj.getString("msg");
                        if (intResult == 0) {
                            //応答結果が正常の場合
                            strItemCode = jsonObj.getString("itemCode");
                            if (jsonObj.isNull("itemName2")){
                                strItemName = jsonObj.getString("itemName1");
                            }else{
                                strItemName = jsonObj.getString("itemName1") + jsonObj.getString("itemName2");
                            }

                            strLotNo = jsonObj.getString("lotNo");
                            strVol = jsonObj.getString("vol");
                            strUnit = jsonObj.getString("unit");
                            strLocation = jsonObj.getString("location");

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
                            //対象品目あり
                            Toast.makeText(context, "現品ラベル読取成功", Toast.LENGTH_SHORT).show();
                            titleText.setText("ピック後、ピック完了ボタンを\n押してください。");
                            //表示項目更新
                            itemCodeText.setText(strItemCode);
                            itemNameText.setText(strItemName);
                            lotNoText.setText(strLotNo);
                            volText.setText(strVol);
                            pickUnitText.setText(strUnit);
                            locationText.setText(strLocation);

                            //確定ボタン有効化
                            nextButton.setEnabled(true);
                            qrcodeText.setEnabled(false);
                        } else {
                            Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        thread.start();
    }
    //ピック完了
    protected void  pickComp() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //連想配列に入力値格納
                    HashMap<String, Object> jsonMap = new HashMap<>();
                    jsonMap.put("termName", globals.strModel);
                    jsonMap.put("version", globals.strAppVersion);
                    jsonMap.put("userName", globals.strUserName);
                    jsonMap.put("orderPickID",globals.strOrderID);
                    jsonMap.put("trkNo",globals.strNo);
                    jsonMap.put("itemCode", strItemCode);
                    jsonMap.put("lotNo", strLotNo);

                    //電文受送信
                    String response = globals.postAPI(globals.IPADRRESS+globals.URL_PICK_PROCESS_COMP, jsonMap);
                    if (response.equals("")) {
                        //応答なしの場合
                        intResult = 2;
                        strMsg = "サーバー接続失敗";
                    } else {
                        //応答ありの場合
                        //応答データをJSON形式に変換
                        JSONObject jsonObj = new JSONObject(response);
                        //応答データ移送
                        intResult = Integer.parseInt(jsonObj.getString("result"));
                        strMsg = jsonObj.getString("msg");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Context context = getApplicationContext();
                        if (intResult == 0) {
                            //残品目がゼロだった場合は、ピッキングリスト読取り画面へ遷移
                            itemCnt -=1;
                            if(itemCnt==0){
                                Toast.makeText(context , "ピッキング完了です。", Toast.LENGTH_SHORT).show();
                                //次画面遷移
                                Intent intent = new Intent(Pick_GenpinActivity.this, PickActivity.class);
                                startActivity(intent);
                            }else{
                                //次品目読み込みのための準備
                                Integer i = itemCnt;
                                globals.strpickCnt = String.valueOf(i);
//                                itemCntText.setText(stritemCnt);
//                                titleText.setText("現品ラベルを読み取って下さい。");
//                                nextButton.setEnabled(false);

                                //次画面遷移
                                Intent intent = new Intent(Pick_GenpinActivity.this, Pick_GenpinActivity.class);
                                startActivity(intent);

//                                String strKuhaku = " ";
//                                qrcodeText.setEnabled(true);
//                                editText.getText().clear();
//                                qrcodeText.setText("");
//                                itemCodeText.setText("");
//                                itemNameText.setText("");
//                                lotNoText.setText("");
//                                volText.setText("");
//                                pickUnitText.setText("");
//                                locationText.setText("");

                            }
                        } else {
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