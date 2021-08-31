package com.android.guicelebrini.qualrole.helper;

import android.util.Base64;

public class Base64Custom {

    public static String encode(String text){
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    public static String decode(String encodedText){
        return new String( Base64.decode(encodedText, Base64.DEFAULT) );
    }

}
