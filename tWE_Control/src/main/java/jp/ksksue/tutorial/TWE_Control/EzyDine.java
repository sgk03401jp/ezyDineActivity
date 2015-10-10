package jp.ksksue.tutorial.TWE_Control;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.ksksue.driver.serial.FTDriver;

/**
 * Created by sugimura on 2015/09/30.
 */
public class EzyDine extends Activity implements Runnable {
    // [FTDriver] Permission String
    private static final String ACTION_USB_PERMISSION =
            "jp.ksksue.tutorial.USB_PERMISSION";
    final int SERIAL_BAUDRATE = FTDriver.BAUD9600;
    final boolean SHOW_LOGCAT = true;
    String TAG = "EzyDine";
    // [FTDriver] Object
    FTDriver mSerial;
    Handler mHandler;
    Bitmap image_amber, image_black, image_green, image_red, image_white, image_blue, image_gray;
    CustomData item1, item2, item3, item4, item5, item6, item7, item8, item9, item10;
    TextView textView;
    private Thread mLooper;
    private boolean mStop = false;
    private boolean mRunningMainLoop = false;

    @Override
    public void run() {
        int i, len;
        double ondo1;

        // [FTDriver] Create Read Buffer
        byte[] rbuf = new byte[128]; // 1byte <--slow-- [Transfer Speed] --fast--> 4096 byte
        while (mLooper != null) {
            // [FTDriver] Read from USB Serial
            len = mSerial.read(rbuf);
            String str1 = new String(rbuf);
            //String new_str1 = str1.substring(43, 45);
            if (len > 0) {
                if (SHOW_LOGCAT) {
                    Log.i(TAG, "Read  Length : " + len + ",buf: " + str1);
                }
                //Message msg = new Message(); //非推奨
                Message msg = Message.obtain(); //推奨
                //Message msg = mHandler.obtainMessage(); //推奨
                msg.obj = str1;

                //ハンドラへのメッセージ送信
                mHandler.sendMessage(msg);

//                    //@todo mText1 = (String) mY.getText();
//                    try { //エラーが出そうな処理
//                        //String x;
//                        //int a;
//                        //x = new_str1.toLowerCase(Locale.ENGLISH);
//                        //a = Integer.parseInt(x, 16);
//                    for (i = 0; i < len; ++i) {
//                        if (SHOW_LOGCAT) {
//                            Log.i(TAG, "Read  Data[" + i + "] : " + rbuf[i]);
//                        }
//
//                            if (rbuf[i] == 0x0A) {
//                                if (!(new_str1.equals("FF"))) {
//                                    ondo1 = ((Integer.parseInt(new_str1, 16)) * 16);
//                                    ondo1 = (ondo1 / 10) - 60;
//                                    if (ondo1 > 0) {
//                                        //@todo mText1 = "" + String.format("%.1f", ondo1) + "\n";
//                                    }
//                                }
//
//                            }
//
//                        }
//                    } catch (NumberFormatException nfe) {
//
//                    }
//                mHandler.post(new Runnable() {
//                    public void run() {
////                            TextView dateText = (TextView) findViewById(R.id.date_id);
////                            Time time = new Time("Asia/Tokyo");
////                            time.setToNow();
////                            String date = time.year + "年" + (time.month + 1) + "月" + time.monthDay + "日　"
////                                    + time.hour + "時" + time.minute + "分" + time.second + "秒";
////                            dateText.setText(date);
//                        //mX.setText(String.valueOf(time.second));
//                        //@todo mY.setText(mText1);
//                    }
//                });
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
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ezydine);

        // [FTDriver] setPermissionIntent() before begin()
        mSerial = new FTDriver((UsbManager) getSystemService(Context.USB_SERVICE));

        // [FTDriver] setPermissionIntent() before begin()
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        mSerial.setPermissionIntent(permissionIntent);

        if (mSerial.begin(SERIAL_BAUDRATE)) {
         //   mainloop();
        }

        // リソースに準備した画像ファイルからBitmapを作成しておく
        image_amber = BitmapFactory.decodeResource(getResources(), R.drawable.amber_32);
        image_black = BitmapFactory.decodeResource(getResources(), R.drawable.black_32);
        image_green = BitmapFactory.decodeResource(getResources(), R.drawable.green_32);
        image_red = BitmapFactory.decodeResource(getResources(), R.drawable.red_32);
        image_white = BitmapFactory.decodeResource(getResources(), R.drawable.white_32);
        image_blue = BitmapFactory.decodeResource(getResources(), R.drawable.blue_32);
        image_gray = BitmapFactory.decodeResource(getResources(), R.drawable.gray_32);

        // データの作成
        List<CustomData> objects = new ArrayList<CustomData>();

        item1 = new CustomData();
        item2 = new CustomData();
        item3 = new CustomData();
        item4 = new CustomData();
        item5 = new CustomData();
        item6 = new CustomData();
        item7 = new CustomData();
        item8 = new CustomData();
        item9 = new CustomData();
        item10 = new CustomData();

        initView();
        objects.add(item1);
        objects.add(item2);
        objects.add(item3);
        objects.add(item4);
        objects.add(item5);
        objects.add(item6);
        objects.add(item7);
        objects.add(item8);
        objects.add(item9);
        objects.add(item10);

        item1.setImagaData(image_green);
        item9.setImagaData(image_blue);
        item1.setTextColor(true);
        item10.setImagaData(image_red);

        CustomAdapter customAdapater = new CustomAdapter(this, 0, objects);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(customAdapater);

        //リスト項目がクリックされた時の処理
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                Log.d(TAG, "onItemClick: " + position);
//                String item = (String) listView.getItemAtPosition(position);
//                Toast.makeText(getApplicationContext(), item + " clicked",
//                        Toast.LENGTH_LONG).show();
            }
        });

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //リスト項目が選択された時の処理
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                Log.d(TAG, "onItemSelected: " + position);
//                String item = (String) listView.getItemAtPosition(position);
//                Toast.makeText(getApplicationContext(), item + " selected",
//                        Toast.LENGTH_LONG).show();
            }

            //リスト項目がなにも選択されていない時の処理
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected");
//                Toast.makeText(getApplicationContext(), "no item selected",
//                        Toast.LENGTH_LONG).show();
            }
        });

        //リスト項目が長押しされた時の処理
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                Log.d(TAG, "onItemLongClick: " + position);
//                String item = (String) listView.getItemAtPosition(position);
//                Toast.makeText(getApplicationContext(), item + " long clicked",
//                        Toast.LENGTH_LONG).show();
                return false;
            }
        });
        mHandler = new Handler() {
            //メッセージ受信
            public void handleMessage(Message message) {
                //メッセージの表示
                textView = (TextView) findViewById(R.id.textView);
                textView.setText((String) message.obj);
                Log.d(TAG, "mag: " + (String) message.obj);
                //メッセージの種類に応じてswitch文で制御すれば
                //イベント制御に利用できます
            }

            ;
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mLooper = new Thread(this);
        //スレッド処理を開始
        if (mLooper != null) {
            mLooper.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //スレッドを削除
        mLooper = null;
    }

    private void initView() {
        // デフォルトのCalendarオブジェクト
        Calendar cal = Calendar.getInstance();
        // Calendarクラスによる現在時表示
        String dateTime = cal.get(Calendar.YEAR) + "/"
                + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE)
                + " " + cal.get(Calendar.HOUR_OF_DAY) + ":"
                + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);

        item1.setImagaData(image_white);
        item1.setTextData("000001");
        item1.setTextTime(dateTime);
        item1.setTextColor(false);

        item2.setImagaData(image_white);
        item2.setTextData("000002");
        item2.setTextTime(dateTime);
        item2.setTextColor(false);

        item3.setImagaData(image_white);
        item3.setTextData("000003");
        item3.setTextTime(dateTime);
        item3.setTextColor(false);

        item4.setImagaData(image_white);
        item4.setTextData("000004");
        item4.setTextTime(dateTime);
        item4.setTextColor(false);

        item5.setImagaData(image_white);
        item5.setTextData("000005");
        item5.setTextTime(dateTime);
        item5.setTextColor(false);

        item6.setImagaData(image_white);
        item6.setTextData("000006");
        item6.setTextTime(dateTime);
        item6.setTextColor(false);

        item7.setImagaData(image_white);
        item7.setTextData("000007");
        item7.setTextTime(dateTime);
        item7.setTextColor(false);

        item8.setImagaData(image_white);
        item8.setTextData("000008");
        item8.setTextTime(dateTime);
        item8.setTextColor(false);

        item9.setImagaData(image_white);
        item9.setTextData("000010");
        item9.setTextTime(dateTime);
        item9.setTextColor(false);

        item10.setImagaData(image_white);
        item10.setTextData("999999");
        item10.setTextTime(dateTime);
        item10.setTextColor(false);


        item1.setImagaData(image_green);
        item9.setImagaData(image_blue);
        item1.setTextColor(true);
        item10.setImagaData(image_red);

    }
}

