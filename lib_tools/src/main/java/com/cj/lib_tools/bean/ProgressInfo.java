package com.cj.lib_tools.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ProgressInfo {
    //当前长度
    private long currentlen;

    //总长度
    private long totalLen;

    //进度
    private float progress;

    //源路径
    private String srcPath = "";

    private String destPath = "";

    /**
     * 计算进度
     */
    public float calculateProgress() {
        if (totalLen == 0) {
            return 0;
        }
        progress = currentlen * 1.0f / totalLen;
        return progress;
    }
}
