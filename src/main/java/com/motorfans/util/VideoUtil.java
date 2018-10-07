package com.motorfans.util;

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
     * 执行命令行进行合并
     * @param cmd ffmpeg -i ./audio_input.m4a -i ./video_input.mp4 -acodec copy -vcodec copy outputFile.mp4
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
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection(proxy);
            conn.setConnectTimeout(3000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            InputStream inputStream = conn.getInputStream();
            writeFile(inputStream, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile;
    }

    /**
     *  写文件
     * @param inputStream
     * @param outputFile
     */
    public static void writeFile(InputStream inputStream, File outputFile) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File makeFile(String filePath) {
        int lastIndex = filePath.lastIndexOf("/");
        String dirPath = filePath.substring(0, lastIndex);
        File dirFile = new File(dirPath);
        if(! dirFile.exists()) {
            dirFile.mkdirs();
        }
        File file = new File(filePath);
        return file;
    }

}
