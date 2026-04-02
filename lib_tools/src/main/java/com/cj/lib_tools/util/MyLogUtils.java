package com.cj.lib_tools.util;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.cj.lib_tools.util.rxjava.RxjavaRetry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
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
 * @Description:
 * @Author: CJ
 * @CreateDate: 2024/10/30 16:33
 */
public class MyLogUtils {

    private static Disposable sDisposable;

    public static final String getTag(Object o){
        return o.getClass().getSimpleName();
    }

    private static final String LOG_DIR = "sdcard/log/";

    /**
     * 通过时间获取日志名
     * @param time
     * @return
     */
    private static File getLogFileByTime(long time){
        File file;
        if (time != 0){
            String date = TimeUtils.millis2String(time, new SimpleDateFormat("yyyy-MM-dd"));
            file = new File(LOG_DIR + date + ".log");
            FileUtils.createOrExistsFile(LOG_DIR + date + ".log");
        }else {
            List<File> list = FileUtils.listFilesInDir(LOG_DIR, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            if (list != null && list.size() > 0){
                file = list.get(0);
            }else {
                return getLogFileByTime(System.currentTimeMillis());
            }
        }

        return file;
    }

    /**
     * 开始保存日志
     */
    public static void startSaveLog() {
        stopSaveLog();
        MyTimeUtils.getNetTimeByRx()
                .onErrorReturn(new Function<Throwable, Long>() {
                    @Override
                    public Long apply(Throwable throwable) throws Throwable {
                        return 0l;
                    }
                })
                .map(new Function<Long, File>() {
                    @Override
                    public File apply(Long time) throws Throwable {
                        return getLogFileByTime(time);
                    }
                })
                .flatMap(new Function<File, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(File file) throws Throwable {
                        String cmd = "logcat -v threadtime ";
                        return MyShellUtils.execShellByRx(cmd, true)
                                .doOnNext(new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Throwable {
                                        FileIOUtils.writeFileFromString(file, s + "\n", true);
                                    }
                                }).retryWhen(new RxjavaRetry(10, 2, TimeUnit.SECONDS));
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        sDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull String s) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e("onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Timber.i("onComplete");
                    }
                });
    }

    /**
     * 停止保存日志
     */
    private static void stopSaveLog(){
        if (sDisposable != null && !sDisposable.isDisposed()){
            sDisposable.dispose();
        }
    }
}
