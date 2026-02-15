package kz.rsidash.service;

import kz.rsidash.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final PostRepository postRepository;
    private final FileService fileService;

    public Optional<byte[]> getImage(Long postId) {
        return postRepository.findImagePath(postId)
                .flatMap(path -> Optional.ofNullable(fileService.load(path)));
    }

    public void upload(Long postId, MultipartFile file) {
        final var imagePath = fileService.save(postId, file);

        postRepository.updateImagePath(postId, imagePath);
    }

}
