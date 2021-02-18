package com.idpskuinfo.skuinfo.helper;

import android.database.Cursor;

import com.idpskuinfo.skuinfo.RateInfo;
import com.idpskuinfo.skuinfo.db.DatabaseContract;

import java.util.ArrayList;

public class MappingHelper {
    public static ArrayList<RateInfo> mapCursorToArrayList(Cursor rateCursor) {
        ArrayList<RateInfo> rateList = new ArrayList<>();
        while (rateCursor.moveToNext()) {
            String title = rateCursor.getString(rateCursor.getColumnIndexOrThrow(DatabaseContract.CurrColumns.CURRID));
            String description = rateCursor.getString(rateCursor.getColumnIndexOrThrow(DatabaseContract.CurrColumns.CUR_RET));
            rateList.add(new RateInfo(title, description));
        }
        return rateList;
    }
}
