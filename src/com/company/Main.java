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
        Scanner scan = new Scanner(System.in);
        // collecting valid information about the file
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
        // pre-made paths for testing (skipping the file finding process)
        String hardLocation = "/Users/jasperfeldschuh 1/OneDrive/Jasper Crease Patterns/Test cps for program/test cp 2 thick.png";
        String originalCopy = "/Users/jasperfeldschuh 1/OneDrive/Jasper Crease Patterns/Test cps for program/test cp 2 thick original.png";
        String hardNewLocation = "/Users/jasperfeldschuh 1/OneDrive/Jasper Crease Patterns/Test cps for program/test cp 2 thick hardcoded.png";
//
//
        String location = path + name + ".png"; // adding this so the computer can actually see the file
        String newLocation = path + name + " copy.png"; // new location so that the original image doesn't get marked when looking for points

        // this makes sure that the right number of points have been found
        MaxAdjuster adjuster = new MaxAdjuster(location, newLocation, grid, partials, width);  // non hardcoded version
//        MaxAdjuster adjuster = new MaxAdjuster(hardLocation, hardNewLocation, 32, 1, 6, 20250, 49997, 43902);  // hard coded version where you enter in the correct values for a specific image
        int scanSize = adjuster.getScanSize();
        BufferedImage image = adjuster.getOriginalImage(); // getting the image from the MaxAdjuster class so that it can be analyzed by the line detector class for lines
//        Util.checkColorRecognition(image, newLocation); // this will make all pixels in the copy of the image white except for the ones that it sees as red or blue. used to see if the color recognition is working
        LineDetector lines = new LineDetector(adjuster.getOriginalImage(), adjuster.getPoints(), adjuster.getGrid(), scanSize); // finding the lines and making the .CP file
    }
}


