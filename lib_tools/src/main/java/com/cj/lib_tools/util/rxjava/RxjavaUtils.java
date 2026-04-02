package com.cj.lib_tools.util.rxjava;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.cj.lib_tools.bean.ProgressInfo;

import java.io.File;

import autodispose2.AutoDispose;
import autodispose2.AutoDisposeConverter;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2023/5/31 1:03
 */
public class RxjavaUtils {
    /**
     * rxjava 绑定生命周期
     * @param owner
     * @param event
     * @param <T>
     * @return
     */
    public static <T> AutoDisposeConverter<T> bindAutoDispose(LifecycleOwner owner, Lifecycle.Event event) {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner, event));
    }

    /**
     * rxjava 绑定生命周期
     * @param owner
     * @param <T>
     * @return
     */
    public static <T> AutoDisposeConverter<T> bindAutoDispose(LifecycleOwner owner) {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner, Lifecycle.Event.ON_DESTROY));
    }

    /**
     * 统一线程处理
     *
     * @param <T> 指定的泛型类型
     * @return ObservableTransformer
     */
    public static <T> ObservableTransformer<T, T> obs_io_main() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

}
