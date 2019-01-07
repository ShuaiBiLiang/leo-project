package com.leo.util;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liang on 2017/6/6.
 */
public class Test {
    public static void main(String[] args) {
        String pricePiece = "ctl00$ContentPlaceHolder1$txtBuyMinPrice_value\" type=\"text\" value=\"1.0330\" maxle";
        Pattern pattern = Pattern.compile("value=\"([\\d|.]*)\"");
        Matcher matcher = pattern.matcher(pricePiece);
        if (matcher.find()) {
            pricePiece= matcher.group(1);
        }
        System.out.println(pricePiece);
    }
}
