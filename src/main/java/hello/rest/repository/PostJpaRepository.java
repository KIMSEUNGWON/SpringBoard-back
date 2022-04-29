package hello.rest.repository;

import hello.rest.entity.board.Board;
import hello.rest.entity.board.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostJpaRepository extends JpaRepository<Post, Long> {
    List<Post> findByBoard(Board board);

    @Query("select p from Post p left join p.user where p.postId = :postId")
    Post findByIdFetchJoinUser(@Param("postId") Long postId);
}
