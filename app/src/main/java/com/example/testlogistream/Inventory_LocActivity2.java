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

public class Inventory_LocActivity2 extends AppCompatActivity {
    private Handler handler = new Handler();
    private TextView msgText;
    private EditText placeText,locationText,itemCodeText,itemNameText,lotNoText,volText,numText,qrcodeText;
    private Button returnButton,qrcodeReadButton,nextButton,compButton;

    private Globals globals;

    private int intResult = 1;
    private int intHl_stat;
    private int intType;
    private String strC01_Id;
    private String strC01_Code;
    private String strM04_Name_Upper;
    private String strM04_Name_Lower;
    private String strM04_Unit;
    private String strC01_Lot_No;
    private String strC01_Vol;
    private String strM04_Case_Num_HP;
    private String strC01_Pallet_ID;
    private String strC01_Type;
    private String strC01_Hl_Stat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_loc);

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
        compButton = findViewById(R.id.setCompBtn);

        msgText.setText("現品ラベルを読み取って下さい。");
        placeText.setEnabled(false);
        locationText.setEnabled(false);
        itemCodeText.setEnabled(false);
        itemNameText.setEnabled(false);
        lotNoText.setEnabled(false);
        volText.setEnabled(false);
        numText.setEnabled(false);
        qrcodeText.setEnabled(false);

        //保管場所、ロケーションのセット
        placeText.setText(globals.strPlace);
        locationText.setText(globals.strLocation);

        //QRコード読取ボタン押下イベント
        qrcodeReadButton.setOnClickListener( v-> {
            IntentIntegrator integrator = new IntentIntegrator(Inventory_LocActivity2.this);
            //カメラ起動(QLコード読取画面に遷移)
            integrator.initiateScan();
        });

        //確定ボタン押下イベント
        nextButton.setOnClickListener( v-> {
            //次画面遷移
            Intent intent = new Intent(Inventory_LocActivity2.this, Inventory_LocActivity3.class);
            if (intType == 1){
                intent.putExtra("LabelType",globals.LABEL_TYPE_PALLET);
            }else if (intType == 2){
                intent.putExtra("LabelType",globals.LABEL_TYPE_GENPIN);
            }

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
                Context context = getApplicationContext();
                String strQRcode = qrcodeText.getText().toString().substring(0,2);

                if (strQRcode.equals(globals.QR_GENPIN)){
                    //現品ラベル読取時
                    //在庫情報取得
                    getStock2();

                }else{
                    Toast.makeText(context , "現品ラベルを読み取って下さい。", Toast.LENGTH_SHORT).show();
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
            //QRコード表示部の初期化
            qrcodeText.setText("");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    //パレットIDを元に在庫情報取得
//    protected void  getStock(){
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try{
//                    //パレットIDを元に在庫情報を取得する
//                    //文字列切り出し
//                    String strPalletID = qrcodeText.getText().toString().substring(44);
//                    //連想配列に入力値格納
//                    HashMap<String, Object> jsonMap = new HashMap<>();
//                    jsonMap.put("WebAPI_ID",3);
//                    jsonMap.put("PalletID",strPalletID);
//                    //電文受送信
//                    String response = globals.postAPI(globals.URL_SETIN_GET_STOCK,jsonMap);
//                    //応答データをJSON形式に変換
//                    JSONObject jsonObj = new JSONObject(response);
//                    //応答データ移送
//                    intResult = Integer.parseInt(jsonObj.getString("result"));
//                    globals.strC01_Id = jsonObj.getString("C01_ID");
//                    globals.strC01_Code = jsonObj.getString("C01_CODE");
//                    globals.strM04_Name = jsonObj.getString("M04_NAME_UPPER") + jsonObj.getString("M04_NAME_LOWER");
//                    globals.strM04_Unit = jsonObj.getString("M04_UNIT");
//                    globals.strC01_Lot_No = jsonObj.getString("C01_LOT_NO");
//                    globals.strC01_Vol = jsonObj.getString("C01_VOL");
//                    globals.strM04_Case_Num_HP = jsonObj.getString("M04_CASE_NUM_HP");
//                    globals.strC01_Pallet_ID = jsonObj.getString("C01_PALLET_ID");
//
//                }catch (JSONException e){
//                    e.printStackTrace();
//                }
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Context context = getApplicationContext();
//                        if(intResult == 1){
//                            //対象品目あり
//                            Toast.makeText(context , "パレットラベル読取成功", Toast.LENGTH_SHORT).show();
//                            intType = 1;
//                            //確定ボタン有効化
//                            nextButton.setEnabled(true);
//                        }else{
//                            //対象品目なし
//                            Toast.makeText(context , "対象製品がありません。", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
//        thread.start();
//    }
    //品目コード、ロットNoを元に在庫情報取得
    protected void  getStock2(){
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //パレットIDを元に在庫情報を取得する
                    //文字列切り出し
                    String strItem = qrcodeText.getText().toString().substring(12,28);
                    String strLot = qrcodeText.getText().toString().substring(28,44);
                    int intHl_stat = 0;
                    //連想配列に入力値格納
                    HashMap<String, Object> jsonMap = new HashMap<>();
                    jsonMap.put("termName", globals.strModel);
                    jsonMap.put("version", globals.strAppVersion);
                    jsonMap.put("userName", globals.strUserName);
                    jsonMap.put("InvID",globals.strInvID);
                    jsonMap.put("itemCode",strItem.trim());
                    jsonMap.put("lotNo",strLot.trim());
                    jsonMap.put("no",globals.strNo);
                    jsonMap.put("idx",globals.strIdx);

                    //電文受送信
                    String response = globals.postAPI(globals.IPADRRESS+globals.URL_INVENTORY_PROCESS_GENPIN,jsonMap);
                    //応答データをJSON形式に変換
                    JSONObject jsonObj = new JSONObject(response);
                    //応答データ移送
                    globals.strC01_Code = strItem.trim();
                    globals.strC01_Lot_No = strLot.trim();
                    globals.strM04_Name = jsonObj.getString("itemName");
                    globals.strC01_Vol = jsonObj.getString("vol");
                    globals.strM04_Unit = jsonObj.getString("unit");

                }catch (JSONException e){
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Context context = getApplicationContext();
                        if(intResult == 1){
                            //対象品目あり
                            Toast.makeText(context , "現品ラベル読取成功", Toast.LENGTH_SHORT).show();
                            //Intent intent = new Intent(SetInActivity2.this, SetInActivity3.class);
                            //intent.putExtra("LabelType",globals.LABEL_TYPE_GENPIN);
                            //次画面遷移
                            //startActivity(intent);
                            intType = 2;
                            //確定ボタン有効化
                            nextButton.setEnabled(true);
                        }else{
                            //対象品目なし
                            Toast.makeText(context , "対象製品がありません。", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        thread2.start();
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
