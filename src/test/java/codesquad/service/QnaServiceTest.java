package codesquad.service;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.exceptions.UnAuthorizedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityNotFoundException;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

//    @Mock
    Question question;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    @Test(expected = EntityNotFoundException.class)
    public void read_fail_question_not_found() {
        when(questionRepository.findById(anyLong())).thenReturn(Optional.empty());

        qnaService.findById(1);
    }

    @Test
    public void read_success() {
        Question question = new Question("title", "good");
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        assertThat(qnaService.findById(1), is(question));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_fail_owner_not_match() {
        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        User other = new User("javajigi", "password", "name", "javajigi@slipp.net");
        Question question = new Question("title", "good");
        question.writeBy(other);
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        when(questionRepository.findById(anyLong()).filter(q -> q.isOwner(user))).thenReturn(Optional.empty());

        qnaService.update(user, 1, question.toQuestionDto());
    }

    @Test
    public void update_success_owner_match() {
        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        Question question = new Question("title", "good");
        question.writeBy(user);
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        when(questionRepository.findById(anyLong()).filter(q -> q.isOwner(user))).thenReturn(Optional.of(question));

        qnaService.update(user, 1, question.toQuestionDto());
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_fail_owner_not_match() {
        User writer = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        Question deleteQuestion = new Question("title", "good");
        deleteQuestion.writeBy(writer);
        when(questionRepository.findById(anyLong())).thenReturn(Optional.empty());

        qnaService.deleteQuestion(writer, 1);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_fail_answer_writer_not_match() {
        User writer = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        User other = new User("javajigi", "password", "name", "javajigi@slipp.net");
        Question deleteQuestion = new Question("title", "good");
        deleteQuestion.writeBy(writer);
        deleteQuestion.addAnswer(new Answer(other, "hello"));
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(deleteQuestion));
        when(question.isDeletable(writer)).thenReturn(false);

        qnaService.deleteQuestion(writer, 1);

    }

    @Test
    public void delete_success() {
        User writer = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        Question deleteQuestion = new Question("title", "good");
        deleteQuestion.writeBy(writer);
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(deleteQuestion));
        when(question.isDeletable(writer)).thenReturn(true);
        doNothing().when(question).logicalDelete();
        doNothing().when(deleteHistoryService).registerHistory(writer, deleteQuestion);
        when(questionRepository.save(deleteQuestion)).thenReturn(deleteQuestion);

        Question returned = qnaService.deleteQuestion(writer, 1);
        assertThat(deleteQuestion, is(returned));
    }


}
