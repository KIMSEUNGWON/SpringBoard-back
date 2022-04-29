package hello.rest.entity.board;

import hello.rest.dto.BoardDto;
import hello.rest.entity.Noticeable;
import hello.rest.entity.User;
import hello.rest.entity.comment.Comment;
import hello.rest.entity.common.CommonDateEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
public class Post extends CommonDateEntity implements Noticeable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false, length = 50)
    private String author;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //https://velog.io/@woodyn1002/%EC%82%BD%EC%A7%88-%EB%A1%9C%EA%B7%B8-Hibernate%EC%97%90%EC%84%9C-%EB%B6%80%EB%AA%A8%EA%B0%80-%EB%91%98%EC%9D%B8-Entity%EC%9D%98-%ED%95%9C%EC%AA%BD-%EB%B6%80%EB%AA%A8%EB%A5%BC-%EC%A7%80%EC%9A%B0%EB%A9%B4-%EC%B0%B8%EC%A1%B0-%EB%AC%B4%EA%B2%B0%EC%84%B1-%EC%98%A4%EB%A5%98%EA%B0%80-%EB%B0%9C%EC%83%9D%ED%95%98%EB%8A%94-%EB%AC%B8%EC%A0%9C
    // 참조 무결성 오류 해결을 위한 코드
    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Board getBoard() {
        return board;
    }

    public Post(User user, Board board, String author, String title, String content) {
        this.user = user;
        this.board = board;
        this.author = author;
        this.title = title;
        this.content = content;
    }

    public Post updatePost(String author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
        return this;
    }

    public void updatePost(BoardDto boardDto) {
        this.author = boardDto.getContent();
        this.title = boardDto.getTitle();
        this.content = boardDto.getContent();
    }

    public boolean isInBoard(Board board) {
        if (this.board == board) {
            return true;
        }
        return false;
    }

    public boolean isOwner(String userEmail) {
        if (this.user.getEmail().equals(userEmail)) {
            return true;
        }
        return false;
    }


    @Override
    public Long getTargetObjectId() {
        return postId;
    }

    @Override
    public Long getTargetUserId() {
        return user.getId();
    }

    @Override
    public String getFormatMessage(String actionUserName, String format) {
        return "내 게시글에 " + actionUserName + "님이 " + format;
    }

    @Override
    public String getFormatUrl() {
        String boardName = board.getName();
        return "/board/" + boardName + "/post/" + postId;
    }

    @Override
    public String getFormat() {
        return "댓글을 남겼습니다.";
    }
}
