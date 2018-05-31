package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest{
    
    private final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    private HttpEntity<MultiValueMap<String, Object>> request;
    private ResponseEntity<String> response;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void cannot_read_for_inappropriate_question() {
        Long noQuestionId = 10L;
        response = template()
                .getForEntity(String.format("/questions/%d", noQuestionId), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void can_read_for_login_user() {
        Question defaultQuestion = defaultQuestion();
        response = basicAuthTemplate()
                .getForEntity(String.format("/questions/%d", defaultQuestion.getId()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void can_read_for_guest_user() {
        Question defaultQuestion = defaultQuestion();
        response = template()
                .getForEntity(String.format("/questions/%d", defaultQuestion.getId()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }
//
//    @Test
//    public void can_show_create_form_for_login_user() {
//        response = basicAuthTemplate()
//                .getForEntity("/questions/create", String.class);
//        assertThat(response.getStatusCode(), is(HttpStatus.OK));
//        log.debug("body : {}", response.getBody());
//    }
//
//    @Test
//    public void cannot_show_create_form_for_guest_user() {
//        response = template()
//                .getForEntity("/questions/create", String.class);
//        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
//        log.debug("body : {}", response.getBody());
//    }
//
//    @Test
//    public void can_create_for_login_user() {
//        request = HtmlFormDataBuilder.urlEncodedForm()
//                .addParameter("title", "질문 있어요!")
//                .addParameter("contents", "ㅇㅋ").build();
//
//        response = basicAuthTemplate().postForEntity("/questions", request, String.class);
//        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
//    }
//
//    @Test // 이 테스트를 해야하나?
//    public void cannnot_create_for_guest_user() {
//        request = HtmlFormDataBuilder.urlEncodedForm()
//                .addParameter("title", "질문 있어요!")
//                .addParameter("contents", "ㅇㅋ").build();
//
//        response = template().postForEntity("/questions", request, String.class);
//        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
//    }
//
//    @Test
//    public void can_show_update_form_for_owner() {
//
//    }
//
//    @Test
//    public void cannot_show_update_form_for_other_user() {
//
//    }
//
//    @Test
//    public void cannot_show_update_form_for_guest_user() {
//
//    }
//
//    @Test
//    public void cannot_update_for_other_user() {
//
//    }
//
//    @Test
//    public void cannot_update_for_guest_user() {
//
//    }
//
//    @Test
//    public void can_delete_for_owner() {
//
//    }
//
//    @Test
//    public void cannot_delete_for_other_user() {
//
//    }
//
//    @Test
//    public void cannot_delete_for_guest_user() {
//
//    }

}
