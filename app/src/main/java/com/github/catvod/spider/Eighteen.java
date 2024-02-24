package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.bean.Class;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import android.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Eighteen extends Spider {

    private String url = "https://mjv002.com/zh/";
    private String cookie;

    private void getCookie() {
        try {
            cookie = OkHttp.newCall(url + "chinese_IamOverEighteenYearsOld/19/index.html").headers("set-cookie").get(0).split(";")[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("Cookie", cookie);
        return header;
    }

    @Override
    public void init(Context context, String extend) throws Exception {
        if(!extend.isEmpty()) {
            this.url = extend;
        }
        getCookie();
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        List<Class> classes = new ArrayList<>();
        List<Vod> list = new ArrayList<>();
        Document doc = Jsoup.parse(OkHttp.string(url, getHeader()));
        for (Element a : doc.select("ul.animenu__nav > li > a")) {
            String typeName = a.text();
            String typeId = a.attr("href").replace(url, "");
            if (!typeId.contains("random/all/")) continue;
            if (typeName.contains("18H")) break;
            classes.add(new Class(typeId, typeName));
        }
        for (Element div : doc.select("div.post")) {
            String id = div.select("a").attr("href").replace(url, "");
            String name = div.select("h3").text();
            String pic = div.select("a > img").attr("src");
            String remark = div.select("div.meta").text();
            list.add(new Vod(id, name, pic, remark));
        }
        return Result.string(classes, list);
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        List<Vod> list = new ArrayList<>();
        tid = tid.replace("random", "list");
        tid = tid.replace("index", pg);
        Document doc = Jsoup.parse(OkHttp.string(url + tid, getHeader()));
        for (Element div : doc.select("div.post")) {
            String id = div.select("a").attr("href").replace(url, "");
            String name = div.select("h3").text();
            String pic = div.select("a > img").attr("src");
            String remark = div.select("div.meta").text();
            list.add(new Vod(id, name, pic, remark));
        }
        return Result.string(list);
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String content = OkHttp.string(url + ids.get(0), getHeader());
        Document doc = Jsoup.parse(content);
        Element wrap = doc.select("div.video-wrap").get(0);
        String name = wrap.select("div.archive-title > h1").text();
        String pic = wrap.select("div.player-wrap > img").attr("src");
        Vod vod = new Vod();
        vod.setVodId(ids.get(0));
        vod.setVodPic(pic);
        vod.setVodName(name);
        vod.setVodPlayFrom("18AV");
        String playUrl = getPlayUrl(content);
        vod.setVodPlayUrl("播放$" + playUrl);
        return Result.string(vod);
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        return searchContent(key, "1");
    }

    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        return searchContent(key, pg);
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        return Result.get().parse().url(id).header(getHeader()).string();
    }

    private String searchContent(String key, String pg) {
        HashMap<String, String> params = new HashMap<>();
        params.put("search_keyword", key);
        params.put("search_type", "fc");
        params.put("op", "search");
        String res = OkHttp.post(url + "searchform_search/all/" + pg + ".html", params, getHeader()).getBody();
        List<Vod> list = new ArrayList<>();
        for (Element div : Jsoup.parse(res).select("div.post")) {
            String id = div.select("a").attr("href").replace(url, "");
            String name = div.select("h3").text();
            String pic = div.select("a > img").attr("src");
            String remark = div.select("div.meta").text();
            list.add(new Vod(id, name, pic, remark));
        }
        return Result.string(list);
    }

    public static String extractVariableValue(String javascriptCode, String variableName) {
        // Regular expression to match variable declaration
        Pattern pattern = Pattern.compile("\\b" + variableName + "\\s*=\\s*('[^']+'|\\d+)");
        Matcher matcher = pattern.matcher(javascriptCode);

        // Find the variable declaration
        if (matcher.find()) {
            String value = matcher.group(1).trim();
            // Remove surrounding quotes if value is a string
            if (value.startsWith("'") && value.endsWith("'")) {
                value = value.substring(1, value.length() - 1);
            }
            return value;
        } else {
            return ""; // Variable not found
        }
    }

    private String decrypt(String g, int hcdeedg252, int hadeedg252) {
        char[] f = new char[g.length()];

        hcdeedg252 = 25 >= hcdeedg252 ? hcdeedg252 : hcdeedg252 % 25;
        char h = (char) (hcdeedg252 + 97);
        String[] parts = g.split(String.valueOf(h));

        for (int i = 0; i < parts.length; i++) {
            int k = Integer.parseInt(parts[i], hcdeedg252);
            k = 1 * k ^ hadeedg252;
            f[i] = (char) k;
        }

        return new String(f).trim();
    }

    private String aesDecrypt(String g, int hcdeedg252, int hadeedg252, String argdeqweqweqwe, String hdddedg252 ) throws Exception {
        g = decrypt(g, hcdeedg252, hadeedg252);

        byte[] keyBytes = argdeqweqweqwe.getBytes();
        byte[] ivBytes = hdddedg252.getBytes();

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] encryptedBytes = Base64.decode(g, Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes);
    }

    private String getPlayUrl(String content) throws Exception {
        String g = getEncryptdValue(content);
        int hcdeedg252 = Integer.parseInt(extractVariableValue(content, "hcdeedg252"));
        int hadeedg252 = Integer.parseInt(extractVariableValue(content, "hadeedg252"));
        String argdeqweqweqwe = extractVariableValue(content, "argdeqweqweqwe");
        String hdddedg252 = extractVariableValue(content, "hdddedg252");
        String decVal = this.aesDecrypt(g, hcdeedg252, hadeedg252, argdeqweqweqwe, hdddedg252);
        return String.format("%sjs/player/play.php?id=%s", url.replace("zh/", ""), decVal);
    }

    private String getEncryptdValue(String content) {
        Pattern pattern = Pattern.compile("mvarr\\['10_1'\\]=\\[(.*?)\\];", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        List<List<String>> parsedArray = null;
        while (matcher.find()) {
            String arrayContent = matcher.group(1);
            parsedArray = parseArray(arrayContent);
        }
        return parsedArray.get(0).get(1);
    }

    public static List<List<String>> parseArray(String arrayContent) {
        List<List<String>> outerList = new ArrayList<>();

        // 去除首尾的方括号
        arrayContent = arrayContent.replaceAll("^\\[|\\]$", "");

        // 按逗号分割内部数组
        String[] innerArrays = arrayContent.split("\\],\\[");
        for (String innerArray : innerArrays) {
            // 去除内部数组首尾的方括号
            innerArray = innerArray.replaceAll("^\\[|\\]$", "");
            List<String> innerList = new ArrayList<>();
            // 按逗号分割元素，并去掉前后的单引号
            String[] elements = innerArray.split(",");
            for (String element : elements) {
                innerList.add(element.replaceAll("^'|'$", ""));
            }
            outerList.add(innerList);
        }

        return outerList;
    }
}
