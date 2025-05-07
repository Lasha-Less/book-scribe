package com.lb.book_scribe.scraping;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class InfoLinkScraper {

    public ScrapedBookData scrape(String infoLink, String canonicalVolumeLink) {
        ScrapedBookData data = tryScrape(infoLink);
        if (!isEmpty(data)) {
            data.setSourceUrl(infoLink);
        }

        if (isEmpty(data) && canonicalVolumeLink != null) {
            data = tryScrape(canonicalVolumeLink);
            if (!isEmpty(data)) {
                data.setSourceUrl(canonicalVolumeLink);
            }
        }

        return data != null ? data : new ScrapedBookData();
    }



    private ScrapedBookData tryScrape(String url) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String html = EntityUtils.toString(entity);
                    return parseHtml(html);
                }
            }
        } catch (IOException e) {
            // optionally log the failure
        }
        return null;
    }


    //check the elements you actually gonna need to scrape per link and update the method
    private ScrapedBookData parseHtml(String html) {
        Document doc = Jsoup.parse(html);
        ScrapedBookData data = new ScrapedBookData();

        data.setSummary(extractSummary(doc));
        data.setEditors(extractContributorsByLabels(doc, List.of("Editor", "Redacteur")));
        data.setTranslators(extractContributorsByLabels(doc, List.of("Translated by", "vertaald door")));
        data.setIsbns(extractIsbns(doc));


        return data;
    }



    private String extractSummary(Document doc) {
        Element summaryElement = doc.selectFirst("#synopsistext");
        return summaryElement != null ? summaryElement.text() : null;
    }



    private List<String> extractContributorsByLabels(Document doc, List<String> labelAliases) {
        Elements rows = doc.select("#metadata_content_table tr.metadata_row");

        for (Element row : rows) {
            Element labelCell = row.selectFirst("td.metadata_label");
            Element valueCell = row.selectFirst("td.metadata_value");

            if (labelCell != null && valueCell != null) {
                String label = labelCell.text().trim().toLowerCase();
                for (String alias : labelAliases) {
                    if (label.equals(alias.toLowerCase())) {
                        return valueCell.select("span").eachText();
                    }
                }
            }
        }

        return List.of(); // not found
    }

    private List<String> extractIsbns(Document doc) {
        String bodyText = doc.body().text();
        if (bodyText == null) return List.of();

        return java.util.regex.Pattern.compile("(97[89][-\\s]?[\\d-]{10,16}|\\b\\d{9}[\\dX]\\b)")
                .matcher(bodyText)
                .results()
                .map(mr -> mr.group().replaceAll("[^\\dX]", "")) // remove dashes, spaces
                .distinct()
                .toList();
    }


    private boolean isEmpty(ScrapedBookData data) {
        if (data == null) return true;

        return isNullOrEmpty(data.getEditors()) &&
                isNullOrEmpty(data.getTranslators()) &&
                (data.getSummary() == null || data.getSummary().isBlank());
    }



    private boolean isNullOrEmpty(List<String> list) {
        return list == null || list.isEmpty();
    }

}
