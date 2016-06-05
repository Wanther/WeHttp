package cn.wanther.http.sample.fragment;

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
import cn.wanther.http.request.GetRequest;

import java.io.IOException;

/**
 * Created by wanghe on 2015/7/8.
 */
public class HttpGetTestFragment extends Fragment {

    private TextView mConsoleTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mConsoleTv = new TextView(getActivity());
        return mConsoleTv;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new HttpGetStringTask().execute("http://ufound.cn");
    }

    private class HttpGetStringTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            Request req = new GetRequest(params[0]);
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
