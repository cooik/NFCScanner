package info.androidhive.materialdesign.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import info.androidhive.materialdesign.model.Product;

/**
 * Created by yuqiao-liucs on 2016/3/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String CLOUMN_NAME = "Name";
    public static final String CLOUMN_ID = "ProductID";
    public static final String CLOUMN_CNAME = "CName";
    public static final String CLOUMN_TIME = "ScannerTime";
    public static final String CLOUMN_DESC = "Description";
    public static final String CLOUMN_LAT="Lat";
    public static final String CLOUMN_LNG="Lng";

    public static final String DBNAME = "Nfc.db";
    private static final String TABLENAME="products";
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private AssetsDatabaseManager assetsDatabaseManager;
    public String[] allFiles = {CLOUMN_NAME,CLOUMN_ID,CLOUMN_CNAME,CLOUMN_TIME ,CLOUMN_DESC,CLOUMN_LAT,CLOUMN_LNG};
    public DatabaseHelper(Context context) {
        super(context,DBNAME,null,1);
        this.mContext = context;
        AssetsDatabaseManager.initManager(mContext.getApplicationContext());
        assetsDatabaseManager = AssetsDatabaseManager.getManager().getManager();

    }


    @Override
    public void onCreate(SQLiteDatabase db)
    { }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void openDatabase() {
//        String dbPath = mContext.getDatabasePath(DBNAME).getPath();
//        Log.i("tanjin",dbPath);
//        if (mDatabase != null && mDatabase.isOpen()) {
//            return;
//        }
//        mDatabase = SQLiteDatabase.openDatabase(dbPath,null,SQLiteDatabase.OPEN_READWRITE);
        mDatabase = assetsDatabaseManager.getDatabase(DBNAME);
    }
    public void closeDatabase() {
        assetsDatabaseManager.closeDatabase(DBNAME);
        }


    public List<Product> getListProduct()
    {
        Product product = null;
        List<Product> productList = new ArrayList<>();
        openDatabase();
//        String sql ="SELECT * FROM products";
        String sql = "select  * from products where ScannerTime in(Select max(ScannerTime) FROM products group by ProductID)";
        Cursor cursor = mDatabase.rawQuery(sql, null);
        if(cursor!=null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                product = cursorToProduce(cursor);
                productList.add(product);
                cursor.moveToNext();
            }
            cursor.close();
            closeDatabase();
            return productList;
        }
        return null;
    }

    public List<Product> getProduct(String id)
    {
        List<Product> sameIdproducts = new ArrayList<Product>();
        openDatabase();
        String sql = "SELECT * FROM products WHERE ProductID = "+id;
        Cursor cursor = mDatabase.rawQuery(sql,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Product product = cursorToProduce(cursor);
            sameIdproducts.add(product);
            cursor.moveToNext();
        }
        cursor.close();
        return  sameIdproducts;
    }

    public List<LatLng> productPoints(String id){
        List<LatLng> allPoints = new ArrayList<>();
        List<Product> products = getProduct(id);
        if(products!=null&&products.size()!=0)
        {
            for(int position=0;position<products.size();position++){
                LatLng point = new LatLng(products.get(position).getLat(),products.get(position).getLng());
                allPoints.add(point);
            }
        }

        return allPoints;
    }

    public void addProduct(Product product){
        ContentValues values = new ContentValues();
        if(mDatabase==null) {
            openDatabase();
        }
        values.put(CLOUMN_NAME,product.getName());
        values.put(CLOUMN_ID,product.getId());
        values.put(CLOUMN_CNAME,product.getCname());
        values.put(CLOUMN_TIME,product.getTime());
        values.put(CLOUMN_DESC,product.getDescroption());
        values.put(CLOUMN_LAT,String.valueOf(product.getLat()));
        values.put(CLOUMN_LNG,String.valueOf(product.getLng()));
        long insertId = mDatabase.insert(TABLENAME, null,
                values);
        Log.i("tanjin", "insercard---->" + insertId);

    }

    public Product cursorToProduce(Cursor cursor){

        Product product  = new Product(cursor.getString(cursor.getColumnIndex(allFiles[0])),
                cursor.getString(cursor.getColumnIndex(allFiles[1])),
                cursor.getString(cursor.getColumnIndex(allFiles[2])),
                cursor.getString(cursor.getColumnIndex(allFiles[3])),
                cursor.getString(cursor.getColumnIndex(allFiles[4])),
                Double.parseDouble(cursor.getString(cursor.getColumnIndex(allFiles[5]))),
                Double.parseDouble(cursor.getString(cursor.getColumnIndex(allFiles[6])))
                );
        return product;
    }



}
