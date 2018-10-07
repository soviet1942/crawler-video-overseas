package com.motorfans.controller;

import com.motorfans.service.YoutubeDownload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/video")
public class VideoController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private YoutubeDownload youtubeDownload;

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download() {
        youtubeDownload.getUrl("_eQLFVpOYm4");
    }


}
