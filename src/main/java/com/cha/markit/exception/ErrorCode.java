package com.cha.markit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // 400 Bad Request
    INVALID_IMAGE("INVALID_IMAGE", HttpStatus.BAD_REQUEST, "잘못된 이미지입니다"),
    INVALID_IMAGE_FORMAT("INVALID_IMAGE_FORMAT", HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다"),
    INVALID_IMAGE_SIZE("INVALID_IMAGE_SIZE", HttpStatus.BAD_REQUEST, "이미지 크기가 제한을 초과했습니다"),
    EMPTY_IMAGE("EMPTY_IMAGE", HttpStatus.BAD_REQUEST, "빈 이미지 파일입니다"),
    INVALID_WATERMARK_POSITION("INVALID_WATERMARK_POSITION", HttpStatus.BAD_REQUEST, "잘못된 워터마크 위치입니다"),
    INVALID_WATERMARK_SIZE("INVALID_WATERMARK_SIZE", HttpStatus.BAD_REQUEST, "워터마크 크기는 1~100 사이여야 합니다"),
    INVALID_WATERMARK_OPACITY("INVALID_WATERMARK_OPACITY", HttpStatus.BAD_REQUEST, "워터마크 투명도는 0.0~1.0 사이여야 합니다"),
    VALIDATION_ERROR("VALIDATION_ERROR", HttpStatus.BAD_REQUEST, "입력값 검증에 실패했습니다"),
    MISSING_PARAMETER("MISSING_PARAMETER", HttpStatus.BAD_REQUEST, "필수 파라미터가 누락되었습니다"),
    TYPE_MISMATCH("TYPE_MISMATCH", HttpStatus.BAD_REQUEST, "파라미터 타입이 올바르지 않습니다"),
    
    // 413 Payload Too Large
    FILE_TOO_LARGE("FILE_TOO_LARGE", HttpStatus.PAYLOAD_TOO_LARGE, "업로드 파일 크기가 제한을 초과했습니다"),
    
    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다");
    
    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
