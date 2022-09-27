package com.company;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.nio.Buffer;
import java.util.Scanner;
import java.util.ArrayList;
public class Util {
    public static int[] getRGB(BufferedImage image, int x, int y){
        int pixel = image.getRGB(x, y);
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = pixel & 0xff;
        int[] rgb = {red, green, blue};
        return rgb;
    }
    // returns true if pixel is red and false otherwise
    public static boolean isRed(int red, int green, int blue){
//        if(red >= 150){
//            if((green == 0 || blue == 0) && Math.abs(green - blue) <= 50){
//                return true;
//            }
//            // this prevents dividing by zero error
//            if(green == 0 || blue == 0){
//                return true;
//            }
//            if(red / blue >= 2 && red / green >= 2 && Math.abs(blue - green) <= 50){
//                return true;
//            }
//        }
        if((red + green + blue) < 600) {
            if (red > blue && red > green) { // very loose definition. potentially prone to inaccuracy
                return true;
            }
        }
        return false;
    }
    // returns true if pixel is blue and false if it isn't
    public static boolean isBlue(int red, int green, int blue) {
//        if (blue >= 150) {
//            if ((green == 0 || red == 0) && Math.abs(green - red) <= 50) {
//                return true;
//            }
//            if(green == 0 || red == 0){
//                return true;
//            }
//            if (blue / red >= 2 && blue / green >= 2 && Math.abs(blue - green) <= 50) {
//                return true;
//            }
//        }
        if((red + blue + green) < 600) {
            if (blue > red && blue > green) { // very loose definition. potentially prone to inaccuracy
                return true;
            }
        }
        return false;
    }
    // finds biggest difference between two of three ints
    public static int biggestDifference(int a, int b, int c) {
        // finding largest number
        if (a == b && a == c) {
            return 0;
        }

        if (a >= b && a >= c) {
            if (b <= c) {
                return a - b;
            }
            return a - c;
        } else if (b >= a && b >= c) {
            if (a <= c) {
                return b - a;
            }
            return b - c;
        } else if (a <= b) {
            return c - a;
        }
        return c - b;
    }
    // returns total rgb values of a square of pixels
    public static int totalRGB(BufferedImage image, int x, int y, int scanSize){
//        System.out.println("x: " + x + " y: " + y);
        int number = 0;
        int offset = divideRound(scanSize, 2);
        for(int i = y - offset; i < y + offset; i++){
            for(int j = x - offset; j < x + offset; j++){
                int pixel = image.getRGB(j, i);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;
                number += red;
                number += blue;
                number += green;
            }
        }
        return number;
    }
    // returns values like total rgb, total red pixels, and total blue pixels
    public static int[] scanPixels(BufferedImage image, int x, int y, int scanSize){
        int totalRGB = 0;  // total rgb
        int redPixel = 0;  //  number of red pixels
        int bluePixel = 0;  //  number of blue pixels
        int offset = divideRound(scanSize, 2);
//        System.out.println("offset = " + offset);
//        System.out.println("scansize = " + scanSize);
        int totalPixels = (offset * 2) * (offset * 2);  // total number of pixels. We can't just square the scan size because of potential rounding discrepencies
        for(int i = y - offset; i < y + offset; i++){
            for(int j = x - offset; j < x + offset; j++){
                int pixel = image.getRGB(j, i);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;
                totalRGB += red;
                totalRGB += blue;
                totalRGB += green;
                if(isRed(red, green, blue)){
                    redPixel++;
                } else if(isBlue(red, green, blue)){
                    bluePixel++;
                }
            }
        }
//        if((int)(1.0 * totalRGB / totalPixels + 0.5) > 750) {
//            System.out.println("Average pixel value: " + (int) (1.0 * totalRGB / totalPixels + 0.5));
//        }
        if(1.0 * totalRGB / totalPixels > 765) {
            System.out.println("average rgb is: " + 1.0 * totalRGB / totalPixels);
        }
//        System.out.println("the total pixels are " + totalPixels);
        return new int[] {totalRGB, redPixel, bluePixel, totalPixels};
    }
    // returns the line type, if any
    public static int isLine(int averageRGB, int redPixel, int bluePixel, int totalPixels){
        if(averageRGB > 500){
            return -1;
        }
        if(averageRGB < 300){
            return 1;  // makes it an edge line
        }
        double averageRed = 1.0 * redPixel / totalPixels;
        if(averageRed > 0.3){
            return 2;  // makes it a mountain line
        }
        double averageBlue = 1.0 * bluePixel / totalPixels;
        if(averageBlue > 0.3){
            return 3;  // makes it a valley line
        }
        return -1;
    }
    // makes pixel black
    public static void makeBlack(BufferedImage image, int x, int y, int scanSize) {
        int offset = divideRound(scanSize, 2);
        for (int i = y - offset; i < y + offset; i++) {
            for (int j = x - offset; j < x + offset; j++) {
                int red = 0;
                int green = 0;
                int blue = 0;
                int alpha = 255;
                int p = (alpha << 24) | (red << 16) | (green << 8) | blue;
                image.setRGB(j, i, p);
            }
        }
    }
    // makes green square
    public static void makeGreenSquare(BufferedImage image, int x, int y, int scanSize) {
        int offset = divideRound(scanSize, 2);
        for (int i = y - offset; i < y + offset; i++) {
            for (int j = x - offset; j < x + offset; j++) {
                int red = 0;
                int green = 255;
                int blue = 0;
                int alpha = 255;
                int p = (alpha << 24) | (red << 16) | (green << 8) | blue;
                image.setRGB(j, i, p);
            }
        }
    }    // makes single green pixel to show the exact pixel of the grid
    public static void makeGreen(BufferedImage image, int x, int y){
        int red = 0;
        int green = 255;
        int blue = 0;
        int alpha = 255;
        int p = (alpha << 24) | (red << 16) | (green << 8) | blue;
        image.setRGB(x, y, p);
    }
    // makes single yellow pixel
    public static void makeYellow(BufferedImage image, int x, int y){
        int red = 255;
        int green = 255;
        int blue = 0;
        int alpha = 255;
        int p = (alpha << 24) | (red << 16) | (green << 8) | blue;
        image.setRGB(x, y, p);
    }
    // returns true if pixel is black, and false if it isn't
    public static boolean isBlack(BufferedImage image, int x, int y){
        int pixel = image.getRGB(x, y);
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = pixel & 0xff;
        if(red < 100 && green < 100 && blue < 100){
            return true;
        }
        return false;
    }
    // makes pixel white
    public static void makeWhite(BufferedImage image, int x, int y){
        int red = 255;
        int green = 255;
        int blue = 255;
        int alpha = 255;
        int p = (alpha << 24) | (red << 16) | (green << 8) | blue;
        image.setRGB(x, y, p);
    }
    // returns true if pixel is white
    public static boolean isWhite(BufferedImage image, int x, int y){
        int pixel = image.getRGB(x, y);
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = pixel & 0xff;
        if(red >= 240 && blue >= 240  && green >= 240){
            return true;
        }
        return false;
    }
    //makes pixel red
    public static void makeRed(BufferedImage image, int x, int y, int scanSize){
        int offset = divideRound(scanSize, 2);
        for (int i = y - offset; i < y + offset; i++) {
            for (int j = x - offset; j < x + offset; j++) {
                int red = 255;
                int green = 0;
                int blue = 0;
                int alpha = 255;
                int p = (alpha << 24) | (red << 16) | (green << 8) | blue;
                image.setRGB(j, i, p);
            }
        }
    }
    //makes pixel blue
    public static void makeBlue(BufferedImage image, int x, int y){
        int red = 0;
        int green = 0;
        int blue = 255;
        int alpha = 255;
        int p = (alpha << 24) | (red << 16) | (green << 8) | blue;
        image.setRGB(x, y, p);
    }
    // makes pixel purple
    public static void makePurple(BufferedImage image, int x, int y){
        int red = 255;
        int green = 0;
        int blue = 255;
        int alpha = 255;
        int p = (alpha << 24) | (red << 16) | (green << 8) | blue;
        image.setRGB(x, y, p);
    }
    // edit a pre-existing file
    public static void editFile(String location, String fileContent) {
        try {
            FileOutputStream fos = new FileOutputStream(location, true);  // true for append mode
            byte[] byt = fileContent.getBytes();       //converts string into bytes
            fos.write(byt);           //writes bytes into file
            fos.close();            //close the file
            System.out.println("file saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // make a new file
//    public static  newFile(String location){
//        try {
//            File f = new File(location);
//            BufferedImage image = ImageIO.read(f);
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//    }
    //edits image data (mainly used for testing)
    public static boolean editImage(BufferedImage image, String location) {
        try {
            File f = new File(location);
            ImageIO.write(image, "png", f);
            return true;
        } catch (IOException e) {
            System.out.println("edit failed");
            return false;
        }
    }
    // makes image file
    public static BufferedImage makeImage(String location){
        BufferedImage image = null;
        try {
            File f = new File(location);
            image = ImageIO.read(f);
        } catch (IOException e) {
            System.out.println(e);
        }
        return image;
    }
    // checks if a string fits one of two parameters
    public static String ensureValid(String answer, String parrameter1, String parrameter2){
        Scanner scan = new Scanner(System.in);
        answer.toLowerCase();
        parrameter1.toLowerCase();
        parrameter2.toLowerCase();
        while(!(answer.equals(parrameter1) || answer.equals(parrameter2))){
            System.out.println("invalid input");
            answer = scan.next();
        }
        return answer;
    }
    //shrinks the points down
//    public static ArrayList shrinkPoints(int scanSize, ArrayList <Coordinates> rawData){
//        ArrayList finalPoints = new ArrayList <Coordinates>();// final array of shrunken points
//        Coordinates point = rawData.get(0);                  // setting the first point to compare other points to
//        int numPoints = rawData.size();                     // number of points value used to stop the loop when all points have been averaged
//        int clusteredPoints = 1;                           // set to one because we are starting analyzing at the first point
//        point.makeCluster();                              // setting value to true because I already clustered the first point
//        int averageX = point.getX();                     // average x values, reset after each new cluster is made
//        int averageY = point.getY();                    // average y values, reset after each new cluster is made
//        int iterations = 0;                            // iterations used to average the points, also reset after each new cluster
//        while(clusteredPoints <= numPoints){          // while loop to repeat looking for points touching until all points are grouped
//            // System.out.println("while loop entered");
//            for(int i = 1; i < rawData.size(); i++){     // searching through all points
//                //System.out.println("for loop entered");
//                if(rawData.get(i).inCluster() == false){        // only comparing points that aren't already grouped
//                    //System.out.println("not in cluster check passed");
//                    if((point.getX() + scanSize >= rawData.get(i).getX() && point.getX() <= rawData.get(i).getX())|| (point.getY() + scanSize >= rawData.get(i).getY() && point.getY() <= rawData.get(i).getY()) || (rawData.get(i).getX() + scanSize >= point.getX() && rawData.get(i).getX() <= point.getX()) || (rawData.get(i).getY() + scanSize >= point.getY() && rawData.get(i).getY() <= point.getY())){
//                        // System.out.println("overlapping check passed");
//                        averageX += rawData.get(i).getX();
//                        averageY += rawData.get(i).getY();
//                        point = rawData.get(i);
//                        point.makeCluster();
//                        clusteredPoints++;
//                        iterations++;
//                    } else{
//                        System.out.println("not overlapping");
//                        averageX = (int)(1.0 * averageX / iterations + 0.5);
//                        averageY = (int)(1.0 * averageY / iterations + 0.5);
//                        finalPoints.add(new Coordinates(averageX, averageY));
//                        point = rawData.get(i);
//                        clusteredPoints++;
//                        iterations = 1;
//                        averageX = rawData.get(i).getX();
//                        averageY = rawData.get(i).getY();
//                        System.out.println("cluster made at: " + finalPoints.get(finalPoints.size()-1));
//                        System.out.println("number of clusters " + finalPoints.size());
//
//                    }
//                }
//            }
//        }
//        return finalPoints;
//    }
    // rounds value to the nearest int value
    public static int divideRound(int dividend, int divisor){
        return((int)(1.0 * dividend / divisor + 0.5));
    }
    // returns the slope between two points
    public static double[] getSlope(Coordinates c1, Coordinates c2){
        double run = c2.getX() - c1.getX();
        double rise = c2.getY() - c1.getY();
//        System.out.println("rise " + rise + " run " + run);
        Double slope = 0.0; // pointless line so compiler stops being a bitch

        if(run == 0 && rise < 0){
//            System.out.println("slope set to minimum value");
            slope = Double.MIN_VALUE; // if the slope is undefined and the starting values are larger than the ending values
        } else if(run == 0 && rise > 0){
//            System.out.println("slope set to maximum value");
            slope = Double.MAX_VALUE; // if the slope is undefined and the starting values are smaller than the ending values
        }
        double yIntercept = 0;
        if(run != 0){ // if the run is zero, the equation is undefined, so calculating the slope will throw an error
            slope = rise / run;  // we can now safely calculate the slope since we know the run is not zero
            yIntercept = c1.getY() - slope * c1.getX();
        }
        double[] equation =  {slope, yIntercept, run};
        return equation;
        }

    public static void drawLine(BufferedImage image, Coordinates c1, Coordinates c2){
        double equation[] = getSlope(c1, c2); // getting the equation
        // getting the x and y starts and ends
        int xStart = c1.getX();
        int xEnd = c2.getX();
        int yStart = c1.getY();
        int yEnd = c2.getY();
        // checking if equation is undefined
        if(equation[0] == Double.MAX_VALUE){ // if equation is undefined and starting values are smaller than ending values
            for(int i = yStart; i <= yEnd; i++){
                makeGreen(image, xStart, i);
            }
        } else if(equation[0] == Double.MIN_VALUE){ // if equation is undefined and starting values are larger than ending values
            for(int i = yStart; i >= yEnd; i--){
                makeGreen(image, xStart, i);
            }
        } else if(equation[0] == 0 && equation[2] > 0){
            for(int i = xStart; i <= xEnd; i++){ // if slope is zero and starting values are smaller than ending values
                makeYellow(image, i, yStart);
            }
        } else if(equation[0] == 0 && equation[2] < 0){  // if slope is zero and starting values are larger than ending values
            for(int i = xStart; i >= xEnd; i--){
                makeYellow(image, i, yStart);
            }
        } else if(equation[2] < 0){  // if starting values are larger than ending values
            for(int i = xStart; i >= xEnd; i--){
                int y = (int)(equation[0] * i + equation[1] + 0.5); // finding the y value by plugging the x value into the equation. may result in rounding error from improper casting
                makePurple(image, i, y);
            }
        } else{ // if starting values are smaller than ending values
            for(int i = xStart; i <= xEnd; i++){
                int y = (int)(equation[0] * i + equation[1] + 0.5); // finding the y value by plugging the x value into the equation. may result in rounding error from improper casting
                makePurple(image, i, y);
            }
        }
    }
    // returns the average rgb value, number of a collection of points along line
    public static int[] lineData(BufferedImage image, Coordinates c1, Coordinates c2, int scanSize){
        double equation[] = getSlope(c1, c2); // getting the equation
        // getting the x and y starts and ends
        int xStart = c1.getX();
        int xEnd = c2.getX();
        int yStart = c1.getY();
        int yEnd = c2.getY();
        int totalRGB = 0;
        int redPixel = 0;  //  number of red pixels
        int bluePixel = 0;  //  number of blue pixels
        int iterations = 0;
        // checking if equation is undefined
        if(equation[0] == Double.MAX_VALUE){ // if equation is undefined and starting values are smaller than ending values
            for(int i = yStart; i <= yEnd; i++){
                int[] lineData = scanPixels(image, xStart, i, scanSize);
//                makeGreenSquare(image, xStart, i, scanSize);
                totalRGB += lineData[0];
                redPixel += lineData[1];
                bluePixel += lineData[2];
                iterations += lineData[3];
            }
        } else if(equation[0] == Double.MIN_VALUE){ // if equation is undefined and starting values are larger than ending values
            for(int i = yStart; i >= yEnd; i--){
                int[] lineData = scanPixels(image, xStart, i, scanSize);
//                makeGreenSquare(image, xStart, i, scanSize);
                totalRGB += lineData[0];
                redPixel += lineData[1];
                bluePixel += lineData[2];
                iterations += lineData[3];
            }
        } else if(equation[0] == 0 && equation[2] > 0){
            for(int i = xStart; i <= xEnd; i++){ // if slope is zero and starting values are smaller than ending values
                int[] lineData = scanPixels(image, i, yStart, scanSize);
//                makeGreenSquare(image, i, yStart, scanSize);
                totalRGB += lineData[0];
                redPixel += lineData[1];
                bluePixel += lineData[2];
                iterations += lineData[3];
            }
        } else if(equation[0] == 0 && equation[2] < 0){  // if slope is zero and starting values are larger than ending values
            for(int i = xStart; i >= xEnd; i--){
                int[] lineData = scanPixels(image, i, yStart, scanSize);
//                makeGreenSquare(image, i, yStart, scanSize);
                totalRGB += lineData[0];
                redPixel += lineData[1];
                bluePixel += lineData[2];
                iterations += lineData[3];
            }
        } else if(equation[2] < 0){  // if starting values are larger than ending values
            for(int i = xStart; i >= xEnd; i--){
                int y = (int)(equation[0] * i + equation[1] + 0.5); // finding the y value by plugging the x value into the equation.
                int[] lineData = scanPixels(image, i, y, scanSize);
//                makeGreenSquare(image, i, y, scanSize);
                totalRGB += lineData[0];
                redPixel += lineData[1];
                bluePixel += lineData[2];
                iterations += lineData[3];
            }
        } else{ // if starting values are smaller than ending values
            for(int i = xStart; i <= xEnd; i++){
                int y = (int)(equation[0] * i + equation[1] + 0.5); // finding the y value by plugging the x value into the equation.
                int[] lineData = scanPixels(image, i, y, scanSize);
//                makeGreenSquare(image, i, y, scanSize);
                totalRGB += lineData[0];
                redPixel += lineData[1];
                bluePixel += lineData[2];
                iterations += lineData[3];
            }
        }
        return new int[] {(int)(1.0 * totalRGB / iterations + 0.5), redPixel, bluePixel, iterations};
    }
    // resetting the marked image back to its original state while maintaining the original memory address of the image
    // it blows my mind that this is not pre-built into the BufferedImage class
    public static BufferedImage resetImage(BufferedImage image, BufferedImage original){
        for(int i = 0; i < image.getHeight(); i++){
            for(int j = 0; j < image.getWidth(); j++){
                int pixel = original.getRGB(j, i);
                image.setRGB(j,i,pixel);
            }
        }
        return image;
    }
    // tester to make sure the color recognition works
    public static void checkColorRecognition(BufferedImage image, String location){
        System.out.println("checking image");
        for(int i = 0; i < image.getHeight(); i++){
            for(int j = 0; j < image.getWidth(); j++){
                int pixel = image.getRGB(j, i);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;
                if((red+green+blue) < 600) {
                    if (Util.isRed(red, green, blue)) {
                        red = 255;
                        green = 0;
                        blue = 0;
                        pixel = (255 << 24) | (red << 16) | (green << 8) | blue;
                        image.setRGB(j, i, pixel);
                    } else if (Util.isBlue(red, green, blue)) {
                        blue = 255;
                        green = 0;
                        red = 0;
                        pixel = (255 << 24) | (red << 16) | (green << 8) | blue;
                        image.setRGB(j, i, pixel);
                    }
                }
                else{
                    red = 255;
                    green = 255;
                    blue = 255;
                    pixel = (255 << 24) | (red << 16) | (green << 8) | blue;
                    image.setRGB(j, i, pixel);
                }
            }
        }
                Util.editImage(image, location);
    }
}


