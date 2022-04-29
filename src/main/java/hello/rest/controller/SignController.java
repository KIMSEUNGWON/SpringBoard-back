package hello.rest.controller;

import hello.rest.config.security.JwtTokenProvider;
import hello.rest.dto.SigninDto;
import hello.rest.dto.SignupDto;
import hello.rest.model.response.SingleResult;
import hello.rest.service.ResponseService;
import hello.rest.service.SignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


@Api(tags = {"1. Sign"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Slf4j
public class SignController {

    private final SignService signService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseService responseService;
    private final PasswordEncoder passwordEncoder;

    @ApiOperation(value = "로그인", notes = "회원 로그인을 한다.")
    @PostMapping("/signin")
    public SingleResult<Map<String, String>> signin(
            @ApiParam(value = "회원 이메일, 비밀번호", required = true)
                @RequestBody @Valid SigninDto signinDto) {

        String token = signService.signin(signinDto);
        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        result.put("email", signinDto.getEmail());

        return responseService.getSingleResult(result);
    }

    @ApiOperation(value = "가입", notes = "회원가입을 한다.")
    @PostMapping("/signup")
    public SingleResult signup(@ApiParam(value = "회원 이메일, 비밀번호, 이름", required = true)
                                   @RequestBody @Valid SignupDto signupDto) {

        SignupDto signup = signService.signup(signupDto);

        return responseService.getSingleResult(signup);
    }
}
