package com.android.guicelebrini.qualrole.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Preferences {

    private Context context;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private final String FILE_NAME = "qualrole.preferences";
    private final int MODE = 0;

    private final String KEY_FOLLOW_CODE = "followCode";

    public Preferences(Context parameterContext){
        this.context = parameterContext;
        preferences = context.getSharedPreferences(FILE_NAME, MODE);
        editor = preferences.edit();
    }

    public void saveData(String followCode){
        editor.putString(KEY_FOLLOW_CODE, followCode);
        editor.commit();
    }

    public String getFollowCode(){
        return preferences.getString(KEY_FOLLOW_CODE, null);
    }
}
