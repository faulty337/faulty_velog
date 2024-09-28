package com.faulty;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VelogRSSFetcher {
    private final String rssUrl;

    public VelogRSSFetcher(String rssUrl) {
        this.rssUrl = rssUrl;
    }

    public Map<String, String> getFeedSource() {
        Map<String, String> feeds = new HashMap<>();
        try {
            URL feedSource = new URL(rssUrl);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));

            FlexmarkHtmlConverter converter = FlexmarkHtmlConverter.builder().build();

            for (SyndEntry entry : feed.getEntries()) {
                String feedName = entry.getTitle().replace("/", "-").replace("\\", "-") + ".md";
                String sourceHtml = entry.getDescription().getValue();
                String sourceMarkdown = converter.convert(sourceHtml);

                feeds.put(feedName, sourceMarkdown);
            }

        } catch (FeedException | IOException e) {
            throw new RuntimeException(e);
        }
        return feeds;
    }

}
