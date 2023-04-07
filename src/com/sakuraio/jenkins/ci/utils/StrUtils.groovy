package com.sakuraio.jenkins.ci.utils

import java.text.SimpleDateFormat

/**
 * <p>StrUtils</p>
 *
 * @author nekoimi 2023/02/14
 */
class StrUtils {

    private StrUtils() {
    }

    /**
     * 获取当前时间
     * @return
     */
    static String nowDatetime() {
        def dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormat.format(new Date())
    }

    /**
     * 清楚字符串开头和结尾的字符
     * @param text
     * @param regex
     * @return
     */
    static String cleanStartEndWiths(String text, String regex) {
        if (!text) {
            return text
        }
        if (text.startsWith(regex)) {
            text = text.replaceFirst(regex, "")
        }
        if (text.endsWith(regex)) {
            text = text.substring(0, text.length() - 1)
        }
        return text
    }
}
