package com.motorfans.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexUtil {

    /**
     * 匹配所有
     * @param regex "W(or)(ld!)"
     * @param source "Hello,World! in Java."
     * @return "World!"
     */
    public static String match(String regex, String source) {
        Matcher matcher = Pattern.compile(regex).matcher(source);
        if(matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }

    /**
     * 匹配第一个
     * @param regex "W(or)(ld!)"
     * @param source "Hello,World! in Java."
     * @return "or"
     */
    public static String matchFirst(String regex, String source) {
        Matcher matcher = Pattern.compile(regex).matcher(source);
        if(matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    /**
     * 根据索引指定的位置匹配
     * @param regex "W(or)(ld!)"
     * @param index 2
     * @param source "Hello,World! in Java."
     * @return "ld!"
     */
    public static String getMatchByIndex(String regex, int index, String source) {
        Matcher matcher = Pattern.compile(regex).matcher(source);
        if(matcher.find()) {
            return matcher.group(index);
        } else {
            return null;
        }
    }

    /**
     * 匹配括号
     * @param regex "W(or)(ld!)"
     * @param source "Hello,World! in Java."
     * @return {"or", "ld"}
     */
    public static List<String> getMatchList(String regex, String source) {
        Matcher matcher = Pattern.compile(regex).matcher(source);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(matcher.group(0));
        }
        return result;
    }

    /**
     * 根据索引指定的多个位置匹配
     * @param regex "W(or)(ld!)"
     * @param index {1, 2}
     * @param source "Hello,World! in Java."
     * @return {1:"or", 2:"ld!"}
     */
    public static Map<Integer, String> getMatchesByIndexs(String regex, int[] index, String source) {
        Matcher matcher = Pattern.compile(regex).matcher(source);
        Map<Integer, String> result = new HashMap<>();
        if(matcher.find()) {
            for(int i : index) {
                result.put(i, matcher.group(i));
            }
        }
        return result;
    }

}
