package com.motorfans.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.motorfans.common.Context;
import org.jsoup.Jsoup;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.*;

import static com.motorfans.util.RegexUtil.matchFirst;
import static com.motorfans.util.VideoUtil.downloadFile;
import static com.motorfans.util.VideoUtil.makeFile;
import static com.motorfans.util.VideoUtil.mergeByPath;


@Service
public class YoutubeDownload {

    //youtube视频信息url 模板
    private static final String YOUTUBE_INFO_URL_TEMPLATE = "https://www.youtube.com/get_video_info?video_id=%s";
    //youtube视频页面url 模板
    private static final String YOUTUBE_VIDEO_URL_TEMPLATE = "https://www.youtube.com/watch?v=%s";
    //默认请求头
    private static final String DEFAULT_HEADER = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
    //文件路径
    private static final String FILE_PATH = Context.getProp(Context.VIDEO_STORE_PATH);
    //视频合并超时时间
    private static int MERGE_TIMEOUT = Integer.parseInt(Context.getProp(Context.VIDEO_MERGE_TIMEOUT));
    //代理
    private static Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1080));
    //日志
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public void downloadVideo(String videoId) {
        //合成前文件路径前缀
        String rawPathPrefix = FILE_PATH + "/raw/" + videoId;
        //合成文件后的路径
        String cookedPath = FILE_PATH + "/cooked/" + videoId + ".mp4";
        String[] urls = getUrl(videoId);
        if(urls.length != 2) {
            logger.error("链接缺失, videoId={}, linkNum={}", videoId, urls.length);
        }
        File rawVideoFile = makeFile(rawPathPrefix + ".mp4");
        File rawAudioFile = makeFile(rawPathPrefix + ".m4a");
        //确保本地ss端口1080且全局代理开启
        String videoUrl = urls[0];
        String audioUrl = urls[1];
        String videoPath = downloadFile(videoUrl, rawVideoFile, proxy).getPath();
        String audioPath = downloadFile(audioUrl, rawAudioFile, proxy).getPath();
        //合并文件
        try {
            mergeByPath(videoPath, audioPath, cookedPath);
            int interval = 200;
            while (new File(cookedPath).length() == 0) {
                Thread.sleep(interval);
                MERGE_TIMEOUT -= interval;
                if(MERGE_TIMEOUT < 0) {
                    throw new IllegalArgumentException("视音频合并超时20s");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rawVideoFile.delete();
            rawAudioFile.delete();
        }
    }

    /**
     * 获取youtube 视频和音频真实地址
     * @param videoId
     * @return {视频真实地址，音频真实地址}
     * @throws IOException
     * @throws IllegalAccessException
     */
    public String[] getUrl(String videoId) {
        //存储结果
        String videoInfoUrl = String.format(YOUTUBE_INFO_URL_TEMPLATE, videoId);
        String videoPageUrl = String.format(YOUTUBE_VIDEO_URL_TEMPLATE, videoId);
        String[] urls = new String[2];
        //请求访问youtube 视频info信息
        String videoInfoSource = null;
        try {
            videoInfoSource = Jsoup.connect(videoInfoUrl).ignoreContentType(true).header("User-Agent", DEFAULT_HEADER)
                    .proxy("127.0.0.1", 1080).get().text().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, String> attrMap = parseCsvToMap(videoInfoSource);
        //判断状态是否ok
        if(!attrMap.get("status").equals("ok")) {
            logger.error("视频源出现异常");
            return null;
        }
        //获取标题
        String title = attrMap.get("title");
        //访问视频地址页面
        String videoPageSource = null;
        try {
            videoPageSource = Jsoup.connect(videoPageUrl).header("User-Agent", DEFAULT_HEADER)
                    .proxy("127.0.0.1", 1080).get().html();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //正则匹配出视频参数
        String ytPlayerConfigStr  = matchFirst("ytplayer\\.config\\s*=\\s*([^\\n]+?});", videoPageSource);
        JSONObject jsonObject = JSON.parseObject(ytPlayerConfigStr);
        //获取真实地址
        String adaptiveStr = jsonObject.getJSONObject("args").getString("adaptive_fmts");
        List<String> adaptiveList = Arrays.asList(adaptiveStr.split(","));
        logger.info("starting fetching youtube video => videoId={}, title={}", videoId, title);
        for(String adaptive : adaptiveList) {
            Map<String, String> map = parseCsvToMap(adaptive);
            String url = null;
            try {
                url = URLDecoder.decode(map.get("url"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String quality = map.get("quality_label") != null ? map.get("quality_label") : "";
            String type = getUrlParams(url).get("mime");
            type = type.split(";", 1)[0].split(":", 1)[0];
            String mediaType = type.split("/")[0];
            String videoFormat = type.split("/")[1];
            //只匹配mp4格式的视频和音频
            if(videoFormat.equals("mp4")) {
                if(mediaType.equals("video") && quality.equals("480p")) {
                    logger.info("mediaType={}, videoFormat={}, quality={}, url={}", mediaType, videoFormat, quality, url);
                    urls[0] = url;
                } else if(mediaType.equals("audio")) {
                    logger.info("mediaType={}, videoFormat={}, url={}", mediaType, videoFormat, url);
                    urls[1] = url;
                }
            }
        }
        return urls;
    }

    /**
     * 抽取url地址中的参数
     * @param url http:www.baidu.com?key1=pig&key2=dog
     * @return {key1=pig, key2=dog}
     */
    public static Map<String, String> getUrlParams(String url) {
        Map<String, String> resultMap = new HashMap<>();
        String params = url.split("\\?", 2)[1];
        for(String str1 : Arrays.asList(params.split("&"))) {
            String[] arr = str1.split("=", 2);
            if(arr.length == 2) {
                resultMap.put(arr[0], arr[1]);
            } else if(arr.length == 1) {
                resultMap.put(arr[0], "");
            }
        }
        return resultMap;
    }

    /**
     * 将cvs格式的字符串 格式化为映射的形式
     * @param source tom=cat&jerry=mouse&url=http%3A%2F%2Fwww.helloword.com%3Fa%3Db%26c%3Dd
     * @return map {tom=cat, jerry=mouse, url=http://www.helloword.com?a=b&c=d}
     */
    public static Map<String, String> parseCsvToMap(String source) {
        Map<String, String> resultMap = new HashMap<>();
        String[] arr1 = source.split("&");
        for(int i=0; i<arr1.length; i++) {
            String temp = "";
            try {
                temp = URLDecoder.decode(arr1[i], "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String[] arr2 = temp.split("=", 2);
            if(arr2.length == 2) {
                resultMap.put(arr2[0], arr2[1]);
            } else if(arr2.length == 1) {
                resultMap.put(arr2[0], "");
            }
        }
        return resultMap;
    }


    @Test
    //https://www.youtube.com/watch?v=_eQLFVpOYm4
    public void testProcess() throws FileNotFoundException {
        downloadVideo("_eQLFVpOYm4");
    }

}
