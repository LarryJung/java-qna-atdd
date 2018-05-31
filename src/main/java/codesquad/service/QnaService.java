package codesquad.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import codesquad.dto.QuestionDto;
import codesquad.exceptions.CannotFindQuestionException;
import codesquad.exceptions.UnAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquad.exceptions.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;

@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question original = findById(loginUser, id); // 여기서도 유저 매칭 확인,
        original.update(loginUser, updatedQuestion); // 여기서도 유저 매칭 확인, 같은 확인절차를 반복해서 하는 이유? 더욱 안전하게?
        return questionRepository.save(original);
    }

    private Question findById(User loginUser, long id) {
        return questionRepository.findById(id)
                .filter(q -> q.isOwner(loginUser))
                .orElseThrow(() -> new UnAuthorizedException("owner is not matched!"));
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question original = findById(loginUser, questionId);
        log.debug("original question : {}", original);
        if (original.isDeletable(loginUser)) {
            log.debug("question {} will be deleted", questionId);
            original.logicalDelete();
            deleteHistoryService.registerHistory(loginUser, original);
            questionRepository.save(original);
            return;
        }
        throw new CannotDeleteException("other user's answers are still remained!");
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        return null;
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현 
        return null;
    }
}
