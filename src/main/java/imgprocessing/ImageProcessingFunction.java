package imgprocessing;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import io.github.cdimascio.dotenv.Dotenv;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import javax.imageio.ImageIO;

public class ImageProcessingFunction {
    @FunctionName("ImageProcessingFunction")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws Exception {

        context.getLogger().info("Java HTTP trigger processed a request.");

        String blobUrl = "https://imgprocessing28e51d5.blob.core.windows.net/images/ekQOnRnjsAzK4nCwNtvR.jpg";

        // Download the image from Azure Blob Storage
        byte[] imageBytes = downloadImageFromBlobStorage(blobUrl);

        // Read the downloaded image
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(bis);

        // Perform image processing tasks
        BufferedImage processedImage = processImage(image);

        // Convert the processed image back to bytes
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(processedImage, "jpg", bos);
        byte[] processedImageBytes = bos.toByteArray();

        // Return the processed image as the response
        return request.createResponseBuilder(HttpStatus.OK)
                .body(processedImageBytes)
                .header("Content-Type", "image/jpeg")
                .build();
    }

    private byte[] downloadImageFromBlobStorage(String blobUrl) {
        try {
            URL url = new URL(blobUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (InputStream in = connection.getInputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
            }

            return bos.toByteArray();
        } catch (IOException e) {
            // Handle the exception
        }

        return null;
    }

    private BufferedImage processImage(BufferedImage image) {
        // Convert the image to grayscale
        BufferedImage processedImage = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                int avg = (r + g + b) / 3;
                int grayRgb = (avg << 16) | (avg << 8) | avg;

                processedImage.setRGB(x, y, grayRgb);
            }
        }

        return processedImage;
    }
}