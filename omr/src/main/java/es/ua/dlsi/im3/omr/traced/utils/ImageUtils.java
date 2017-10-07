/*
 * Copyright (C) 2016 David Rizo Valero
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ua.dlsi.im3.omr.traced.utils;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author drizo
 */
public class ImageUtils {
    public static final int getPixelGrayscale(BufferedImage image, int x, int y) {
	    int p = image.getRGB(x, y);
	    //int a = (p>>24)&0xff;
	    int r = (p>>16)&0xff;
	    int g = (p>>8)&0xff;
	    int b = p&0xff;	    
	    int avg = (r+g+b)/3;
	    return avg;	
    }
    
    public static int[][] getGrayscalePixels(BufferedImage image) {
	int [][] result = new int[image.getWidth()][image.getHeight()];
	for (int i=0; i<result.length; i++) {
	    for (int j=0; j<result[i].length; j++) {
		result[i][j] = getPixelGrayscale(image, i, j);
	    }
	}	    
	return result;
    }
    
    public static BufferedImage rescaleToGray(BufferedImage image, int w, int h) {
	BufferedImage scaledImage = new BufferedImage(w, h, image.getType());
	Graphics g = scaledImage.createGraphics();
	g.drawImage(image, 0, 0, w, h, null);
	g.dispose();	
	return scaledImage;	
    }
    
    
    public static int[][] loadScaledDesaturatedImage(File file, int w, int h) throws MalformedURLException, IOException {
	BufferedImage image = ImageIO.read(file);
	BufferedImage scaledImage = new BufferedImage(w, h, image.getType());
	Graphics g = scaledImage.createGraphics();
	g.drawImage(image, 0, 0, w, h, null);
	g.dispose();	
	return getGrayscalePixels(scaledImage);	
    }
    
    public static void writeImage(int[][] grayScaleBitmap) {
	
    }
    
    
}
