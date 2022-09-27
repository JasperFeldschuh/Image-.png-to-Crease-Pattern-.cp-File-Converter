package com.company;

public class Line {
    private int lineType;
    private double cpXstart, cpYstart, cpXend, cpYend;
    private double slope;
    private double distance;
    public Line(int lineType, double cpXstart, double cpYstart, double cpXend, double cpYend){
        this.lineType = lineType;
        this.cpXstart = cpXstart;
        this.cpYstart = cpYstart;
        this.cpXend = cpXend;
        this.cpYend = cpYend;
        double run = cpXend - cpXstart;
        double rise = cpYend - cpYstart;
        if(rise == 0 && run != 0){
            distance = run;
        } else if(run == 0 && rise != 0){
            distance = rise;
        } else{
            distance = Math.sqrt(Math.pow(rise, 2) + Math.pow(run, 2));
        }
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

}
