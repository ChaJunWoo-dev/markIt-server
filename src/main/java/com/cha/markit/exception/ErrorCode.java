package com.cha.markit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    VALIDATION_ERROR("VALIDATION_ERROR", HttpStatus.BAD_REQUEST, "입력값 검증에 실패했습니다"),
    MISSING_PARAMETER("MISSING_PARAMETER", HttpStatus.BAD_REQUEST, "필수 파라미터가 누락되었습니다"),
    TYPE_MISMATCH("TYPE_MISMATCH", HttpStatus.BAD_REQUEST, "파라미터 타입이 올바르지 않습니다"),
    FILE_TOO_LARGE("FILE_TOO_LARGE", HttpStatus.PAYLOAD_TOO_LARGE, "업로드 파일 크기가 제한을 초과했습니다"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다");
    
    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
