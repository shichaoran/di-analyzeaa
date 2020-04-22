package com.vd.canary.data.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import org.springframework.util.StringUtils;

/**
 * 类名
 *
 * @author zichaoyang
 * @version $: StringUtil.java v1.0  2019年01月11日 11:54:58 zichaoyang Exp $
 * @name 类名
 */
public class StringUtil {


    public static String[] toStringArray(Collection<?> coll) {
        if (!CollectionUtils.isEmpty(coll)) {
            List<String> result = new ArrayList<>();
            for (Object t : coll) {
                if (!StringUtils.isEmpty(t)) {
                    result.add(String.valueOf(t));
                }
            }
            return StringUtils.toStringArray(result);
        }
        return new String[] {};
    }

    /**
     * 字符串以分隔符, separator切割
     * @param string 待切割的字符串
     * @param separator 分隔符
     * @return
     */
    public static List<String> splitToStringList(String string, String separator) {
        if (!StringUtils.isEmpty(string)) {
            return Splitter.on(separator).splitToList(string);
        }
        return Lists.newArrayList();
    }


    /**
     * 字符串转long集合
     * @param string
     * @param separator
     * @return
     */
    public static List<Long> splitToLongList(String string, String separator) {
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(string)){
            List<String> strings = StringUtil.splitToStringList(string, separator);
            List<Long> ids = Lists.newArrayList();
            for (String str : strings) {
                Long id = Long.valueOf(str.trim());
                ids.add(id);
            }
            return ids;
        }
        return null;
    }

    /**
     * 字符串转Integer集合
     * @param string
     * @param separator
     * @return
     */
    public static List<Integer> splitToIntegerList(String string, String separator) {
        List<String> strings = StringUtil.splitToStringList(string, separator);
        List<Integer> ids = Lists.newArrayList();
        for (String str : strings) {
            Integer id = Integer.valueOf(str.trim());
            ids.add(id);
        }
        return ids;
    }

    /**
     * 拼接list集合元素, 去空格, 格式: xxx,xxxx,xxx
     * @param list
     * @return
     */
    public static String appendElementWithList(List list) {
        if (!CollectionUtils.isEmpty(list)){
            String str = list.toString();
            str = str.substring(1, str.length() - 1);
            str = str.replaceAll(" ","");
            return str;
        }
        return "";
    }

    /**
     * 下划线转驼峰法(默认小驼峰)
     *
     * @param line
     *            源字符串
     * @param smallCamel
     *            大小驼峰,是否为小驼峰(驼峰，第一个字符是大写还是小写)
     * @return 转换后的字符串
     */
    public static String underline2Camel(String line, boolean ... smallCamel) {
        if (line == null || "".equals(line)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("([A-Za-z\\d]+)(_)?");
        Matcher matcher = pattern.matcher(line);
        //匹配正则表达式
        while (matcher.find()) {
            String word = matcher.group();
            //当是true 或则是空的情况
            if((smallCamel.length ==0 || smallCamel[0] ) && matcher.start()==0){
                sb.append(Character.toLowerCase(word.charAt(0)));
            }else{
                sb.append(Character.toUpperCase(word.charAt(0)));
            }

            int index = word.lastIndexOf('_');
            if (index > 0) {
                sb.append(word.substring(1, index).toLowerCase());
            } else {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰法转下划线
     *
     * @param line
     *            源字符串
     * @return 转换后的字符串
     */
    public static String camel2Underline(String line) {
        if (line == null || "".equals(line)) {
            return "";
        }
        line = String.valueOf(line.charAt(0)).toUpperCase()
                     .concat(line.substring(1));
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(word.toUpperCase());
            sb.append(matcher.end() == line.length() ? "" : "_");
        }
        return sb.toString();
    }
}