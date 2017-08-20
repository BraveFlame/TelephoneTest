package cn.com.telecomphone;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView showView;
    private String[] statusNames, phoneType, simStatus, callStatus;
    private ArrayList<String> statusValues = new ArrayList<>();
    private TelephonyManager tManager;
    private Button monitor_btn, set_blackPh;
    private TextView textView;
    private PhoneStateListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 获取TelephonyManager实例
         */
        tManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        /**
         * initView()初始化控件
         * initString()初始化字符串数组，getPhonePara()开始获取手机SIM的各项数据
         * initListent()初始化PhoneStateListener对象
         */
        initView();
        initString();
        getPhonePara();
        initListent();

        /**
         * 将名称与数据值以key-value形式保存在列表中
         */
        ArrayList<Map<String, String>> status = new ArrayList<>();
        for (int i = 0; i < statusValues.size(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", statusNames[i]);
            map.put("value", statusValues.get(i));
            status.add(map);
        }

        /**
         * 将存有手机参数名称与值的列表加入适配器，显示
         */
        SimpleAdapter adapter = new SimpleAdapter(this, status,
                R.layout.para_item, new String[]{"name", "value"}, new int[]{R.id.name, R.id.value});
        showView.setAdapter(adapter);


        /**
         * 监听手机来电号码
         */


    }

    /**
     * 按键设置监听黑名单和开启监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getphoen_call_btn:
                tManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
                textView.setText("已开启监听黑名单模式！");
                monitor_btn.setEnabled(false);
                break;
            case R.id.set_black_phone:
                startActivity(new Intent(MainActivity.this, SetPhoneBlack.class));
                break;
            default:
                break;
        }

    }


    /**
     * 初始化监听对象，并判断哪些号码是黑名单中的号码
     */
    public void initListent() {
        listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        DBOperate dbOperate = DBOperate.getDbOperate(MainActivity.this);
                        List<Person> list = new ArrayList();
                        if (isBlackName(dbOperate.getBlackName(list), incomingNumber)) {
                            endCall();
//
//                            AlertDialog.Builder alter = new AlertDialog.Builder(MainActivity.this)
//                                    .setTitle("来电提醒").setIcon(R.mipmap.ic_launcher)
//                                    .setMessage("此电话在黑名单中...\n 是否挂断？")
//
//                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            endCall();
//                                        }
//                                    })
//
//                                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//
//                                        }
//                                    });
//                            alter.show();
                        }


                        break;
                    default:
                        break;
                }

                super.onCallStateChanged(state, incomingNumber);

            }
        };
    }

    public boolean isBlackName(List<Person> list, String incomingNumber) {


        for (int i = 0; i < list.size(); i++) {
            if (incomingNumber.equals(list.get(i).getPhoneNumber())) {
                return true;
            }
        }
        return false;
    }


    public void test() {

    }

    /**
     * 初始化view子类控件
     */
    public void initView() {
        showView = (ListView) findViewById(R.id.show_lv);
        monitor_btn = (Button) findViewById(R.id.getphoen_call_btn);
        set_blackPh = (Button) findViewById(R.id.set_black_phone);
        textView = (TextView) findViewById(R.id.is_monitor);
        monitor_btn.setOnClickListener(this);
        set_blackPh.setOnClickListener(this);
    }

    /**
     * 主要从array里面获取手机各项参数的名称以及常量所对于的值（GSM等）
     */
    public void initString() {
        statusNames = getResources().getStringArray(R.array.statusNames);
        phoneType = getResources().getStringArray(R.array.phoneType);
        simStatus = getResources().getStringArray(R.array.simStatu);
        callStatus = getResources().getStringArray(R.array.callStatus);
    }

    /**
     * getPhonePara()开始获取手机SIM的各项数据
     */
    public void getPhonePara() {
        statusValues.add(tManager.getDeviceId());
        statusValues.add(tManager.getDeviceSoftwareVersion() != null ? tManager.getDeviceSoftwareVersion() : "未知");
        statusValues.add(tManager.getNetworkOperator());
        statusValues.add(tManager.getNetworkOperatorName());
        statusValues.add(phoneType[tManager.getPhoneType()]);
        String str = String.valueOf(tManager.getCellLocation());
        if (str == null) {
            statusValues.add("未知位置");
        } else {
            statusValues.add(str);
        }

        statusValues.add(tManager.getSimCountryIso());
        statusValues.add(tManager.getSimSerialNumber());
        statusValues.add(simStatus[tManager.getSimState()]);
        statusValues.add(callStatus[tManager.getCallState()]);
        statusValues.add(tManager.getSubscriberId());
    }

    /**
     * 利用JAVA反射机制调用ITelephony的endCall()结束通话。
     */
    private void endCall() {
        // 初始化iTelephony
        Class<TelephonyManager> c = TelephonyManager.class;
        Method getITelephonyMethod = null;
        try {
            // 获取所有public/private/protected/默认
            // 方法的函数，如果只需要获取public方法，则可以调用getMethod.
            getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            // 将要执行的方法对象设置是否进行访问检查，也就是说对于public/private/protected/默认
            // 我们是否能够访问。值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查。值为 false
            // 则指示反射的对象应该实施 Java 语言访问检查。
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(tManager, (Object[]) null);
            iTelephony.endCall();
            Log.v(this.getClass().getName(), "endCall......");
            Toast.makeText(MainActivity.this, "已挂断！", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "endCallError", e);
            Toast.makeText(MainActivity.this, "失败！", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 此方法不成功
     */
    void endCallTow() {
        try {
            Method method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(tManager, new Object[]{TELECOM_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.endCall();
            Toast.makeText(MainActivity.this, "已挂断！", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "失败！", Toast.LENGTH_LONG).show();
        }
    }
}