package com.sun.sylvanas.spider.breadth_first;

import com.sun.sylvanas.utils.HtmlParserUtils;
import com.sun.sylvanas.utils.HttpGetUtils;
import com.sun.sylvanas.utils.model.LinkFilter;

import java.util.Set;

/**
 * 一个简单的宽度优先查找爬虫
 * <p>
 * Created by sylvanasp on 2016/10/27.
 */
public class BfsSpider {

    /**
     * 使用种子初始化URL队列
     */
    private void initCrawlerWithSeeds(String[] seeds) {
        for (int i = 0; i < seeds.length; i++) {
            SpiderQueue.addUnvisitedUrl(seeds[i]);
        }
    }

    /**
     * 定义过滤器，
     * 提取以 <a target=_blank href="http://www.xxxx.com/"
     * style="color: rgb(0, 102, 153); text-decoration: none;">http://www.xxxx.com</a>开头的链接
     */
    public void crawling(String[] seeds) {
        final LinkFilter filter = new LinkFilter() {
            public boolean accept(String url) {
                if (url.startsWith("<a target=_blank href=\"http://www.baidu.com/\"" +
                        " style=\"color: rgb(0, 102, 153); text-decoration: none;\">" +
                        "http://www.baidu.com</a>")) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        //初始化URL队列
        initCrawlerWithSeeds(seeds);
        //循环条件：待抓取的链接不空且抓取的网页不多于 1000
        while (!SpiderQueue.unvisitedIsEmpty()
                && SpiderQueue.getVisitedUrlSize() <= 1000) {
            //取出队头URL并移出未访问队列
            String visitUrl = (String) SpiderQueue.removeFirstUnvisitedUrl();
            if (visitUrl == null) {
                continue;
            }
            //下载网页
            HttpGetUtils.downloadURL(visitUrl);
            //将该URL放入已访问队列
            SpiderQueue.addVisitedUrl(visitUrl);
            //提取出下载网页中的URL
            Set<String> links = HtmlParserUtils.extractLinks(visitUrl, filter);
            //将新的未访问URL入列
            for (String link : links) {
                SpiderQueue.addUnvisitedUrl(link);
            }
        }
    }

    public static void main(String[] args) {
        BfsSpider bfsSpider = new BfsSpider();
        bfsSpider.crawling(new String[]{"<a target=_blank href=\"http://www.baidu.com/\"" +
                "style=\"color: rgb(0, 102, 153);" +
                " text-decoration: none;\">http://www.baidu.com</a>"});
    }

}
