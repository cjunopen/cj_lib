package com.cj.lib_tools.util;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2025/3/14 20:32
 */
public class StringUtils {
    /**
     * 是否是汉字
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        return (c >= '\u4e00' && c <= '\u9fa5') || (c >= '\u3400' && c <= '\u4DBF');
    }

    /**
     * 是否包含中文
     * @param str
     * @return
     */
    public static boolean containsChinese(String str){
        for (int i = 0; i < str.length(); i++) {
            if (isChinese(str.charAt(i))){
                return true;
            }
        }
        return false;
    }

    /**
     * 是否纯英文（可以包含空格）
     * @param str
     * @return
     */
    public static boolean isEnglishOnly(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isLetter(str.charAt(i)) && str.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }
}
