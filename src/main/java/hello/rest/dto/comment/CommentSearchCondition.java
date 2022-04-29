package hello.rest.dto.comment;

import lombok.Data;

import java.util.Set;

@Data
public class CommentSearchCondition {

    private Long postId;
    private Set<Long> commentGroupNumbers;

    public CommentSearchCondition(Long postId) {
        this.postId = postId;
    }
}
