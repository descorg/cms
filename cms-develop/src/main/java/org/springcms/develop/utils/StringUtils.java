package org.springcms.develop.utils;


public class StringUtils {

    /**
     * 按大驼峰命名
     * @param table
     * @return
     */
    public static String generateClassName(String table) {
        StringBuffer sb = new StringBuffer(table.substring(0, 1).toUpperCase());
        for (int i=1; i<table.length(); i++) {
            if (table.substring(i, i+1).equalsIgnoreCase("_")) {
                i++;
                sb.append(table.substring(i, i+1).toUpperCase());
            } else {
                sb.append(table.substring(i, i+1).toLowerCase());
            }
        }
        return sb.toString();
    }
}
