package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.exceptions.CannotFindQuestionException;
import codesquad.exceptions.UnAuthorizedException;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    private final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    QnaService qnaService;

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) throws CannotFindQuestionException {
        return qnaService.findById(id).map(q -> {
            model.addAttribute("question", q);
            return "qna/show";
        }).orElseThrow(() -> new CannotFindQuestionException("no such question"));
    }

    @GetMapping("/form")
    public String showCreateForm(@LoginUser User loginUser) {
//        log.debug("login user info : ", loginUser.toString());
//        if (loginUser.isGuestUser()) {
//            return "user/login";
//        }
        return "qna/form";
    }

    @PostMapping("")
    public String createQuestion(@LoginUser User loginUser, QuestionDto questionDto) {
        Question createdQuestion = qnaService.create(loginUser, new Question(questionDto.getTitle(), questionDto.getContents()));
        return String.format("redirect:/questions/%d", createdQuestion.getId());
    }

    @GetMapping("/{id}/form")
    public String showUpdateForm(@LoginUser User loginUser, @PathVariable Long id, Model model) {
        return qnaService.findById(id).filter(q -> q.isOwner(loginUser))
                .map(q -> {
                    model.addAttribute("question", q);
                    return "qna/updateForm";
                }).orElseThrow(() -> new UnAuthorizedException("owner is not matched!"));
    }

}
