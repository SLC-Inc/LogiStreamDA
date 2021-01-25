package com.example.testlogistream;

import android.app.Application;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

public class Globals extends Application {
    //テスト用一時変数
    public String loginID = "";
    public String password = "";

    //ログイン情報
    public String strUserName = "";         //ユーザー名
    public String strModel = "";         //端末名
    public String strAppVersion = "";         //アンドロイドバージョン

    //入庫設定画面用変数
    public String strPlace = "";            //保管場所
    public String strLocation = "";         //ロケーション
    public String strNo = "";               //TrackNo
    public String strIdx = "";              //TrackIDX
    public String strC01_Id = "";           //在庫ID
    public String strC01_Code = "";         //品目コード
    public String strM04_Name = "";         //品目名
    public String strM04_Unit = "";         //単位
    public String strC01_Lot_No = "";       //ロットNo
    public String strC01_Vol = "";          //数量
    public String strM04_Case_Num_HP = "";  //標準入庫数量
    public String strC01_Pallet_ID = "";    //パレットID
    public String strStrageID = "";        //保管場所ID
    public String strC01_Type = "";
    public String strC01_Hl_Stat = "";
    public String strOrderID = "";         //オーダーピッキングID
    public String strpickSum = "";         //対象品目総数
    public String strpickCnt = "";         //対象品目総数
    public String strInvID="";             //棚卸しID

    //定数
    //public static String IPADRRESS = "http://192.168.10.16:5000";
    public static String IPADRRESS = "";
//    public static String URL_LOGIN = IPADRRESS + "/api/post/login";
//    public static String URL_SETIN_INTO_LOCATION = IPADRRESS + "/api/post/setin_into_location";
//    public static String URL_SETIN_GET_STOCK = IPADRRESS + "/api/post/setin_get_stock";
//    public static String URL_SETIN_GET_STOCK2 = IPADRRESS + "/api/post/setin_get_stock2";
//    public static String URL_SETIN_ADD_COMP_PALLET = IPADRRESS + "/api/post/setin_add_comp_pallet";
//    public static String URL_SETIN_ADD_COMP_GENPIN = IPADRRESS + "/api/post/setin_add_comp_genpin";
//    public static String URL_PICK_ORDER_CHK = IPADRRESS + "/api/post/pick_order_check";
//    public static String URL_PICK_PROCESS = IPADRRESS + "/api/post/pick_process";
//    public static String URL_PICK_PROCESS_COMP = IPADRRESS + "/api/post/pick_process_comp";
//    public static String URL_INVENTORY_START = IPADRRESS + "/api/post/inventory_start";
//    public static String URL_INVENTORY_PROCESS_GENPIN = IPADRRESS + "/api/post/inventory_process_genpin";
//    public static String URL_INVENTORY_PROCESS = IPADRRESS + "/api/post/inventory_process";
//    public static String URL_INVENTORY_PROCESS_COMP = IPADRRESS + "/api/post/inventory_process_comp";

    public static String URL_LOGIN = "/api/post/login";
    public static String URL_SETIN_INTO_LOCATION = "/api/post/setin_into_location";
    public static String URL_SETIN_GET_STOCK = "/api/post/setin_get_stock";
    public static String URL_SETIN_GET_STOCK2 = "/api/post/setin_get_stock2";
    public static String URL_SETIN_ADD_COMP_PALLET = "/api/post/setin_add_comp_pallet";
    public static String URL_SETIN_ADD_COMP_GENPIN = "/api/post/setin_add_comp_genpin";
    public static String URL_PICK_ORDER_CHK = "/api/post/pick_order_check";
    public static String URL_PICK_PROCESS = "/api/post/pick_process";
    public static String URL_PICK_PROCESS_COMP = "/api/post/pick_process_comp";
    public static String URL_INVENTORY_START = "/api/post/inventory_start";
    public static String URL_INVENTORY_PROCESS_GENPIN = "/api/post/inventory_process_genpin";
    public static String URL_INVENTORY_PROCESS = "/api/post/inventory_process";
    public static String URL_INVENTORY_PROCESS_COMP = "/api/post/inventory_process_comp";

    public static String QR_LOCATION ="XX";         //ロケーションラベル
    public static String QR_PALLET ="X2";           //パレットラベル
    public static String QR_GENPIN = "X1";          //現品ラベル
    public static String QR_PLIST = "P1";           //ピッキングリスト

    public static int LABEL_TYPE_PALLET = 1;
    public static int LABEL_TYPE_GENPIN = 2;

    public static String PLACE_NAME_JOUON = "常温平置";
    public static String PLACE_NAME_GENRYO = "原料倉庫";
    public static String PLACE_NAME_KANSEIHIN = "完成品倉庫";
    public static String PLACE_NAME_HONSYA2F = "本社 2F";
    public static String PLACE_NAME_HENPIN = "返品置場";

    public static String PLACE_CODE_JOUON = "6000";
    public static String PLACE_CODE_GENRYO = "8000";
    public static String PLACE_CODE_KANSEIHIN = "8100";
    public static String PLACE_CODE_HONSYA2F = "8001";
    public static String PLACE_CODE_HENPIN = "7000";

    /*POSTリクエスト送信
    * 引数1:接続URL
    * 引数2:送信データ（連想配列）
    * 戻り値：応答データ（JSON形式） */
    public String postAPI(String strURL, HashMap<String, Object> jsonMap){
        HttpURLConnection urlConnection = null;
        InputStream recvStream = null;
        OutputStream sendStream = null;
        String result = "";
        String str = "";

        try {
            //URL接続
            URL url = new URL(strURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(100000);
            urlConnection.setReadTimeout(100000);
            urlConnection.addRequestProperty("User-Agent", "Android");
            urlConnection.addRequestProperty("Accept-Language", Locale.getDefault().toString());
            urlConnection.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            //接続先にデータ送信
            sendStream = urlConnection.getOutputStream();

            //連想配列をJSON形式に変換
            JSONObject responseJsonObject = new JSONObject(jsonMap);
            String jsonText = responseJsonObject.toString();
            //送信データ書き込み
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(sendStream, "utf-8"));
            bufferedWriter.write(jsonText);
            bufferedWriter.flush();
            bufferedWriter.close();

            //通信ステータス取得
            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200){
                //通信成功
                //応答データの処理
                recvStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(recvStream));
                //JSONを文字列型に変換
                result = bufferedReader.readLine();

                while (result != null){
                    str += result;
                    //sbSentence.append(result);
                    result = bufferedReader.readLine();
                }
                bufferedReader.close();
            }
            //URL接続の切断
            urlConnection.disconnect();

        }catch (IOException e){
            e.printStackTrace();
        }
        return  str;
    }

}
