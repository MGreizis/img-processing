package imgprocessing;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

public class ImageProcessingFunction {
    @FunctionName("ImageProcessingFunction")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<ByteArrayOutputStream> request,
            final ExecutionContext context) throws Exception {

        context.getLogger().info("Java HTTP trigger processed a request.");

        // Retrieve the image from the request
        byte[] imageBytes = request.getBody().toByteArray();
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

    private BufferedImage processImage(BufferedImage image) {
        // TODO: Implement your image processing logic here
        // Example: Apply a grayscale filter to the image
        BufferedImage processedImage = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        processedImage.getGraphics().drawImage(image, 0, 0, null);
        return processedImage;
    }
}
