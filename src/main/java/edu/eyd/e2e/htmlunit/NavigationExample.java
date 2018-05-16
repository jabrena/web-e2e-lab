package edu.eyd.e2e.htmlunit;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.common.base.Charsets;

import java.util.List;

public class NavigationExample {

    public static void main(String[] args) throws Exception {

        final WebClient webClient = getClient();
        final HtmlPage page = webClient.getPage("http://demo.guru99.com/test/newtours/");
        //System.out.println(page.asXml());
        WebResponse response = page.getWebResponse();
        checkHeaders(response);

        //Populate Login
        final HtmlForm form = page.getFormByName("home");

        final HtmlImageInput button = (HtmlImageInput) form.getInputsByName("submit").get(0);
        final HtmlTextInput textField = form.getInputByName("userName");
        final HtmlPasswordInput textField2 = form.getInputByName("password");

        textField.setValueAttribute("demo");
        textField2.setValueAttribute("demo");

        final Page page2 = button.click();
        //System.out.println(page2.());
        //System.out.println(page2.asText().indexOf("Login Successfully"));
        WebResponse response2 = page2.getWebResponse();
        //System.out.println(response2.getContentAsString());
        checkHeaders(response);
        System.out.println(response2.getContentAsString(Charsets.UTF_8).indexOf("Login Successfully"));
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
