package test;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test4 {

    ArrayList<String> baseUrl = new ArrayList<>();
    ArrayList<String> addedUrl = new ArrayList<>();

    public ArrayList<String> getAllUrls(String urls) {

        ArrayList<String> allUrls = new ArrayList<>();
        ArrayList<String> baseUrls = getBaseUrls(urls);

        for (String baseUrl : baseUrls) {

            allUrls.addAll(getImportsUrl(baseUrl));
            allUrls.addAll(getMediaUrl(baseUrl));
            allUrls.addAll(getLinkUrl(baseUrl));
        }
        return allUrls;
    }

    private ArrayList<String> getBaseUrls(String urls) {

        addedUrl.add(urls);
        ArrayList<String> allUrls = new ArrayList<>();
        ArrayList<String> baseUrl = new ArrayList<>();
        allUrls = getLinkUrl(urls);
        for (String url : allUrls) {
            if (url.contains(".html") || url.endsWith("/"))
                baseUrl.add(url);
        }
        for (int i = 0; i < baseUrl.size() - 1; i++) {
            for (int j = i + 1; j < baseUrl.size() - 1; j++) {
                if (baseUrl.get(j).equals(baseUrl.get(i)))
                    baseUrl.remove(j);
            }
            if (!baseUrl.get(i).contains("xxx.com"))
                baseUrl.remove(i);
        }
        this.baseUrl.addAll(baseUrl);
        for (int i = 0; i < baseUrl.size() - 1; i++) {
            baseUrl.remove(0);
            String url = baseUrl.get(i);
            if (!addedUrl.contains(url) && url.contains("xxx.com")) {
                // System.out.println("begin debug");
                // System.out.println(url);
                // System.out.println("end debug");
                getBaseUrls(url);
                // addedUrl.add(url);
            }
        }
        for (int i = 0; i < this.baseUrl.size() - 1; i++) {
            for (int j = i + 1; j < this.baseUrl.size() - 1; j++) {
                if (this.baseUrl.get(j).equals(this.baseUrl.get(i))) {
                    this.baseUrl.remove(j);
                }
            }
            if (!this.baseUrl.get(i).contains("xxx.com"))
                this.baseUrl.remove(i);
        }
        return this.baseUrl;
    }

    private ArrayList<String> getMediaUrl(String urls) {

        ArrayList<String> allUrls = new ArrayList<>();
        Document doc;
        try {
            doc = Jsoup.connect(urls).get();

            Elements media = doc.select("[src]");

            for (Element src : media) {
                String url = src.attr("abs:src");

                if (!url.isEmpty()) {
                    allUrls.add(url);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allUrls;
    }

    private ArrayList<String> getImportsUrl(String urls) {

        ArrayList<String> allUrls = new ArrayList<>();
        Document doc;
        try {
            doc = Jsoup.connect(urls).get();

            Elements imports = doc.select("link[href]");

            for (Element src : imports) {
                String url = src.attr("abs:href");

                if (!url.isEmpty()) {
                    allUrls.add(url);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allUrls;
    }

    private ArrayList<String> getLinkUrl(String urls) {

        ArrayList<String> allUrls = new ArrayList<>();
        Document doc;
        try {

            doc = Jsoup.connect(urls).get();

            Elements links = doc.select("a[href]");

            for (Element src : links) {
                String url = src.attr("abs:href");

                if (!url.isEmpty()) {
                    allUrls.add(url);
                }
            }
            for (int i = 0; i < allUrls.size() - 1; i++) {
                for (int j = i + 1; j < allUrls.size() - 1; j++) {
                    if (allUrls.get(j).equals(allUrls.get(i))) {
                        allUrls.remove(j);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allUrls;
    }

}