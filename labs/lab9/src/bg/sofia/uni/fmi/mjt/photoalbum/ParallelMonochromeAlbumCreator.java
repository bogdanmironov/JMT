package bg.sofia.uni.fmi.mjt.photoalbum;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelMonochromeAlbumCreator implements MonochromeAlbumCreator {
    private final int imageProcessorsCount;

    public ParallelMonochromeAlbumCreator(int imageProcessorsCount) {
        this.imageProcessorsCount = imageProcessorsCount;
    }

    @Override
    public void processImages(String sourceDirectory, String outputDirectory) {
        final Queue<Image> queuedImages = new LinkedList<>();

        int numOfImages = createProducerForEachImage(sourceDirectory, queuedImages);
        List<Thread> threads = createConsumers(outputDirectory, numOfImages, queuedImages);

        for (var thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //Returns the amount of producers
    private int createProducerForEachImage(String sourceDirectory, Queue<Image> queuedImages) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Path.of(sourceDirectory),
            "*.{jpeg,jpg,png}")) {
            int i = 0;

            for (Path imagePath : directoryStream) {
                Thread.startVirtualThread(new ImageLoader(queuedImages, imagePath));
                ++i;
            }

            return i;
        } catch (IOException e) {
            throw new RuntimeException("Could not open source directory.", e);
        }
    }

    private List<Thread> createConsumers(String outputDirectory, int numOfImages, Queue<Image> queuedImages) {
        Path outputDirectoryPath;
        final AtomicInteger imagesPassedForProcessing = new AtomicInteger(0);

        try {
            outputDirectoryPath = Files.createDirectories(Path.of(outputDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Could not open or create output directory", e);
        }

        List<Thread> threads = new LinkedList<>();
        for (int i = 0; i < imageProcessorsCount; ++i) {
            threads.add(Thread.startVirtualThread(
                new ImageProcessor(queuedImages, outputDirectoryPath, imagesPassedForProcessing, numOfImages)));
        }

        return threads;
    }
}
