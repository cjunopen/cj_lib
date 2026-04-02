package com.cj.lib_tools.util.rxjava;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2024/10/28 14:30
 */

import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import timber.log.Timber;

/**
 * Rxjava 实现延时重试
 */
public class RxjavaRetry implements
        Function<Observable<? extends Throwable>, Observable<?>> {

    private int mMaxRetries;
    private int mDelay;
    private int retryCount;
    private TimeUnit mTimeUnit;

    private OnRetryListener mOnRetryListener;

    public interface OnRetryListener {
        void onRetry(int retryCount, int maxRetries, Throwable throwable);
    }

    public RxjavaRetry(int maxRetries, int delay) {
        this(maxRetries, delay, TimeUnit.MILLISECONDS);
    }

    public RxjavaRetry(int maxRetries, int delay, OnRetryListener listener) {
        this(maxRetries, delay);
        mOnRetryListener = listener;
    }

    public RxjavaRetry(int maxRetries, int delay, TimeUnit unit) {
        mMaxRetries = maxRetries;
        mDelay = delay;
        mTimeUnit = unit;
    }

    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) throws Throwable {
        return observable
                .flatMap(new Function<Throwable, Observable<?>>() {
                    @Override
                    public Observable<?> apply(Throwable throwable) throws Throwable {
                        if (++retryCount <= mMaxRetries) {
                            if (!TextUtils.isEmpty(throwable.getMessage())) {
                                Timber.e("触发重试: " + throwable.getMessage());
                            }
                            // When this Observable calls onNext, the original Observable will be retried (i.e. re-subscribed).
                            Timber.e("get error, it will try after %s %s," +
                                    " retry count %s, maxRetries: %s", mDelay, mTimeUnit, retryCount, mMaxRetries);
                            if (mOnRetryListener != null) {
                                mOnRetryListener.onRetry(retryCount, mMaxRetries, throwable);
                            }
                            return Observable.timer(mDelay, mTimeUnit);
                        }
                        // Max retries hit. Just pass the error along.
                        return Observable.error(throwable);
                    }
                });
    }
}
