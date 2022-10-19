package com.company;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Scanner;

public class BoundaryFinder {
    // purpose of this class: to find the boundaries of the crease pattern.
    // this is done by traversing from the center of the image out until a black pixel is found, and then subtracting/adding half of the line width to get the exact center pixel
    // it is very important to get the exact boundary, because this is what all of the grid points are based off of, and if they are off, the program will struggle to detect the lines

    private String location;
    // information for grid snapping
    private int grid;
    private int partials;

    private BufferedImage image = null;
    private BufferedImage original = null;
    private File f = null;
    private int red = 255;
    private int green = 255;
    private int blue = 255;
    private int pixel;
    // determining the bounds of the image
    private int yStart;
    private double trueYStart;
    private int yEnd;
    private double trueYEnd;
    private int xStart;
    private double trueXStart;
    private int xEnd;
    private double trueXEnd;
    private boolean black = false;
    private int midX;
    private int midY;
    // arraylist of coordinates of all the possible grid points in an image
    private ArrayList <int[]> allPoints = new ArrayList<int[]>();
    public BoundaryFinder(String location, int lineWidth) {
        // setting up the file
        try {
            f = new File(location);
            image = ImageIO.read(f);
        } catch (IOException e) {
            System.out.println(e);
        }
        try { // making the copy that is marked up in the process of finding the points
            f = new File(location);
            original = ImageIO.read(f);
        } catch (IOException e) {
            System.out.println(e);
        }
        // prevents the program from starting directly on top of a line (which can cause the program to not detect the edge and go out of bounds or be inaccurate)
        midX = image.getWidth() / 2;
        midY = image.getHeight() / 2;
        while (!(Util.isWhite(image, midX, midY))) {
            midX += 2 * lineWidth;
            midY += 2 * lineWidth;
        }
        double realLineWidth  = lineWidth + 1; // this is so that when you divide the line width by two and add or subtract that value, it brings you to the exact right point
        // determining starting y pixel
        int counter = midY + 1; // set to negative one so that it will become zero when incremented in the beginning
        while (!black) {
            counter--;
            if (Util.isBlack(image, midX, counter)) {
                yStart = counter - Util.divideRound(lineWidth, 2);
                trueYStart = 1.0 * counter + 1.0 - realLineWidth / 2;
//                System.out.println("True yStart: " + trueYStart);
//                System.out.println("rounded version: " + yStart);
                black = true;
            }
        }
        // determining the ending y pixel
        black = false;
        counter = midY - 1;
        while (!(black)) {
            counter++;
            if (Util.isBlack(image, midX, counter)) {
                yEnd = counter + Util.divideRound(lineWidth, 2);
                trueYEnd = 1.0 * counter - 1.0 + realLineWidth / 2;
//                System.out.println("True yEnd: " + trueYEnd);
//                System.out.println("rounded version: " + yEnd);
                black = true;
            }
        }
        // determining the starting x pixel
        black = false;
        counter = midX + 1;
        while (!(black)) {
            counter--;
            if (Util.isBlack(image, counter, midY)) {
                xStart = counter - Util.divideRound(lineWidth, 2);
                trueXStart = 1.0 * counter + 1.0 - realLineWidth / 2;
//                System.out.println("True xStart: " + trueXStart);
//                System.out.println("rounded version: " + xStart);
                black = true;
            }
        }
        // determining the ending x pixel
        black = false;
        counter = midX - 1;
        while (!(black)) {
            counter++;
            if (Util.isBlack(image, counter, midY)) {
                xEnd = counter + Util.divideRound(lineWidth, 2);
                trueXEnd = 1.0 * counter - 1.0 + realLineWidth / 2;
//                System.out.println("True XEnd: " + trueXEnd);
//                System.out.println("rounded version: " + xEnd);
                black = true;
            }
        }
    }
    //getter methods for the pixel boundaries
    //get starting y
    public int getStartingY () {
        return yStart;
    }
    // get un rounded version
    public double getTrueYStart(){
        return trueYStart;
    }
    // get ending y
    public int getEndingY () {
        return yEnd;
    }
    // get un rounded version
    public double getTrueYEnd(){
        return trueYEnd;
    }
    // get starting x
    public int getStartingX () {
        return xStart;
    }
    // get un rounded version
    public double getTrueXStart(){
        return trueXStart;
    }
    // get ending x
    public int getEndingX () {
        return xEnd;
    }
    // get un rounded version
    public double getTrueXEnd(){
        return trueXEnd;
    }
    public String getMidPoint () {
        return "mid x: " + midX + " midY: " + midY;
    }
    public BufferedImage getImage(){
        return image;
    }
    public BufferedImage getOriginalImage(){
        return original;
    }
    public String getPixel ( int x, int y){
        return " red: " + red + " green: " + green + " blue: " + blue;
    }
    // tester to show the boundary (to make sure it is in the right spot)
    public void showBoundary(){
        for(int i = xStart; i <= xEnd; i++){
            Util.makeBlue(original, i, yEnd);
        }
        for(int i = xStart; i <= xEnd; i++){
            Util.makeBlue(original, i, yStart);
        }
        for(int i = yStart; i <= yEnd; i ++){
            Util.makeBlue(original, xStart, i);
        }
        for(int i = yStart; i <=yEnd; i++){
            Util.makeBlue(original, xEnd, i);
        }
        Util.makeBlack(original, xStart, yStart, 1);
    }
}
