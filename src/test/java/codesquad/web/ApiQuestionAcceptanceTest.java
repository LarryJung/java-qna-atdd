package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    private static final String BASE_URL = "/api/questions";

    private static final Long DEFAULT_QUESTION_ID = 1L;

    @Autowired
    private UserRepository userRepository;

    private ResponseEntity<String> response;

    private QuestionDto newQuestion;

    private String location;

    @Before
    public void setup() {
        newQuestion = new QuestionDto("title", "contents");
        location = createResource(BASE_URL, newQuestion);
    }

    @Test
    public void create_success() {
        QuestionDto newQuestion = new QuestionDto("hello", "good");
        response = basicAuthTemplate().postForEntity(BASE_URL, newQuestion, String.class);
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
        response = template().postForEntity(BASE_URL, newQuestion, String.class);
        log.debug("response : {}", response);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void show_success() {
        ResponseEntity<String> response = template().getForEntity(location, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void show_fail() { ;
        ResponseEntity<String> response = template().getForEntity(BASE_URL+"/100", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void update_success() {
        QuestionDto updateQuestion = new QuestionDto("tititile", "ccontents");
        basicAuthTemplate().put(location, updateQuestion);
        QuestionDto dbQuestion = basicAuthTemplate().getForObject(location, QuestionDto.class);
        assertThat(dbQuestion.toQuestion(), is(updateQuestion.toQuestion()));
    }

    @Test
    public void update_fail_other_user() {
        QuestionDto updateQuestion = new QuestionDto("tititile", "ccontents");
        User loginUser = userRepository.findByUserId("sanjigi").get();
        basicAuthTemplate(loginUser).put(location, updateQuestion);
        QuestionDto dbQuestion = basicAuthTemplate(loginUser).getForObject(location, QuestionDto.class);
        assertThat(dbQuestion.toQuestion(), is(newQuestion.toQuestion()));
    }

    @Test
    public void can_delete_for_owner_and_owner_answers() {
        User sanjigi = findByUserId("sanjigi");
        response = basicAuthTemplate(sanjigi).exchange(BASE_URL+"/2", HttpMethod.DELETE, getHttpEntity(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void cannot_delete_for_owner_and_other_answers() {
        User javajigi = defaultUser();
        response = basicAuthTemplate(javajigi).exchange(BASE_URL+"/"+DEFAULT_QUESTION_ID, HttpMethod.DELETE, getHttpEntity(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void cannot_delete_for_other_user() {
        response = basicAuthTemplate(defaultUser()).exchange(BASE_URL+"/2", HttpMethod.DELETE, getHttpEntity(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void cannot_delete_for_guest_user() {
        response = template().exchange(BASE_URL+"/"+DEFAULT_QUESTION_ID, HttpMethod.DELETE, getHttpEntity(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }


}
