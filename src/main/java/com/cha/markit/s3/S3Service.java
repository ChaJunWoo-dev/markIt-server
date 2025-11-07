package com.cha.markit.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private final S3AsyncClient s3AsyncClient;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public CompletableFuture<String> uploadBytesAsync(String key, byte[] zipData) {
        log.info("S3 업로드 시작 - key: {}, size: {} bytes", key, zipData.length);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/zip")
                .build();

        return s3AsyncClient.putObject(objectRequest, AsyncRequestBody.fromBytes(zipData))
                .thenApply(response -> {
                    log.info("S3 업로드 완료 - key: {}, ETag: {}", key, response.eTag());

                    return response.eTag();
                })
                .exceptionally(e -> {
                    log.error("S3 업로드 실패 - key: {}", key, e);

                    throw new RuntimeException("S3 업로드 실패: " + e.getMessage(), e);
                });
    }
}
