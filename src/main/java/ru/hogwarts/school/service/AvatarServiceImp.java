package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarServiceImp implements AvatarService {

    private static final Logger logger = LoggerFactory.getLogger(AvatarServiceImp.class);

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${path.to.avatars.folder}")
    private String avatarsDir;

    public AvatarServiceImp(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Was invoked method for upload avatar for student with id: {}", studentId);

        if (file == null || file.isEmpty()) {
            logger.error("Avatar file is empty for student id: {}", studentId);
            throw new IllegalArgumentException("Avatar file cannot be empty");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("Student with id {} not found", studentId);
                    return new IllegalArgumentException("Student with id " + studentId + " not found");
                });

        String extension = getExtension(file.getOriginalFilename());
        Path filePath = Paths.get(avatarsDir, studentId + "." + extension);

        try {
            Files.createDirectories(filePath.getParent());
            Files.deleteIfExists(filePath);

            try (InputStream is = file.getInputStream();
                 OutputStream os = Files.newOutputStream(filePath, CREATE_NEW)) {
                is.transferTo(os);
            }
            logger.debug("Avatar file saved to disk: {}", filePath);
        } catch (IOException e) {
            logger.error("Error saving avatar file for student id {}: {}", studentId, e.getMessage());
            throw e;
        }

        byte[] previewData = generatePreview(filePath, extension);

        Avatar avatar = avatarRepository.findByStudentId(studentId)
                .orElse(new Avatar());
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(previewData);

        avatarRepository.save(avatar);
        logger.info("Avatar for student id {} successfully uploaded and saved", studentId);
    }

    @Override
    public Avatar findAvatar(Long studentId) {
        logger.info("Was invoked method for find avatar by student id: {}", studentId);
        return avatarRepository.findByStudentId(studentId)
                .orElse(null);
    }

    public Page<Avatar> findAll(Pageable pageable) {
        logger.info("Was invoked method for get all avatars with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Avatar> page = avatarRepository.findAll(pageable);
        logger.debug("Found {} avatars out of {}", page.getContent().size(), page.getTotalElements());
        return page;
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "png";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private byte[] generatePreview(Path filePath, String extension) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            BufferedImage image = ImageIO.read(is);
            if (image == null) {
                logger.error("Cannot read image file: {}", filePath);
                throw new IOException("Cannot read image file");
            }

            int width = 100;
            int height = image.getHeight() * width / image.getWidth();

            BufferedImage preview = new BufferedImage(width, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, width, height, null);
            graphics.dispose();

            ImageIO.write(preview, extension, baos);
            return baos.toByteArray();
        }
    }
}