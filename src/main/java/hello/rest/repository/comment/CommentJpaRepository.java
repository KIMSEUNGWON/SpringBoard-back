package hello.rest.repository.comment;

import hello.rest.entity.board.Post;
import hello.rest.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    List<Comment> findByPost(Post post);
}
