package info.androidhive.materialdesign.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.materialdesign.R;
import info.androidhive.materialdesign.database.DatabaseHelper;
import info.androidhive.materialdesign.model.Product;

public class TagDetailActivity extends AppCompatActivity {
    String id;
    DatabaseHelper dh;
    List<LatLng> allPoints = new ArrayList<>();
    List<Product> sameIdProduct = new ArrayList<>();
    private AMap aMap;
    private MapView mapView;
    private Toolbar mToolbar;
    private TextView tv_productid, tv_name, tv_cname, tv_description;

    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_detail);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");
        dh = new DatabaseHelper(this);
        allPoints = dh.productPoints(id);
        sameIdProduct = dh.getProduct(id);
        product = sameIdProduct.get(0);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();

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
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {
        PolylineOptions options = new PolylineOptions();
        if (allPoints != null && allPoints.size() != 0) {
            options.addAll(allPoints).geodesic(true).color(Color.RED);
        }
        aMap.addPolyline(options);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}