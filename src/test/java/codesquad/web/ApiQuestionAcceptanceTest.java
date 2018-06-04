package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Autowired
    private UserRepository userRepository;

    private ResponseEntity<String> response;

    @Test
    public void create_success() {
        QuestionDto newQuestion = new QuestionDto("hello", "good");
        response = basicAuthTemplate().postForEntity("/api/questions", newQuestion, String.class);
        log.debug("response : {}", response);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String location = response.getHeaders().getLocation().getPath();
        log.debug("location : {}", location);
        QuestionDto dbQuestion = basicAuthTemplate().getForObject(location, QuestionDto.class);
        log.debug("db question : {}", dbQuestion);
        assertThat(dbQuestion.getTitle(), is("hello"));
    }

    @Test
    public void create_fail_no_login() {
        QuestionDto newQuestion = new QuestionDto("hello", "good");
        response = template().postForEntity("/api/questions", newQuestion, String.class);
        log.debug("response : {}", response);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

//
//    @Test
//    public void show_success() {
//        ResponseEntity<String> response = template().getForEntity("/api/questions/1", String.class);
//        assertThat(response.getStatusCode(), is(HttpStatus.OK));
//    }
//
//    @Test
//    public void show_fail() {
//        ResponseEntity<String> response = template().getForEntity("/api/questions/3", String.class);
//        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
//    }

    private Question defaultQuestion;
    private HttpEntity<MultiValueMap<String, Object>> request;

    @Test
    public void update_success() {
        defaultQuestion = defaultQuestion();
        request = updateQuestionReq();
        response = basicAuthTemplate().exchange("api/"+defaultQuestion.generateUrl(), HttpMethod.PUT, request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void update_fail_no_login() {
        defaultQuestion = defaultQuestion();
        request = updateQuestionReq();
        response = template().exchange("api/"+defaultQuestion.generateUrl(), HttpMethod.PUT, request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update_fail_other_user() {
        User otherUser = userRepository.findByUserId("sanjigi").get();
        defaultQuestion = defaultQuestion();
        request = updateQuestionReq();
        response = basicAuthTemplate(otherUser).exchange("api/"+defaultQuestion.generateUrl(), HttpMethod.PUT, request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void cannot_delete_for_owner_and_other_answers() {
        HttpEntity entity = getHttpEntity();
        defaultQuestion = defaultQuestion();

        response = basicAuthTemplate().exchange("api/"+defaultQuestion.generateUrl(), HttpMethod.DELETE, entity, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void can_delete_for_owner_and_owner_answers() {
        HttpEntity entity = getHttpEntity();
        Question question = findById(2L);
        String userId = "sanjigi";

        response = basicAuthTemplate(findByUserId(userId)).exchange("api/"+question.generateUrl(), HttpMethod.DELETE, entity, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void cannot_delete_for_other_user() {
        HttpEntity entity = getHttpEntity();
        defaultQuestion = defaultQuestion();
        User otherUser = userRepository.findByUserId("sanjigi").get();

        response = basicAuthTemplate(otherUser).exchange("api/"+defaultQuestion.generateUrl(), HttpMethod.DELETE, entity, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void cannot_delete_for_guest_user() {
        HttpEntity entity = getHttpEntity();
        defaultQuestion = defaultQuestion();

        response = template().exchange("api/"+defaultQuestion.generateUrl(), HttpMethod.DELETE, entity, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }


}
