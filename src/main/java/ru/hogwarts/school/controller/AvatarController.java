package ru.hogwarts.school.controller;

import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

     @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadAvatar(@PathVariable Long id,
                             @RequestParam MultipartFile avatar) throws IOException {
        avatarService.uploadAvatar(id, avatar);
    }

    @GetMapping("/{id}/avatar/preview")
    public ResponseEntity<byte[]> getAvatarFromDB(@PathVariable Long id) {
        Avatar avatar = avatarService.findAvatar(id);
        if (avatar == null || avatar.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }

    @GetMapping("/{id}/avatar")
    public void getAvatarFromFileSystem(@PathVariable Long id,
                                        HttpServletResponse response) throws IOException {
        Avatar avatar = avatarService.findAvatar(id);
        if (avatar == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Path filePath = Path.of(avatar.getFilePath());
        if (!Files.exists(filePath)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(avatar.getMediaType());
        response.setContentLength((int) avatar.getFileSize());

        try (InputStream is = Files.newInputStream(filePath);
             OutputStream os = response.getOutputStream()) {
            is.transferTo(os);
            os.flush();
        }
    }
}