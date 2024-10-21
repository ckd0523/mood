package com.codehows.board.service;

import com.codehows.board.entity.Board;
import com.codehows.board.entity.FileEntity;
import com.codehows.board.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileService {

    @Value("${fileLocation}")
    private String fileLocation;
    private final FileRepository fileRepository;

    public Long saveFile(MultipartFile files, Long bno) throws IOException {
        if (files.isEmpty()) {
            return null;
        }
        // 원래 파일 이름 추출
        String fileOrigin = files.getOriginalFilename();

        // 파일 이름으로 쓸 uuid 생성
        String uuid = UUID.randomUUID().toString();

        // 확장자 추출(ex : .png)
        String extension = fileOrigin.substring(fileOrigin.lastIndexOf("."));

        // uuid와 확장자 결합
        String fileName = uuid + extension;

        // 파일을 불러올 때 사용할 파일 경로
        String fileUrl = fileLocation + fileName;

        // 파일 경로 없으면 생성
        Path directoryPath = Paths.get(fileLocation);
        if (Files.notExists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
        // 파일 엔티티 생성
        FileEntity file = FileEntity.builder()
                .fileOrigin(fileOrigin)
                .fileName(fileName)
                .fileUrl(fileUrl)
                .build();

        Board board = new Board();
        board.setBno(bno);
        file.setBoard(board);

        // 실제로 로컬에 uuid를 파일명으로 저장
        files.transferTo(new File(fileUrl));

        // 데이터베이스에 파일 정보 저장
        FileEntity savedFile = fileRepository.save(file);

        return savedFile.getFno();
    }

    public void deleteFilesByBoardId(Long bno) {
        List<FileEntity> files = fileRepository.findByBoardBno(bno);
        for (FileEntity file : files) {
            // 실제 파일 삭제
            File fileToDelete = new File(file.getFileUrl());
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }
        }
        // 데이터베이스에서 파일 정보 삭제
        fileRepository.deleteByBoardBno(bno);
    }

    public void deleteFileByFno(Long fno) {
        FileEntity file = fileRepository.findById(fno).orElseThrow(() -> new IllegalArgumentException("Invalid file Id: " + fno));

        // 실제 파일 삭제
        File fileToDelete = new File(file.getFileUrl());
        if (fileToDelete.exists()) {
            fileToDelete.delete();
        }

        // 데이터베이스에서 파일 정보 삭제
        fileRepository.deleteById(fno);
    }

    public List<FileEntity> findFilesByBoard(Board board) {
        return fileRepository.findByBoard(board);
    }
}
