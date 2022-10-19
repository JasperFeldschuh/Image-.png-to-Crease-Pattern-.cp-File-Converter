package com.company;

public class Line {
    private int lineType;
    private double cpXstart, cpYstart, cpXend, cpYend;
    private double slope;
    private double distance;
    public Line(int lineType, double cpXstart, double cpYstart, double cpXend, double cpYend){
        // purpose of this class: to hold and organize information about potential lines. It finds the slope and distance, which is then used to get rid of duplicate lines
        this.lineType = lineType; // variable for the line type
        // x and y starting and ending coordinates for the .cp file (not the same number as the buffered image coordinates)
        this.cpXstart = cpXstart;
        this.cpYstart = cpYstart;
        this.cpXend = cpXend; // x end for t
        this.cpYend = cpYend;
        // rise and run (for calculating slope)
        double run = cpXend - cpXstart;
        double rise = cpYend - cpYstart;
        // getting the distance
        if(rise == 0 && run != 0){
            distance = run;
        } else if(run == 0 && rise != 0){
            distance = rise;
        } else{
            distance = Math.sqrt(Math.pow(rise, 2) + Math.pow(run, 2));
        }
        // making sure distance is always positive, otherwise some duplicate lines will not get removed
        if(distance < 0){
            distance *= -1;
        }
        // dealing with undefined slopes
        if(run == 0 && rise < 0){
//            System.out.println("slope set to minimum value");
            slope = Double.MIN_VALUE; // if the slope is undefined and the starting values are larger than the ending values
        } else if(run == 0 && rise > 0){
//            System.out.println("slope set to maximum value");
            slope = Double.MAX_VALUE; // if the slope is undefined and the starting values are smaller than the ending values
        }else {
            slope = (cpYend - cpYstart) / run;
        }
    }
    // returns the slope of the line
    public double getSlope(){
        return slope;
    }
    // returns the run
    public double getDistance(){
        return distance;
    }
    // returns the line in .cp file format
    public String toString(){
        return lineType + " " + cpXstart + " " + cpYstart + " " + cpXend + " " + cpYend + "\n";
    }
    // is used for debugging and returns extra information in a format that is easier for humans to read
    public void lineAttributes(){
        System.out.println("Length: " + distance + " slope: " + slope + " cpXStart: " + cpXstart + " cpYStart: " + cpYstart + " cpXEnd: " + cpXend + " cpYEnd: " + cpYend);
    }

}
