package edu.eyd.e2e.htmlunit;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;

@Slf4j
@RunWith(JUnit4ClassRunner.class)
public class NavigationExampleTest {

    final static String DOCUMENT ="http://demo.guru99.com/test/newtours/";
    final static String HEADER_FIELD = "Date";

    @Test
    public void authenticate_StateDefault_ExpectedOk() throws Exception {

        final WebClient webClient = getClient();
        final HtmlPage page = webClient.getPage(DOCUMENT);
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
        WebResponse response2 = page2.getWebResponse();
        checkHeaders(response);
        assertThat(response2.getContentAsString(Charsets.UTF_8).indexOf("Login Successfully"), is(not(-1)));

        webClient.close();
    }

    @Test
    public void authenticate_StateDefault_ExpectedBadCredentials() throws Exception {

        final WebClient webClient = getClient();
        final HtmlPage page = webClient.getPage(DOCUMENT);
        WebResponse response = page.getWebResponse();
        checkHeaders(response);

        //Populate Login
        final HtmlForm form = page.getFormByName("home");

        final HtmlImageInput button = (HtmlImageInput) form.getInputsByName("submit").get(0);
        final HtmlTextInput textField = form.getInputByName("userName");
        final HtmlPasswordInput textField2 = form.getInputByName("password");

        textField.setValueAttribute("hello");
        textField2.setValueAttribute("world");

        final Page page2 = button.click();
        WebResponse response2 = page2.getWebResponse();
        checkHeaders(response);
        assertThat(response2.getContentAsString(Charsets.UTF_8).indexOf("Login Successfully"), is(-1));

        webClient.close();
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

        boolean existHeader = false;

        for (NameValuePair header : headers) {
            if(header.getName().equals(HEADER_FIELD)) {
                LOGGER.trace("Header detected: true");
                existHeader = true;
                break;
            }
        }

        assertThat(existHeader, is(true));
    }

}