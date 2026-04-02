package com.cj.lib_tools.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import timber.log.Timber;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2024/12/17 9:55
 */
public class MyShellUtils {
    /**
     * rxjava 模式执行shell
     *
     * @param cmd
     * @return
     */
    public static Observable<String> execShellByRx(String cmd, boolean isRetry) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                Timber.i("getExecObservable 开始执行：%s", cmd);
                Process process = Runtime.getRuntime().exec(cmd);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    emitter.onNext(line);
                }
                if (process != null) {
                    process.destroy();
                }
                if (isRetry) {
                    throw new Exception("execShellByRx 断开，重试");
                }else {
                    emitter.onComplete();
                }
            }
        });
    }
}
