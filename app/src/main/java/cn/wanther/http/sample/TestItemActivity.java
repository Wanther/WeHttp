package cn.wanther.http.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by wanghe on 2015/7/7.
 */
public class TestItemActivity extends FragmentActivity {
    private static final String TAG = "TestItemActivity";

    public static final String KEY_ITEM_NAME = "_item_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            return;
        }

        String itemName = getIntent().getStringExtra(KEY_ITEM_NAME);

        if(TextUtils.isEmpty(itemName)) {
            return;
        }

        Fragment f = getFragmentByItemName(itemName);

        if(BuildConfig.DEBUG) {
            Log.d(TAG, f + "");
        }

        if(f != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, f)
                    .commit();
        }
    }

    protected Fragment getFragmentByItemName(String itemName){
        String fullName = "cn.wanther.http.sample.fragment." + itemName.replaceAll("[/]", ".") + "Fragment";

        if(BuildConfig.DEBUG) {
            Log.d(TAG, "fragment fullName=" + fullName);
        }

        try {
            Class<? extends Fragment> fragmentClass = (Class<? extends Fragment>)Class.forName(fullName);
            return fragmentClass.newInstance();
        } catch (Exception e) {
            if(BuildConfig.DEBUG) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        return null;
    }
}
