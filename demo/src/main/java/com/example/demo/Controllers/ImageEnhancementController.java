package com.example.demo.Controllers;

import com.example.demo.Entities.ImageResult;
import com.example.demo.Services.ImageProcessingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping(path="/enhancement")
public class ImageEnhancementController {
    @PostMapping("/enhancecontrast")
    public ResponseEntity<?> enhanceContrast(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        // format check
        if (file == null || !file.getContentType().startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Image File");
        }

        try {
            // Parse the uploaded file to a BufferedImage
            BufferedImage uploadedImage = ImageIO.read(file.getInputStream());

            // enhance contrast
            BufferedImage equalizedImage = ImageProcessingService.histogramEqualization(uploadedImage);

            // write to stream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(equalizedImage, "png", outputStream);
            outputStream.flush();

            // success
            return ResponseEntity
                    .ok()
                    .contentType(org.springframework.http.MediaType.IMAGE_PNG)
                    .body(outputStream.toByteArray());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing image: " + e.getMessage());
        }
    }
}
