package com.likelion.demoday.global.aws;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    public String uploadImage(MultipartFile file) throws IOException {
        // 파일 이름 중복 방지 UUID
        String originalFilename = file.getOriginalFilename();
        String key = UUID.randomUUID() + "-" + originalFilename;

        // S3에 파일 업로드
        s3Template.upload(bucket, key, file.getInputStream());

        // URL 주소 생성
        String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);

        return imageUrl;
    }
}