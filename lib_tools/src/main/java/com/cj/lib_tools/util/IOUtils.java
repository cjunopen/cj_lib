package com.cj.lib_tools.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2025/1/22 上午 10:50:40
 */
public class IOUtils {
    /**
     * 输入流写到输出流
     *
     * @param is
     * @param os
     * @return
     */
    public static boolean writeStream(InputStream is, OutputStream os) {
        try {
            int len;
            byte[] b = new byte[1024 * 8];
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
                os.flush();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

}
