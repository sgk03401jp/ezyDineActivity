package jp.ksksue.tutorial.TWE_Control;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import jp.ksksue.driver.serial.FTDriver;


public class Temp01 extends Activity {
    private static final String ACTION_USB_PERMISSION =
            "jp.ksksue.tutorial.USB_PERMISSION";
    final int mOutputType = 0;
    final boolean SHOW_LOGCAT = false;
    final int SERIAL_BAUDRATE = FTDriver.BAUD115200;
    public int currentTime;
    TextView mX, mY, dateText;
    FTDriver mSerial;
    Handler mHandler = new Handler();
    String TAG = "TWE_Line";
    EditText xValue, yValue;
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private XYSeries mCurrentSeries;
    private XYSeriesRenderer mCurrentRenderer;
    private GraphicalView mChartView;
    private String mText1;
    private boolean mStop = false;
    private boolean mRunningMainLoop = false;
    private Runnable mLoop = new Runnable() {
        @Override
        public void run() {

            int i, len;
            double ondo1;

            // [FTDriver] Create Read Buffer
            byte[] rbuf = new byte[4096]; // 1byte <--slow-- [Transfer Speed] --fast--> 4096 byte
            for (; ; ) {

                // [FTDriver] Read from USB Serial
                len = mSerial.read(rbuf);
                String str1 = new String(rbuf);

                String new_str1 = str1.substring(37, 39);


                if (len > 0) {
                    if (SHOW_LOGCAT) {
                        Log.i(TAG, "Read  Length : " + len);
                    }
                    mText1 = (String) mY.getText();


                    try { //エラーが出そうな処理
                        //String x;
                        //int a;

                        //x = new_str1.toLowerCase(Locale.ENGLISH);
                        //a = Integer.parseInt(x, 16);


                        for (i = 0; i < len; ++i) {
                            if (SHOW_LOGCAT) {
                                Log.i(TAG, "Read  Data[" + i + "] : " + rbuf[i]);
                            }


                            if (rbuf[i] == 0x0A) {
                                if (!(new_str1.equals("FF"))) {
                                    ondo1 = ((Integer.parseInt(new_str1, 16)) * 16);
                                    ondo1 = (ondo1 / 10) - 60;
                                    if (ondo1 > 0) {
                                        mText1 = "" + String.format("%.1f", ondo1) + "\n";
                                    }
                                }

                            }

                        }

                    } catch (NumberFormatException nfe) {

                    }


                    mHandler.post(new Runnable() {
                        public void run() {
                            TextView dateText = (TextView) findViewById(R.id.date_id);
                            Time time = new Time("Asia/Tokyo");
                            time.setToNow();
                            String date = time.year + "年" + (time.month + 1) + "月" + time.monthDay + "日　"
                                    + time.hour + "時" + time.minute + "分" + time.second + "秒";
                            dateText.setText(date);
                            //mX.setText(String.valueOf(time.second));
                            mY.setText(mText1);

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
    private Handler myHandler;
    private Handler pHandler;
    private Runnable myTask;
    private Runnable pTask;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save the current data, for instance when changing screen orientation
        outState.putSerializable("dataset", mDataset);
        outState.putSerializable("renderer", mRenderer);
        outState.putSerializable("current_series", mCurrentSeries);
        outState.putSerializable("current_renderer", mCurrentRenderer);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        // restore the current data, for instance when changing the screen
        // orientation
        mDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
        mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");
        mCurrentSeries = (XYSeries) savedState.getSerializable("current_series");
        mCurrentRenderer = (XYSeriesRenderer) savedState.getSerializable("current_renderer");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp01);

        //カウントアップ開始
        cancelTimer();
        pTimer();
        myTask = new MyTimerTask();
        myHandler = new Handler();
        myHandler.postDelayed(myTask, 1000);

        pTask = new PaintTask();
        pHandler = new Handler();
        pHandler.postDelayed(pTask, 1000);


        currentTime = 0;
        mSerial = new FTDriver((UsbManager) getSystemService(Context.USB_SERVICE));

        // [FTDriver] setPermissionIntent() before begin()
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        mSerial.setPermissionIntent(permissionIntent);

        if (mSerial.begin(SERIAL_BAUDRATE)) {
            mainloop();
        }


        // the top part of the UI components for adding new data points
        mX = (TextView) findViewById(R.id.xValue);
        mY = (TextView) findViewById(R.id.yValue);


        //mAdd.setOnClickListener(new View.OnClickListener() {


        // グラフの各種設定

        mRenderer.setChartTitle("温度変位");
        mRenderer.setXTitle("経過時間[s]");
        mRenderer.setYTitle("温度[℃]");
        mRenderer.setAxisTitleTextSize(16);
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setXLabels(10); // X軸ラベルの数
        mRenderer.setYLabels(10); // Y軸ラベルの数
        mRenderer.setLabelsTextSize(15);
        mRenderer.setLegendTextSize(15);
        mRenderer.setXAxisMin(0);
        mRenderer.setXAxisMax(100); //X最大値
        mRenderer.setYAxisMin(0);
        mRenderer.setYAxisMax(50); //Ｙ最大値
        mRenderer.setXLabelsAlign(Align.CENTER);
        mRenderer.setYLabelsAlign(Align.RIGHT);
        mRenderer.setAxesColor(Color.LTGRAY);
        mRenderer.setLabelsColor(Color.YELLOW);
        mRenderer.setBackgroundColor(Color.BLACK);
        mRenderer.setShowGrid(true);
        mRenderer.setGridColor(Color.parseColor("#00FFFF"));

        mRenderer.setMargins(new int[]{30, 30, 15, 40});
        mRenderer.setPanLimits(new double[]{0, 5000, 0, 100});
        mRenderer.setZoomLimits(new double[]{0, 5000, 0, 100});

        mRenderer.setPointSize(4);


        // 新しいデータ系列の作成
        XYSeries series = new XYSeries("温度01");
        mDataset.addSeries(series);
        mCurrentSeries = series;
        // 新しいデータ系列の新規レンダラー作成
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);
        // 各種設定をレンダラーに設定する
        renderer.setColor(Color.YELLOW);
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(true);
        //renderer.setDisplayChartValues(true);
        renderer.setDisplayChartValuesDistance(10);
        mCurrentRenderer = renderer;
        setSeriesWidgetsEnabled(true);

    }

    private void pTimer() {
        if (pHandler != null) {
            pHandler.removeCallbacks(pTask);
        }
    }

    private void cancelTimer() {
        if (myHandler != null) {
            myHandler.removeCallbacks(myTask);
        }
    }

    private void mainloop() {
        mRunningMainLoop = true;
        new Thread(mLoop).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
            mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
            // enable the chart click events
            mRenderer.setClickEnabled(true);
            mRenderer.setSelectableBuffer(10);
            mChartView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // handle the click event on the chart
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
                    if (seriesSelection == null) {
                /*Toast.makeText(MainActivity.this, "要素がありません", Toast.LENGTH_SHORT).show();*/
                    } else {
                        // display information of the clicked point
                        Toast.makeText(
                                Temp01.this,
                                " データポイントのインデックス " + seriesSelection.getPointIndex() + " クリックされた"
                                        + " 最も近い点の値 X=" + seriesSelection.getXValue() + ", Y="
                                        + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.FILL_PARENT));
            boolean enabled = mDataset.getSeriesCount() > 0;
            setSeriesWidgetsEnabled(enabled);
        } else {
            mChartView.repaint();
        }
    }

    /**
     * Enable or disable the add data to series widgets
     *
     * @param enabled the enabled state
     */
    private void setSeriesWidgetsEnabled(boolean enabled) {
        mX.setEnabled(true);
        mY.setEnabled(true);
    }

    private class PaintTask implements Runnable {
        @Override
        public void run() {

            double x = 0;
            double y = 0;
            try {
                x = Double.parseDouble(mX.getText().toString());

            } catch (NumberFormatException e) {
                mX.requestFocus();
                return;
            }
            try {
                y = Double.parseDouble(mY.getText().toString());
            } catch (NumberFormatException e) {
                mY.requestFocus();
                return;
            }
            // add a new data point to the current series
            mCurrentSeries.add(x, y);
            mX.requestFocus();
            // repaint the chart such as the newly added point to be visible
            mChartView.repaint();
            pHandler.postDelayed(this, 1000);

        }
    }

    private class MyTimerTask implements Runnable {
        @Override
        public void run() {
            currentTime++;
            mX.setText(Integer.toString(currentTime));
            myHandler.postDelayed(this, 1000);
        }
    }

}

