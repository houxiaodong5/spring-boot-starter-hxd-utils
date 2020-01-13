package com.hxd.utils;



import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    private static List<String> str=new ArrayList<>();
    static {
        for(char c='A';c<='Z';c++){
            str.add(String.valueOf(c));
        }
        for(char c='a';c<='z';c++){
            str.add(String.valueOf(c));
        }
        for(char c='0';c<='9';c++){
            str.add(String.valueOf(c));
        }
    }
    private static Pattern p = Pattern.compile("\\s*|\t|\r|\n");

    public static List<String> splitStr(String tag) {
        List<String> list = new ArrayList<>();
        if (null != tag) {
            if (tag.contains(";")) {
                StringTokenizer tokenizer = new StringTokenizer(tag, ";");
                while (tokenizer.hasMoreTokens()) {
                    String str = tokenizer.nextToken();
                    Matcher m = p.matcher(str);
                    str = m.replaceAll("");
                    list.add(str);
                }
            }
            if (tag.contains("；")) {
                StringTokenizer tokenizer1 = new StringTokenizer(tag, "；");
                while (tokenizer1.hasMoreTokens()) {
                    String str = tokenizer1.nextToken();
                    Matcher m = p.matcher(str);
                    str = m.replaceAll("");
                    list.add(str);
                }
            }
            return list;
        } else return list;

    }

    //转义单引号
    public static String standardStr(String str) {
        if (null == str) {
            return null;
        }
        if (!str.contains("'")) {
            return str;
        }
        StringBuffer buffer = new StringBuffer(str);
        Boolean flag = true;
        while (flag) {
            for (int c = 0; c < buffer.length(); c++) {
                if (String.valueOf(buffer.charAt(c)).equals("'")) {
                    buffer.insert(c + 1, '\'');
                    c++;
                }
            }
            flag = false;
        }
        return buffer.toString();
    }

    /**
     *      获取指定长度的随机字符串
     * */
    public static String getRandomString(int length){
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        for(int i=0;i<length;i++){
            int nextInt = random.nextInt(str.size());

            buffer.append(str.get(nextInt));
        }

        return buffer.toString();

    }

    public static boolean isDigit(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static void main(String[] args) {
        System.out.println(isDigit("7b02fa6f739b11e996b900163e108cd9"));
    }


}
