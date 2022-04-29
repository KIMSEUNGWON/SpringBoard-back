package hello.rest.controller.board;

import hello.rest.dto.BoardCreateDto;
import hello.rest.entity.User;
import hello.rest.entity.board.Board;
import hello.rest.model.response.ListResult;
import hello.rest.model.response.SingleResult;
import hello.rest.service.BoardService;
import hello.rest.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"3. Board"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/board")
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final ResponseService responseService;

    @ApiOperation(value = "게시판 정보 조회", notes = "게시판 정보를 조회한다.")
    @GetMapping("/{boardName}")
    public SingleResult<Board> boardInfo(@ApiParam(value = "게시판 이름", required = true)
                                             @PathVariable String boardName,
                                         @AuthenticationPrincipal User user) {

        log.info("user: {}", user.getEmail());

        return responseService.getSingleResult(boardService.findBoard(boardName));
    }

    @ApiOperation(value = "게시판 생성", notes = "새로운 게시판을 생성한다")
    @PostMapping("/")
    public SingleResult<Board> boardCreate(@ApiParam(value = "게시판 이름", required = true)
                                               @RequestBody BoardCreateDto boardCreateDto,
                                           @AuthenticationPrincipal User user) {

        log.info("user: {}", user.getEmail());

        String boardName = boardCreateDto.getBoardName();
        return responseService.getSingleResult(boardService.insertBoard(boardName));
    }

    @ApiOperation(value = "게시판 리스트 조회", notes = "게시판 리스트를 조회한다.")
    @GetMapping("/")
    public ListResult<Board> boardList() {
        return responseService.getListResult(boardService.findBoardList());
    }
}
