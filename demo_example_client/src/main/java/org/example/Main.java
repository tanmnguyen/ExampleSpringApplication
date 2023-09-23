package org.example;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        // Specify the endpoint URL
        String endpoint = "http://localhost:8080/enhancement/enhancecontrast";

        // Specify the path to the image file you want to upload
        String imagePath = "path/to/your/image/here";

        try {
            sendImageToEndpoint(endpoint, imagePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error Sending Image");
        }
    }

    public static void sendImageToEndpoint(String endpoint, String imagePath) throws IOException {
        // Create an HTTP client
        HttpClient httpClient = HttpClients.createDefault();

        // Create an HTTP POST request
        HttpPost httpPost = new HttpPost(endpoint);

        // Create a multipart entity for file upload
        File imageFile = new File(imagePath);
        HttpEntity entity = MultipartEntityBuilder.create()
                .addBinaryBody("file", imageFile, ContentType.IMAGE_JPEG, imageFile.getName())
                .build();

        httpPost.setEntity(entity);

        // Execute the request and get the response
        HttpResponse response = httpClient.execute(httpPost);

        // Check if the response status is OK (HTTP 200)
        if (response.getStatusLine().getStatusCode() == 200) {
            InputStream inputStream = response.getEntity().getContent();
            BufferedImage bufferedImage = ImageIO.read(inputStream);

            // save image
            File outputImageFile = new File("src/main/java/org/example/Images/output.png");
            ImageIO.write(bufferedImage, "png", outputImageFile);
            System.out.println("Image saved to: " + outputImageFile.getAbsolutePath());
        } else {
            throw new IOException("Failed to fetch the image. HTTP Status: " + response.getStatusLine());
        }

//        System.out.println("Status " + response.getStatusLine());
//        // Handle the response, e.g., read response content
//        String responseBody = EntityUtils.toString(response.getEntity());
//        System.out.println("Server Response: " + responseBody);
    }
}
