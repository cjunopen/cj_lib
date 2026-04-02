package com.cj.lib_tools.util;

import com.cj.lib_tools.util.rxjava.RxjavaUtils;

import java.util.Date;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2024/12/16 11:18
 */
public class MyTimeUtils {

    /**
     * @return 获取当前时间
     */
    public static Observable<Long> getNetTimeByRx() {
        return Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Long> emitter) throws Exception {
                Timber.i("请求获取网络时间");
                OkHttpClient okHttpClient = OkhttpUtils.getInstance().getOkHttpClient();
                Request request = new Request.Builder().get().url("http://www.baidu.com").build();
                try (Response response = okHttpClient.newCall(request).execute()){
                    //GMT转北京时间
                    long curTime = Date.parse(response.header("Date"));
                    emitter.onNext(curTime);
                    emitter.onComplete();
                } catch (Exception e) {
                    throw e;
                }
            }
        })
                .compose(RxjavaUtils.obs_io_main());
    }
}
