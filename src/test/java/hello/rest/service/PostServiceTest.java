package hello.rest.service;

import hello.rest.dto.BoardDto;
import hello.rest.dto.PostDto;
import hello.rest.dto.SignupDto;
import hello.rest.entity.board.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private SignService signService;

    private String email = "a@a.com";
    private String password = "123";
    private String name = "a";
    private String boardName = "free";

    @BeforeEach
    void init() {
        signService.signup(new SignupDto(email, password, name));
        boardService.insertBoard(boardName);
    }

    @Test
    void insertBoard() {
        // given

        // when

        // then
        assertThatThrownBy(() -> boardService.insertBoard(boardName));
    }

    @Test
    void findPosts() {
        // given
        for (int i = 0; i < 5; i++) {
            String author = "author" + i;
            String title = "title" + i;
            String content = "content" + i;
            BoardDto boardDto = new BoardDto(author, title, content);
            postService.writePost(boardName, email, boardDto);
        }

        // when
        List<Post> posts = postService.findPosts(boardName);

        // then
        assertThat(posts.size()).isEqualTo(5);
    }

    @Test
    void getPost() {
        // given
        BoardDto boardDto = new BoardDto("author", "title", "content");
        Post post = postService.writePost(boardName, email, boardDto);

        // when
        Post findPost = postService.getPostWithBoardName(boardName, post.getPostId());

        // then
        assertThat(post).isEqualTo(findPost);
    }

    @Test
    void isPostInBoard() {
        // given
        BoardDto boardDto = new BoardDto("author", "title", "content");
        Post post = postService.writePost(boardName, email, boardDto);
        Post findPost = postService.getPostWithBoardName(boardName, post.getPostId());

        // when
        boolean isPostInBoardTrue = postService.isPostInBoard(boardName, findPost.getPostId());

        boardService.insertBoard(boardName + 1);
        boolean isPostInBoardFalse = postService.isPostInBoard(boardName + 1, findPost.getPostId());

        // then
        assertThat(isPostInBoardTrue).isEqualTo(true);
        assertThat(isPostInBoardFalse).isEqualTo(false);
    }

    @Test
    void writePost() {
        // given
        BoardDto boardDto = new BoardDto("author", "title", "content");

        // when
        Post post = postService.writePost(boardName, email, boardDto);
        Post findPost = postService.getPostWithBoardName(boardName, post.getPostId());

        // then
        assertThat(post.getUser().getEmail()).isEqualTo(email);
        assertThat(post.getAuthor()).isEqualTo(boardDto.getAuthor());
        assertThat(post.getTitle()).isEqualTo(boardDto.getTitle());
        assertThat(post.getContent()).isEqualTo(boardDto.getContent());
        assertThat(post).isEqualTo(findPost);
    }

    @Test
    void updatePost() {
        // given
        BoardDto boardDto = new BoardDto("author", "title", "content");
        Post savePost = postService.writePost(boardName, email, boardDto);
        Post findPost = postService.getPostWithBoardName(boardName, savePost.getPostId());

        Long postId = findPost.getPostId();
        BoardDto update = new BoardDto("edit", "edit", "edit");

        // when
        Post updatePost = postService.updatePost(boardName, postId, email, update);

        // then
        assertThat(updatePost.getUser().getEmail()).isEqualTo(email);
        assertThat(updatePost.getAuthor()).isEqualTo(update.getAuthor());
        assertThat(updatePost.getTitle()).isEqualTo(update.getTitle());
        assertThat(updatePost.getContent()).isEqualTo(update.getContent());
    }

    @Test
    void updatePost2() {
        // given
        BoardDto boardDto = new BoardDto("author", "title", "content");
        Post savePost = postService.writePost(boardName, email, boardDto);
        Post findPost = postService.getPostWithBoardName(boardName, savePost.getPostId());

        Long postId = findPost.getPostId();
        BoardDto updateDto = new BoardDto("edit", "edit", "edit");

        // when
        PostDto updatePost = postService.updatePost(boardName, postId, updateDto);

        // then
        assertThat(updatePost.getAuthor()).isEqualTo(updateDto.getAuthor());
        assertThat(updatePost.getTitle()).isEqualTo(updateDto.getTitle());
        assertThat(updatePost.getContent()).isEqualTo(updateDto.getContent());
    }

    @Test
    void deletePost() {
        // given
        BoardDto boardDto = new BoardDto("author", "title", "content");
        Post savePost = postService.writePost(boardName, email, boardDto);
        Post findPost = postService.getPostWithBoardName(boardName, savePost.getPostId());

        Long postId = findPost.getPostId();

        // when
        boolean result = postService.deletePost(boardName, postId, email);

        // then
        assertThat(result).isEqualTo(true);
        assertThatThrownBy(() -> postService.getPostWithBoardName(boardName, postId));
    }

    @Test
    void deletePost2() {
        // given
        BoardDto boardDto = new BoardDto("author", "title", "content");
        Post savePost = postService.writePost(boardName, email, boardDto);
        Post findPost = postService.getPostWithBoardName(boardName, savePost.getPostId());

        Long postId = findPost.getPostId();

        // when
        postService.deletePost(boardName, postId);

        // then
        assertThatThrownBy(() -> postService.getPostWithBoardName(boardName, postId));
    }
}