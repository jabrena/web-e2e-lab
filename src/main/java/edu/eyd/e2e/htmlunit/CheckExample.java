package edu.eyd.e2e.htmlunit;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class CheckExample {

    public static void main(String[] args) throws Exception {

        final WebClient webClient = getClient();
        final HtmlPage page = webClient.getPage("https://www.juanantonio.info");
        //System.out.println(page.asXml());
        WebResponse response = page.getWebResponse();
        checkHeaders(response);

        //Check images
        checkImages(webClient, page);

        //Check scripts
        checkScripts(webClient, page);

        //Check css/ico
        checkCSS(webClient, page);

        //Check link
        checkLinks(webClient, page);
    }

    private static void checkCSS(WebClient webClient, HtmlPage page) throws IOException {

        System.out.println("\nCheck CSS\n");

        List<HtmlLink> scripts = page.<HtmlLink>getByXPath("//link");
        scripts.stream().forEach(x -> {
            try {
                System.out.println("Original: " + x.getHrefAttribute());
                if(x.getHrefAttribute().startsWith("http")) {
                    downloadElement(webClient, new URL(x.getHrefAttribute()));
                } else if(x.getHrefAttribute().startsWith("//")) {
                    downloadElement(webClient, new URL(x.getHrefAttribute().replace("//", "https://")));
                } else {
                    downloadElement(webClient, page.getFullyQualifiedUrl(x.getHrefAttribute()));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void checkLinks(WebClient webClient, HtmlPage page) throws IOException {

        System.out.println("\nCheck Links\n");

        List<HtmlAnchor> scripts = page.<HtmlAnchor>getByXPath("//a");
        scripts.stream().forEach(x -> {
            try {
                downloadElement(webClient, page.getFullyQualifiedUrl(x.getHrefAttribute()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void checkScripts(WebClient webClient, HtmlPage page) throws IOException {

        System.out.println("\nCheck Scripts\n");

        List<HtmlScript> scripts = page.<HtmlScript>getByXPath("//script");
        scripts.stream().forEach(x -> {
            try {
                if(x.getSrcAttribute().equals("")) {
                    return;
                }
                downloadElement(webClient, page.getFullyQualifiedUrl(x.getSrcAttribute()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void checkImages(WebClient webClient, HtmlPage page) throws IOException {

        System.out.println("Check Images \n");

        List<HtmlImage> image = page.<HtmlImage>getByXPath("//img");
        image.stream().forEach(x -> {
            try {
                downloadElement(webClient, page.getFullyQualifiedUrl(x.getSrcAttribute()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        /*
        //TODO Remove
        //Bad Case
        URL url = new URL("http://www.juanantonio.info/Images/jabLogo2004.gif");
        downloadElement(webClient, url);
        */
    }

    private static void downloadElement(WebClient webClient, URL url) throws IOException {

            System.out.println(url);
            final String accept = webClient.getBrowserVersion().getImgAcceptHeader();
            final WebRequest request = new WebRequest(url, accept);
            WebResponse imageWebResponse =  webClient.loadWebResponse(request);
            System.out.println("Http Status: " + imageWebResponse.getStatusCode());
            if(imageWebResponse.getStatusCode() != 200 && imageWebResponse.getStatusCode() != 999) {
                throw new RuntimeException("Bad element:" + url);
            }
            checkHeaders(imageWebResponse);
    }

    private static WebClient getClient() {
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.setCssErrorHandler(new SilentCssErrorHandler());
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setDownloadImages(true);
        webClient.getOptions().setDoNotTrackEnabled(false);
        return webClient;
    }

    private static void checkHeaders(WebResponse response){
        List<NameValuePair> headers = response.getResponseHeaders();

        for (NameValuePair header : headers) {
            if(header.getName().equals("Date")) {
                System.out.println("Header detected: true");
                break;
            }
            //System.out.println(header.getName() + " = " + header.getValue());
        }
    }

}
