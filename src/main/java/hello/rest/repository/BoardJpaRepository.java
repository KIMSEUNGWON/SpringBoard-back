package hello.rest.repository;

import hello.rest.entity.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardJpaRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByName(String name);
}
