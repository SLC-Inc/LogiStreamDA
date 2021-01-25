package com.example.testlogistream;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class InventoryActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private Handler handler = new Handler();
    private Globals globals;
    private ToggleButton tb1, tb2,tb3,tb4,tb5,tb6;
    private Button startButton,returnButton;

    private int intResult = 1;
    private String strMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        //共通クラス取得
        globals = (Globals) this.getApplication();

        tb1 = findViewById(R.id.invButton1);
        tb2 = findViewById(R.id.invButton2);
        tb3 = findViewById(R.id.invButton3);
        tb4 = findViewById(R.id.invButton4);
        tb5 = findViewById(R.id.invButton5);
        tb6 = findViewById(R.id.invButton6);
        startButton = findViewById(R.id.invStartButton);
        returnButton = findViewById(R.id.invReturnButton);

        tb1.setText(globals.PLACE_NAME_JOUON);
        tb1.setTextOn(globals.PLACE_NAME_JOUON);
        tb1.setTextOff(globals.PLACE_NAME_JOUON);
        tb2.setText(globals.PLACE_NAME_GENRYO);
        tb2.setTextOn(globals.PLACE_NAME_GENRYO);
        tb2.setTextOff(globals.PLACE_NAME_GENRYO);
        tb3.setText(globals.PLACE_NAME_KANSEIHIN);
        tb3.setTextOn(globals.PLACE_NAME_KANSEIHIN);
        tb3.setTextOff(globals.PLACE_NAME_KANSEIHIN);
        tb4.setText("");
        tb4.setTextOn("");
        tb4.setTextOff("");
        tb5.setText("");
        tb5.setTextOn("");
        tb5.setTextOff("");

        //トグルボタンチェックリスナーの登録
        tb1.setOnCheckedChangeListener(this);
        tb2.setOnCheckedChangeListener(this);
        tb3.setOnCheckedChangeListener(this);
        tb4.setOnCheckedChangeListener(this);
        tb5.setOnCheckedChangeListener(this);
        tb6.setOnCheckedChangeListener(this);

        //作業開始ボタン押下イベント
        startButton.setOnClickListener( v-> {
            //トグルボタン押下チェック
            if (tb1.isChecked()){
                //トグルボタン1選択時
                //棚卸し作業開始処理
                if (startInventory(tb1.getTextOn().toString())){
                    //次画面遷移
                    nextActivity();

                }

            }else if (tb2.isChecked()){
                //トグルボタン2選択時
                //棚卸し作業開始処理
                if (startInventory(tb2.getTextOn().toString())){
                    //次画面遷移
//                    nextActivity();
                    //次画面遷移
                    Intent intent = new Intent(InventoryActivity.this,
                            InventoryStartActivity.class);
                    startActivity(intent);
                }

            }else if (tb3.isChecked()){
                //トグルボタン3選択時
                //棚卸し作業開始処理
                if (startInventory(tb3.getTextOn().toString())){
                    //次画面遷移
                    nextActivity();
                }

                  }else if (tb4.isChecked()){
                //トグルボタン4選択時
                //棚卸し作業開始処理
                 if (startInventory(tb4.getTextOn().toString())){
                     //次画面遷移
                     nextActivity();
                  }

                }else if (tb5.isChecked()){
                //トグルボタン5選択時
                //棚卸し作業開始処理
                    if (startInventory(tb5.getTextOn().toString())){
                        //次画面遷移
                        nextActivity();
                   }

                }else if (tb6.isChecked()){
                //トグルボタン6選択時
                //棚卸し作業開始処理
                     if (startInventory(tb6.getTextOn().toString())){
                         //次画面遷移
                         nextActivity();
                   }

            }else{
                Toast.makeText(this, "保管場所を選択してください。", Toast.LENGTH_SHORT).show();
            }
        });

        //戻るボタン押下イベント
        returnButton.setOnClickListener( v-> {
            //画面を閉じる
            finish();
        });
    }
    //トグルボタンチェックイベント
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        switch (buttonView.getId()){
            case R.id.invButton1:
                if (isChecked){
                    //トグルボタンをONにした場合
                    //他のボタンを非選択状態にする
                    tb2.setChecked(false);
                    tb3.setChecked(false);
                    tb4.setChecked(false);
                    tb5.setChecked(false);
                    tb6.setChecked(false);
                }
                break;
            case R.id.invButton2:
                if (isChecked) {
                    //トグルボタンをONにした場合
                    //他のボタンを非選択状態にする
                    tb1.setChecked(false);
                    tb3.setChecked(false);
                    tb4.setChecked(false);
                    tb5.setChecked(false);
                    tb6.setChecked(false);
                }
                break;
            case R.id.invButton3:
                if (isChecked) {
                    //トグルボタンをONにした場合
                    //他のボタンを非選択状態にする
                    tb1.setChecked(false);
                    tb2.setChecked(false);
                    tb4.setChecked(false);
                    tb5.setChecked(false);
                    tb6.setChecked(false);
                }
                break;
            case R.id.invButton4:
                if (isChecked) {
                    //トグルボタンをONにした場合
                    //他のボタンを非選択状態にする
                    tb1.setChecked(false);
                    tb2.setChecked(false);
                    tb3.setChecked(false);
                    tb5.setChecked(false);
                    tb6.setChecked(false);
                }
                break;
            case R.id.invButton5:
                if (isChecked) {
                    //トグルボタンをONにした場合
                    //他のボタンを非選択状態にする
                    tb1.setChecked(false);
                    tb2.setChecked(false);
                    tb3.setChecked(false);
                    tb4.setChecked(false);
                    tb6.setChecked(false);
                }
                break;
            case R.id.invButton6:
                if (isChecked) {
                    //トグルボタンをONにした場合
                    //他のボタンを非選択状態にする
                    tb1.setChecked(false);
                    tb2.setChecked(false);
                    tb3.setChecked(false);
                    tb4.setChecked(false);
                    tb5.setChecked(false);
                }
                break;
        }
    }
    //棚卸し作業開始処理
    private boolean startInventory(String strPlace){
        boolean bolRes = false;

        if (strPlace.equals(globals.PLACE_NAME_JOUON)){
            globals.strPlace = globals.PLACE_NAME_JOUON;
            globals.strStrageID = globals.PLACE_CODE_JOUON;
            bolRes = true;
        }else if (strPlace.equals(globals.PLACE_NAME_GENRYO)){
            globals.strPlace = globals.PLACE_NAME_GENRYO;
            globals.strStrageID = globals.PLACE_CODE_GENRYO;
            bolRes = true;
        }else if (strPlace.equals(globals.PLACE_NAME_KANSEIHIN)){
            globals.strPlace = globals.PLACE_NAME_KANSEIHIN;
            globals.strStrageID = globals.PLACE_CODE_KANSEIHIN;
            bolRes = true;
//        }else if (strPlace.equals(globals.PLACE_NAME_HONSYA2F)){
//            globals.strStrageID = globals.PLACE_CODE_HONSYA2F;
//        }else if (strPlace.equals(globals.PLACE_NAME_HENPIN)){
//            globals.strStrageID = globals.PLACE_CODE_HENPIN;
        }



        //作業開始になっているかチェック
        if(bolRes) {


        }else{
            Toast.makeText(this, "選択した作業場所には在庫がない為、棚卸し作業開始できません。",
                    Toast.LENGTH_SHORT).show();
        }


        return bolRes;
    }

    private void nextActivity(){
        //次画面遷移
        Intent intent = new Intent(InventoryActivity.this, InventoryStartActivity.class);
        startActivity(intent);

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