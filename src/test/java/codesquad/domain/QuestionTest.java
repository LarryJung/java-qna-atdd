package codesquad.domain;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertFalse;

public class QuestionTest {

    private final Logger log = LoggerFactory.getLogger(QuestionTest.class);

    private Question question;
    private Answer answer;
    private User writer1;
    private User writer2;

    @Before
    public void setup() {
        question = new Question("title", "contents");
        writer1 = new User("javajigi", "password", "name", "javajigi@slipp.net");
        writer2 = new User("sanjigi", "password", "name", "javajigi@slipp.net");
    }

    @Test
    public void deletable_true() {
        question.writeBy(writer1);
        question.addAnswer(new Answer(writer1, "hello"));
        assertTrue(question.isDeletable(writer1));
    }

    @Test
    public void deletable_false_owner_not_match() {
        question.writeBy(writer1);

        assertFalse(question.isDeletable(writer2));
    }

    @Test
    public void deletable_false_answer_owner_not_match() {
        question.writeBy(writer1);
        question.addAnswer(new Answer(writer2, "hello2"));
        assertFalse(question.isDeletable(writer1));
    }


}
