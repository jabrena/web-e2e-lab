package edu.eyd.e2e.htmlunit;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

import java.io.IOException;
import java.util.List;

public class SimpleExample {

    public static void main(String... args) throws IOException, FailingHttpStatusCodeException {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_52);
        //webClient.setCssErrorHandler(new SilentCssErrorHandler());
        //webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setRedirectEnabled(false);
        webClient.getOptions().setAppletEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setPopupBlockerEnabled(false);
        webClient.getOptions().setTimeout(10000);
        HtmlPage page = (HtmlPage) webClient.getPage("https://as.com");
        System.out.println(page.asXml());

        List<NameValuePair> response = page.getWebResponse().getResponseHeaders();
        for (NameValuePair header : response) {
            System.out.println(header.getName() + " = " + header.getValue());
        }

        webClient.close();
    }
}
