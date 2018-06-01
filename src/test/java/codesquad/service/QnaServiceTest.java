package codesquad.service;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityNotFoundException;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test(expected = EntityNotFoundException.class)
    public void read_fail_question_not_found() {
        long faultId = 1;
        when(questionRepository.findById(faultId)).thenReturn(Optional.empty());

        qnaService.findById(1);
    }

    @Test
    public void read_success() {
        long qId = 1;
        Question question = new Question("title", "good");
        when(questionRepository.findById(qId)).thenReturn(Optional.of(question));
        assertThat(qnaService.findById(qId), is(question));
    }


}
