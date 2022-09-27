package com.company;
import java.nio.Buffer;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
//        Double k = Double.MAX_VALUE;
//        double l = Double.MAX_VALUE;
//        Double m = Double.MIN_VALUE;
//        double n = Double.MIN_VALUE;

//        System.out.println("k = " + k);
//        System.out.println("l = " + l);
//        System.out.println("m = " + m);
//        System.out.println("n = " + n);

        Scanner scan = new Scanner(System.in);
        System.out.println("enter the name of the file");
        String name = scan.nextLine();
        System.out.println("enter the grid size");
        while(!scan.hasNextInt()) {
            System.out.println("invalid input");
            scan.next();
        }
        int grid = scan.nextInt();
        System.out.println("enter in the partials (1 for no partials, 2 for half units and so on)");
        while(!scan.hasNextInt()){
            System.out.println("invalid input");
            scan.next();
            }
        int partials = scan.nextInt();
        System.out.println("enter the line width (in pixels)");
        while(!scan.hasNextInt()) {
            System.out.println("invalid input");
            scan.next();
        }
        int width = scan.nextInt();
        String path = "/Users/jasperfeldschuh 1/OneDrive/Jasper Crease Patterns/Test cps for program/";
        String hardLocation = "/Users/jasperfeldschuh 1/OneDrive/Jasper Crease Patterns/Test cps for program/test cp 2 thick.png";
        String originalCopy = "/Users/jasperfeldschuh 1/OneDrive/Jasper Crease Patterns/Test cps for program/test cp 2 thick original.png";
        String hardNewLocation = "/Users/jasperfeldschuh 1/OneDrive/Jasper Crease Patterns/Test cps for program/test cp 2 thick hardcoded.png";
//
//
        String location = path + name + ".png";
        String newLocation = path + name + " copy.png";

        // this makes sure that the right number of points have been found
        MaxAdjuster adjuster = new MaxAdjuster(location, newLocation, grid, partials, width);  // non hardcoded version
//        MaxAdjuster adjuster = new MaxAdjuster(hardLocation, hardNewLocation, 32, 1, 6, 20250, 49997, 43902);  // hard coded version
        int scanSize = adjuster.getScanSize();
        BufferedImage image = adjuster.getOriginalImage();
        Util.checkColorRecognition(image, newLocation);
        LineDetector lines = new LineDetector(adjuster.getOriginalImage(), adjuster.getPoints(), adjuster.getGrid(), scanSize);


//        boolean stop = false;
//        double imageLength = 952.0;
//        double xStart = 18.5;
//        double yStart = 22.5;
//        while(!stop) {
//            System.out.println("Enter in the x coordinate");
//            while(!scan.hasNextDouble()) {
//            System.out.println("invalid input");
//            scan.next();
//        }
//            double xCoordinate = scan.nextDouble();
//            System.out.println("Enter in the y coordinate");
//            while(!scan.hasNextDouble()) {
//            System.out.println("invalid input");
//            scan.next();
//        }
//            double yCoordinate = scan.nextDouble();
//            for(double i = 0; i < 33; i++){
//                for(double j = 0; j < 33; j++){
//                    int y = (int)(yStart + (imageLength/32.0 * i) + 0.5);
//                    System.out.println("yStart + imageLength/32.0 = " + "yStart" + "+" + imageLength + "/32.0 = " + (yStart + imageLength/32.0));
//                    System.out.println("yStart + imageLength/32.0 * yCoordinate = " + imageLength/32.0 + "*" + yCoordinate + " = " + (yStart + imageLength/32.0 * yCoordinate));
//                    System.out.println("rounded version: " + (int)(yStart + (imageLength/32.0 * yCoordinate) + 0.5));
//                    int x = (int)(xStart + (imageLength/32.0 * j) + 0.5);
//                    Util.makeGreenSquare(adjuster.getOriginalImage(), x, y, 10);
//                }
//            }
//            Util.editImage(adjuster.getOriginalImage(), adjuster.imageLocation());
//        }


    }
}


