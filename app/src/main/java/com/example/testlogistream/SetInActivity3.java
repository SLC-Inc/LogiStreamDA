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

public class SetInActivity3 extends AppCompatActivity {
    private Handler handler = new Handler();
    private TextView msgText,unitText,unitText2;
    private EditText placeText,locationText,itemCodeText,itemNameText,lotNoText,volText,numText,qrcodeText;
    private Button returnButton,setInButton,qrcodeReadButton;

    private int intResult = 1;
    private String strMsg;
    private String strC01_Id;

    private int intItemCode;
    private String strItemName;

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
        unitText = findViewById(R.id.setInUnitText);
        unitText2 = findViewById(R.id.setInUnitText2);
        returnButton = findViewById(R.id.returnBtn);
        setInButton = findViewById(R.id.setInNextBtn);
        qrcodeReadButton = findViewById(R.id.setInQRcodeReadBtn);

        msgText.setText("入庫数を入力して\n入庫確認を押して下さい。");
        setInButton.setText("入庫確認");
        //setInButton.setEnabled(false);
        placeText.setEnabled(false);
        locationText.setEnabled(false);
        itemCodeText.setEnabled(false);
        itemNameText.setEnabled(false);
        lotNoText.setEnabled(false);
        volText.setEnabled(false);

        //QRコード表示部は非表示に設定
        qrcodeText.setVisibility(android.view.View.INVISIBLE);
        qrcodeReadButton.setVisibility(android.view.View.INVISIBLE);

        setInButton.setEnabled(true);

        //前画面から受け取った値をセット
        Intent intent = getIntent();
        int intLabelType =  intent.getIntExtra("LabelType",0);
        if (intLabelType == globals.LABEL_TYPE_PALLET){
            //パレット入庫の場合、入庫数量固定、今回入庫数を入力不可に設定
            numText.setText(globals.strC01_Vol);
            numText.setEnabled(false);
        }else{
            setInButton.setEnabled(false);
        }

        placeText.setText(globals.strPlace);
        locationText.setText(globals.strLocation);
        itemCodeText.setText(globals.strC01_Code.trim());
        itemNameText.setText(globals.strM04_Name);
        lotNoText.setText(globals.strC01_Lot_No.trim());
        volText.setText(globals.strC01_Vol);
        unitText.setText(globals.strM04_Unit);
        unitText2.setText(globals.strM04_Unit);

        //入庫確認ボタン押下イベント
        setInButton.setOnClickListener( v-> {
            if(isNumber(numText.getText().toString())) {
                new AlertDialog
                        .Builder(SetInActivity3.this)
                        .setTitle("入庫確認")
                        .setMessage("入庫してもよろしいですか？")
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //今回入庫数量のチェック
                                if (Integer.parseInt(numText.getText().toString()) <= 0){
                                    Context context = getApplicationContext();
                                    Toast.makeText(context , "今回入庫数量を入力して下さい。", Toast.LENGTH_SHORT).show();
                                }else{
                                    //DB更新処理を実行
                                    if (intLabelType == globals.LABEL_TYPE_PALLET){
                                        //パレット入庫の場合
                                        addInOutComp(numText.getText().toString());

                                    }else{
                                        //現品入庫の場合
                                        addInOutComp2(numText.getText().toString());

                                    }
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
            }else{
                Context context = getApplicationContext();
                Toast.makeText(context , "今回入庫数は、数値を入力して下さい。", Toast.LENGTH_SHORT).show();

            }

        });

        //戻るボタン押下イベント
        returnButton.setOnClickListener( v-> {
            //画面を閉じる
            finish();
        });
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
                    setInButton.setEnabled(false);
                }else{
                    if (Integer.parseInt(numText.getText().toString()) > 0){
                        setInButton.setEnabled(true);
                    }
                }
            }
        });
    }
    protected  boolean isNumber(String strVol){
        //正規表現
        try {
            Integer.parseInt(strVol);
            return true;
        } catch (NumberFormatException nfex) {
            return false;
        }
    }

    //パレット入庫処理
    protected void addInOutComp(String intVol){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //連想配列に入力値格納
                    HashMap<String, Object> jsonMap = new HashMap<>();
                    //
                    jsonMap.put("termName", globals.strModel);
                    jsonMap.put("version", globals.strAppVersion);
                    jsonMap.put("userName", globals.strUserName);
                    jsonMap.put("no",globals.strNo);
                    jsonMap.put("idx",globals.strIdx);
                    jsonMap.put("stockID",globals.strC01_Id);
                    jsonMap.put("palletID",globals.strC01_Pallet_ID);
                    jsonMap.put("itemCode",globals.strC01_Code);
                    jsonMap.put("lotNo",globals.strC01_Lot_No);
                    jsonMap.put("unit",globals.strM04_Unit);
                    jsonMap.put("vol",intVol);

                    //電文受送信
                    String response = globals.postAPI(globals.IPADRRESS+globals.URL_SETIN_ADD_COMP_PALLET, jsonMap);
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
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Context context = getApplicationContext();
                        if (intResult == 0){
                            //対象品目あり
                            Toast.makeText(context , "入庫完了", Toast.LENGTH_SHORT).show();
                            //次画面遷移
                            Intent intent = new Intent(SetInActivity3.this, SetInActivity.class);
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

    //現品入庫処理
    protected void addInOutComp2(String intVol){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //連想配列に入力値格納
                    HashMap<String, Object> jsonMap = new HashMap<>();
                    //
                    jsonMap.put("termName", globals.strModel);
                    jsonMap.put("version", globals.strAppVersion);
                    jsonMap.put("userName", globals.strUserName);
                    jsonMap.put("no",globals.strNo);
                    jsonMap.put("idx",globals.strIdx);
                    jsonMap.put("stockID",globals.strC01_Id);
                    jsonMap.put("palletID",globals.strC01_Pallet_ID);
                    jsonMap.put("itemCode",globals.strC01_Code);
                    jsonMap.put("lotNo",globals.strC01_Lot_No);
                    jsonMap.put("unit",globals.strM04_Unit);
                    jsonMap.put("vol",intVol);


                    //電文受送信
                    String response = globals.postAPI(globals.IPADRRESS+globals.URL_SETIN_ADD_COMP_GENPIN, jsonMap);
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
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Context context = getApplicationContext();
                        if (intResult == 0){
                            //対象品目あり
                            Toast.makeText(context , "入庫完了", Toast.LENGTH_SHORT).show();
                            //次画面遷移
                            Intent intent = new Intent(SetInActivity3.this, SetInActivity.class);
                            startActivity(intent);

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
