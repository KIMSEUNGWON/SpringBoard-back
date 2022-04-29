package hello.rest.dto;

import hello.rest.entity.board.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private Long postId;

    private String author;

    private String title;

    private String content;

    private boolean isOwner;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public PostDto(Post post) {
        this.postId = post.getPostId();
        this.author = post.getAuthor();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
    }
}
