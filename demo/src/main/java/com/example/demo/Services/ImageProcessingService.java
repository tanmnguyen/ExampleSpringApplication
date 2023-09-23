package com.example.demo.Services;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class ImageProcessingService {
    /**
     * Convert BufferedImage type into 3D RGB array
     * @param image BufferedImage type
     * @return an 3D RGB array
     */
    public static int[][][] convertTo3DArray(BufferedImage image) {
        int height = image.getHeight(), width = image.getWidth();
        int[][][] result = new int[height][width][3];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0x000000FF;
                int green = (rgb >>8 ) & 0x000000FF;
                int blue = (rgb) & 0x000000FF;

                result[y][x][0] = red;
                result[y][x][1] = green;
                result[y][x][2] = blue;
            }
        }

        return result;
    }

    /**
     * Convert RGB 3D array into a 2D Gray image array
     * @param image RGB 3D image array
     * @return a 2D Gray Image array
     */
    public static int[][] convertoGrayScale(int[][][] image) {
        int height = image.length, width = image[0].length;
        int[][] result = new int[height][width];

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                result[i][j] = (int) ((image[i][j][0] + image[i][j][1] + image[i][j][2]) / 3);

        return result;
    }

    public static BufferedImage convertGrayToBufferedImage(int[][] grayImage) {
        int height = grayImage.length, width = grayImage[0].length;

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = bufferedImage.getRaster();
        ColorModel model = bufferedImage.getColorModel();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelValue = grayImage[y][x];
                raster.setSample(x, y, 0, pixelValue);
            }
        }

        return new BufferedImage(model, raster, false, null);
    }

    public static BufferedImage histogramEqualization(BufferedImage image) {
        int height = image.getHeight(), width = image.getWidth();

        int[][][] imageRGBArray = convertTo3DArray(image);
        int[][] imageGrayArray  = convertoGrayScale(imageRGBArray);

        // Step 1: Compute the histogram for the grayscale image
        int[] histogram = new int[256];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixelValue = imageGrayArray[i][j];
                histogram[pixelValue]++;
            }
        }

        // Step 2: Compute the cumulative distribution function (CDF)
        int sum = 0;
        int[] cdf = new int[256];
        for (int i = 0; i < 256; i++) {
            sum += histogram[i];
            cdf[i] = sum;
        }

        // Step 3: Normalize the CDF to map intensity values
        int totalPixels = height * width;
        for (int i = 0; i < 256; i++) {
            cdf[i] = (int) (255.0 * cdf[i] / totalPixels + 0.5);
        }

        // Step 4: Apply the mapping to the original grayscale image
        int[][] equalizedImage = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixelValue = imageGrayArray[i][j];
                int newPixelValue = cdf[pixelValue];
                equalizedImage[i][j] = newPixelValue;
            }
        }

        return convertGrayToBufferedImage(equalizedImage);
    }
}
