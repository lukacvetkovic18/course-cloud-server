package com.example.demo.api.file;

import com.example.demo.api.lesson.Lesson;
import com.example.demo.api.lesson.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final LessonRepository lessonRepository;

    @Autowired
    public FileService(FileRepository fileRepository, LessonRepository lessonRepository) {
        this.fileRepository = fileRepository;
        this.lessonRepository = lessonRepository;
    }

    public File saveFile(MultipartFile fileRequest, Long lessonId) throws IOException {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();

        File file = File.builder()
                .name(fileRequest.getOriginalFilename())
                .type(fileRequest.getContentType())
                .data(fileRequest.getBytes())
                .lesson(lesson)
                .build();

        return fileRepository.save(file);
    }

    @Transactional
    public Optional<File> getFile(Long id) {
        return fileRepository.findById(id);
    }

    public void deleteFile(Long id) {
        fileRepository.deleteById(id);
    }

    @Transactional
    public List<File> getFilesByLessonId(Long id) {
        return fileRepository.findFilesByLessonId(id);
    }

    public void deleteFilesByLessonId(Long id) {
        fileRepository.deleteFilesByLessonId(id);
    }

    public List<File> getFilesByLessonIds(List<Long> lessonIds) {
        return fileRepository.findFilesByLessonIds(lessonIds);
    }
}