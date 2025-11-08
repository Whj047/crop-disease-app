\
package listview.tianhetbm.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {
    private static final String PREF = "crop_app_prefs";

    public static void setString(Context ctx, String key, String value) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    public static String getString(Context ctx, String key, String def) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sp.getString(key, def);
    }
}
