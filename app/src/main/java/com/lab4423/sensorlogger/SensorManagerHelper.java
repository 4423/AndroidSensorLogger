package com.lab4423.sensorlogger;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * SensorEventを正しく購読できるようにします。
 * SensorManagerにregisterListenerする時、getDefaultSensor()で得られるタイプは正しくない場合があるらしい。
 */
public class SensorManagerHelper {

    protected SensorManager sensorManager;
    protected int sensorDelay;

    /***
     * SensorManagerHelperのインスタンスを作成します。
     * @param sensorManager
     * @param sensorDelay Androidで使用可能な全てのSensorを購読するときに、設定される値です。　
     *                    例：SensorManager.SENSOR_DELAY_NORMAL
     */
    public SensorManagerHelper(SensorManager sensorManager, int sensorDelay){
        this.sensorManager = sensorManager;
        this.sensorDelay = sensorDelay;
    }


    /***
     * Android4.0以上で使用可能なSensor.TYPE〜を列挙します。
     * @return
     */
    private List<Integer> getAllSensorType(){
        return Arrays.asList(
                Sensor.TYPE_ACCELEROMETER,  //加速度
                Sensor.TYPE_AMBIENT_TEMPERATURE,    //周辺温度
                Sensor.TYPE_GRAVITY,    //重力
                Sensor.TYPE_GYROSCOPE,  //ジャイロスコープ
                Sensor.TYPE_LINEAR_ACCELERATION,    //重力加速度を除いた加速度
                Sensor.TYPE_LIGHT,      //輝度（照度）
                Sensor.TYPE_MAGNETIC_FIELD, //磁界（磁気）
                Sensor.TYPE_PRESSURE,   //気圧
                Sensor.TYPE_PROXIMITY,  //近接
                Sensor.TYPE_RELATIVE_HUMIDITY,  //湿度
                Sensor.TYPE_ROTATION_VECTOR,    //回転ベクトル
                Sensor.TYPE_STEP_COUNTER,   //ステップカウンター
                Sensor.TYPE_STEP_DETECTOR   //ステップ感知
        );
    }


    private boolean hasListener = false;

    /***
     * 動作を開始します。
     */
    public void start(){
        if (!hasListener){
            //すべてのSensorにListenerを登録
            for (Integer sensorType : getAllSensorType()){
                sensorManager.registerListener(
                        listener,
                        this.sensorManager.getDefaultSensor(sensorType),
                        this.sensorDelay);
            }
            this.hasListener = true;
        }
    }

    /***
     * 動作を停止します。
     */
    public void stop(){
        if (hasListener) {
            //すべてのSensorからListenerを削除
            sensorManager.unregisterListener(listener);
            this.hasListener = false;
        }
    }


    Map<Integer, List<SensorEventListener>> listenerMap = new HashMap<>();

    /***
     * SensorEventListenerを追加します。
     * @param sensorType
     * @param listener
     */
    public void addSensorEventListener(int sensorType, SensorEventListener listener){
        List<SensorEventListener> l = this.listenerMap.get(sensorType);
        if (l == null){
           l = new ArrayList<>();
        }
        l.add(listener);
        this.listenerMap.put(sensorType, l);
    }

    /***
     * SensorEventListenerを削除します。
     * 指定されたリスナーへの登録は解除されますが、このHelper内のセンサーは動作を続けます。
     * 完全にイベントの購読を停止する場合は、stop()メソッドを叩いてください。
     * @param sensorType
     * @param listener
     */
    public void removeSensorEventListener(int sensorType, SensorEventListener listener){
        List<SensorEventListener> l = this.listenerMap.get(sensorType);
        if (l != null){
            l.remove(l.indexOf(listener));
            this.listenerMap.put(sensorType, l);
        }
    }


    //発火したイベントはこのlistenerを経由して、listenerMapに登録されたlistenerへ伝搬される
    private final SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            //発火したSensorと一致するTypeのlistenerを発火
            int type = event.sensor.getType();
            for (SensorEventListener listener : Utils.nullGuard(listenerMap.get(type))){
                listener.onSensorChanged(event);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
}
