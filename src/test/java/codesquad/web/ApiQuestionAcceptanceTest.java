package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Test
    public void create() throws Exception {
        QuestionDto newQuestion = new QuestionDto("hello", "good");
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/api/questions", newQuestion, String.class);
        log.debug("response : {}", response);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String location = response.getHeaders().getLocation().getPath();
        log.debug("location : {}", location);
        QuestionDto dbQuestion = basicAuthTemplate().getForObject(location, QuestionDto.class);
        log.debug("db question : {}", dbQuestion);
        assertThat(dbQuestion.getTitle(), is("hello"));
    }



}
