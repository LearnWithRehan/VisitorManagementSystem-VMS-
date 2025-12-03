package com.example.visitormanagementsys.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {

    private static final String PREF_NAME = "VMS_SESSION";
    private static final String KEY_VISITOR_ID = "visitor_id";

    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;

    public Session(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Save visitor ID
    public void saveVisitorId(String visitorId) {
        editor.putString(KEY_VISITOR_ID, visitorId);
        editor.commit();
    }

    // Get saved visitor ID
    public static String getUserId() {
        return pref.getString(KEY_VISITOR_ID, "");
    }
}