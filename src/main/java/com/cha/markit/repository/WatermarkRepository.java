package com.cha.markit.repository;

import com.cha.markit.domain.Watermark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatermarkRepository extends JpaRepository<Watermark, String> {
    List<Watermark> findByUserIdOrderByCreatedAtDesc(String userId);
}
