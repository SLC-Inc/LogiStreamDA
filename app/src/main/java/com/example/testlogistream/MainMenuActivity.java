package com.example.testlogistream;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

public class MainMenuActivity extends AppCompatActivity {
    private Button setInButton,pickButton,inventoryButton,returnButton;
    private TextView mainMenuMsgTextView;

    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setInButton = findViewById(R.id.setInBtn);
        pickButton = findViewById(R.id.pickBtn);
        inventoryButton = findViewById(R.id.inventoryBtn);
        returnButton = findViewById(R.id.returnBtn);
        mainMenuMsgTextView = findViewById(R.id.mainMenuMsgText);

        //入庫設定ボタン押下イベント
        setInButton.setOnClickListener( v-> {
            //インスタンス生成
            Intent setIn_intent = new Intent(MainMenuActivity.this, SetInActivity.class);
            //次画面遷移
            startActivity(setIn_intent);
        });

        //ピッキング指示ボタン押下イベント
        pickButton.setOnClickListener( v-> {
            //インスタンス生成
            Intent setIn_intent = new Intent(MainMenuActivity.this, PickActivity.class);
            //次画面遷移
            startActivity(setIn_intent);
        });

        //棚卸し設定ボタン押下イベント
        inventoryButton.setOnClickListener( v-> {
            //インスタンス生成
            Intent setIn_intent = new Intent(MainMenuActivity.this, InventoryActivity.class);
            //次画面遷移
            startActivity(setIn_intent);
        });

        //戻るボタン押下イベント
//        returnButton.setOnClickListener( v-> {
//            //画面を閉じる
//            finish();
//        });
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