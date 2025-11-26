package com.cha.markit.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "watermarks")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Watermark {

    @Id
    @Column(name = "s3_key")
    private String key;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String eTag;

    @Column(nullable = false)
    private int imageCount;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public String getZipKey() {
        return key + ".zip";
    }

    public String getThumbnailKey() {
        return key + "_thumb.jpg";
    }
}
