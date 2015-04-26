package com.lab4423.sensorlogger;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class MainActivity extends ActionBarActivity {

    protected SensorManagerHelper smh;
    protected PrintWriter writer;
    protected TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //表示準備
        this.textView = (TextView) findViewById(R.id.textview);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        mSeries = new LineGraphSeries<DataPoint>();
        graph.addSeries(mSeries);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(60);
        graph.getViewport().setMinY(-5);
        graph.getViewport().setMaxY(5);

        //ファイル書き込み準備
        try {
            OutputStream outputStream = openFileOutput("log.txt", MODE_APPEND);
            this.writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            this.writer.append("\n=========\n");
            this.writer.append(Utils.getNowDate() + "\n");
        }
        catch (IOException e){
            //Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT);
        }

        //Sensorの準備
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        smh = new SensorManagerHelper(sm, sm.SENSOR_DELAY_GAME);
        //smh.addSensorEventListener(Sensor.TYPE_ACCELEROMETER, loggingListener);
        //smh.addSensorEventListener(Sensor.TYPE_GRAVITY, loggingListener);
        //smh.addSensorEventListener(Sensor.TYPE_GYROSCOPE, loggingListener);
        smh.addSensorEventListener(Sensor.TYPE_LINEAR_ACCELERATION, loggingListener);
        //smh.addSensorEventListener(Sensor.TYPE_ROTATION_VECTOR, loggingListener);
        //Android4.4だとステップカウンターなるものがあって、歩数をカウントしてくれるらしい・・。
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        smh.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        //バックグラウンドでも動き続ける必要があるので、stopする必要はない
        smh.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.writer.close();
    }

    private double graph2LastXValue = 5d;
    private LineGraphSeries<DataPoint> mSeries;

    //ロギング用リスナー
    private final SensorEventListener loggingListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            try {
                writer.append(event.sensor.getName() + "\t" + values[0] + "\t" + values[1] + "\t" + values[2] + "\n");

                graph2LastXValue += 1d;
                mSeries.appendData(new DataPoint(graph2LastXValue, values[2]), true, 60);

                String data = event.sensor.getName() +
                        "\nx_axis: " + values[0] +
                        "\ny_axis: " + values[1] +
                        "\nz_axis: " + values[2] + "\n";
                textView.setText(data, TextView.BufferType.EDITABLE);
            }catch (Exception ex){}
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
}