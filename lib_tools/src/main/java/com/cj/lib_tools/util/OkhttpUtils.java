package com.cj.lib_tools.util;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.StringUtils;
import com.cj.lib_tools.bean.ProgressInfo;
import com.cj.lib_tools.util.rxjava.RxjavaRetry;
import com.cj.lib_tools.util.rxjava.RxjavaUtils;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2024/10/28 11:18
 */
public class OkhttpUtils {

    private OkHttpClient mOkHttpClient;

    public static OkhttpUtils getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private Map<String, Disposable> mUrlDisposable;

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 下载
     * @param url
     * @param path
     * @return
     */
    public Observable<ProgressInfo> download(String url, String path) {
        Disposable d = mUrlDisposable.get(url);
        if (d != null && !d.isDisposed()) {
            d.dispose();
        }

        ProgressInfo progressInfo = new ProgressInfo();
        progressInfo.setDestPath(path);

        // 创建下载文件对象，为了支持断点续传，要在下载前仅创建一次
        File file = new File(path);
        FileUtils.createFileByDeleteOldFile(file);

        Observable observable = Observable.create(new ObservableOnSubscribe<ProgressInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ProgressInfo> emitter) throws Throwable {
                // 断点续传：重新开始下载的位置：file.length()
                String range = StringUtils.format("bytes=%d-", file.length());
                Timber.i("download url: %s, range: %s", url, range);

                OkHttpClient client = mOkHttpClient;
                Request request = new Request.Builder()
                        .url(url)
                        .header("range", range)
                        .build();
                // 使用OkHttp请求服务器
                Call call = client.newCall(request);

                //try方式打开会自动关闭文件
                try (RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
                     Response response = call.execute();
                     ResponseBody body = response.body();
                     InputStream inputStream = body.byteStream()) {
                    if (response.code() == 404 || response.code() == 403) {
                        throw new Exception(response.message());
                    }
                    if (progressInfo.getTotalLen() == 0) {
                        progressInfo.setTotalLen(body.contentLength());
                    }
                    Timber.i("url = %s, path = %s, 下载前文件大小: %s B, 还需要下载: %s B",
                            url, file.getPath(), file.length(), progressInfo.getTotalLen() - file.length());
                    // 移动文件指针到断点续传的位置
                    accessFile.seek(file.length());
                    // 开始断点续传
                    byte[] bytes = new byte[1024 * 8];
                    int len;
                    while ((len = inputStream.read(bytes)) != -1) {
                        accessFile.write(bytes, 0, len);
                    }
                    emitter.onComplete();
                } catch (Exception e) {
                    throw e;
                }
            }
        })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Throwable {
                        mUrlDisposable.put(url, disposable);
                    }
                })
                .subscribeOn(Schedulers.io());

        return MyFileUtils.getFileLengthObservable(progressInfo, observable)
                .timeout(10, TimeUnit.SECONDS)
                .retryWhen(new RxjavaRetry(6 * 60 * 24, 2, TimeUnit.SECONDS))
                .compose(RxjavaUtils.obs_io_main());
    }

    private static class SingletonHolder {
        private static OkhttpUtils INSTANCE = new OkhttpUtils();
    }

    private OkhttpUtils() {
        mOkHttpClient = new OkHttpClient();
        mUrlDisposable = new HashMap<>();
    }
}
