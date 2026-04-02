package com.cj.lib_tools.util;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.cj.lib_tools.util.rxjava.RxjavaRetry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @Description: 日志工具（改造版：按 年月日文件夹 + 时分秒文件名 保存）
 * @Author: CJ
 */
public class MyLogUtils {

    private static Disposable sDisposable;

    public static final String getTag(Object o) {
        return o.getClass().getSimpleName();
    }

    // 根日志目录
    private static final String LOG_ROOT_DIR = "sdcard/log/";

    /**
     * 获取今天的日志文件夹：sdcard/log/2026-04-02/
     */
    private static String getTodayLogFolder() {
        String dateFolder = TimeUtils.millis2String(System.currentTimeMillis(),
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()));
        return LOG_ROOT_DIR + dateFolder + "/";
    }

    /**
     * 获取本次日志文件：sdcard/log/2026-04-02/2026-04-02_10-25-30.log
     */
    private static File getLogFileByTime(long time) {
        // 1. 文件夹：年月日
        String folderPath = getTodayLogFolder();

        // 2. 文件名：年月日_时分秒
        String fileName;
        if (time == 0){
            time = System.currentTimeMillis();
        }
        fileName = TimeUtils.millis2String(time,
                new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())) + ".log";

        // 3. 拼接完整路径
        File logFile = new File(folderPath, fileName);

        // 4. 自动创建文件夹 + 文件
        FileUtils.createOrExistsDir(folderPath);
        FileUtils.createOrExistsFile(logFile);

        return logFile;
    }

    /**
     * 开始保存日志
     */
    public static void startSaveLog() {
        stopSaveLog();
        MyTimeUtils.getNetTimeByRx()
                .onErrorReturn(throwable -> 0L)
                .map(time -> getLogFileByTime(time))
                .flatMap(file -> {
                    String cmd = "logcat -v threadtime ";
                    return MyShellUtils.execShellByRx(cmd, true)
                            .doOnNext(s -> FileIOUtils.writeFileFromString(file, s + "\n", true))
                            .retryWhen(new RxjavaRetry(10, 2, TimeUnit.SECONDS));
                })
                .observeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        sDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull String s) {}

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e("日志保存失败: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Timber.i("日志保存完成");
                    }
                });
    }

    /**
     * 停止保存日志
     */
    public static void stopSaveLog() {
        if (sDisposable != null && !sDisposable.isDisposed()) {
            sDisposable.dispose();
        }
    }
}