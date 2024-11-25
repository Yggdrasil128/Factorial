package de.yggdrasil128.factorial.scraper.satis;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class WikiIconScraper {

    private static final Logger LOG = LoggerFactory.getLogger(WikiIconScraper.class);
    private static final String HOST = "https://satisfactory.wiki.gg";

    public static Map<String, String> findIconPages() throws IOException {
        Map<String, String> result = new HashMap<>();
        String page = HOST + "/wiki/Special:PrefixIndex?prefix=File:&namespace=0&hideredirects=1&stripprefix=1";
        while (true) {
            LOG.trace("Reading Page: {}", page);
            Document document = Jsoup.connect(page).post();
            for (Element list : document.getElementsByClass("mw-prefixindex-list")) {
                for (Element listEntry : list.children()) {
                    Element anchor = listEntry.children().first();
                    String name = anchor.text();
                    if (name.endsWith(".mp4")) {
                        continue;
                    }
                    if (name.endsWith(".png")) {
                        name = name.substring(0, name.length() - ".png".length());
                    }
                    String iconPage = HOST + URLDecoder.decode(anchor.attr("href"), StandardCharsets.UTF_8.name());
                    result.put(name, iconPage);
                }
            }
            Elements nav = document.getElementsByClass("mw-prefixindex-nav");
            if (nav.isEmpty()) {
                break;
            }
            page = HOST + URLDecoder.decode(nav.first().children().first().attr("href"), StandardCharsets.UTF_8.name());
        }
        return result;
    }

    public static String readIconPage(String url) throws IOException {
        LOG.trace("Reading Page: {}", url);
        Document document = Jsoup.connect(url).get();
        Element file = document.getElementById("file");
        return null == file ? null : HOST + file.children().first().attr("href");
    }

}
