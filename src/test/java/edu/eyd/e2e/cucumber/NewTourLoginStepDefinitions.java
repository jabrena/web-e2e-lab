package edu.eyd.e2e.cucumber;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.common.base.Charsets;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.CombinableMatcher.either;
import static org.hamcrest.core.IsNot.not;

@Slf4j
public class NewTourLoginStepDefinitions {

    final static String DOCUMENT ="http://demo.guru99.com/test/newtours/";
    final static String REGISTER_PAGE = "http://demo.guru99.com/test/newtours/login.php";
    final static String HEADER_FIELD = "Date";

    WebClient webClient;
    HtmlPage page;

    private static WebClient getClient() {
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.setCssErrorHandler(new SilentCssErrorHandler());
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setDownloadImages(true);
        webClient.getOptions().setDoNotTrackEnabled(false);
        return webClient;
    }

    @Given("^a register document$")
    public void a_register_document() throws Throwable {
        webClient = getClient();

        page = webClient.getPage(REGISTER_PAGE);

        assertThat(page.asText().indexOf("Welcome back to Mercury Tours!"), is(not(-1)));
    }

    @When("^the user authenticate with the user \"([^\"]*)\" and password \"([^\"]*)\"$")
    public void the_user_authenticate_with_the_user_and_password(String arg1, String arg2) throws Throwable {

        //Populate Login
        final HtmlForm form = page.getFormByName("register");

        final HtmlImageInput button = (HtmlImageInput) form.getInputsByName("submit").get(0);
        final HtmlTextInput textField = form.getInputByName("userName");
        final HtmlPasswordInput textField2 = form.getInputByName("password");

        textField.setValueAttribute(arg1);
        textField2.setValueAttribute(arg2);

        page = (HtmlPage) button.click();
    }

    @Then("^the user will see the message \"([^\"]*)\"$")
    public void th_user_see_the_message(String arg1) throws Throwable {
        assertThat(page.asText().indexOf(arg1), is(not(-1)));
    }

    @Then("^the response headers are valid$")
    public void the_response_headers_are_valid() throws Throwable {
        WebResponse response = page.getWebResponse();
        checkHeaders(response);
    }


    @Then("^the CSS links are not broken$")
    public void the_CSS_links_are_not_broken() throws Throwable {

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

    @Then("^the Scripts links are not broken$")
    public void the_Scripts_links_are_not_broken() throws Throwable {

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

    @Then("^the Images links are not broken$")
    public void the_Images_links_are_not_broken() throws Throwable {

        List<HtmlImage> image = page.<HtmlImage>getByXPath("//img");
        image
                .stream()
                //.filter(x -> "/images/spacer.gif".equals(x.getSrcAttribute()))
                .forEach(x -> {
            try {
                LOGGER.info(x.getSrcAttribute());

                if(x.getSrcAttribute().equals("images/spacer.gif")) {
                    LOGGER.info("found");
                }else if(x.getSrcAttribute().equals("/images/spacer.gif")) {
                        LOGGER.info("found");
                }else {
                    downloadElement(webClient, page.getFullyQualifiedUrl(x.getSrcAttribute()));
                }

            } catch (IOException e) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        });
    }

    @Then("^the Links are not broken$")
    public void the_Links_are_not_broken() throws Throwable {

        List<HtmlAnchor> scripts = page.<HtmlAnchor>getByXPath("//a");
        scripts.stream().forEach(x -> {
            try {
                downloadElement(webClient, page.getFullyQualifiedUrl(x.getHrefAttribute()));
            } catch (IOException e) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        });
    }

    //TODO Migrate to a Generic class
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
}
