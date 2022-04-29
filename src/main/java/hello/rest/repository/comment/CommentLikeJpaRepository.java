package hello.rest.repository.comment;

import hello.rest.entity.comment.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeJpaRepository extends JpaRepository<CommentLike, Long>, CommentLikeRepositoryCustom {

}
