package jp.ksksue.tutorial.TWE_Control;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import jp.ksksue.driver.serial.FTDriver;


public class Title extends Activity {
    // [FTDriver] Permission String
    private static final String ACTION_USB_PERMISSION =
            "jp.ksksue.tutorial.USB_PERMISSION";
    Button mRC, mRS, btnBegin, btnEnd, mEzyDine;
    FTDriver mSerial;
    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // タイトルを非表示にします。
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // splash.xmlをViewに指定します。
        setContentView(R.layout.title);

        btnBegin = (Button) findViewById(R.id.btnBegin);
        btnEnd = (Button) findViewById(R.id.btnEnd);
        mRC = (Button) findViewById(R.id.RC);
        mRS = (Button) findViewById(R.id.RS);
        mEzyDine = (Button) findViewById(R.id.ezyDine);

        mText = (TextView) findViewById(R.id.textView1);

        mRC.setEnabled(false);
        mRS.setEnabled(false);
        mEzyDine.setEnabled(false);
        btnEnd.setEnabled(false);

        // [FTDriver] Create Instance
        mSerial = new FTDriver((UsbManager) getSystemService(Context.USB_SERVICE));

        // [FTDriver] setPermissionIntent() before begin()
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        mSerial.setPermissionIntent(permissionIntent);


        mRC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Title.this, TWE_Control.class);
                startActivity(intent);
            }
        });

        mRS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Title.this, ChartList.class);
                startActivity(intent);
            }
        });

        mEzyDine.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Title.this, EzyDine.class);
                startActivity(intent);
            }
        });
    }

    public void onBeginClick(View view) {
        // [FTDriver] Open USB Serial
        if (mSerial.begin(FTDriver.BAUD9600)) {
            mRC.setEnabled(true);
            mRS.setEnabled(true);
            mEzyDine.setEnabled(true);
            btnEnd.setEnabled(true);
            btnBegin.setEnabled(false);

            String wbuf = ":788001000F0000000000000000F8\r\n";
            mSerial.write(wbuf.getBytes());

            mText.setText("接続中");
            mText.setTextColor(Color.BLUE);
        } else {
            mText.setText("未接続");
            mText.setTextColor(Color.RED);
        }
    }

    public void onEndClick(View view) {

        mRC.setEnabled(false);
        mRS.setEnabled(false);
        btnEnd.setEnabled(false);
        btnBegin.setEnabled(true);

        String wbuf = ":788001000F0000000000000000F8\r\n";
        mSerial.write(wbuf.getBytes());
        mSerial.end();

        mText.setText("未接続");
        mText.setTextColor(Color.RED);

    }
}

		
	


