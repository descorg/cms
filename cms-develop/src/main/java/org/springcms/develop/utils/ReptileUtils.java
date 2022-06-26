package org.springcms.develop.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springcms.develop.vo.ReptileVO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Web爬虫
 */
public class ReptileUtils {

    /**
     * 物友网
     * @param page
     * @return
     */
    public static List<ReptileVO> qiYe(Integer page) {
        Random r = new Random();
        List<ReptileVO> reptileList = new ArrayList<>();
        try {
            String url = String.format("https://qiye.56ye.net/search-htm-areaid-23-typeid--page-%d.html", page);
            Document doc = Jsoup.connect(url).get();
            Elements lists = doc.getElementsByClass("list");
            for (Element e : lists) {
                try {
                    ReptileVO reptile = new ReptileVO();

                    String name = e.getElementsByClass("sup-name").get(0).text().trim();
                    String logo = e.getElementsByTag("img").get(0).absUrl("src");
                    String tel = e.getElementsByClass("tel").get(0).text().trim();
                    Elements lis = e.getElementsByClass("q_fl");

                    reptile.setName(name);
                    reptile.setTitle(name);
                    reptile.setKeywords(name);
                    reptile.setDescription(name);
                    reptile.setImage(logo);
                    reptile.setTelephone(tel);
                    reptile.setAddress(lis.get(3).text().replace("公司地址：", "").trim());
                    reptile.setAuthor("物友网");
                    reptile.setCopyright("qiye.56ye.net");
                    reptile.setCreateTime(String.format("2022-%d-%d %d:%d:%d", r.nextInt(11) + 1, r.nextInt(30) + 1, r.nextInt(22) + 1, r.nextInt(59) + 1, r.nextInt(59) + 1));

                    reptileList.add(reptile);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return reptileList;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 东方财富
     * @param page
     * @return
     */
    public static List<ReptileVO> eastmoneyList(Integer page) {
        Random r = new Random();
        List<ReptileVO> reptileList = new ArrayList<>();
        try {
            String url = page == 1 ? "https://finance.eastmoney.com/a/cgnjj.html" : String.format("https://finance.eastmoney.com/a/cgnjj_%d.html", page);
            Document doc = Jsoup.connect(url).get();
            Element ul = doc.getElementById("newsListContent");
            if (ul != null) {
                Elements lis = ul.getElementsByTag("li");
                for (Element li : lis) {
                    ReptileVO reptile = new ReptileVO();

                    String title = li.getElementsByClass("title").get(0).text().trim();
                    String info = li.getElementsByClass("info").get(0).attr("title").trim();
                    String href = li.getElementsByTag("a").get(0).absUrl("href");

                    reptile.setName(title);
                    reptile.setTitle(title);
                    reptile.setDescription(info);
                    reptile.setUrl(href);
                    reptile.setCreateTime(String.format("2022-%d-%d %d:%d:%d", r.nextInt(11) + 1, r.nextInt(30) + 1, r.nextInt(22) + 1, r.nextInt(59) + 1, r.nextInt(59) + 1));

                    reptileList.add(reptile);
                }
            }

            for (int i=0; i<reptileList.size(); i++) {
                reptileList.set(i, eastmoneyInfo(reptileList.get(i)));
            }

            return reptileList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 东方财富文章详情
     * @param reptile
     * @return
     */
    public static ReptileVO eastmoneyInfo(ReptileVO reptile) {
        try {
            Document doc = Jsoup.connect(reptile.getUrl()).get();

            Elements metas = doc.getElementsByTag("meta");
            for (Element e : metas) {
                if (e.attr("name").equals("keywords")) {
                    reptile.setKeywords(e.attr("content"));
                    break;
                }
            }

            Element body = doc.getElementById("ContentBody");
            reptile.setBody(body.html());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reptile;
    }
}
