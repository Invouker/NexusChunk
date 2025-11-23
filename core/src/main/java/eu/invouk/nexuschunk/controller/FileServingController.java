package eu.invouk.nexuschunk.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

@Controller
public class FileServingController {

    @Value("${upload.path}")
    private String uploadPath;

    private static final Pattern SAFE_FILENAME_PATTERN =
            Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}(\\.[a-z0-9]+)$");

    @GetMapping("/images/news/{filename}")
    public ResponseEntity<Resource> serveNewsImage(@PathVariable String filename) {

        // 1. Validácia a Sanitizácia (zabezpečuje bezpečnosť reťazca)
        if (!SAFE_FILENAME_PATTERN.matcher(filename.toLowerCase()).matches() ||
                filename.contains("/") || filename.contains("\\") || filename.contains("..")) {
            return ResponseEntity.badRequest().build();
        }

        Path rootLocation = Paths.get(uploadPath).toAbsolutePath().normalize();

        Path file = rootLocation.resolve(filename); // NOSONAR

        if (!file.normalize().toAbsolutePath().startsWith(rootLocation)) {
            return ResponseEntity.notFound().build();
        }

        try {
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(file);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}