package com.faulty;

import java.util.Map;

public class Main {

    private static String repoPath = ".";
    private static String postsDir = "velog-posts";
    private static String velogUrl = "https://api.velog.io/rss/@faulty337";
    public static void main(String[] args) {
        VelogRSSFetcher velogRSSFetcher = new VelogRSSFetcher(velogUrl);
        Map<String, String> feeds = velogRSSFetcher.getFeedSource();

        FileSaver fileSaver = new FileSaver();
        for(String key : feeds.keySet()) {
            try{
                fileSaver.saveMarkdown(repoPath, postsDir, key.replace(".md", ""), feeds.get(key));
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }


    }
}