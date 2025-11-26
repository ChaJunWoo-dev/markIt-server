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
    
    WATERMARK_NOT_FOUND("WATERMARK_NOT_FOUND", HttpStatus.NOT_FOUND, "워터마크를 찾을 수 없습니다"),
    FORBIDDEN("FORBIDDEN", HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),
    EMPTY_IMAGE_LIST("EMPTY_IMAGE_LIST", HttpStatus.BAD_REQUEST, "이미지가 비어있습니다"),
    
    INVALID_ID_TOKEN("INVALID_ID_TOKEN", HttpStatus.UNAUTHORIZED, "유효하지 않은 ID 토큰입니다"),
    ID_TOKEN_VERIFICATION_FAILED("ID_TOKEN_VERIFICATION_FAILED", HttpStatus.UNAUTHORIZED, "ID 토큰 검증에 실패했습니다"),
    
    IMAGE_READ_FAILED("IMAGE_READ_FAILED", HttpStatus.BAD_REQUEST, "이미지를 읽을 수 없습니다"),
    IMAGE_PROCESSING_FAILED("IMAGE_PROCESSING_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "이미지 처리 중 오류가 발생했습니다"),
    
    S3_UPLOAD_FAILED("S3_UPLOAD_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다"),
    S3_DELETE_FAILED("S3_DELETE_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다"),
    WATERMARK_DELETE_FAILED("WATERMARK_DELETE_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "워터마크 삭제에 실패했습니다"),
    
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다");
    
    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
