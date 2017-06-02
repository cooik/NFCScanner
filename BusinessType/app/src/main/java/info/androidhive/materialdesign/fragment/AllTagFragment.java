package info.androidhive.materialdesign.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.materialdesign.R;
import info.androidhive.materialdesign.activity.TagDetailActivity;
import info.androidhive.materialdesign.activity.TagReadActivity;
import info.androidhive.materialdesign.adapter.TagAdapter;
import info.androidhive.materialdesign.database.DatabaseHelper;
import info.androidhive.materialdesign.model.Product;
import info.androidhive.materialdesign.view.DividerItemDecoration;


public class AllTagFragment extends Fragment {
    private List<Product> productList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TagAdapter mAdapter;
    public Context mContext;
    public DatabaseHelper dh;
    public AllTagFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("tanjin", "AllTagFragment------->onCreate");
        mContext = getActivity();
      dh = new DatabaseHelper(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("tanjin","AllTagFragment------->onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_alltags, container, false);
        recyclerView  = (RecyclerView) rootView.findViewById(R.id.tag_list);

        mAdapter = new TagAdapter(productList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(mContext.getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Product product = productList.get(position);
                Intent intent = new Intent(mContext, TagDetailActivity
                .class);
                intent.putExtra("id",product.getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        prepareProductsData();

        // Inflate the layout for this fragment
        return rootView;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    private void prepareProductsData() {
//        for (int i = 0;i<10;i++) {
//            Product product = new Product();
//            product.setName("COCO");
//            product.setId("11111112323435443543532512341");
//            product.setTime("2016/10/11");
//            productList.add(product);
//        }
        List<Product> Products = dh.getListProduct();
        if(Products.size()!=0){
        productList.addAll(productList.size(),dh.getListProduct());
        mAdapter.notifyDataSetChanged();}

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
