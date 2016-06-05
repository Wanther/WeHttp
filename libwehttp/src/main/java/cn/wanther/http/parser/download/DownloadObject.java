package cn.wanther.http.parser.download;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DownloadObject {

	public static final int STATUS_NEW = 0;
	public static final int STATUS_RUNNING = 1;
	public static final int STATUS_COMPLETE = 100;
	public static final int STATUS_CALCELLD = 101;

	public static final String SUFFIX_RUNNING = "" + STATUS_RUNNING;

	public static int getStatus(File destination) {
		if (destination.exists()) {
			return STATUS_COMPLETE;
		}

		File f = getRunningDestination(destination.getAbsolutePath());
		if (f.exists()) {
			return STATUS_RUNNING;
		}

		return STATUS_NEW;
	}

	public static File getRunningDestination(String path) {
		return new File(path + "." + SUFFIX_RUNNING);
	}

	private String url;
	private File destination;
	private boolean reset;
	private Map<String, Object> extra = new HashMap<String, Object>();
	private final Object extraLock = new Object();

	private long downloadSize;
	private long totalSize;

	public DownloadObject(String url, File destination, boolean reset) {
		this.url = url;
		this.destination = destination;
		this.reset = reset;
	}

	public String getUrl() {
		return url;
	}

	public boolean isReset() {
		return reset;
	}

	/*package*/ boolean isAppend() {
		if (reset) {
			return false;
		}

		return getRunningDestination().length() > 0;
	}

	public long getDownloadSize() {
		return downloadSize;
	}

	/*package*/ void setDownloadSize(long downloadSize) {
		this.downloadSize = downloadSize;
	}

	public long getTotalSize() {
		return totalSize;
	}

	/*package*/ void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public File getDestination() {
		return destination;
	}

	public int getStatus() {
		return getStatus(getDestination());
	}

	public void putExtra(String key, Object value) {
		synchronized (extraLock) {
			extra.put(key, value);
		}
	}

	public <T> T getExtra(String key, Class<T> type) {
		synchronized (extraLock) {
			return type.cast(extra.get(key));
		}
	}

	public <T> T getExtra(String key, Class<T> type, T defaultValue) {
		synchronized (extraLock) {
			T value = type.cast(extra.get(key));
			return value == null ? defaultValue : value;
		}
	}

	public File getRunningDestination() {
		return getRunningDestination(destination.getAbsolutePath());
	}

}
