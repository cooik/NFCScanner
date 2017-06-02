package info.androidhive.materialdesign.adapter;

import android.nfc.Tag;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import info.androidhive.materialdesign.R;
import info.androidhive.materialdesign.model.Product;

/**
 * Created by yuqiao-liucs on 2016/3/15.
 */
public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    private List<Product> productList;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_list_row,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.title.setText(product.getName());
        holder.id.setText(product.getId());
        holder.time.setText(product.getTime());

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title,id,time;
        public ViewHolder(View view)
        {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            id = (TextView) view.findViewById(R.id.id);
            time= (TextView) view.findViewById(R.id.time);
        }

    }

    public TagAdapter(List<Product> productList){
        this.productList=productList;
    }
}
