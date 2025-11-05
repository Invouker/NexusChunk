package eu.invouk.nexuschunk.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${upload.path}")
    private String uploadPath;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    public String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String safeExtension = validateAndGetExtension(originalFilename);
        String finalFilename = UUID.randomUUID().toString() + safeExtension;
        Path targetPath = Path.of(uploadPath, finalFilename);
        Path uploadDir = Path.of(uploadPath).toAbsolutePath().normalize();
        Path absoluteTargetPath = targetPath.toAbsolutePath().normalize();

        if (!absoluteTargetPath.startsWith(uploadDir)) {
            throw new IOException("Path Traversal bol detekovaný. Súbor by opustil povolený priečinok.");
        }

        // 5. Uloženie
        Files.createDirectories(targetPath.getParent());
        file.transferTo(targetPath);

        log.info("Súbor bol úspešne uložený na ABSOLÚTNEJ ceste: {}", absoluteTargetPath.toString());

        return finalFilename;
    }

    /**
     * Zvaliduje extenziu voči povolenému zoznamu (Whitelist).
     * @param filename Pôvodný názov súboru.
     * @return Bezpečný reťazec extenzie (vrátane bodky).
     * @throws IOException Ak extenzia nie je povolená.
     */
    private String validateAndGetExtension(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            throw new IOException("Názov súboru je prázdny.");
        }

        int dotIndex = filename.lastIndexOf('.');
        String extension = (dotIndex == -1) ? "" : filename.substring(dotIndex).toLowerCase();
        if (ALLOWED_EXTENSIONS.contains(extension)) {
            return extension;
        }

        throw new IOException("Nepovolený typ súboru: " + extension);
    }
}