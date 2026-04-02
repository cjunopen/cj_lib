package com.cj.lib_tools.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2024/10/28 11:20
 */
@Getter
@Setter
public class DownloadInfo {
    //已下载长度
    private long downloadLen;

    //总长度
    private long totalLen;

    //进度
    private float progress;

    private String path = "";

    private String url = "";

    private String md5;

    /**
     * 计算进度
     */
    public float calculateProgress() {
        progress = downloadLen * 1.0f / totalLen;
        return progress;
    }
}
