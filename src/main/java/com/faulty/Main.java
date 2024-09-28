package com.faulty;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
        String repoPath = System.getProperty("REPO_PATH");
        String postsDir = System.getProperty("POSTS_DIR");
        String velogUrl = System.getProperty("RSS_URL");
        
        VelogRSSFetcher velogRSSFetcher = new VelogRSSFetcher(velogUrl);
        Map<String, String> feeds = velogRSSFetcher.getFeedSource();

        FileSaver fileSaver = new FileSaver();
        for(String key : feeds.keySet()) {
            try{
                String fileName = key.endsWith(".md") ? key.substring(0, key.length() - 3) : key; //key에 .md 확장자 붙어 있음
                fileSaver.saveMarkdown(repoPath, postsDir, fileName, feeds.get(key));
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }


    }
}