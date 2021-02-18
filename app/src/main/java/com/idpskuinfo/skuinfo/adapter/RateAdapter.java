package com.idpskuinfo.skuinfo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.idpskuinfo.skuinfo.R;
import com.idpskuinfo.skuinfo.RateInfo;

import java.util.ArrayList;

public class RateAdapter extends RecyclerView.Adapter<RateAdapter.RateViewHolder> {
    private ArrayList<RateInfo> rateInfos = new ArrayList<>();
    private Activity activity;
    private static final String TAG = RateAdapter.class.getSimpleName();

    /*public RateAdapter(ArrayList<RateInfo> rateInfos) {
        this.rateInfos = rateInfos;
    }*/

    public RateAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setListRate(ArrayList<RateInfo> rateAdapters) {
        if (rateAdapters.size() > 0) {
            this.rateInfos.clear();
        }
        this.rateInfos.addAll(rateAdapters);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //return null;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_exchange, parent, false);
        return new RateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RateViewHolder holder, int position) {
        holder.tvCurrency.setText(rateInfos.get(position).getCurrency());
        holder.tvRateIdr.setText(rateInfos.get(position).getRateidr());
        Glide.with(holder.itemView.getContext())
                .load(R.drawable.rate)
                .apply(new RequestOptions().override(55, 55))
                .into(holder.imgrate);
    }

    @Override
    public int getItemCount() {
        return rateInfos.size();
    }

    public class RateViewHolder extends RecyclerView.ViewHolder {
        TextView tvCurrency, tvRateIdr;
        ImageView imgrate;

        public RateViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCurrency = itemView.findViewById(R.id.tv_currency);
            tvRateIdr = itemView.findViewById(R.id.tv_rateidr);
            imgrate = itemView.findViewById(R.id.img_item_photo);
        }
    }
}
