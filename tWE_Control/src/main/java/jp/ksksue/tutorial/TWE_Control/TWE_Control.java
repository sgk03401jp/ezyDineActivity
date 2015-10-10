/*
 * TWE Control
 * 2013-11-25
 * TOKYO COSMOS CO.,LTD.
 * 
 */

package jp.ksksue.tutorial.TWE_Control;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import jp.ksksue.driver.serial.FTDriver;


public class TWE_Control extends Activity {

    // [FTDriver] Permission String
    private static final String ACTION_USB_PERMISSION =
            "jp.ksksue.tutorial.USB_PERMISSION";
    final int mOutputType = 0;
    final boolean SHOW_LOGCAT = false;
    final int SERIAL_BAUDRATE = FTDriver.BAUD115200;
    // [FTDriver] Object
    FTDriver mSerial;
    String TAG = "FTSampleTerminal";
    Handler mHandler = new Handler();
    SeekBar seekBar1, seekBar2, seekBar3, seekBar4;
    TextView tv1, tv2, tv3, tv4;
    Button LED1_ON, LED1_OFF, LED2_ON, LED2_OFF, LED3_ON, LED3_OFF, LED4_ON, LED4_OFF, btnEnd, LEDALL_ON, LEDALL_OFF;
    TextView tvMonitor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twe_control);

        // [FTDriver] setPermissionIntent() before begin()
        mSerial = new FTDriver((UsbManager) getSystemService(Context.USB_SERVICE));

        // [FTDriver] setPermissionIntent() before begin()
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        mSerial.setPermissionIntent(permissionIntent);

        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
        seekBar3 = (SeekBar) findViewById(R.id.seekBar3);
        seekBar4 = (SeekBar) findViewById(R.id.seekBar4);
        tv1 = (TextView) findViewById(R.id.textView3);
        tv2 = (TextView) findViewById(R.id.textView4);
        tv3 = (TextView) findViewById(R.id.textView5);
        tv4 = (TextView) findViewById(R.id.textView6);

        LED1_ON = (Button) findViewById(R.id.LED1_ON);
        LED1_OFF = (Button) findViewById(R.id.LED1_OFF);
        LED2_ON = (Button) findViewById(R.id.LED2_ON);
        LED2_OFF = (Button) findViewById(R.id.LED2_OFF);
        LED3_ON = (Button) findViewById(R.id.LED3_ON);
        LED3_OFF = (Button) findViewById(R.id.LED3_OFF);
        LED4_ON = (Button) findViewById(R.id.LED4_ON);
        LED4_OFF = (Button) findViewById(R.id.LED4_OFF);
        btnEnd = (Button) findViewById(R.id.btnEnd);
        LEDALL_ON = (Button) findViewById(R.id.LEDALL_ON);
        LEDALL_OFF = (Button) findViewById(R.id.LEDALL_OFF);

        LED1_ON.setEnabled(false);
        LED1_OFF.setEnabled(false);
        LED2_ON.setEnabled(false);
        LED2_OFF.setEnabled(false);
        LED3_ON.setEnabled(false);
        LED3_OFF.setEnabled(false);
        LED4_ON.setEnabled(false);
        LED4_OFF.setEnabled(false);
        LEDALL_ON.setEnabled(false);
        LEDALL_OFF.setEnabled(false);

        seekBar1.setEnabled(false);
        seekBar2.setEnabled(false);
        seekBar3.setEnabled(false);
        seekBar4.setEnabled(false);

        tv1.setText("PWM1:" + seekBar1.getProgress());
        tv2.setText("PWM2:" + seekBar2.getProgress());
        tv3.setText("PWM3:" + seekBar3.getProgress());
        tv4.setText("PWM4:" + seekBar4.getProgress());


        if (mSerial.begin(SERIAL_BAUDRATE)) {
            Toast.makeText(this, "接続しました", Toast.LENGTH_SHORT).show();
            LED1_ON.setEnabled(true);
            LED1_OFF.setEnabled(true);
            LED2_ON.setEnabled(true);
            LED2_OFF.setEnabled(true);
            LED3_ON.setEnabled(true);
            LED3_OFF.setEnabled(true);
            LED4_ON.setEnabled(true);
            LED4_OFF.setEnabled(true);
            LEDALL_ON.setEnabled(true);
            LEDALL_OFF.setEnabled(true);

            seekBar1.setEnabled(true);
            seekBar2.setEnabled(true);
            seekBar3.setEnabled(true);
            seekBar4.setEnabled(true);
        } else {
            Toast.makeText(this, "接続できません", Toast.LENGTH_SHORT).show();
        }

        seekBar1.setOnSeekBarChangeListener(
                new OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {

                        // ツマミをドラッグしたときに呼ばれる
                        tv1.setText("PWM1:" + progress);

                        int a1, a2, a3, a4, a5, a6, a7, a8, a9, x, z, z2, z3, z4, z5;
                        String hex, hex2, hex3, hex4;
                        a1 = 120; //78
                        a2 = 128; //80
                        a3 = 1; //01
                        a4 = 255;
                        a5 = 255;
                        a6 = 255;
                        a7 = 255;
                        a8 = 255;
                        a9 = 255;

                        hex4 = Integer.toHexString(progress);
                        String PR = hex4.toUpperCase(Locale.ENGLISH);
                        z4 = PR.length();

                        if (z4 == 1) {
                            PR = "000" + PR;
                        }
                        if (z4 == 2) {
                            PR = "00" + PR;
                        }
                        if (z4 == 3) {
                            PR = "0" + PR;
                        }
                        String new_PR1 = PR.substring(0, 2);
                        String new_PR2 = PR.substring(2, 4);
                        int PR1 = Integer.parseInt(new_PR1, 16);
                        int PR2 = Integer.parseInt(new_PR2, 16);

                        x = a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9 + PR1 + PR2;
                        hex = Integer.toHexString(x);
                        hex2 = hex.substring(hex.length() - 2);
                        z = Integer.parseInt(hex2, 16);
                        hex3 = Integer.toHexString(~z);
                        String rrr = hex3.substring(hex3.length() - 2);
                        z2 = Integer.parseInt(rrr, 16);
                        z3 = z2 + 1;
                        String jjj = Integer.toHexString(z3);
                        String xxx = jjj.toUpperCase(Locale.ENGLISH);

                        z5 = xxx.length();
                        if (z5 == 1) {
                            xxx = "0" + xxx;
                        }

                        String wbuf = (":7880010000" + PR + "FFFFFFFFFFFF" + xxx + "\r\n");
                        mSerial.write(wbuf.getBytes());

                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );

        seekBar2.setOnSeekBarChangeListener(
                new OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {
                        tv2.setText("PWM2:" + progress);

                        int a1, a2, a3, a4, a5, a6, a7, a8, a9, x, z, z2, z3, z4, z5;
                        String hex, hex2, hex3, hex4;
                        a1 = 120; //78
                        a2 = 128; //80
                        a3 = 1; //01
                        a4 = 255;
                        a5 = 255;
                        a6 = 255;
                        a7 = 255;
                        a8 = 255;
                        a9 = 255;

                        hex4 = Integer.toHexString(progress);
                        String PR = hex4.toUpperCase(Locale.ENGLISH);
                        z4 = PR.length();

                        if (z4 == 1) {
                            PR = "000" + PR;
                        }
                        if (z4 == 2) {
                            PR = "00" + PR;
                        }
                        if (z4 == 3) {
                            PR = "0" + PR;
                        }
                        String new_PR1 = PR.substring(0, 2);
                        String new_PR2 = PR.substring(2, 4);
                        int PR1 = Integer.parseInt(new_PR1, 16);
                        int PR2 = Integer.parseInt(new_PR2, 16);

                        x = a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9 + PR1 + PR2;
                        hex = Integer.toHexString(x);
                        hex2 = hex.substring(hex.length() - 2);
                        z = Integer.parseInt(hex2, 16);
                        hex3 = Integer.toHexString(~z);
                        String rrr = hex3.substring(hex3.length() - 2);
                        z2 = Integer.parseInt(rrr, 16);
                        z3 = z2 + 1;
                        String jjj = Integer.toHexString(z3);
                        String xxx = jjj.toUpperCase(Locale.ENGLISH);

                        z5 = xxx.length();
                        if (z5 == 1) {
                            xxx = "0" + xxx;
                        }

                        String wbuf = (":7880010000FFFF" + PR + "FFFFFFFF" + xxx + "\r\n");
                        mSerial.write(wbuf.getBytes());

                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );

        seekBar3.setOnSeekBarChangeListener(
                new OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {
                        tv3.setText("PWM3:" + progress);

                        int a1, a2, a3, a4, a5, a6, a7, a8, a9, x, z, z2, z3, z4, z5;
                        String hex, hex2, hex3, hex4;
                        a1 = 120; //78
                        a2 = 128; //80
                        a3 = 1; //01
                        a4 = 255;
                        a5 = 255;
                        a6 = 255;
                        a7 = 255;
                        a8 = 255;
                        a9 = 255;

                        hex4 = Integer.toHexString(progress);
                        String PR = hex4.toUpperCase(Locale.ENGLISH);
                        z4 = PR.length();

                        if (z4 == 1) {
                            PR = "000" + PR;
                        }
                        if (z4 == 2) {
                            PR = "00" + PR;
                        }
                        if (z4 == 3) {
                            PR = "0" + PR;
                        }
                        String new_PR1 = PR.substring(0, 2);
                        String new_PR2 = PR.substring(2, 4);
                        int PR1 = Integer.parseInt(new_PR1, 16);
                        int PR2 = Integer.parseInt(new_PR2, 16);

                        x = a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9 + PR1 + PR2;
                        hex = Integer.toHexString(x);
                        hex2 = hex.substring(hex.length() - 2);
                        z = Integer.parseInt(hex2, 16);
                        hex3 = Integer.toHexString(~z);
                        String rrr = hex3.substring(hex3.length() - 2);
                        z2 = Integer.parseInt(rrr, 16);
                        z3 = z2 + 1;
                        String jjj = Integer.toHexString(z3);
                        String xxx = jjj.toUpperCase(Locale.ENGLISH);

                        z5 = xxx.length();
                        if (z5 == 1) {
                            xxx = "0" + xxx;
                        }

                        String wbuf = (":7880010000FFFFFFFF" + PR + "FFFF" + xxx + "\r\n");
                        mSerial.write(wbuf.getBytes());

                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );

        seekBar4.setOnSeekBarChangeListener(
                new OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {
                        tv4.setText("PWM4:" + progress);

                        int a1, a2, a3, a4, a5, a6, a7, a8, a9, x, z, z2, z3, z4, z5;
                        String hex, hex2, hex3, hex4;
                        a1 = 120; //78
                        a2 = 128; //80
                        a3 = 1; //01
                        a4 = 255;
                        a5 = 255;
                        a6 = 255;
                        a7 = 255;
                        a8 = 255;
                        a9 = 255;

                        hex4 = Integer.toHexString(progress);
                        String PR = hex4.toUpperCase(Locale.ENGLISH);
                        z4 = PR.length();

                        if (z4 == 1) {
                            PR = "000" + PR;
                        }
                        if (z4 == 2) {
                            PR = "00" + PR;
                        }
                        if (z4 == 3) {
                            PR = "0" + PR;
                        }
                        String new_PR1 = PR.substring(0, 2);
                        String new_PR2 = PR.substring(2, 4);
                        int PR1 = Integer.parseInt(new_PR1, 16);
                        int PR2 = Integer.parseInt(new_PR2, 16);

                        x = a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9 + PR1 + PR2;
                        hex = Integer.toHexString(x);
                        hex2 = hex.substring(hex.length() - 2);
                        z = Integer.parseInt(hex2, 16);
                        hex3 = Integer.toHexString(~z);
                        String rrr = hex3.substring(hex3.length() - 2);
                        z2 = Integer.parseInt(rrr, 16);
                        z3 = z2 + 1;
                        String jjj = Integer.toHexString(z3);
                        String xxx = jjj.toUpperCase(Locale.ENGLISH);

                        z5 = xxx.length();
                        if (z5 == 1) {
                            xxx = "0" + xxx;
                        }

                        String wbuf = (":7880010000FFFFFFFFFFFF" + PR + xxx + "\r\n");
                        mSerial.write(wbuf.getBytes());

                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );


    }

    /*    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.activity_ftdriver_tutorial1, menu);
            return true;
        }
      */
    @Override
    public void onDestroy() {
        super.onDestroy();

        // [FTDriver] Close USB Serial
        mSerial.end();
    }


    public void onLED1_ONClick(View view) {

        String wbuf = ":7880010101FFFFFFFFFFFFFFFF0D\r\n";

        // [FTDriver] Wirte to USB Serial
        mSerial.write(wbuf.getBytes());


    }

    public void onLED1_OFFClick(View view) {

        String wbuf = ":7880010001FFFFFFFFFFFFFFFF0E\r\n";
        mSerial.write(wbuf.getBytes());


    }

    public void onLED2_ONClick(View view) {

        String wbuf = ":7880010202FFFFFFFFFFFFFFFF0B\r\n";
        mSerial.write(wbuf.getBytes());


    }

    public void onLED2_OFFClick(View view) {

        String wbuf = ":7880010002FFFFFFFFFFFFFFFF0D\r\n";
        mSerial.write(wbuf.getBytes());

    }

    public void onLED3_ONClick(View view) {

        String wbuf = ":7880010404FFFFFFFFFFFFFFFF07\r\n";
        mSerial.write(wbuf.getBytes());

    }

    public void onLED3_OFFClick(View view) {

        String wbuf = ":7880010004FFFFFFFFFFFFFFFF0B\r\n";
        mSerial.write(wbuf.getBytes());

    }

    public void onLED4_ONClick(View view) {

        String wbuf = ":7880010808FFFFFFFFFFFFFFFFFF\r\n";
        mSerial.write(wbuf.getBytes());

    }

    public void onLED4_OFFClick(View view) {

        String wbuf = ":7880010008FFFFFFFFFFFFFFFF07\r\n";
        mSerial.write(wbuf.getBytes());


    }

    public void LEDALL_ONClick(View view) {

        String wbuf = ":7880010F0F0400040004000400D9\r\n";
        mSerial.write(wbuf.getBytes());


    }

    public void LEDALL_OFFClick(View view) {

        String wbuf = ":788001000F0000000000000000F8\r\n";
        mSerial.write(wbuf.getBytes());

    }

}
