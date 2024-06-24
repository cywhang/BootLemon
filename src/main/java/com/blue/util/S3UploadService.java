package com.blue.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // ----------------------------------- 이미지 업로드 처리 ----------------------------------
    // MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
    // private upload 메소드에 전파되는 형식
    public void upload(MultipartFile multipartFile, String dirName, String fileName) throws IOException { // dirName의 디렉토리가 S3 Bucket 내부에 생성됨
        System.out.println("-----------S3uploadService----------");
        System.out.println("upload - bucket: " + bucket);
        System.out.println("upload - dirName: " + dirName);
        System.out.println("upload - fileName: " + fileName);
        // convert 메소드에서 multipartFile을 File로 반환받는 부분.
        // 전환중 에러가 나면 Exception메세지를 콘솔에 출력해준다.
        File uploadFile = convert(multipartFile, fileName)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));

        // convert에서 반환받은 파일을 upload에 넘겨준다.
        upload(uploadFile, dirName);
    }

    // upload 메소드에서 전파받는 곳
    // 이미지를 전환하는 과정에서(convert) 임시로 로컬에 저장하기 때문에
    // putS3메소드에 파일을 넘겨주고 로컬에 저장된 파일을 삭제처리 하는 과정
    private void upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + uploadFile.getName();

        // 직접적으로 S3에 접근하여 이미지를 업로드 하는 부분.
        putS3(uploadFile, fileName);

        // convert()함수로 인해서 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)
        removeNewFile(uploadFile);
    }

    // 직접적으로 S3에 접근하여 이미지를 업로드 하는곳
    private void putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)	// PublicRead 권한으로 업로드 됨
        );

    }

    // 임시로 로컬에 저장된 파일 삭제하는곳
    private void removeNewFile(File targetFile) {
        if(targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        }else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    // MultipartFile을 File로 전환하는곳
    // Optianal을 사용하는 이유
    // 파일을 생성하고 변환하는데 실패할 수 있기때문에 (파일 시스템 권한 문제, 디스크 공간 부족 등)
    // 실패 상황을 처리하기 위해 Optional을 사용한다.
    private Optional<File> convert(MultipartFile file, String fileName) throws  IOException {
        // 전환받을 파일 객체 생성
        File convertFile = new File(fileName);
        
        // 파일이 정상적으로 생성 되었을때
        if(convertFile.createNewFile()) {
            // 파일 생성 후
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                // Multipart에서 바이트 배열로 변환하여 파일에 쓰기
                fos.write(file.getBytes());
            }
            // Optional로 감싸서 생성된 파일을 반환
            return Optional.of(convertFile);
        }
        // 파일이 이미 존재하거나 생성에 실패한 경우 비어있는 Optional을 반환
        return Optional.empty();
    }


    // -------------------------- 이미지 삭제 처리 -------------------------------
    public Boolean deleteFile(String FilePath, String FileName) {
        try {
            // 폴더명 + 파일이름.확장자
            String keyName = FilePath + FileName;
            boolean isObjectExist = amazonS3Client.doesObjectExist(bucket, keyName);
            if (isObjectExist) {
                amazonS3Client.deleteObject(bucket, keyName);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.debug("Delete File failed", e);
        }
        return true;
    }


    // ----------------------- 이미지 수정 처리 ---------------------------------
    public void renameFile(String sourceKey, String destinationKey) {
        // sourceKey      = post/post_Seq-i  -> 복사할 객체이름
        // destinationKey = post/post_Seq-i  -> 복사후 객체이름

        // 버킷에서 지정된 이미지를 다른 파일명으로 복사한다.
        amazonS3Client.copyObject(bucket, sourceKey, bucket, destinationKey);

        // 복사후 남아있는 이미지를 삭제한다.
        amazonS3Client.deleteObject(bucket, sourceKey);
    }


    /*
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
    */
}
