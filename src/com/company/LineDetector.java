package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
public class LineDetector {
    private BufferedImage image;
    private ArrayList pointsCP;
    private String cpString = ""; // string to hold the numerical values for the .cp file format: line type (1 = edge, 2 = mountain, 3 = valley) , starting coordinates, ending coordinates
    private ArrayList<Coordinates> points;
    private int grid, scanSize;

    public LineDetector(BufferedImage image, ArrayList<Coordinates> points, int grid, int scanSize) {
        System.out.println("LINE DETECTOR METHOD ENTERED");
        this.image = image;
        this.points = points;
        this.grid = grid;
        this.scanSize = scanSize;
        double increment = 400.0 / grid; // manually setting the edges of the crease pattern
        for (double i = -200; i <= 200 - increment; i += increment) {
            cpString += "1 " + i + " -200 " + (i + increment) + " -200\n";        // top edge
            cpString += "1 " + i + " 200 " + (i + increment) + " 200\n";         // bottom edge
            cpString += "1 " + "-200 " + i + " -200 " + (i + increment) + "\n"; // left edge
            cpString += "1 " + "200 " + i + " 200 " + (i + increment) + "\n";  // right edge
        }
        ArrayList<ArrayList<Line>> potentialLines = getPotentialLines(image, points, grid, scanSize);

        getFinalLines(potentialLines);

        makeCP();
    }


    // boolean statements to check if it is a line
    private static int isLine(int averageRGB, double redAverage, double blueAverage) {
//        System.out.println("Average rgb is " + averageRGB + " red average: " + redAverage + " blue average: " + blueAverage);
        if (averageRGB < 200) {
            return 1;
        }
        if (averageRGB < 650) {
            if (redAverage > 0.5 && redAverage / blueAverage > 2) {
                return 2;
            } else if (blueAverage > 0.5 && blueAverage / redAverage > 2) {
                return 3;
            }
        }
        return 0;
    }

    // returns a 2D arrayList of all potential points, organized by coordinate point
    private static ArrayList<ArrayList<Line>> getPotentialLines(BufferedImage image, ArrayList<Coordinates> points, int grid, int scanSize) {
        System.out.println("GETTING POTENTIAL LINES");
        ArrayList<ArrayList<Line>> lineGroups = new ArrayList<ArrayList<Line>>();
        for (int i = 0; i < points.size() - 1; i++) {
            ArrayList<Line> linesFromPoint = new ArrayList<Line>(); // this needs to reset after each point
            for (int j = i + 1; j < points.size(); j++) {
                Coordinates start = points.get(i);
                Coordinates end = points.get(j);
//                double slope = Util.getSlope(start, end)[0];
                int[] data = Util.lineData(image, points.get(i), points.get(j), scanSize); // collecting points like this for efficiency
//                System.out.println("AVERAGE RGB: " + data[0] + " RED COUNT: " + data[1] + " BLUE COUNT: " + data[2] + " ITERATIONS " + data[3]);
                int averageRGB = data[0];
                double redAverage = 1.0 * data[1] / data[3];
                double blueAverage = 1.0 * data[2] / data[3];
                int lineValue = isLine(averageRGB, redAverage, blueAverage);
                if (lineValue != 0) { // adding the coordinates to the .cp file text (temporarily stored in a String object
                    Line line = new Line(lineValue, start.getCPX(), start.getCPY(), end.getCPX(), end.getCPY());
                    linesFromPoint.add(line);
                }
            }

            lineGroups.add(linesFromPoint);
//            System.out.println("LINE GROUP " + i);
//            System.out.print(linesFromPoint.get(i) + " ");
        }
        System.out.println(lineGroups);
        return lineGroups;
    }
    // removes all lines containing smaller lines inside of them
    public void getFinalLines(ArrayList<ArrayList<Line>> lineGroups){
        System.out.println("GETTING FINAL LINES");
        ArrayList<Line> finalLines = new ArrayList<Line>();  // arraylist to hold the final line data
        for(int i = 0; i < lineGroups.size(); i++){  // traversing the arraylist of arraylists of lines coming from single point
                for (int j = 0; j < lineGroups.get(i).size(); j++) { // traversing through an individual linegroup arraylist
                    boolean validLine = true; // if this stays true by the end, the line is added to the final list
                    double slope = lineGroups.get(i).get(j).getSlope(); // getting the slope
                    double distance = lineGroups.get(i).get(j).getDistance();  // getting the run
                    // checking if any lines (before or after) in the line group make this line invalid
                    for(int k = j + 1; k < lineGroups.get(i).size(); k++){ // checking if any lines ahead in the group invalidate the current line
                        if (slope == lineGroups.get(i).get(k).getSlope() && distance > lineGroups.get(i).get(k).getDistance()) {
//                        System.out.println("false line found");
//                            cpString += "FALSE LINE: " + lineGroups.get(i).get(j).toString();
                            validLine = false;
                        }//else{
                           // System.out.println("Line slope: " + slope + " Case slope: " + lineGroups.get(i).get(k).getSlope() + " Line distance: " + distance + " Case distance: " + lineGroups.get(i).get(k).getDistance());
                        //}
                    }
                    for(int k = j - 1; k >= 0; k--){ // checking if any lines before invalidate the current line
                        if (slope == lineGroups.get(i).get(k).getSlope() && distance > lineGroups.get(i).get(k).getDistance()) {
//                        System.out.println("false line found");
//                            cpString += "FALSE LINE: " + lineGroups.get(i).get(j).toString();
                            validLine = false;
                        } //else{
                            //System.out.println("Line slope: " + slope + " Case slope: " + lineGroups.get(i).get(k).getSlope() + " Line distance: " + distance + " Case distance: " + lineGroups.get(i).get(k).getDistance());
                        //}
                    }
                    if(validLine){
                        cpString += lineGroups.get(i).get(j).toString(); // Adding the first element to the final list if it meets the criteria
                    }
                }
//                System.out.println(i + " out of " + lineGroups.size());
        }
    }

        // method to make the .cp file
        private void makeCP () {
            String content = cpString;
            FileOutputStream fos = null;
            File file;

            try {
                //Specify the file path here
                file = new File("/Users/jasperfeldschuh 1/OneDrive/Jasper Crease Patterns/Test cps for program/first cp from program.cp");
                fos = new FileOutputStream(file);

                /* This logic will check whether the file
                 * exists or not. If the file is not found
                 * at the specified location it would create
                 * a new file*/
                if (!file.exists()) {
                    System.out.println("NEW FILE CREATED");
                    file.createNewFile();
                }

                /*String content cannot be directly written into
                 * a file. It needs to be converted into bytes
                 */
                byte[] bytesArray = content.getBytes();

                fos.write(bytesArray);
                fos.flush();
                System.out.println("File Written Successfully");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException ioe) {
                    System.out.println("Error in closing the Stream");
                }
            }
        }
}
