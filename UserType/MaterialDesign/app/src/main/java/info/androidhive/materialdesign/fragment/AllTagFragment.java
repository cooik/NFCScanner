package info.androidhive.materialdesign.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.materialdesign.R;
import info.androidhive.materialdesign.activity.TagDetailActivity;
import info.androidhive.materialdesign.adapter.TagAdapter;
import info.androidhive.materialdesign.database.DatabaseHelper;
import info.androidhive.materialdesign.model.Product;
import info.androidhive.materialdesign.view.DividerItemDecoration;


public class AllTagFragment extends Fragment {
    public Button btn_start;
    public Context mContext;
    public DatabaseHelper dh;
    public TextView tv_productid,tv_name;
    public ImageView image;
    public Bundle bundle=null;
    public TextView tv_isreal;
    public AllTagFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("tanjin", "AllTagFragment------->onCreate");
        mContext = getActivity();
      dh = new DatabaseHelper(getActivity());
       bundle = getArguments();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("tanjin", "AllTagFragment------->onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_alltags, container, false);
        tv_productid = (TextView) rootView.findViewById(R.id.tv_productid);
        image = (ImageView) rootView.findViewById(R.id.iv_isreal);
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        tv_isreal = (TextView) rootView.findViewById(R.id.tv_isreal);
        btn_start = (Button) rootView.findViewById(R.id.start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackgroundTask().execute();
            }
        });
        // Inflate the layout for this fragment
        if(!TextUtils.isEmpty(bundle.getString("tagContent")))
        {
            btn_start.performClick();
        }
        return rootView;
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(mContext);
        List<Product> products;
        boolean isreal;
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Doing something, please wait.");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            image.setVisibility(View.VISIBLE);
            if(isreal){
                image.setImageResource(R.drawable.isreal);
                tv_name.setText(products.get(0).getName());
                tv_productid.setText(products.get(0).getId());
                tv_isreal.setText("此产品为真品");
            }else{
                image.setImageResource(R.drawable.noreal);
                tv_isreal.setText("此产品为假");
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return null;
            }

            if(bundle!=null){
                boolean isavaliable = bundle.getBoolean("TagIsAvaliable",false);
                String content = bundle.getString("tagContent","");
                Log.i("tanjin", String.valueOf(isavaliable)+content);
                if(isavaliable) {
                    products = dh.getProduct(content);
                    if (products.size() != 0) {
                        isreal = true;

                    } else {
                        isreal = false;
                    }
                }
            }

            return null;
        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
