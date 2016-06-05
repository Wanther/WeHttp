package cn.wanther.http.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import cn.wanther.http.HttpExecutor;
import cn.wanther.http.sample.TestItemActivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView)findViewById(android.R.id.list);

        List<String> testList = onCreateTestList();
        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testList));

        mListView.setOnItemClickListener(this);

        HttpExecutor.setDebug(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        String itemName = (String)parent.getAdapter().getItem(position);
        Intent intent = new Intent(this, TestItemActivity.class);
        intent.putExtra(TestItemActivity.KEY_ITEM_NAME, itemName);
        startActivity(intent);
    }

    protected List<String> onCreateTestList() {
        List<String> itemList = new ArrayList<String>();
        itemList.add("HttpGetTest");
        itemList.add("HttpsGetTest");
        itemList.add("HttpPostJSONTest");
        itemList.add("DownloadTest");

        return itemList;
    }

}
