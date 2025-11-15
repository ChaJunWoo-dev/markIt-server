package com.cha.markit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DownloadUrlResponse {
    private String downloadUrl;
    private long expiresInSeconds;
}
