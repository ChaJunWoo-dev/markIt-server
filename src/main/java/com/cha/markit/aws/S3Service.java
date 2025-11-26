package com.cha.markit.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private final S3AsyncClient s3AsyncClient;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public CompletableFuture<String> uploadBytesAsync(String key, byte[] data, String contentType) {
        log.info("S3 업로드 시작 - key: {}, size: {} bytes, contentType: {}", key, data.length, contentType);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        return s3AsyncClient.putObject(objectRequest, AsyncRequestBody.fromBytes(data))
                .thenApply(response -> {
                    log.info("S3 업로드 완료 - key: {}, ETag: {}", key, response.eTag());

                    return response.eTag();
                })
                .exceptionally(e -> {
                    log.error("S3 업로드 실패 - key: {}", key, e);

                    throw new RuntimeException("S3 업로드 실패: " + e.getMessage(), e);
                });
    }

    public String generatePresignedUrl(String key, Duration expiration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }

    public CompletableFuture<Object> deleteAsync(String key) {
        log.info("S3 삭제 시작 - key: {}", key);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3AsyncClient.deleteObject(deleteRequest)
                .thenApply(response -> {
                    log.info("S3 삭제 완료 - key: {}", key);
                    return null;
                })
                .exceptionally(e -> {
                    log.error("S3 삭제 실패 - key: {}", key, e);
                    throw new RuntimeException("S3 삭제 실패: " + e.getMessage(), e);
                });
    }
}
