package com.blue.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 파일 업로드 처리
    public String saveFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();

        System.out.println("bucket: " + bucket);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        // putObject()메소드가 파일을 저장해주는 메소드이다.
        amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);
        // getURL()을 통해 파일이 저장된 URL을 return 해주고, 이 URL로 이동 시 해당 파일이 오픈됨
        // (버킷 정책 변경을 하지 않았으면 파일은 업로드 되지만 해당 URL로 이동 시 accessDenied 됨)
        return amazonS3.getUrl(bucket, originalFilename).toString();
    }

    // 파일 다운로드 처리
    public ResponseEntity<UrlResource> downloadImage(String originalFilename) {

        // 로컬 파일 다운로드 할 때에는 UrlResource()메소드에 "file:" + 로컬 파일 경로를 넣어주면 로컬 파일이 다운로드 되었음.
        // S3에 올라간 파일은 아래와 같이 amazonS3.getUrl(버킷이름, 파일이름)을 통해 파일 다운로드를 할 수 있음
        UrlResource urlResource = new UrlResource(amazonS3.getUrl(bucket, originalFilename));

        String contentDisposition = "attachment; filename=\"" + originalFilename + "\"";

        // header에 CONTENT_DISPOSITION 설정을 통해 클릭 시 다운로드 진행
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);
    }

    
    
}
