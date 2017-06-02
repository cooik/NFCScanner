package info.androidhive.materialdesign.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import info.androidhive.materialdesign.R;
import info.androidhive.materialdesign.activity.TagReadActivity;
import info.androidhive.materialdesign.database.DatabaseHelper;
import info.androidhive.materialdesign.model.Product;

/**
 * Created by Ravi on 29/07/15.
 */
public class ReadTagFragment extends Fragment {
    public DatabaseHelper dh;
    private  Context mContext;
    public ReadTagFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        dh = new DatabaseHelper(getActivity());



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_readtags, container, false);
        Bundle bundle = getArguments();
        if(bundle!=null){
            boolean isavaliable = bundle.getBoolean("TagIsAvaliable",false);
            String content = bundle.getString("tagContent","");
                Log.i("tanjin",String.valueOf(isavaliable)+content);
            if(isavaliable)
            {
                List<Product> products = dh.getProduct(content);
                if(products!=null&&products.size()!=0) {
                    Intent intent = new Intent(mContext, TagReadActivity
                            .class);
                    intent.putExtra("id",content);
                    startActivity(intent);
                }else{
                    Toast.makeText(mContext, "此产品为假!", Toast.LENGTH_SHORT).show();
                }
            }
//            Toast.makeText(getActivity(),content,Toast.LENGTH_SHORT).show();
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
