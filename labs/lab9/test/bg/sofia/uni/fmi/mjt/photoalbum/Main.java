package bg.sofia.uni.fmi.mjt.photoalbum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        ParallelMonochromeAlbumCreator creator = new ParallelMonochromeAlbumCreator(5);

        Path workingDir = Paths.get("")
            .toAbsolutePath();

        Path currentDir = workingDir.resolve( "labs/lab9/res");
        Path inputDir = currentDir.resolve("input1");
        Path outputDir = currentDir.resolve("output6");

        Stream<Path> inputDirStream = Files.list(inputDir);
        System.out.println(inputDirStream.toList());
        inputDirStream.close();

        creator.processImages(inputDir.toString(), outputDir.toString());
    }
}