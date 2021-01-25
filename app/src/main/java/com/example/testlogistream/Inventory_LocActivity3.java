package com.example.testlogistream;

import android.content.Context;
import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Inventory_LocActivity3 extends AppCompatActivity {
    private Handler handler = new Handler();
    private TextView msgText,unitText,unitText2;
    private EditText placeText,locationText,itemCodeText,itemNameText,lotNoText,volText,numText,qrcodeText;
    private Button returnButton,setInButton,qrcodeReadButton,compButton,nextLocaButton;

    private int intResult = 1;
    private String strMsg;

//    private int intItemCode;
//    private String strItemName;

    private Globals globals;

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
        unitText = findViewById(R.id.setInUnitText);
        unitText2 = findViewById(R.id.setInUnitText2);
        returnButton = findViewById(R.id.returnBtn);
        setInButton = findViewById(R.id.setInNextBtn);
        qrcodeReadButton = findViewById(R.id.setInQRcodeReadBtn);
        compButton = findViewById(R.id.setCompBtn);
        nextLocaButton = findViewById(R.id.setNextLocaBtn);

        msgText.setText("実数量を入力してください。");
        setInButton.setText("次　品目");
        nextLocaButton.setText("次　ロケ");
        compButton.setText("棚卸完了");
        placeText.setEnabled(false);
        locationText.setEnabled(false);
        itemCodeText.setEnabled(false);
        itemNameText.setEnabled(false);
        lotNoText.setEnabled(false);
        volText.setEnabled(false);

        //QRコード表示部は非表示に設定
        qrcodeText.setVisibility(android.view.View.INVISIBLE);
        qrcodeReadButton.setVisibility(android.view.View.INVISIBLE);
        nextLocaButton.setEnabled(false);
        setInButton.setEnabled(false);

        //前画面から受け取った値をセット
        Intent intent = getIntent();
        int intLabelType =  intent.getIntExtra("LabelType",0);
        if (intLabelType == globals.LABEL_TYPE_PALLET){
            //パレット入庫の場合、入庫数量固定、今回入庫数を入力不可に設定
            numText.setText(globals.strC01_Vol);
            numText.setEnabled(false);
        }

        placeText.setText(globals.strPlace);
        locationText.setText(globals.strLocation);
        itemCodeText.setText(globals.strC01_Code);
        itemNameText.setText(globals.strM04_Name);
        lotNoText.setText(globals.strC01_Lot_No);
        volText.setText(globals.strC01_Vol);
        unitText.setText(globals.strM04_Unit);
        unitText2.setText(globals.strM04_Unit);

        //次ロケ押下イベント
        nextLocaButton.setOnClickListener( v-> {
                //今回入庫数量のチェック
                if (Integer.parseInt(numText.getText().toString()) <= 0){
                    Context context = getApplicationContext();
                    Toast.makeText(context , "実数量を入力して下さい。", Toast.LENGTH_SHORT).show();
                }else{

                    //DB更新処理を実行
                    InveProcess();

                    //ロケーション読取り画面へ遷移
                    Intent intent1 = new Intent(Inventory_LocActivity3.this, Inventory_LocActivity.class);
                    startActivity(intent1);

                }
        });
        //次品目押下イベント
        setInButton.setOnClickListener( v-> {
            //今回入庫数量のチェック
            if (Integer.parseInt(numText.getText().toString()) <= 0){
                Context context = getApplicationContext();
                Toast.makeText(context , "実数量を入力して下さい。", Toast.LENGTH_SHORT).show();
            }else{

                //DB更新処理を実行
                InveProcess();

                //現品読取り画面へ遷移
                Intent intent1= new Intent(Inventory_LocActivity3.this, Inventory_LocActivity2.class);
                startActivity(intent1);

            }


        });
        //棚卸し完了ﾎﾞﾀﾝ
        compButton.setOnClickListener( v-> {
            new AlertDialog
                    .Builder(Inventory_LocActivity3.this)
                    .setTitle("棚卸し確認")
                    .setMessage("棚卸しを完了してもよろしいですか？")
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //今回入庫数量のチェック
                            if (Integer.parseInt(numText.getText().toString()) <= 0){
                                Context context = getApplicationContext();
                                Toast.makeText(context , "実数量を入力して下さい。", Toast.LENGTH_SHORT).show();
                            }else{


                                //更新処理
                                InveProcess();
                                //完了処理
                                InveComp();

                            }
                        }
                    })
                    .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //キャンセル押下時は何もしない
                        }
                    })
                    .show();
        });

        //戻るボタン押下イベント
        returnButton.setOnClickListener( v-> {
            //画面を閉じる
            finish();
        });
        //QRコードセット検知イベント
        numText.addTextChangedListener(new TextWatcher() {
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
                if (numText.getText().toString().equals("")){
                    //棚卸し完了表示
                    compButton.setEnabled(false);
                    //次ロケ表示
                    nextLocaButton.setEnabled(false);
                    //次品目表示
                    setInButton.setEnabled(false);
                }else{
                    if (Integer.parseInt(numText.getText().toString()) > 0){
                        //棚卸し完了表示
                        compButton.setEnabled(true);
                        //次ロケ表示
                        nextLocaButton.setEnabled(true);
                        //次品目表示
                        setInButton.setEnabled(true);
                    }
                }
            }
        });
    }

    //棚卸し処理中
    protected void InveProcess(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //連想配列に入力値格納
                    HashMap<String, Object> jsonMap = new HashMap<>();
                    jsonMap.put("termName", globals.strModel);
                    jsonMap.put("version", globals.strAppVersion);
                    jsonMap.put("userName", globals.strUserName);
                    jsonMap.put("InvID",globals.strInvID);
                    jsonMap.put("no",globals.strNo);
                    jsonMap.put("idx",globals.strIdx);
                    jsonMap.put("itemCode",itemCodeText.getText().toString());
                    jsonMap.put("lotNo",lotNoText.getText().toString());
                    jsonMap.put("vol",numText.getText().toString());

                    //電文受送信
                    String response = globals.postAPI(globals.IPADRRESS+globals.URL_INVENTORY_PROCESS,jsonMap);
                    //応答データをJSON形式に変換
                    JSONObject jsonObj = new JSONObject(response);
                    //応答データ移送
                    intResult = Integer.parseInt(jsonObj.getString("result"));
                    strMsg = jsonObj.getString("msg");

                }catch (JSONException e){
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Context context = getApplicationContext();
                        if(intResult == 0){
                            //対象品目あり
                            Toast.makeText(context , "更新成功", Toast.LENGTH_SHORT).show();

                        }else{
                            //対象品目なし
                            Toast.makeText(context , strMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        thread.start();
    }
    //棚卸し完了
    protected void InveComp(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //連想配列に入力値格納
                    HashMap<String, Object> jsonMap = new HashMap<>();
                    jsonMap.put("termName", globals.strModel);
                    jsonMap.put("version", globals.strAppVersion);
                    jsonMap.put("userName", globals.strUserName);
                    jsonMap.put("InvID",globals.strInvID);
                    jsonMap.put("strageName",globals.strNo);

                    //電文受送信
                    String response = globals.postAPI(globals.IPADRRESS+globals.URL_INVENTORY_PROCESS_COMP,jsonMap);
                    //応答データをJSON形式に変換
                    JSONObject jsonObj = new JSONObject(response);
                    //応答データ移送
                    intResult = Integer.parseInt(jsonObj.getString("result"));
                    strMsg = jsonObj.getString("msg");

                }catch (JSONException e){
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Context context = getApplicationContext();
                        if(intResult == 0){

                            Toast.makeText(context , "棚卸し完了", Toast.LENGTH_SHORT).show();
                            //メインメニュー画面へ遷移
                            Intent intent = new Intent(Inventory_LocActivity3.this, MainMenuActivity.class);
                            startActivity(intent);
                        }else{
                            //対象品目なし
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
