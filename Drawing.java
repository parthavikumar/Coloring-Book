package ColoringBook.graphics;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Encapsulates a representation of a generic image file. A Drawing object 
 * can be created only by providing a valid image file as argument to its constructor. 
 * In return, the object can be used for accessing and modifying the image at pixel level.
 */
public class Drawing {
    
    private BufferedImage _image = null;
    
    /**
     * Creates an instance of a Drawing object encapsulating the representation of 
     * the imageFile given as argument.
     * @param imageFile - Filename of a drawing image.
     * @throws IOException - the imageFile doesn't exist or cannot be read.
     */
    public Drawing(String imageFile) throws IOException {
        File drwFile = new File(imageFile);
        if (!drwFile.exists() || drwFile.isDirectory()) {
            throw new IOException();
        }
        _image = ImageIO.read(drwFile);
    }
    
    BufferedImage getImage() {
        return _image;
    }

    /**
     * Gets the width of the drawing image.
     * @return the width of the drawing image in pixels.
     */
    public int getWidth() {
        return _image.getWidth();
    }
    
    /**
     * Gets the height of the drawing image.
     * @return the height of the drawing image in pixels.
     */
    public int getHeight() {
        return _image.getHeight();
    }
    
    /**
     * Indicates whether the pixel coordinates given as arguments fall within the bounds
     * of the image. The top-left valid coordinate of the image is (0, 0), 
     * the bottom-right valid coordinate is (width-1, height-1). 
     * @param x - x coordinate value.
     * @param y - y coordinate value.
     * @return true if both x and y are within their respective ranges, false otherwise.
     */
    public boolean isValidPixel(int x, int y) {
        return (x >= 0 && x <= _image.getWidth()-3 && y >=0 && y <= _image.getHeight()-3);
    }
    
    /**
     * Indicates whether the pixel at the given x and y coordinates is of a bright-toned color.
     * This is defined as a color where each of the three color-components (R, G and B)
     * have a value larger than 220.
     * @param x - x coordinate value.
     * @param y - y coordinate value.
     * @return true if the pixel has a bright-toned color, false otherwise.
     */
    public boolean isBrightPixel(int x, int y) {
        Color c = new Color(_image.getRGB(x, y));
        return c.getRed() > 220 && c.getGreen() > 220 && c.getBlue() > 220;
    }
    
    /**
     * Indicates whether the pixel at the given x and y coordinates is of a dark-toned color.
     * This is defined as a color where each of the three color-components (R, G and B)
     * have a value lesser than 30.
     * @param x - x coordinate value.
     * @param y - y coordinate value.
     * @return true if the pixel has a dark-toned color, false otherwise.
     */
    public boolean isDarkPixel(int x, int y) {
        Color c = new Color(_image.getRGB(x, y));
        return c.getRed() < 30 && c.getGreen() < 30 && c.getBlue() < 30;
    }
    
    /**
     * Gets the color of the pixel at the given x and y coordinates.
     * @param x - x coordinate value.
     * @param y - y coordinate value.
     * @return the Color value at the given coordinates.
     */
    public Color getPixel(int x, int y) {
        return new Color(_image.getRGB(x, y));
    }
    
    /**
     * Sets the pixel of the given x and y coordinates to the given color. 
     * @param x - x coordinate value.
     * @param y - y coordinate value.
     * @param c - the Color value to be set at the given coordinates.
     */
    public void setPixel(int x, int y, Color c) {
        _image.setRGB(x, y, c.getRGB());
    }
}
