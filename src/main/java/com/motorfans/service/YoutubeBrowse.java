package com.motorfans.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeBrowse {

    private static String YOUTUBE_SEARCH_URL_TEMPLATE = "https://m.youtube.com/results?search_query=%s";
    private static String YOUTUBE_RESULT_URL_TEMPLATE = "https://m.youtube.com/results?itct=%s&ctoken=%s&pbj=1";
    private static final String USER_AGENT_MOBILE = "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Mobile Safari/537.36";

    public static void searchByName(String param) {
        try {
            String searchUrl = String.format(YOUTUBE_SEARCH_URL_TEMPLATE, URLEncoder.encode(param, "utf-8"));
            String html = Jsoup.connect(searchUrl).proxy("127.0.0.1", 1080).header("User-Agent", USER_AGENT_MOBILE).get().html();
            Matcher m = Pattern.compile("\"continuations\":\\[\\{\"nextContinuationData\":\\{\"continuation\":\"(.+)\",\"clickTrackingParams\":\"(.+)\",\"label\"").matcher(html);
            if(m.find()) {
                String ctoken = m.group(1);
                String itct = m.group(2);
                String resultUrl = String.format(YOUTUBE_RESULT_URL_TEMPLATE, itct, ctoken);
                String jsonStr = Jsoup.connect(resultUrl).ignoreContentType(true).proxy("127.0.0.1", 1080)
                        .header("User-Agent", USER_AGENT_MOBILE)
                        .header("x-youtube-client-name", "2")
                        .header("x-youtube-client-version", "2.20181103")
                        .get().text();
                JSONObject jsonRoot = JSONObject.parseObject(jsonStr);
                JSONArray jsonArray = jsonRoot.getJSONObject("response").getJSONObject("continuationContents")
                        .getJSONObject("itemSectionContinuation").getJSONArray("contents");
                for(int i=0; i<jsonArray.size(); i++) {
                    JSONObject compact = jsonArray.getJSONObject(i).getJSONObject("compactVideoRenderer");
                    String videoId = Optional.ofNullable(compact.getString("videoId")).orElse(compact.getString("playlistId"));
                    if(videoId == null) continue;
                    String title = compact.getJSONObject("title").getJSONArray("runs").getJSONObject(0).getString("text");

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        searchByName("机车");
    }
}
