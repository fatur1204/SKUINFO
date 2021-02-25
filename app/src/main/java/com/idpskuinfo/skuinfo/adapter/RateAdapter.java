package com.idpskuinfo.skuinfo.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
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
    private static final String TAG = RateAdapter.class.getSimpleName();
    private final ArrayList<RateInfo> rateInfos = new ArrayList<>();
    private final Activity activity;

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
        holder.tvRateIdr.setText("IDR. " + String.format("%,.3f", Float.valueOf(rateInfos.get(position).getRateidr())));
        holder.txtCountry.setText(rateInfos.get(position).getDescription());

        String imgdata = rateInfos.get(position).getCurrency();
        imgdata = imgdata.toLowerCase();
        String uri = "@drawable/" + imgdata;
        int imageResource = activity.getResources().getIdentifier(uri, null, activity.getPackageName());
        Drawable drawable = activity.getResources().getDrawable(imageResource);
        Glide.with(holder.itemView.getContext())
                .load(drawable)
                .apply(new RequestOptions().override(55, 55))
                .into(holder.imgrate);
    }

    @Override
    public int getItemCount() {
        return rateInfos.size();
    }

    public class RateViewHolder extends RecyclerView.ViewHolder {
        TextView tvCurrency, tvRateIdr, txtCountry;
        ImageView imgrate;

        public RateViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCurrency = itemView.findViewById(R.id.tv_currency);
            tvRateIdr = itemView.findViewById(R.id.tv_rateidr);
            imgrate = itemView.findViewById(R.id.img_item_photo);
            txtCountry = itemView.findViewById(R.id.txtcountry);
        }
    }
}
