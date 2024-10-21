package com.codehows.board.controller;

import com.codehows.board.dto.FileDto;
import com.codehows.board.entity.FileEntity;
import com.codehows.board.repository.FileRepository;
import com.codehows.board.service.FileService;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileRepository fileRepository;

    @PostMapping("/file/add")
    public ResponseEntity<FileDto> uploadFiles(@RequestParam("uploadFiles") MultipartFile files, FileDto fileDto) throws IOException {


        String fileLocation = "/home/mood/board/file/";
        Path directoryPath = Paths.get(fileLocation);
        if (Files.notExists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        try {
            String fileName = UUID.randomUUID().toString() + "_" + files.getOriginalFilename();
            String fileUrl = "/home/mood/board/file/" + fileName; // 파일 URL 생성
            File dest = new File("/home/mood/board/file/" + fileName);

            files.transferTo(dest);
            fileDto = new FileDto();
            fileDto.setFileName(fileName);
            fileDto.setFileUrl(fileUrl);
            fileDto.setFileOrigin(files.getOriginalFilename());

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


        return new ResponseEntity<FileDto>(fileDto, HttpStatus.OK);
    }



    @DeleteMapping("/api/delete-file")
    public ResponseEntity<?> deleteFile(@RequestBody List<Long> fnoList ) {
        try {

            for(Long fno : fnoList){
                FileEntity fileEntity = fileRepository.findById(fno).orElse(null);
                File file = new File((fileEntity.getFileUrl()));
                file.delete();
                fileService.deleteFileByFno(fno);
            }


            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 삭제에 실패했습니다.");
        }
    }

    // 파일을 업로드할 디렉터리 경로
    private final String uploadDir = Paths.get("/home/mood/", "tui-editor", "upload").toString();

    /**
     * 에디터 이미지 업로드
     * @param image 파일 객체
     * @return 업로드된 파일명
     */
    @PostMapping("/tui-editor/image-upload")
    public String uploadEditorImage(@RequestParam final MultipartFile image) {
        if (image.isEmpty()) {
            return "";
        }

        String orgFilename = image.getOriginalFilename();                                         // 원본 파일명
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");           // 32자리 랜덤 문자열
        String extension = orgFilename.substring(orgFilename.lastIndexOf(".") + 1);  // 확장자
        String saveFilename = uuid + "." + extension;                                             // 디스크에 저장할 파일명
        String fileFullPath = Paths.get(uploadDir, saveFilename).toString();                      // 디스크에 저장할 파일의 전체 경로

        // uploadDir에 해당되는 디렉터리가 없으면, uploadDir에 포함되는 전체 디렉터리 생성
        File dir = new File(uploadDir);
        if (dir.exists() == false) {
            dir.mkdirs();
        }

        try {
            // 파일 저장 (write to disk)
            File uploadFile = new File(fileFullPath);
            image.transferTo(uploadFile);
            return saveFilename;

        } catch (IOException e) {
            // 예외 처리는 따로 해주는 게 좋습니다.
            throw new RuntimeException(e);
        }
    }

    /**
     * 디스크에 업로드된 파일을 byte[]로 반환
     * @param filename 디스크에 업로드된 파일명
     * @return image byte array
     */
    @GetMapping(value = "/tui-editor/image-print", produces = { MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    public byte[] printEditorImage(@RequestParam final String filename) {
        // 업로드된 파일의 전체 경로
        String fileFullPath = Paths.get(uploadDir, filename).toString();

        // 파일이 없는 경우 예외 throw
        File uploadedFile = new File(fileFullPath);
        if (uploadedFile.exists() == false) {
            throw new RuntimeException();
        }

        try {
            // 이미지 파일을 byte[]로 변환 후 반환
            byte[] imageBytes = Files.readAllBytes(uploadedFile.toPath());
            return imageBytes;

        } catch (IOException e) {
            // 예외 처리는 따로 해주는 게 좋습니다.
            throw new RuntimeException(e);
        }
    }

}
