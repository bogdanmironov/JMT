package bg.sofia.uni.fmi.mjt.foodanaliyzer.barcodescanner;

import bg.sofia.uni.fmi.mjt.foodanaliyzer.exception.BarcodeImageException;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BarcodeScanner {
    public static String scanImage(String pathToImage) throws BarcodeImageException {
        try {
            var imageInputStream = new FileInputStream(pathToImage);

            String barcodeText = getBarcodeText(imageInputStream);

            imageInputStream.close();

            return barcodeText;
        } catch (IOException e) {
            throw new BarcodeImageException("Could not load barcode image.", e);
        } catch (NotFoundException | ChecksumException | FormatException e) {
            throw new BarcodeImageException("Could not decode barcode image.", e);
        }
    }

    private static String getBarcodeText(InputStream imageInputStream)
        throws IOException, ChecksumException, NotFoundException, FormatException {
        Reader reader = new MultiFormatReader();
        var bufferedImage = ImageIO.read(imageInputStream);

        var imageBitmap = new BinaryBitmap(
            new HybridBinarizer(
                new BufferedImageLuminanceSource(bufferedImage)));

        return reader.decode(imageBitmap).getText();
    }
}
