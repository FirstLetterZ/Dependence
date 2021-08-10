package com.zpf.update;

/**
 * @author Created by ZPF on 2021/3/29.
 */
public interface INetCall {

    void checkVersion(final FileVersionInfo versionInfo, final INetResultListener listener);

    void download(final FileVersionInfo versionInfo, final IDownloadListener listener);
}
