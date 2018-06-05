package codesquad.dto;

import codesquad.domain.Question;
import codesquad.domain.User;

import javax.validation.constraints.Size;
import java.util.Objects;

public class AnswerDto {

    private Question question;

    @Size(min = 5)
    private String contents;

    private boolean deleted = false;

    public AnswerDto() {
    }

    public AnswerDto(Question question, @Size(min = 5) String contents, boolean deleted) {
        this.question = question;
        this.contents = contents;
        this.deleted = deleted;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerDto answerDto = (AnswerDto) o;
        return deleted == answerDto.deleted &&
                Objects.equals(question, answerDto.question) &&
                Objects.equals(contents, answerDto.contents);
    }

    @Override
    public int hashCode() {

        return Objects.hash(question, contents, deleted);
    }

    @Override
    public String toString() {
        return "AnswerDto{" +
                "question=" + question +
                ", contents='" + contents + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
