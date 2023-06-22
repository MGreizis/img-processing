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
    Dotenv dotenv = Dotenv.configure().load();

    String connectionString = dotenv.get("STORAGE_CONNECTION_STRING");
    String containerName = dotenv.get("STORAGE_CONTAINER_NAME");

    @FunctionName("ImageProcessingFunction")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws Exception {

        context.getLogger().info("Java HTTP trigger processed a request.");

        String blobUrl = "https://imgprocessing28e51d5.blob.core.windows.net/images/6ydirw2ugyb61.jpg";

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
        // TODO: Implement your image processing logic here
        // Example: Apply a grayscale filter to the image
        BufferedImage processedImage = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        processedImage.getGraphics().drawImage(image, 0, 0, null);
        return processedImage;
    }
}
