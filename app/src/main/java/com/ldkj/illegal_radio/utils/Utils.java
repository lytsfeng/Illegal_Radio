package com.ldkj.illegal_radio.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by john on 15-5-6.
 */
public class Utils {
    /**
     * 判断字符串是否为全数字组成
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str.trim());
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
