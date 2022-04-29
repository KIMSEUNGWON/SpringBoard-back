package hello.rest.service;

import hello.rest.advice.exception.CNotOwnerException;
import hello.rest.advice.exception.CPostNotInBoardException;
import hello.rest.advice.exception.CResourceNotExistException;
import hello.rest.annotation.ForbiddenWordCheck;
import hello.rest.dto.BoardDto;
import hello.rest.dto.PostDto;
import hello.rest.entity.User;
import hello.rest.entity.board.Board;
import hello.rest.entity.board.Post;
import hello.rest.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final BoardService boardService;
    private final PostJpaRepository postJpaRepository;
    private final UserService userService;

    public List<Post> findPosts(String boardName) {
        Board board = boardService.findBoard(boardName);
        return postJpaRepository.findByBoard(board);
    }

    public Post getPost(Long postId) {
        return postJpaRepository.findById(postId).orElseThrow(CResourceNotExistException::new);
    }

    public boolean isPostInBoard(String boardName, Long postId) {
        Board board = boardService.findBoard(boardName);
        Post post = this.getPost(postId);

        boolean result = post.isInBoard(board);

        return result;
    }

    public Post getPostWithBoardName(String boardName, Long postId) {
        if (!isPostInBoard(boardName, postId)) {
            throw new CPostNotInBoardException();
        }

        return this.getPost(postId);
    }

    public PostDto getPostWithBoardNameAndIsOwner(String boardName, Long postId, String email) {
        if (!isPostInBoard(boardName, postId)) {
            throw new CPostNotInBoardException();
        }

        Post post = postJpaRepository.findByIdFetchJoinUser(postId);
        PostDto postDto = new PostDto(post);
        isPostOwnedByUser(post, email, postDto);

        return postDto;
    }

    private void isPostOwnedByUser(Post post, String email, PostDto postDto) {
        User user = userService.findUser(email);
        if (post.getUser().equals(user)) {
            postDto.setOwner(true);
        } else {
            postDto.setOwner(false);
        }
    }

    @ForbiddenWordCheck
    public Post writePost(String boardName, String email, BoardDto boardDto) {
        Board board = boardService.findBoard(boardName);
        User user = userService.findUser(email);

        Post post = new Post(user, board, boardDto.getAuthor(), boardDto.getTitle(), boardDto.getContent());
        return postJpaRepository.save(post);
    }

    @ForbiddenWordCheck
    public Post updatePost(String boardName, Long postId, String email, BoardDto boardDto) {
        Post post = this.getPostWithBoardName(boardName, postId);
        User user = post.getUser();
        if (!email.equals(user.getEmail())) {
            throw new CNotOwnerException();
        }

        post.updatePost(boardDto.getAuthor(), boardDto.getTitle(), boardDto.getContent());
        return post;
    }

    @ForbiddenWordCheck
    public PostDto updatePost(String boardName, Long postId, BoardDto boardDto) {
        Post post = this.getPostWithBoardName(boardName, postId);
        post.updatePost(boardDto);

        PostDto postDto = new PostDto(post);

        return postDto;
    }

    public boolean deletePost(String boardName, Long postId, String email) {
        Post post = this.getPostWithBoardName(boardName, postId);
        User user = post.getUser();
        if (!email.equals(user.getEmail())) {
            throw new CNotOwnerException();
        }

        postJpaRepository.delete(post);
        return true;
    }

    public void deletePost(String boardName, Long postId) {
        Post post = this.getPostWithBoardName(boardName, postId);

        postJpaRepository.delete(post);
    }
}
