package codesquad.dto;

import codesquad.domain.ContentType;
import codesquad.domain.User;

import java.time.LocalDateTime;

public class DeleteHistoryDto {

    private Long id;

    private ContentType contentType;

    private Long contentId;

    private User deletedBy;

    private LocalDateTime createDate;

    DeleteHistoryDto() {}

    public DeleteHistoryDto(Long id, ContentType contentType, Long contentId, User deletedBy, LocalDateTime createDate) {
        this.id = id;
        this.contentType = contentType;
        this.contentId = contentId;
        this.deletedBy = deletedBy;
        this.createDate = createDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public User getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(User deletedBy) {
        this.deletedBy = deletedBy;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
}
