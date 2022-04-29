package hello.rest.controller.board;

import hello.rest.dto.BoardDto;
import hello.rest.dto.PostDto;
import hello.rest.entity.User;
import hello.rest.entity.board.Post;
import hello.rest.model.response.CommonResult;
import hello.rest.model.response.ListResult;
import hello.rest.model.response.SingleResult;
import hello.rest.service.PostService;
import hello.rest.service.ResponseService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"4. Post"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/board")
@Slf4j
public class PostController {

    private final PostService postService;
    private final ResponseService responseService;

    @ApiOperation(value = "게시판 글 리스트", notes = "게시판 게시글 리스트를 조회한다.")
    @GetMapping("/{boardName}/posts")
    public ListResult<PostDto> posts(@ApiParam(value = "게시판 이름", required = true)
                                      @PathVariable String boardName) {
        List<Post> posts = postService.findPosts(boardName);
        List<PostDto> result = posts.stream()
                .map(post -> new PostDto(post))
                .collect(Collectors.toList());
        return responseService.getListResult(result);
    }

    @ApiOperation(value = "게시판 글 생성", notes = "게시판에 글을 작성한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "게시판 이름", required = true, dataType = "String", name = "boardName"),
            @ApiImplicitParam(value = "저자, 제목, 내용", required = true, dataType = "BoardDto", name = "boardDto")
    })
    @PostMapping("/{boardName}")
    public SingleResult<PostDto> boardCreate(@PathVariable String boardName,
                                          @RequestBody BoardDto boardDto,
                                          @AuthenticationPrincipal User user) {

        log.info("user.email: {}", user.getEmail());
        log.info("boardDto: {}", boardDto.getTitle());

        String email = user.getEmail();
        Post post = postService.writePost(boardName, email, boardDto);
        PostDto result = new PostDto(post);
        return responseService.getSingleResult(result);
    }

    @ApiOperation(value = "게시판 글 상세", notes = "게시판 글 상세 정보를 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "게시판 번호", required = true, dataType = "Long", name = "postId"),
    })
    @GetMapping("/{boardName}/post/{postId}")
    public SingleResult<PostDto> post(@PathVariable String boardName,
                                      @PathVariable Long postId,
                                      @AuthenticationPrincipal User user) {
        return responseService.getSingleResult(postService.getPostWithBoardNameAndIsOwner(boardName, postId, user.getEmail()));
    }

    @ApiOperation(value = "게시판 글 수정", notes = "게시판 글을 수정한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "게시판 번호", required = true, dataType = "Long", name = "postId"),
            @ApiImplicitParam(value = "저자, 제목, 내용", required = true, dataType = "BoardDto", name = "boardDto")
    })
    @PreAuthorize("hasPermission(#postId, 'post', 'edit')")
    @PutMapping("/{boardName}/post/{postId}")
    public SingleResult<PostDto> post(@PathVariable String boardName,
                                   @PathVariable Long postId,
                                   @RequestBody BoardDto boardDto) {

        log.info("boardDto: {}", boardDto.getTitle());

        PostDto postDto = postService.updatePost(boardName, postId, boardDto);
        return responseService.getSingleResult(postDto);
    }

    @ApiOperation(value = "게시판 글 삭제", notes = "게시판 글을 삭제한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "게시판 번호", required = true, dataType = "Long", name = "postId"),
    })
    @PreAuthorize("hasPermission(#postId, 'post', 'delete')")
    @DeleteMapping("/{boardName}/post/{postId}")
    public CommonResult deletePost(@PathVariable String boardName,
                                   @PathVariable Long postId) {

        postService.deletePost(boardName, postId);
        return responseService.getSuccessResult();
    }
}
