package bg.sofia.uni.fmi.mjt.photoalbum;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Queue;

public class ImageLoader implements Runnable {

    private final Queue<Image> imageQueue;
    private final Path imagePath;

    public ImageLoader(Queue<Image> imageQueue, Path imagePath) {
        this.imageQueue = imageQueue;
        this.imagePath = imagePath;
    }

    @Override
    public void run() {
        Image loadedImage = loadImage(imagePath);

        synchronized (imageQueue) {
            imageQueue.add(loadedImage);
            imageQueue.notifyAll();
        }
    }

    public static Image loadImage(Path imagePath) {
        try {
            BufferedImage imageData = ImageIO.read(imagePath.toFile());
            return new Image(imagePath.getFileName().toString(), imageData);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Failed to load image %s", imagePath.toString()), e);
        }
    }
}
