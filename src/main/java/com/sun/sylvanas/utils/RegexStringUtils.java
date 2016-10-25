package com.sun.sylvanas.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式Utils
 * <p>
 * Created by sylvanasp on 2016/10/25.
 */
public class RegexStringUtils {

    /**
     * @param patternStr 正则表达式
     * @param targetStr  目标字符串
     * @param group      组号
     */
    public static String regexString(String patternStr, String targetStr, int group) {
        if (patternStr == null || "".equals(patternStr)) {
            throw new IllegalArgumentException("正则表达式为空");
        }
        if (targetStr == null || "".equals(targetStr)) {
            throw new IllegalArgumentException("目标字符串为空");
        }

        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(targetStr);
        //判断是否找到
        if (matcher.find()) {
            return matcher.group(group);
        }
        return "";
    }

}
