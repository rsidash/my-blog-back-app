package kz.rsidash.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    private static final Path ROOT = Paths.get("uploads/posts");

    public String save(Long postId, MultipartFile file) {
        try {
            Path postDir = ROOT.resolve(postId.toString());
            Files.createDirectories(postDir);

            String filename = UUID.randomUUID() + getExtension(file.getOriginalFilename());
            Path filePath = postDir.resolve(filename);

            file.transferTo(filePath);

            return postId + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("File save error", e);
        }
    }

    public byte[] load(String relativePath) {
        try {
            Path filePath = ROOT.resolve(relativePath).normalize();
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("File read error", e);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.'));
    }

}
