package hello.rest.controller;

import hello.rest.dto.UserDto;
import hello.rest.dto.UserPasswordChangeDto;
import hello.rest.dto.UserUpdateDto;
import hello.rest.model.response.CommonResult;
import hello.rest.model.response.ListResult;
import hello.rest.model.response.SingleResult;
import hello.rest.service.ResponseService;
import hello.rest.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"2. User"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final ResponseService responseService;

    @ApiOperation(value = "회원 리스트 조회", notes = "모든 회원을 조회한다")
    @GetMapping("/users")
    public ListResult<UserDto> findAllUser() {

        List<UserDto> result = userService.findAll();

        // 결과 데이터가 여러 건인 경우 getListResult를 이용해서 출력한다
        return responseService.getListResult(result);
    }

    @ApiOperation(value = "회원 단 건 조회", notes = "userId로 회원을 조회한다")
//    @GetMapping("/user/{userId}")
    public SingleResult<UserDto> findUserById(
            @ApiParam(value = "회원 ID", required = true) @PathVariable Long userId) {

        UserDto result = userService.findUserById(userId);

        // 결과 데이터가 단일 건인 경우 getSingleResult를 이용해서 출력한다
        return responseService.getSingleResult(result);
    }

    @ApiOperation(value = "회원 수정", notes = "회원정보를 수정한다")
//    @PutMapping("/user/{userId}")
    public SingleResult<UserDto> modify(
            @ApiParam(value = "회원 ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "회원 이메일 및 이름", required = true)
            @RequestBody UserUpdateDto userUpdateDto) {

        UserDto result = userService.modifyUser(userId, userUpdateDto);

        return responseService.getSingleResult(result);
    }

    @ApiOperation(value = "회원 삭제", notes = "회원정보를 삭제한다")
//    @DeleteMapping("/user/{userId}")
    public CommonResult delete(
            @ApiParam(value = "회원 ID", required = true) @PathVariable Long userId) {

        userService.deleteUser(userId);

        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "회원 단 건 조회", notes = "email로 회원을 조회한다")
    @GetMapping("/user/{email}")
    public SingleResult<UserDto> findUserByEmail(
            @ApiParam(value = "회원 Email", required = true) @PathVariable String email) {

        UserDto result = userService.findUserById(userService.findUser(email).getId());

        // 결과 데이터가 단일 건인 경우 getSingleResult를 이용해서 출력한다
        return responseService.getSingleResult(result);
    }

    @ApiOperation(value = "회원 비밀번호 수정", notes = "회원 비밀번호를 수정한다")
    @PutMapping("/user/{email}")
    public SingleResult<UserDto> changePasswordByEmail(
            @ApiParam(value = "회원 email", required = true) @PathVariable String email,
            @ApiParam(value = "회원 현재 비밀번호, 새 비밀번호, 새 비밀번호 확인", required = true)
            @RequestBody UserPasswordChangeDto userPasswordChangeDto) {

        UserDto result = userService.changePasswordUser(email, userPasswordChangeDto);

        return responseService.getSingleResult(result);
    }

    @ApiOperation(value = "회원 삭제", notes = "회원정보를 삭제한다")
    @DeleteMapping("/user/{email}")
    public CommonResult deleteByEmail(
            @ApiParam(value = "회원 email", required = true) @PathVariable String email) {

        userService.deleteUser(userService.findUser(email).getId());

        return responseService.getSuccessResult();
    }
}
