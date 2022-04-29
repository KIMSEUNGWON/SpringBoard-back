package hello.rest.advice;

import hello.rest.advice.exception.*;
import hello.rest.model.response.CommonResult;
import hello.rest.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    private final ResponseService responseService;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult defaultException(HttpServletRequest request, Exception e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(-999, "알 수 없는 오류입니다.");
    }

    @ExceptionHandler(CUserNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult userNotFoundException(HttpServletRequest request, CUserNotFoundException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(-1000, "존재하지 않는 회원입니다.");
    }

    @ExceptionHandler(CEmailSigninFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult emailSigninFailedException(HttpServletRequest request, CEmailSigninFailedException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(-1001, "계정이 존재하지 않거나 이메일 또는 비밀번호가 정확하지 않습니다.");
    }

    @ExceptionHandler(CAuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected CommonResult authenticationEntryPointException(HttpServletRequest request, CAuthenticationEntryPointException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(-1002, "해당 리소스에 접근하기 위한 권한이 없습니다.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected CommonResult accessDeniedException(HttpServletRequest request, AccessDeniedException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(-1003, "보유한 권한으로 접근할 수 없는 리소스 입니다.");
    }

    @ExceptionHandler(CNotOwnerException.class)
    @ResponseStatus(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
    protected CommonResult notOwnerException(HttpServletRequest request, CNotOwnerException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(-1004, "해당 자원의 소유자가 아닙니다.");
    }

    @ExceptionHandler(CResourceNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected CommonResult resourceNotExistException(HttpServletRequest request, CResourceNotExistException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(-1005, "요청한 자원이 존재하지 않습니다.");
    }

    @ExceptionHandler(CForbiddenWordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult forbiddenWordException(HttpServletRequest request, CForbiddenWordException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(-1006, String.format("입력한 내용에 금칙어(%s)가 포함되어 있습니다", e.getMessage()));
    }

    @ExceptionHandler(CUserDuplicatedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult userDuplicatedException(HttpServletRequest request, CUserDuplicatedException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(-1007, "계정이 이미 존재합니다.");
    }

    @ExceptionHandler(CPostNotInBoardException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult postNotInBoardException(HttpServletRequest request, CPostNotInBoardException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(-1008, "게시물이 게시판에 존재하지 않습니다.");
    }

    @ExceptionHandler(CBoardAlreadyExistedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult boardAlreadyExistedException(HttpServletRequest request, CBoardAlreadyExistedException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(-1009, "게시판이 이미 존재합니다.");
    }

    @ExceptionHandler(CNewPasswordNotMatchWithNewPasswordCheckException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult newPasswordNotMatchWithNewPasswordCheckException(HttpServletRequest request, CNewPasswordNotMatchWithNewPasswordCheckException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(-1010, "새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.");
    }

    @ExceptionHandler(CUserPasswordNotMatchException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult userPasswordNotMatchException(HttpServletRequest request, CUserPasswordNotMatchException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(-1011, "기존 비밀번호가 일치하지 않습니다.");
    }
}