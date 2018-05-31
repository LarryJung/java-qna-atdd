package codesquad.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such question")
public class CannotFindQuestionException extends Exception {

    public CannotFindQuestionException(String message) {
        super(message);
    }
}
