package com.motorfans.util;

import org.jsoup.Jsoup;

import java.io.*;
import java.net.*;

/**
 * 视频音频合并
 */
public class VideoUtil {


    /**
     * 提供视频和音频的路径参数，将两者合并
     * @param inputVideoFilePath video_input.mp4
     * @param inputAudioFilePath audio_input.m4a
     * @param outputFilePath -> output_video.mp4
     * @return
     */
    public static boolean mergeByPath(String inputVideoFilePath, String inputAudioFilePath, String outputFilePath) {
        String[] exeCmd = new String[]{"ffmpeg", "-i", inputAudioFilePath, "-i", inputVideoFilePath,
                "-acodec", "copy", "-vcodec", "copy", outputFilePath};
        Validate.isTrue(new File(inputVideoFilePath).exists(), "invalid video file path: " + inputVideoFilePath);
        Validate.isTrue(new File(inputAudioFilePath).exists(), "invalid audio file path: " + inputAudioFilePath);
        if(!new File(outputFilePath).exists()) {
            makeFile(outputFilePath);
        }
        ProcessBuilder pb = new ProcessBuilder(exeCmd);
        pb.redirectErrorStream(true);
        try {
            pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 执行命令行
     * @param cmd
     * @return
     */
    public static boolean executeCmd(String cmd) {
        String[] exeCmd = cmd.split(" ");
        ProcessBuilder pb = new ProcessBuilder(exeCmd);
        pb.redirectErrorStream(true);
        try {
            pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 下载文件到本地
     * @param urlStr
     * @param outputFile
     * @return
     * @throws MalformedURLException
     */
    public static File downloadFile(String urlStr, File outputFile) {
        return downloadFile(urlStr, outputFile, null);
    }

    public static File downloadFile(String urlStr, File outputFile, Proxy proxy) {
        try {
            System.out.println("downloading url: " + urlStr);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection(proxy);
            conn.setConnectTimeout(3000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            InputStream inputStream = conn.getInputStream();
            //写文件
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[512000];
            int len = 0;
            int size = 0;
            int total = conn.getContentLength();
            int progress = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
                size += len;
                int current = size*100/total;
                if(current != progress) {
                    System.out.println("progress->:" + current + "%" + " size:" + size + " total:" + total);
                    progress = current;
                }
            }
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile;
    }


    public static File makeFile(String filePath) {
        File file = new File(filePath);
        if(!file.exists()) {
            if(file.isDirectory()) {
                file.mkdirs();
            } else {
                file.getParentFile().mkdirs();
            }
        }
        return file;
    }

}
