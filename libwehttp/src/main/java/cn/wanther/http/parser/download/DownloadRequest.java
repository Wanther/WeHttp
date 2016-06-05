package cn.wanther.http.parser.download;

import cn.wanther.http.request.GetRequest;

public class DownloadRequest extends GetRequest {

    private DownloadObject mDownloadObject;

    public DownloadRequest(DownloadObject downloadObject) {
        super(downloadObject.getUrl());

        mDownloadObject = downloadObject;

        if (mDownloadObject.isAppend()) {
            long continueLength = mDownloadObject.getRunningDestination().length();
            setHeader("Range", "bytes=" + continueLength + "-");
        }
    }

}
