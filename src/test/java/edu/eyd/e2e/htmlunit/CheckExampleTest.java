package edu.eyd.e2e.htmlunit;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.CombinableMatcher.either;

@Slf4j
@RunWith(JUnit4ClassRunner.class)
public class CheckExampleTest {

    final static String DOCUMENT = "https://www.juanantonio.info";
    final static String HEADER_FIELD = "Date";

    WebClient webClient;

    @Before
    public void before() {
        webClient = getClient();
    }

    private static WebClient getClient() {
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.setCssErrorHandler(new SilentCssErrorHandler());
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setDownloadImages(true);
        webClient.getOptions().setDoNotTrackEnabled(false);
        return webClient;
    }

    @Test
    public void getDemoDocument_StateDefault_ExpectedHeaderOk() throws Exception {

        final HtmlPage page = webClient.getPage(DOCUMENT);
        WebResponse response = page.getWebResponse();

        checkHeaders(response);
    }

    @Test
    public void getDemoDocument_StateDefault_ExpectedImagesOk() throws Exception {

        final HtmlPage page = webClient.getPage(DOCUMENT);

        List<HtmlImage> image = page.<HtmlImage>getByXPath("//img");
        image.stream().forEach(x -> {
            try {
                downloadElement(webClient, page.getFullyQualifiedUrl(x.getSrcAttribute()));
            } catch (IOException e) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        });
    }

    @Test
    public void getDemoDocument_StateDefault_ExpectedScriptsOk() throws Exception {

        final HtmlPage page = webClient.getPage(DOCUMENT);

        List<HtmlScript> scripts = page.<HtmlScript>getByXPath("//script");
        scripts.stream().forEach(x -> {
            try {
                if(x.getSrcAttribute().equals("")) {
                    return;
                }
                downloadElement(webClient, page.getFullyQualifiedUrl(x.getSrcAttribute()));
            } catch (IOException e) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        });
    }

    @Test
    public void getDemoDocument_StateDefault_ExpectedCSSOk() throws Exception {

        final HtmlPage page = webClient.getPage(DOCUMENT);

        List<HtmlLink> scripts = page.<HtmlLink>getByXPath("//link");
        scripts.stream().forEach(x -> {
            try {
                LOGGER.debug("Original: " + x.getHrefAttribute());
                if(x.getHrefAttribute().startsWith("http")) {
                    downloadElement(webClient, new URL(x.getHrefAttribute()));
                } else if(x.getHrefAttribute().startsWith("//")) {
                    downloadElement(webClient, new URL(x.getHrefAttribute().replace("//", "https://")));
                } else {
                    downloadElement(webClient, page.getFullyQualifiedUrl(x.getHrefAttribute()));
                }

            } catch (IOException e) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        });
    }


    @Test
    public void getDemoDocument_StateDefault_ExpectedLinksOk() throws Exception {

        final HtmlPage page = webClient.getPage(DOCUMENT);

        List<HtmlAnchor> scripts = page.<HtmlAnchor>getByXPath("//a");
        scripts.stream().forEach(x -> {
            try {
                downloadElement(webClient, page.getFullyQualifiedUrl(x.getHrefAttribute()));
            } catch (IOException e) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        });
    }

    private static void downloadElement(WebClient webClient, URL url) throws IOException {

        LOGGER.debug("url: {}", url);
        final String accept = webClient.getBrowserVersion().getImgAcceptHeader();
        final WebRequest request = new WebRequest(url, accept);
        WebResponse imageWebResponse =  webClient.loadWebResponse(request);
        LOGGER.debug("Http Status: " + imageWebResponse.getStatusCode());
        assertThat(imageWebResponse.getStatusCode(), either(is(200)).or(is(999)));
        if(imageWebResponse.getStatusCode() != 200 && imageWebResponse.getStatusCode() != 999) {
            throw new RuntimeException("Bad element:" + url);
        }
        checkHeaders(imageWebResponse);
    }

    private static void checkHeaders(WebResponse response){
        final List<NameValuePair> headers = response.getResponseHeaders();

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

    @After
    public void after() {
        webClient.close();
    }

}