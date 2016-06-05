package cn.wanther.http.sample.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import cn.wanther.http.Parser;
import cn.wanther.http.Request;
import cn.wanther.http.executor.SimpleHttpExecutor;
import cn.wanther.http.parser.StringParser;
import cn.wanther.http.request.GetRequest;

/**
 * Created by wanghe on 2015/7/9.
 */
public class HttpsGetTestFragment extends Fragment {
    private TextView mConsoleTv;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ScrollView scrollView = new ScrollView(getActivity());

        mConsoleTv = new TextView(getActivity());

        scrollView.addView(mConsoleTv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return scrollView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new HttpsGetStringTask().execute("http://ufound.cn");
    }

    private class HttpsGetStringTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            Request req = new GetRequest(params[0]);
            Parser<String> parser = new StringParser();

            try {
                return new SimpleHttpExecutor().execute(req, parser);
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            mConsoleTv.setText(s);
        }
    }
}
