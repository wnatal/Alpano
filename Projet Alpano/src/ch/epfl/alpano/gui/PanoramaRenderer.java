package ch.epfl.alpano.gui;

import ch.epfl.alpano.Panorama;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * provides one static function to render a image from a panorama
 * according/through the given ImagePainter.
 * 
 * @author Natal Willisch (262092)
 *
 */
public interface PanoramaRenderer {

    /**
     * Renders a image from a panorama according/through the given ImagePainter.
     * 
     * @param panorama
     *              panorama
     * @param imagePainter
     *              image-painter
     * @return (writable) image
     */
    public static WritableImage renderPanorama(Panorama panorama,
            ImagePainter imagePainter) {

        int width = panorama.parameters().width();
        int height = panorama.parameters().height();
        WritableImage image = new WritableImage(width, height);
        PixelWriter writer = image.getPixelWriter();
        
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                writer.setColor(x, y, imagePainter.colorAt(x, y));
            }
        
        return image;

    }
}
