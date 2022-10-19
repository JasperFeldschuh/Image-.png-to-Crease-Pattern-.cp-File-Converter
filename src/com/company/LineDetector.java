package com.company;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
public class LineDetector {
    private BufferedImage image;
    private String cpString = ""; // string to hold the numerical values for the .cp file format: line type (1 = edge, 2 = mountain, 3 = valley) , starting coordinates, ending coordinates
    private ArrayList<Coordinates> points;
    private int grid, scanSize;

    public LineDetector(BufferedImage image, ArrayList<Coordinates> points, int grid, int scanSize) {
        // purpose of class: to find all possible lines, filter out duplicate lines, and make the .CP file
        System.out.println("LINE DETECTOR METHOD ENTERED");
        this.image = image;
        this.points = points;
        this.grid = grid;
        this.scanSize = scanSize;
        double increment = 400.0 / grid; // manually setting the edges of the crease pattern to make an edge line every grid unit
        for (double i = -200; i <= 200 - increment; i += increment) {
            cpString += "1 " + i + " -200 " + (i + increment) + " -200\n";        // top edge
            cpString += "1 " + i + " 200 " + (i + increment) + " 200\n";         // bottom edge
            cpString += "1 " + "-200 " + i + " -200 " + (i + increment) + "\n"; // left edge
            cpString += "1 " + "200 " + i + " 200 " + (i + increment) + "\n";  // right edge
        }
        ArrayList<ArrayList<Line>> potentialLines = getPotentialLines(image, points, grid, scanSize); // getting all possible lines
        getFinalLines(potentialLines); // filtering out duplicates
        makeCP(); // making the cp file
    }


    // boolean statements to check if it is a line
    private static int isLine(int averageRGB, double redAverage, double blueAverage) {
//        System.out.println("Average rgb is " + averageRGB + " red average: " + redAverage + " blue average: " + blueAverage);
        if (averageRGB < 200) { // rgb value this low means it can only be black
            return 1; // these numbers aren't arbitrary, they are used in the .CP file to tell crease pattern software what color the line should be (1 is edge, 2 is mountain, 3 is valley)
        }
        if (averageRGB < 650) { // rgb value greater than 650 means it must be white
            if (redAverage > 0.5 && redAverage / blueAverage > 2) { // if mostly read, the line is deemed to be red
                return 2;
            } else if (blueAverage > 0.5 && blueAverage / redAverage > 2) { // if mostly blue, the line is deemed to be blue
                return 3;
            }
        }
        return 0; // a value of zero means that it is not a valid line
    }

    // returns a 2D arrayList of all potential points, organized by coordinate point
    private ArrayList<ArrayList<Line>> getPotentialLines(BufferedImage image, ArrayList<Coordinates> points, int grid, int scanSize) {
        System.out.println("GETTING POTENTIAL LINES");
        ArrayList<ArrayList<Line>> lineGroups = new ArrayList<ArrayList<Line>>();
        for (int i = 0; i < points.size() - 1; i++) {
            ArrayList<Line> linesFromPoint = new ArrayList<Line>(); // this needs to reset after each point
            for (int j = i + 1; j < points.size(); j++) {
                Coordinates start = points.get(i);
                Coordinates end = points.get(j);
                int[] data = Util.lineData(image, points.get(i), points.get(j), scanSize); // collecting points like this for efficiency
//                System.out.println("AVERAGE RGB: " + data[0] + " RED COUNT: " + data[1] + " BLUE COUNT: " + data[2] + " ITERATIONS " + data[3]);
                    int averageRGB = data[0];
                    double redAverage = 1.0 * data[1] / data[3];
                    double blueAverage = 1.0 * data[2] / data[3];
                    int lineValue = isLine(averageRGB, redAverage, blueAverage);
//                    System.out.println("lineValue is " + lineValue);
                    if (lineValue != 0) { // adding the coordinates to the .cp file text (temporarily stored in a String object
                        Line line = new Line(lineValue, start.getCPX(), start.getCPY(), end.getCPX(), end.getCPY());
                        linesFromPoint.add(line);
//                        System.out.println("point added");
//                        cpString += lineValue + " " + start.getCPX() + " " + start.getCPY() + " " + end.getCPX() + " " + end.getCPY() + "\n";
                    }
                }
            lineGroups.add(linesFromPoint); // if the line is valid, it gets added to the list lists of lines coming from a single point
            }
        return lineGroups;
    }
    // removes all lines containing smaller lines inside of them
    public void getFinalLines(ArrayList<ArrayList<Line>> lineGroups){
        System.out.println("GETTING FINAL LINES");
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
                        }
                    }
                    for(int k = j - 1; k >= 0; k--){ // checking if any lines before invalidate the current line
                        if (slope == lineGroups.get(i).get(k).getSlope() && distance > lineGroups.get(i).get(k).getDistance()) {
//                        System.out.println("false line found");
//                            cpString += "FALSE LINE: " + lineGroups.get(i).get(j).toString();
                            validLine = false;
                        }
                    }
                    if(validLine){
                        cpString += lineGroups.get(i).get(j).toString(); // Adding the first element to the final list if it meets the criteria
                    }
                }
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
