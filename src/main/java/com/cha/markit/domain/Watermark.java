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
    private String key;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String eTag;

    @Column(nullable = false)
    private int imageCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
