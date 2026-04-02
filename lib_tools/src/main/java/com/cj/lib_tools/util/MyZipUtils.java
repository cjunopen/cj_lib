package com.cj.lib_tools.util;

import com.cj.lib_tools.bean.ProgressInfo;
import com.cj.lib_tools.util.rxjava.RxjavaUtils;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2025/1/22 上午 10:04:53
 */
public class MyZipUtils {

    public interface ZipEntryListener {
        boolean onZipEntry(ZipFile zipFile, ZipEntry zipEntry);
    }

    /**
     * 支持解压中文
     */
    public static boolean unzipFile(String srcPath, String decompressPath) {
        return foreachZipFile(new File(srcPath), new ZipEntryListener() {
            @Override
            public boolean onZipEntry(ZipFile zipFile, ZipEntry zipEntry) {
                File decompressFile = new File(decompressPath + "/" + zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!decompressFile.exists()) {
                        decompressFile.mkdirs();
                    }
                } else {
                    //创建父级文件夹
                    if (!decompressFile.getParentFile().exists()) {
                        decompressFile.getParentFile().mkdirs();
                    }
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(decompressFile));
                         BufferedInputStream bi = new BufferedInputStream(zipFile.getInputStream(zipEntry))) {
                        return IOUtils.writeStream(bi, bos);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });
    }

    /**
     * rxjava 方式，带进度
     */
    public static Observable<ProgressInfo> unzipFileByRx(String srcPath, String destPath) {
        ProgressInfo progressInfo = new ProgressInfo();
        progressInfo.setSrcPath(srcPath).setDestPath(destPath);

        //解压中
        Observable<ProgressInfo> workObservable = Observable.create(new ObservableOnSubscribe<ProgressInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ProgressInfo> emitter) throws Throwable {
                if (!unzipFile(srcPath, destPath)) {
                    throw new Exception("解压失败");
                }
                emitter.onComplete();
            }
        }).compose(RxjavaUtils.obs_io_main());

        return MyFileUtils.getFileLengthObservable(progressInfo, workObservable)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Throwable {
                        progressInfo.setTotalLen(getZipFileContentSize(srcPath));
                    }
                });
    }

    /**
     * 遍历压缩包
     *
     * @param srcFile
     * @param listener
     */
    public static boolean foreachZipFile(File srcFile, ZipEntryListener listener) {
        ZipFile zf = null;
        try {
            zf = new ZipFile(srcFile, "GBK");
            Enumeration e = zf.getEntries();
            while (e.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) e.nextElement();
                listener.onZipEntry(zf, zipEntry);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (zf != null) {
                    zf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 压缩包内容的大小
     */
    public static long getZipFileContentSize(String srcPath) {
        return getZipFileContentSize(new File(srcPath));
    }

    /**
     * 压缩包内容的大小
     */
    public static long getZipFileContentSize(File srcFile) {
        final long[] size = {0L};
        foreachZipFile(srcFile, new ZipEntryListener() {
            @Override
            public boolean onZipEntry(ZipFile zipFile, ZipEntry zipEntry) {
                size[0] += zipEntry.getSize();
                return true;
            }
        });
        return size[0];
    }
}
