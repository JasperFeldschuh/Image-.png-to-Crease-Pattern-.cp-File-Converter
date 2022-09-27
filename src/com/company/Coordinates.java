package com.company;

public class Coordinates {
    private int x;
    private int y;
    private boolean onEdge = false;
    private boolean onCorner = false;
    private double cpX;
    private double cpY;
    private double cpXStart = -200.0;
    private double cpYStart = -200.0;

    public Coordinates(int x, int y, boolean onEdge, boolean onCorner, double cpX, double cpY){
        this.x = x;
        this.y = y;
        this.onEdge = onEdge;
        this.onCorner = onCorner;
        this.cpX = cpX;
        this.cpY = cpY;
    }
    public Coordinates(){  // used to declare a coordinate before the points and edge boolean are set
    }
    // sets x
    public void setX(int x) {
        this.x = x;
    }
    // sets y
    public void setY(int y) {
        this.y = y;
    }
    // sets x value for .cp file
    public void setCPX(double cpX){
        this.cpX = cpX;
    }
    // sets y value for .cp file
    public void setCPY(double cpY){
        this.cpY = cpY;
    }
    // returns CPX
    public double getCPX(){
        return cpX;
    }
    // returns CPY
    public double getCPY(){
        return cpY;
    }
    //returns x
    public int getX(){
        return x;
    }
    //returns y
    public int getY(){
        return y;
    }
    public boolean inCenter(){
        if(onEdge || onCorner){
            return false;
        }
        return true;
    }
    // returns the edge boolean statement
    public boolean onEdge(){
        return onEdge;
    }
    // returns the boolean statment
    public boolean onCorner(){
        return onCorner;
    }
    // sets the boolean to true
    public void makeEdge(){
        onEdge = true;
    }
    // returns string representation of the x and y coordinates
    public String toString(){
        return("y coordinate: " + y + " x coordinate: " + x);
    }
}
