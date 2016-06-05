package com.welearn.http.sample.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.wanther.http.Parser;
import cn.wanther.http.Request;
import cn.wanther.http.exception.AccessException;
import cn.wanther.http.executor.SimpleHttpExecutor;
import cn.wanther.http.parser.StringParser;
import cn.wanther.http.request.JSONPostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by wanghe on 2015/7/8.
 */
public class HttpPostJSONTestFragment extends Fragment {

    private TextView mConsoleTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mConsoleTv = new TextView(getActivity());
        return mConsoleTv;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        JSONObject json = new JSONObject();
        try {
            json.put("name", "value");
            json.put("name1", "value2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpPostJSONTask().execute("http://www.baidu.com", json);
    }

    private class HttpPostJSONTask extends AsyncTask<Object, Void, String>{

        @Override
        protected String doInBackground(Object... params) {


            Request req = new JSONPostRequest((String)params[0], (JSONObject)params[1]);
            Parser<String> parser = new StringParser();
            try {
                return new SimpleHttpExecutor().execute(req, parser);
            } catch (IOException e) {
                return e.getMessage();
            } catch (AccessException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            mConsoleTv.setText(s);
        }
    }
}
