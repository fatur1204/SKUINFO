package com.idpskuinfo.skuinfo;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.idpskuinfo.skuinfo.adapter.RateAdapter;
import com.idpskuinfo.skuinfo.db.CurrencyHelper;
import com.idpskuinfo.skuinfo.helper.MappingHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class Fragment_ListRate extends Fragment implements LoadCurrencyCallback {
    CurrencyHelper currencyHelper;
    private RecyclerView recyclerView;
    private List<RateInfo> rateInfoList = new ArrayList<>();
    private RateAdapter adapter;
    private ProgressBar progressBar;

    public Fragment_ListRate() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__list_rate, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currencyHelper = CurrencyHelper.getInstance(getContext());
        currencyHelper.open();

        recyclerView = view.findViewById(R.id.rv_listrate);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getContext()
        ));

        adapter = new RateAdapter(getActivity());
        recyclerView.setAdapter(adapter);


        // proses ambil data
        new LoadCurrencyAsync(currencyHelper, this).execute();
    }

    @Override
    public void preExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void postExecute(ArrayList<RateInfo> rateInfos) {
        progressBar.setVisibility(View.INVISIBLE);
        if (rateInfos.size() > 0) {
            adapter.setListRate(rateInfos);
        } else {
            adapter.setListRate(new ArrayList<RateInfo>());
            Toast.makeText(getContext(),"No data found!", Toast.LENGTH_LONG).show();
        }
        currencyHelper.close();
    }


    private static class LoadCurrencyAsync extends AsyncTask<Void, Void, ArrayList<RateInfo>> {
        private final WeakReference<CurrencyHelper> weakNoteHelper;
        private final WeakReference<LoadCurrencyCallback> weakCallback;

        private LoadCurrencyAsync(CurrencyHelper noteHelper, LoadCurrencyCallback callback) {
            weakNoteHelper = new WeakReference<>(noteHelper);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<RateInfo> doInBackground(Void... voids) {
            Cursor dataCursor = weakNoteHelper.get().queryAll();
            return MappingHelper.mapCursorToArrayList(dataCursor);
        }

        @Override
        protected void onPostExecute(ArrayList<RateInfo> rates) {
            super.onPostExecute(rates);
            weakCallback.get().postExecute(rates);
        }
    }
}

interface LoadCurrencyCallback {
    void preExecute();

    void postExecute(ArrayList<RateInfo> rateInfos);
}