package kz.rsidash.controller;

import jakarta.validation.constraints.NotNull;
import kz.rsidash.Constants;
import kz.rsidash.service.PostImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class PostImageController {

    private final PostImageService postImageService;

    @PutMapping(value = Constants.POST_IMAGE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updatePostImage(
            final @PathVariable(name = "postId") @NotNull Long postId,
            final @RequestParam(name = "image") MultipartFile image
    ) {
        postImageService.upload(postId, image);
    }

    @GetMapping(Constants.POST_IMAGE)
    public ResponseEntity<byte[]> getPostImage(final @PathVariable(name = "postId") @NotNull Long postId) {
        final var image = postImageService.getImage(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(image);
    }

}
