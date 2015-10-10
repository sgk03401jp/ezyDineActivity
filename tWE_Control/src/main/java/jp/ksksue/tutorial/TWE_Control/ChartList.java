package jp.ksksue.tutorial.TWE_Control;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import jp.ksksue.driver.serial.FTDriver;


public class ChartList extends Activity {

    private static final String ACTION_USB_PERMISSION =
            "jp.ksksue.tutorial.USB_PERMISSION";
    final int mOutputType = 0;
    final boolean SHOW_LOGCAT = false;
    final int SERIAL_BAUDRATE = FTDriver.BAUD115200;
    FTDriver mSerial;
    String TAG = "TWE_Line";
    Handler mHandler = new Handler();
    private boolean mRunningMainLoop = false;
    private TextView mTvSerial1, mTvSerial2, mTvSerial3, mTvSerial4, mTvSerial5, mTvSerial6, mTvSerial7;
    private String mText1, mText2, mText3, mText4, mText5, mText6, mText7;
    private boolean mStop = false;
    private Runnable mLoop = new Runnable() {
        @Override
        public void run() {
            int i, len;
            double ondo1, ondo2, ondo3, ondo4;

            // [FTDriver] Create Read Buffer
            byte[] rbuf = new byte[4096]; // 1byte <--slow-- [Transfer Speed] --fast--> 4096 byte
            for (; ; ) {
                // [FTDriver] Read from USB Serial
                len = mSerial.read(rbuf);
                String str1 = new String(rbuf);

                String new_str1 = str1.substring(9, 11);
                String new_str2 = str1.substring(27, 31);
                String new_str3 = str1.substring(33, 35);
                String new_str4 = str1.substring(37, 39);
                String new_str5 = str1.substring(39, 41);
                String new_str6 = str1.substring(41, 43);
                String new_str7 = str1.substring(43, 45);


                if (len > 0) {
                    if (SHOW_LOGCAT) {
                        Log.i(TAG, "Read  Length : " + len);
                    }
                    mText1 = (String) mTvSerial1.getText();
                    mText2 = (String) mTvSerial2.getText();
                    mText3 = (String) mTvSerial3.getText();
                    mText4 = (String) mTvSerial4.getText();
                    mText5 = (String) mTvSerial5.getText();
                    mText6 = (String) mTvSerial6.getText();
                    mText7 = (String) mTvSerial7.getText();

                    try { //エラーが出そうな処理を記述
                        String x, y;
                        int a, b;


                        x = new_str1.toLowerCase(Locale.ENGLISH);
                        y = new_str2.toLowerCase(Locale.ENGLISH);

                        a = Integer.parseInt(x, 16);
                        b = Integer.parseInt(y, 16);

                        for (i = 0; i < len; ++i) {
                            if (SHOW_LOGCAT) {
                                Log.i(TAG, "Read  Data[" + i + "] : " + rbuf[i]);
                            }


                            if (rbuf[i] == 0x0A) {
                                if (a > 0 && a < 255) {
                                    mText1 = "" + String.valueOf(a) + "\n";
                                }
                                if (b > 0) {
                                    mText2 = "" + String.valueOf(b) + "\n";
                                }
                                mText3 = "" + new_str3 + "\n";

                                //mText4 = "" +new_str4 +"\n";
                                if (!(new_str4.equals("FF"))) {
                                    ondo1 = ((Integer.parseInt(new_str4, 16)) * 16);
                                    ondo1 = (ondo1 / 10) - 60;
                                    mText4 = "" + String.format("%.1f", ondo1) + "℃\n";
                                }

                                //mText5 = "" +new_str5 +"\n";
                                if (!(new_str5.equals("FF"))) {
                                    ondo2 = ((Integer.parseInt(new_str5, 16)) * 16);
                                    ondo2 = (ondo2 / 10) - 60;
                                    //mText5 = "" +ondo1 +"\n";
                                    mText5 = "" + String.format("%.1f", ondo2) + "℃\n";
                                }

                                //mText6 = "" +new_str6 +"\n";
                                if (!(new_str6.equals("FF"))) {
                                    ondo3 = ((Integer.parseInt(new_str6, 16)) * 16);
                                    ondo3 = (ondo3 / 10) - 60;
                                    mText6 = "" + String.format("%.1f", ondo3) + "℃\n";
                                }

                                //mText7 = "" +new_str7 +"\n";
                                if (!(new_str7.equals("FF"))) {
                                    ondo4 = ((Integer.parseInt(new_str7, 16)) * 16);
                                    ondo4 = (ondo4 / 10) - 60;
                                    //mText4 = "" +ondo1 +"\n";
                                    mText7 = "" + String.format("%.1f", ondo4) + "℃\n";
                                }

                            }

                        }

                    } catch (NumberFormatException nfe) {
                        //エラーが出た時の処理を記述
                    }

                    mHandler.post(new Runnable() {
                        public void run() {
                            mTvSerial1.setText(mText1);
                            mTvSerial2.setText(mText2);
                            mTvSerial3.setText(mText3);
                            mTvSerial4.setText(mText4);
                            mTvSerial5.setText(mText5);
                            mTvSerial6.setText(mText6);
                            mTvSerial7.setText(mText7);
                        }
                    });
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (mStop) {
                    mRunningMainLoop = false;
                    return;
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twe_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.set);

        mTvSerial1 = (TextView) findViewById(R.id.tvSerial1);
        mTvSerial2 = (TextView) findViewById(R.id.tvSerial2);
        mTvSerial3 = (TextView) findViewById(R.id.tvSerial3);
        mTvSerial4 = (TextView) findViewById(R.id.tvSerial4);
        mTvSerial5 = (TextView) findViewById(R.id.tvSerial5);
        mTvSerial6 = (TextView) findViewById(R.id.tvSerial6);
        mTvSerial7 = (TextView) findViewById(R.id.tvSerial7);

        mSerial = new FTDriver((UsbManager) getSystemService(Context.USB_SERVICE));

        // [FTDriver] setPermissionIntent() before begin()
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        mSerial.setPermissionIntent(permissionIntent);

        if (mSerial.begin(SERIAL_BAUDRATE)) {
            mainloop();
            Toast.makeText(this, "接続しました", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "接続できません", Toast.LENGTH_SHORT).show();
        }


        // アイテムを追加します
        adapter.add("電圧グラフ");
        adapter.add("LQIグラフ");
        adapter.add("温度グラフ1(LM61BIZ対応)");
        adapter.add("温度グラフ2(LM61BIZ対応)");
        adapter.add("温度グラフ3(LM61BIZ対応)");
        adapter.add("温度グラフ4(LM61BIZ対応)");
        ListView listView = (ListView) findViewById(R.id.listview);
        // アダプターを設定します
        listView.setAdapter(adapter);
        // リストビューのアイテムがクリックされた時に呼び出されるコールバックリスナーを登録します
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ListView listView = (ListView) parent;
                // クリックされたアイテムを取得します
                String item = (String) listView.getItemAtPosition(position);
                if (item == "電圧グラフ") {
                    Intent intent = new Intent(ChartList.this, Voltage.class);
                    startActivity(intent);
                }
                if (item == "LQIグラフ") {
                    Intent intent = new Intent(ChartList.this, LQI.class);
                    startActivity(intent);
                }
                if (item == "温度グラフ1(LM61BIZ対応)") {
                    Intent intent = new Intent(ChartList.this, Temp01.class);
                    startActivity(intent);
                }
                if (item == "温度グラフ2(LM61BIZ対応)") {
                    Intent intent = new Intent(ChartList.this, Temp02.class);
                    startActivity(intent);
                }
                if (item == "温度グラフ3(LM61BIZ対応)") {
                    Intent intent = new Intent(ChartList.this, Temp03.class);
                    startActivity(intent);
                }
                if (item == "温度グラフ4(LM61BIZ対応)") {
                    Intent intent = new Intent(ChartList.this, Temp04.class);
                    startActivity(intent);
                }
            }

        });
        // リストビューのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                ListView listView = (ListView) parent;
                // 選択されたアイテムを取得します
                String item = (String) listView.getSelectedItem();
                Toast.makeText(ChartList.this, item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void mainloop() {

        mRunningMainLoop = true;
        new Thread(mLoop).start();
    }


}

