package info.androidhive.materialdesign.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.androidhive.materialdesign.R;
import info.androidhive.materialdesign.database.DatabaseHelper;
import info.androidhive.materialdesign.model.Product;
import info.androidhive.materialdesign.view.Utils;

public class TagReadActivity extends AppCompatActivity implements AMapLocationListener {
    String id;
    DatabaseHelper dh;
    List<LatLng> allPoints = new ArrayList<>();
    List<Product> sameIdProduct = new ArrayList<>();
    private Toolbar mToolbar;
    private TextView tv_productid, tv_name, tv_cname, tv_description;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    Product product;
    private TextView tv_location;
    private Context mContext;
    private Button btn_start,btn_save;
    double lat,lng;
    private AddPointTask mAuthTask = null;
    ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_read);
        mContext = TagReadActivity.this;
        progressDialog = new ProgressDialog(mContext);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");
        dh = new DatabaseHelper(this);
        allPoints = dh.productPoints(id);
        sameIdProduct = dh.getProduct(id);
        product = sameIdProduct.get(0);
        init();
        locationClient = new AMapLocationClient(mContext.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);

    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tv_productid = (TextView) findViewById(R.id.tv_productid);
        tv_productid.setText(product.getId());
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setText(product.getName());
        tv_cname = (TextView) findViewById(R.id.tv_cname);
        tv_cname.setText(product.getCname());
        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_description.setText(product.getDescroption());
        tv_location = (TextView) findViewById(R.id.tv_location);
        btn_start= (Button) findViewById(R.id.btn_startlocation);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动定位
                locationClient.startLocation();
                mHandler.sendEmptyMessage(Utils.MSG_LOCATION_START);
                // 设置定位监听
                locationClient.setLocationListener(TagReadActivity.this);
            }
        });
        btn_save = (Button) findViewById(R.id.btn_savelocation);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(tv_location.getText()))
                {
                    showProgress(true);
                    mAuthTask = new AddPointTask();
                    mAuthTask.execute();
                }
            }
        });
    }

//    private void setUpMap() {
//        PolylineOptions options = new PolylineOptions();
//        if (allPoints != null && allPoints.size() != 0) {
//            options.addAll(allPoints).geodesic(true).color(Color.RED);
//        }
//        aMap.addPolyline(options);
//    }

    // 定位监听
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            Message msg = mHandler.obtainMessage();
            msg.obj = amapLocation;
            msg.what = Utils.MSG_LOCATION_FINISH;
            mHandler.sendMessage(msg);
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                lat =  amapLocation.getLatitude();//获取纬度
                lng = amapLocation.getLongitude();//获取经度
                tv_location.setText(lat+","+lng);
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }


    public class AddPointTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            product.setLat(lat);
            product.setLng(lng);
            dh.addProduct(product);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            showProgress(false);
            if(aBoolean)
            {
                Toast.makeText(TagReadActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(TagReadActivity.this,"请确定定位成功",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mAuthTask = null;
            showProgress(false);

        }
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if(show) {
            progressDialog = ProgressDialog.show(TagReadActivity.this, "请等待", "正在保存",true);
            progressDialog.setCancelable(true);
        }else{
            if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
        }

    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }

    }

    Handler mHandler = new Handler(){
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case Utils.MSG_LOCATION_START:
                    Toast.makeText(mContext, "正在定位...", Toast.LENGTH_SHORT).show();
                    break;
                //定位完成
                case Utils.MSG_LOCATION_FINISH:
                    AMapLocation loc = (AMapLocation)msg.obj;
                    String result = Utils.getLocationStr(loc);
                    Toast.makeText(mContext, "定位完成...", Toast.LENGTH_SHORT).show();
                    locationClient.stopLocation();
                    Log.i("tanjin", result + "");
                    break;
                case Utils.MSG_LOCATION_STOP:
                    break;
                default:
                    break;
            }
        };
    };
}