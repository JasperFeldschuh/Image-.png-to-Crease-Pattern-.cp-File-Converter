package com.company;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.Scanner;
public class MaxAdjuster {
    private String location;
    private String newLocation;
    private int grid;
    private int partials;
    private final int width;
    private int scanSize;
    private double xStart;
    private double xEnd;
    private double yStart;
    private double yEnd;
    private Vertices points;
    private BufferedImage image;
    private BufferedImage original;
    private ArrayList<Coordinates> finalPoints = new ArrayList<Coordinates>();
    public MaxAdjuster(String location, String newLocation, int grid, int partials, int width) { // non hardcoded version
        // purpose if this class: to calibrate the max allowances to find all of the intersecting lines
        // this class calls the BoundaryFinder and Vertices classes to find the boundaries of the crease pattern and find the intersections
        // if the wrong number of intersections are found, the max allowance values are calibrated with user input until they are correct
        // this process is carried out individually for
        System.out.println("Max adjuster non hardcoded entered");
        // initializing the private variables so that I can use them in the static methods
        this.location = location;
        this.newLocation = newLocation;
        this.grid = grid;
        this.partials = partials;
        this.width = width;
        scanSize = (int)(1.0 * width * 1.5 + 0.5);
        System.out.println("Scan Size is equal to " + scanSize);
        BoundaryFinder boundary = new BoundaryFinder(location, width);  // finding the boundaries of the image
        boundary.showBoundary();
        image = boundary.getImage(); // image with the points marked
        original = boundary.getOriginalImage();  // image without the points marked
        xStart = boundary.getTrueXStart();
        xEnd = boundary.getTrueXEnd();
        yStart = boundary.getTrueYStart();
        yEnd = boundary.getTrueYEnd();
        System.out.println("x start: " + xStart + " x end: " + xEnd + " y start: " + yStart + " y end: " + yEnd);
        finalPoints = adjustCorners(finalPoints);
        finalPoints = adjustEdges(finalPoints);
        finalPoints = adjustCenters(finalPoints);
        ArrayList<Coordinates> unedited = finalPoints; // unedited copy to mark the original location of the points
        Util.editImage(original, newLocation); // marking the points to the image file
    }
    // hardcoded version for testing (called from main and skips the tedious maxAllowance calibration process)
    public MaxAdjuster(String location, String newLocation, int grid, int partials, int width, int cornerMax, int edgeMax, int centerMax) {
        System.out.println("max adjuster entered");
        // initializing the private variables so that I can use them in the static methods
        this.location = location;
        this.newLocation = newLocation;
        this.grid = grid;
        this.partials = partials;
        this.width = width;
        scanSize = (int)(1.0 * width * 1.5 + 0.5);
        BoundaryFinder boundary = new BoundaryFinder(location, width);  // finding the boundaries of the image
        image = boundary.getImage(); // image with the points marked
        original = boundary.getOriginalImage(); // image without the points marked
        xStart = boundary.getTrueXStart();
        xEnd = boundary.getTrueXEnd();
        yStart = boundary.getTrueYStart();
        yEnd = boundary.getTrueYEnd();
        System.out.println("x start: " + xStart + " x end: " + xEnd + " y start: " + yStart + " y end: " + yEnd);
        xStart = boundary.getTrueXStart();
        MaxAllowance max = new MaxAllowance(6, 256); // this line is just so the constructor is satisfied but max is never used
        points = new Vertices(image, xStart, xEnd, yStart, yEnd, grid, partials, width, max);
        finalPoints = points.findIntersections(cornerMax, edgeMax, centerMax);
    }
    // adjusts the corner sensitivity
    private ArrayList<Coordinates> adjustCorners(ArrayList<Coordinates> finalPoints){
        Scanner scan = new Scanner(System.in);
        boolean validIntersections = false;      // boolean statement to stop the continous scanning of the document
        int totalChange = 0;     // total change value to update the max allowances
        int changeAmount = 256; // amount added/subtracted from total change, eventually dividing down to one if necessary (256 = 2^8)
        boolean direction = false;
        int continuedDirection = 0;
        boolean firstIteration = true;
        Vertices points = null;
        ArrayList<Coordinates> corners = new ArrayList<Coordinates>();
        while (!validIntersections) {
            MaxAllowance max = new MaxAllowance(width, totalChange);  //maximum allowances for counting as an intersection
            points = new Vertices(image, xStart, xEnd, yStart, yEnd, grid, partials, width, max);
            corners = points.findCorners();
            // making the file
            Util.editImage(points.getImage(), newLocation);
            System.out.println("Check that the new file has the right number of corner points marked. If that is the case, type y, otherwise, type n");
            String answer = scan.nextLine();
            answer = Util.ensureValid(answer, "y", "n");
            if (answer.equals("y")) {
                System.out.println("corner max: " + max.getCornerAllowance());
                break;
            }
            if (answer.equals("n")) {
                image = Util.resetImage(image, original); // resetting the image to remove the black marking
                System.out.println("type m for too many points and n for not enough points");
            }
            answer = scan.nextLine();
            answer = Util.ensureValid(answer, "m", "n");
            if (answer.equals("m")) {       // enters only if there are too many points
                if (firstIteration == true || direction == false) {
                    continuedDirection++;
                    System.out.println(continuedDirection);
                }     // only checks for direction change after first iteration
                if (!firstIteration) {
                    if (direction == true || continuedDirection == 2) {     // answer m will make the direction false so if the direction was previously true, then the direction changes
                        System.out.println("direction change entered");
                        continuedDirection = 1;
                        System.out.println(continuedDirection);
                        changeAmount /= 2;// we want to divide the total change amount by 2 every time we change direction
                    }
                }
                totalChange -= changeAmount;      // subtracting the change amount to make the program less sensitive
                System.out.println(totalChange); //tester
                direction = false;              // setting the direction to true regardless of previous direction
                firstIteration = false;
            }
            if (answer.equals("n")) {       // only enters if there are too few points
                if (firstIteration == true || direction == true) {
                    continuedDirection++;
                    System.out.println(continuedDirection);
                }      // only checks for direction change after first iteration
                if (!firstIteration) {
                    if (direction == false || continuedDirection == 2) {      // answer n will make the direction true so if the direction was previously false, then the direction changes
                        System.out.println("direction change entered");
                        continuedDirection = 1; // setting to -1 so that it will turn into zero when incremented
                        System.out.println(continuedDirection);
                        changeAmount /= 2;// we want to divide the total change amount by 2 every time we change direction
                    }
                }
                totalChange += changeAmount;   // adding the change amount to make the program more sensitive
                System.out.println(totalChange);  // tester
                direction = true;               // setting the direction to true regardless of previous direction
                firstIteration = false;
            }
        }
        for(int i = 0; i < corners.size(); i++){  // adding the edge points to the arraylist of all points
            finalPoints.add(corners.get(i));
        }
        return finalPoints;
    }
    // adjusts the edge sensitivity
    private ArrayList<Coordinates> adjustEdges(ArrayList<Coordinates> edgePoints){
        Scanner scan = new Scanner(System.in);
        boolean validIntersections = false;      // boolean statement to stop the continous scanning of the document
        int totalChange = 0;     // total change value to update the max allowances
        int changeAmount = 256; // amount added/subtracted from total change, eventually dividing down to one if necessary (256 = 2^8)
        boolean direction = false;
        int continuedDirection = 0;
        boolean firstIteration = true;
        Vertices points = null;
        ArrayList<Coordinates> edges = new ArrayList<Coordinates>();
        while (!validIntersections) {
            MaxAllowance max = new MaxAllowance(width, totalChange);  //maximum allowances for counting as an intersection
            points = new Vertices(image, xStart, xEnd, yStart, yEnd, grid, partials, width, max);
            edges = points.findEdges();
            System.out.println("edges found: " + edges.size());

            // making the file
            Util.editImage(points.getImage(), newLocation);
            System.out.println("Check that the new file has the right number of edge points marked If that is the case, type y, otherwise, type n");
            String answer = scan.nextLine();
            answer = Util.ensureValid(answer, "y", "n");
            if (answer.equals("y")) {
                System.out.println("edge max: " + max.getEdgeAllowance());
                break;
            }
            if (answer.equals("n")) {
                image = Util.resetImage(image, original); // resetting the image to remove the black marking
                System.out.println("type m for too many points and n for not enough points");
            }
            answer = scan.nextLine();
            answer = Util.ensureValid(answer, "m", "n");
            if (answer.equals("m")) {       // enters only if there are too many points
                if (firstIteration == true || direction == false) {
                    continuedDirection++;
                    System.out.println(continuedDirection);
                }     // only checks for direction change after first iteration
                if (!firstIteration) {
                    if (direction == true || continuedDirection == 2) {     // answer m will make the direction false so if the direction was previously true, then the direction changes
                        System.out.println("direction change entered");
                        continuedDirection = 1;
                        System.out.println(continuedDirection);
                        changeAmount /= 2;// we want to divide the total change amount by 2 every time we change direction
                    }
                }
                totalChange -= changeAmount;      // subtracting the change amount to make the program less sensitive
                System.out.println(totalChange); //tester
                direction = false;              // setting the direction to true regardless of previous direction
                firstIteration = false;
            }
            if (answer.equals("n")) {       // only enters if there are too few points
                if (firstIteration == true || direction == true) {
                    continuedDirection++;
                    System.out.println(continuedDirection);
                }      // only checks for direction change after first iteration
                if (!firstIteration) {
                    if (direction == false || continuedDirection == 2) {      // answer n will make the direction true so if the direction was previously false, then the direction changes
                        System.out.println("direction change entered");
                        continuedDirection = 1; // setting to -1 so that it will turn into zero when incremented
                        System.out.println(continuedDirection);
                        changeAmount /= 2;// we want to divide the total change amount by 2 every time we change direction
                    }
                }
                totalChange += changeAmount;   // adding the change amount to make the program more sensitive
                System.out.println(totalChange);  // tester
                direction = true;               // setting the direction to true regardless of previous direction
                firstIteration = false;
            }
        }
        for(int i = 0; i < edges.size(); i++){
           finalPoints.add(edges.get(i));
        }
        return finalPoints;
    }
    // adjusts the center sensitivity
    private ArrayList<Coordinates> adjustCenters(ArrayList<Coordinates> finalPoints){
        Scanner scan = new Scanner(System.in);
        boolean validIntersections = false;      // boolean statement to stop the continous scanning of the document
        int totalChange = 0;     // total change value to update the max allowances
        int changeAmount = 256; // amount added/subtracted from total change, eventually dividing down to one if necessary (256 = 2^8)
        boolean direction = false;
        int continuedDirection = 0;
        boolean firstIteration = true;
        Vertices points = null;
        ArrayList<Coordinates> centers = new ArrayList<Coordinates>();
        while (!validIntersections) {
            MaxAllowance max = new MaxAllowance(width, totalChange);  //maximum allowances for counting as an intersection
            System.out.println("total change is: " + totalChange);
            points = new Vertices(image, xStart, xEnd, yStart, yEnd, grid, partials, width, max);
            centers = points.findCenters();

            // making the file
            Util.editImage(image, newLocation);
            System.out.println("Check that the new file has the right number of center points marked. If that is the case, type y, otherwise, type n");
            String answer = scan.nextLine();
            answer = Util.ensureValid(answer, "y", "n");
            if (answer.equals("y")) {
                System.out.println("center max: " + max.getCenterAllowance());
                break;
            }
            if (answer.equals("n")) {
                image = Util.resetImage(image, original); // resetting the image to remove the black marking
                System.out.println("type m for too many points and n for not enough points");
            }
            answer = scan.nextLine();
            answer = Util.ensureValid(answer, "m", "n");
            if (answer.equals("m")) {       // enters only if there are too many points
                if (firstIteration == true || direction == false) {
                    continuedDirection++;
                    System.out.println(continuedDirection);
                }     // only checks for direction change after first iteration
                if (!firstIteration) {
                    if (direction == true || continuedDirection == 2) {     // answer m will make the direction false so if the direction was previously true, then the direction changes
                        System.out.println("direction change entered");
                        continuedDirection = 1;
                        System.out.println(continuedDirection);
                        changeAmount /= 2;// we want to divide the total change amount by 2 every time we change direction
                    }
                }
                totalChange -= changeAmount;      // subtracting the change amount to make the program less sensitive
                System.out.println(totalChange); //tester
                direction = false;              // setting the direction to true regardless of previous direction
                firstIteration = false;
            }
            if (answer.equals("n")) {       // only enters if there are too few points
                if (firstIteration == true || direction == true) {
                    continuedDirection++;
                    System.out.println(continuedDirection);
                }      // only checks for direction change after first iteration
                if (!firstIteration) {
                    if (direction == false || continuedDirection == 2) {      // answer n will make the direction true so if the direction was previously false, then the direction changes
                        System.out.println("direction change entered");
                        continuedDirection = 1; // setting to -1 so that it will turn into zero when incremented
                        System.out.println(continuedDirection);
                        changeAmount /= 2;// we want to divide the total change amount by 2 every time we change direction
                    }
                }
                totalChange += changeAmount;   // adding the change amount to make the program more sensitive
                System.out.println(totalChange);  // tester
                direction = true;               // setting the direction to true regardless of previous direction
                firstIteration = false;
            }
        }
        for(int i = 0; i < centers.size(); i++){
            finalPoints.add(centers.get(i));
        }
        return finalPoints;
    }
    // adjusts the points to their exact right spots
    private int[] calibratePoints(Coordinates c){
        int x = c.getX(); // the x and y values will constantly be updated as more optimal positions are found
        int y = c.getY();
        int benchMark = Util.totalRGB(image, x, y, scanSize); // the benchMark will constantly be updated as more optimal positions are found
        boolean xOptimised = false;
        boolean yOptimised = false;
        int count = 0;
        // checking if moving to the left results in a lower totalRGB value
        do{
            x--;
            count++;
        } while(Util.totalRGB(image, x, y, scanSize) < benchMark);
        x++; // increased by 1 because the doWhile loop is only exited after it becomes false
        if(count < 2){ // checking if we need to check in the other direction or not
            do { // if so, we check in the other direction
                x++;
            } while (Util.totalRGB(image, x, y, scanSize) < benchMark);
            x--; // cancelling out the last iteration
            benchMark = Util.totalRGB(image, x, y, scanSize); // updating benchmark
        } else{
            benchMark = Util.totalRGB(image, x, y, scanSize); // updating benchMark if we didn't move to the left
        }
        count = 0; // resetting count for the next direction
        // checking if moving the point up is better
        do{
            y--;
            count++;
        } while(Util.totalRGB(image, x, y, scanSize) < benchMark);
        y++; // cancelling the last iteration
        if(count < 2){ // checking if moving down is better only if we haven't moved up already
            do{
                y++;
            } while (Util.totalRGB(image, x, y, scanSize) < benchMark);
            y--; // cancelling out the last iteration
        }
        System.out.println("Old x: " + c.getX() + "Old y: " + c.getY() + "New x: " + x + " New y: " + y);
        return new int[]{x, y};
    }
    // returns the final array of points
    public ArrayList<Coordinates> getPoints(){
        return finalPoints;
    }
    public BufferedImage getImage(){
        return image;
    }
    public BufferedImage getOriginalImage(){
        return original;
    }
    public String imageLocation(){
        return newLocation;
    }
    public int getScanSize(){
        return scanSize;
    }
    public int getGrid(){
        return grid;
    }
}

