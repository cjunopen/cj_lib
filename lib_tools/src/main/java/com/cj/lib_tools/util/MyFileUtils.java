package com.cj.lib_tools.util;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.cj.lib_tools.bean.ProgressInfo;
import com.cj.lib_tools.util.rxjava.RxjavaUtils;

import java.io.File;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2024/11/25 14:13
 */
public class MyFileUtils {
    /**
     * 复制文件，返回一个观察者观察进度
     *
     * @return
     */
    public static Observable<ProgressInfo> copyFileByRxjava(String srcPath, String destPath) {
        ProgressInfo progressInfo = new ProgressInfo();
        progressInfo.setSrcPath(srcPath).setDestPath(destPath);

        Observable<ProgressInfo> workObservable = Observable.create(new ObservableOnSubscribe<ProgressInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ProgressInfo> emitter) throws Throwable {
                if (!com.blankj.utilcode.util.FileUtils.copy(srcPath, destPath)) {
                    throw new Exception("文件复制失败");
                }
                emitter.onComplete();
            }
        }).compose(RxjavaUtils.obs_io_main());

        return getFileLengthObservable(progressInfo, workObservable)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Throwable {
                        progressInfo.setTotalLen(com.blankj.utilcode.util.FileUtils.getLength(srcPath));
                    }
                });
    }

    /**
     * 监听文件目录大小的变化，例如解压进度，复制进度，移动进度。工作前会先删除目标
     * 需要doOnSubscribe种setTotalLen
     *
     * @return
     */
    public static Observable<ProgressInfo> getFileLengthObservable(ProgressInfo progressInfo, Observable workObservable) {
        Timber.i("getFileLengthObservable progressInfo: " + GsonUtils.toJson(progressInfo));
        //监听目标file进度
        Observable<ProgressInfo> o2 = Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Long> emitter) throws Throwable {
                File destFile = new File(progressInfo.getDestPath());
                while (true) {
                    if (progressInfo.getTotalLen() == 0){
                        continue;
                    }
                    if (destFile.exists()) {
                        progressInfo.setCurrentlen(FileUtils.getLength(destFile));
                        progressInfo.calculateProgress();
                    }

                    emitter.onNext(progressInfo.getCurrentlen());

                    if (progressInfo.getCurrentlen() >= progressInfo.getTotalLen()) {
                        emitter.onComplete();
                        break;
                    }

                    Thread.sleep(100);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .distinct()
                .doFinally(new Action() {
                    @Override
                    public void run() throws Throwable {
                        Timber.i("文件监听完成: %s", GsonUtils.toJson(progressInfo));
                    }
                })
                .map(new Function<Long, ProgressInfo>() {
                    @Override
                    public ProgressInfo apply(Long aLong) throws Throwable {
                        return progressInfo;
                    }
                });

        return Observable
                .merge(workObservable, o2);
    }
}
