package bg.sofia.uni.fmi.mjt.photoalbum;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageProcessor implements Runnable {

    private final Queue<Image> imageQueue;
    private final Path outputDirPath;
    private final AtomicInteger imagesPassedForProcessing;
    private final int totalImages;

    public ImageProcessor(Queue<Image> imageQueue, Path outputDirPath, AtomicInteger imagesPassedForProcessing,
                          int totalImages) {
        this.imageQueue = imageQueue;
        this.outputDirPath = outputDirPath;
        this.imagesPassedForProcessing = imagesPassedForProcessing;
        this.totalImages = totalImages;
    }

    @Override
    public void run() {
        while (imagesPassedForProcessing.get() < totalImages) {
            Image imageToConvert;

            synchronized (imageQueue) {
                try {
                    while (imageQueue.isEmpty()) {
                        if (imagesPassedForProcessing.get() == totalImages) return;

                        imageQueue.wait();
                    }
                    imageToConvert = imageQueue.remove();
                    imagesPassedForProcessing.incrementAndGet();
                } catch (InterruptedException e) {
                    throw new RuntimeException("Processor thread interrupted", e);
                }
            }

            //We can add try-catch that puts back in imageQueue in synced section.
            Image convertedImage = convertToBlackAndWhite(imageToConvert);
            saveConvertedImage(convertedImage);
        }
    }

    private Image convertToBlackAndWhite(Image image) {
        BufferedImage processedData =
            new BufferedImage(image.data.getWidth(), image.data.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        processedData.getGraphics().drawImage(image.data, 0, 0, null);

        return new Image(image.name, processedData);
    }

    private void saveConvertedImage(Image convertedImage) {
        try {
            Path savedEmptyImagePath = Files.createFile(outputDirPath.resolve(convertedImage.name));

            String[] splitImageName = convertedImage.name.split("\\.");
            String imageType = splitImageName[splitImageName.length - 1];

            ImageIO.write(convertedImage.data, imageType, savedEmptyImagePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Could not save image.", e);
        }
    }
}
