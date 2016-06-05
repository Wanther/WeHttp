package cn.wanther.http.sample.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.wanther.http.HttpExecutor;
import cn.wanther.http.Request;
import cn.wanther.http.exception.AccessException;
import cn.wanther.http.executor.SimpleHttpExecutor;
import cn.wanther.http.parser.download.DownloadObject;
import cn.wanther.http.parser.download.DownloadParser;
import cn.wanther.http.parser.download.DownloadRequest;
import cn.wanther.http.sample.BuildConfig;
import cn.wanther.http.sample.R;

import java.io.File;
import java.io.IOException;

public class DownloadTestFragment extends Fragment implements View.OnClickListener, DownloadParser.DownloadListener {

	private static final String TAG = "DownloadTestFragment";

	private String mUrl;
	private ProgressBar mDownloadPB;
	private TextView mDownloadSizeTV;
	private DownloadParser mCurrentDownload;
	private Thread mDownloadThread;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		HttpExecutor.setDebug(true);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.download_test, container, false);

		mDownloadPB = (ProgressBar) v.findViewById(android.R.id.progress);
		mUrl = ((TextView) v.findViewById(android.R.id.text1)).getText().toString().trim();
		mDownloadSizeTV = (TextView) v.findViewById(android.R.id.text2);

		v.findViewById(android.R.id.button1).setOnClickListener(this);
		v.findViewById(android.R.id.button2).setOnClickListener(this);

		return v;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case android.R.id.button1:
				startDownload();
				break;
			case android.R.id.button2:
				cancelDownload();
				break;
		}
	}

	@Override
	public void onDownloadStart(DownloadObject downloadObject) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onDownloadStart");
		}
	}

	@Override
	public void onDownloadProgressChanged(final DownloadObject downloadObject) {
		final float lastProgress = downloadObject.getExtra("progress", Float.class);
		final float progress = downloadObject.getDownloadSize() * 1.0f / downloadObject.getTotalSize();
		if (progress - lastProgress > 0.01f) {
			downloadObject.putExtra("progress", progress);
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mDownloadSizeTV.setText(downloadObject.getDownloadSize() + "/" + downloadObject.getTotalSize());
					mDownloadPB.setProgress(Math.round(100 * progress));
				}
			});
		}
	}

	@Override
	public void onDownloadCancelled(DownloadObject downloadObject) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onDownloadCancelled");
		}
	}

	@Override
	public void onDownloadComplete(final DownloadObject downloadObject) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onDownloadComplete");
		}
		mCurrentDownload = null;
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				long totalLength = downloadObject.getDestination().length();
				mDownloadSizeTV.setText(totalLength + "/" + totalLength);
				mDownloadPB.setProgress(100);
			}
		});
	}

	protected void startDownload() {

		final DownloadObject downloadObject = new DownloadObject(mUrl, new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "DownloadTest.mp4"), false);
		downloadObject.putExtra("progress", 0f);

		mDownloadThread = new Thread() {
			@Override
			public void run() {

				Request req = new DownloadRequest(downloadObject);
				mCurrentDownload = new DownloadParser(downloadObject, DownloadTestFragment.this);
				try {
					new SimpleHttpExecutor().execute(req, mCurrentDownload);
				} catch (IOException e) {
					if (BuildConfig.DEBUG) {
						Log.e(TAG, e.getMessage(), e);
					}
				} catch (AccessException e) {
					if (BuildConfig.DEBUG) {
						Log.e(TAG, e.getMessage(), e);
					}
				}
			}
		};
		mDownloadThread.start();
	}

	protected void cancelDownload() {
//        if (mCurrentDownload != null) {
//            mCurrentDownload.cancel();
//        }
		if (mDownloadThread != null) {
			mDownloadThread.interrupt();
		}
	}
}
