package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import codesquad.domain.Answer;
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

    private Question defaultQuestion;
    private HttpEntity<MultiValueMap<String, Object>> request;

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
        QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);
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

    @Test
    public void show_success() {
        QuestionDto newQuestion = new QuestionDto("title", "contents");
        String location = createResource("/api/questions", newQuestion);
        ResponseEntity<String> response = template().getForEntity(location, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void show_fail() {
        QuestionDto newQuestion = new QuestionDto("title", "contents");
        String location = createResource("/api/questions", newQuestion);
        ResponseEntity<String> response = template().getForEntity("/api/questions/100", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void update_success() {
        QuestionDto newQuestion = new QuestionDto("title", "contents");
        String location = createResource("/api/questions", newQuestion);

        QuestionDto updateQuestion = new QuestionDto("tititile", "ccontents");

        basicAuthTemplate().put(location, updateQuestion);
        QuestionDto dbQuestion = basicAuthTemplate().getForObject(location, QuestionDto.class);

        assertThat(dbQuestion.toQuestion(), is(updateQuestion.toQuestion()));
    }

    @Test
    public void update_fail_other_user() {
        QuestionDto newQuestion = new QuestionDto("title", "contents");
        String location = createResource("/api/questions", newQuestion);

        QuestionDto updateQuestion = new QuestionDto("tititile", "ccontents");
        User loginUser = userRepository.findByUserId("sanjigi").get();
        basicAuthTemplate(loginUser).put(location, updateQuestion);

        QuestionDto dbQuestion = basicAuthTemplate(loginUser).getForObject(location, QuestionDto.class);
        assertThat(dbQuestion.toQuestion(), is(newQuestion.toQuestion()));
    }

    @Test
    public void can_delete_for_owner_and_owner_answers() {
        User sanjigi = findByUserId("sanjigi");
        HttpEntity entity = getHttpEntity();
        response = basicAuthTemplate(sanjigi).exchange("/api/questions/2", HttpMethod.DELETE, entity, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void cannot_delete_for_owner_and_other_answers() {
        User javajigi = defaultUser();
        HttpEntity entity = getHttpEntity();
        response = basicAuthTemplate(javajigi).exchange("/api/questions/1", HttpMethod.DELETE, entity, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void cannot_delete_for_other_user() {
        HttpEntity entity = getHttpEntity();
        response = basicAuthTemplate(defaultUser()).exchange("/api/questions/2", HttpMethod.DELETE, entity, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void cannot_delete_for_guest_user() {
        HttpEntity entity = getHttpEntity();
        defaultQuestion = defaultQuestion();
        response = template().exchange("/api/"+defaultQuestion.generateUrl(), HttpMethod.DELETE, entity, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }


}
