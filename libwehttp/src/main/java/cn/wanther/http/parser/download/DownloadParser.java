package cn.wanther.http.parser.download;

import cn.wanther.http.Parser;
import cn.wanther.http.Request;
import cn.wanther.http.Utils;
import cn.wanther.http.exception.AccessException;
import cn.wanther.http.parser.ErrorParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.wanther.http.parser.download.DownloadObject;

public class DownloadParser implements Parser<DownloadObject> {

	private static final int BUFFER_SIZE = 8 * 1024;

	private DownloadObject mDownloadObject;
	private DownloadListener mListener;
	private AtomicBoolean mCancelled = new AtomicBoolean(false);

	public DownloadParser(DownloadObject downloadObject, DownloadListener listener) {
		mDownloadObject = downloadObject;
		mListener = listener;
	}

	public void cancel() {
		mCancelled.set(true);
	}

	public boolean isCancelled() {
		if (Thread.interrupted()) {
			mCancelled.set(true);
		}
		return mCancelled.get();
	}

	@Override
	public DownloadObject parse(Request req, HttpResponse resp) throws IOException, AccessException {

		new ErrorParser().parse(req, resp);

		HttpEntity entity = resp.getEntity();
		if (entity == null) {
			return null;
		}

		final boolean isAppend = mDownloadObject.isAppend();

		final File runningDestination = mDownloadObject.getRunningDestination();

		// 请求断点续传但是服务器不支持206，删除tmp文件
		if (isAppend && resp.getStatusLine().getStatusCode() != HttpStatus.SC_PARTIAL_CONTENT) {
			if (runningDestination.exists()) {
				runningDestination.delete();
			}
		}

		final long runningDestinationSize = runningDestination.length();

		long totalLength = (isAppend ? runningDestinationSize : 0) + entity.getContentLength();
		long currentLength = isAppend ? runningDestinationSize : 0;

		InputStream input;
		OutputStream output = null;
		try {
			input = entity.getContent();
			output = new FileOutputStream(runningDestination, isAppend);

			mDownloadObject.setDownloadSize(currentLength);
			mDownloadObject.setTotalSize(totalLength);

			downloadStart(mDownloadObject);

			byte[] buffer = new byte[BUFFER_SIZE];
			int len;
			while ((len = input.read(buffer)) != -1) {
				output.write(buffer, 0, len);

				currentLength += len;

				mDownloadObject.setDownloadSize(currentLength);
				downloadProgressChanged(mDownloadObject);

				if (isCancelled()) {
					break;
				}

			}
		} finally {
			Utils.close(output);
		}

		if (isCancelled()) {
			downloadCancelled(mDownloadObject);
		} else {
			if (mDownloadObject.getDestination().exists()) {
				mDownloadObject.getDestination().delete();
			}
			runningDestination.renameTo(mDownloadObject.getDestination());
			downloadComplete(mDownloadObject);
		}

		return mDownloadObject;
	}

	protected void downloadStart(DownloadObject downloadObject) {
		if (mListener != null) {
			mListener.onDownloadStart(downloadObject);
		}
	}

	protected void downloadProgressChanged(DownloadObject downloadObject) {
		if (mListener != null) {
			mListener.onDownloadProgressChanged(downloadObject);
		}
	}

	protected void downloadCancelled(DownloadObject downloadObject) {
		if (mListener != null) {
			mListener.onDownloadCancelled(downloadObject);
		}
	}

	protected void downloadComplete(DownloadObject downloadObject) {
		if (mListener != null) {
			mListener.onDownloadComplete(downloadObject);
		}
	}

	public interface DownloadListener {

		void onDownloadStart(DownloadObject downloadObject);

		void onDownloadProgressChanged(DownloadObject downloadObject);

		void onDownloadCancelled(DownloadObject downloadObject);

		void onDownloadComplete(DownloadObject downloadObject);
	}
}
