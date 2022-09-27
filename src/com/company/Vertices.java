package com.company;
import java.awt.image.BufferedImage;
import java.sql.Array;
import java.util.ArrayList;

public class Vertices {
    private ArrayList <Coordinates> coordinates = new ArrayList <Coordinates>();
    //private ArrayList <ArrayList<Integer>> coordinates = new ArrayList<ArrayList<Integer>>();
    private BufferedImage image = null;
    private MaxAllowance max;
    private ArrayList <Coordinates> gridPoints = new ArrayList<Coordinates>();
    private int scanSize;
    public Vertices(BufferedImage image, double startingX, double endingX, double startingY, double endingY, int grid, int partials, int lineWidth, MaxAllowance max) {
        this.coordinates = coordinates;
        this.image = image;
        this.max = max;
        // declaring stuff once so I don't have to call the class in the future
        scanSize = max.getScanLength();
        // making arraylist of all possible points
        boolean edge = false;
        double imageLength = endingX - startingX;
        System.out.println("image length " + imageLength);
        System.out.println("Starting x: " + startingX + " Starting y: " + startingY + " ending x: " + endingX + " ending y: " + endingY);
        int xCoordinate;
        int yCoordinate;
        int trueGrid = grid * partials;
        double increment = 400.0 / trueGrid;
        // manually adding the four corners of the image
        Coordinates topLeft = new Coordinates((int)(startingX + 0.5) + lineWidth, (int)(startingY + 0.5) + lineWidth, false, true, -200, -200);  // top left corner
        gridPoints.add(topLeft);  // adding to array
        Coordinates topRight = new Coordinates((int)(endingX + 0.5) - lineWidth, (int)(startingY + 0.5) + lineWidth, false, true, 200, -200);  // top right corner
        gridPoints.add(topRight);  // adding to array
        Coordinates bottomLeft = new Coordinates((int)(startingX + 0.5) + lineWidth, (int)(endingY + 0.5) - lineWidth, false, true, -200, 200);  // bottom left corner
        gridPoints.add(bottomLeft);  // adding to array
        Coordinates bottomRight = new Coordinates((int)(endingX + 0.5) - lineWidth, (int)(endingY + 0.5) - lineWidth, false, true, 200, 200);  // bottom right corner
        gridPoints.add(bottomRight);  // adding to array
        double yCP = -200.0;  // starting y value for the .cp file
        for (double i = 0; i <= trueGrid; i++) { // outside part of nested for loop which makes all potential intersections.
            boolean xEdge = false;
            if (i == 0) {  // if the point is at the beginning or end of the for loop, it is on the edge
                yCoordinate = (int)(startingY + 0.5) + lineWidth;
                xEdge = true;
            } else if(i == trueGrid){  // if the point is at the beginning or end of the for loop, it is on the edge
                yCoordinate = (int)(imageLength / trueGrid * i + startingY + 0.5) - lineWidth;
                xEdge = true;
            } else{  // no offset is needed if the point is in the center
//                System.out.println("imageLength = " + imageLength + " trueGrid = " + trueGrid)
                yCoordinate = (int)(imageLength / trueGrid * i + startingY + 0.5);
            }
            double xCP = -200.0; // starting x value of the cp file. gets reset for each row
            for (double j = 0; j <= trueGrid; j++) {  // if the point is at the beginning or end of the for loop, it is on the edge
                Coordinates point = new Coordinates();
                if (j == 0) {
                    xCoordinate = (int)(startingX + 0.5) + lineWidth;
                    point.makeEdge();
                } else if(j == trueGrid){  // if the point is at the beginning or end of the for loop, it is on the edge
                    xCoordinate = (int)(imageLength / trueGrid * j + startingX + 0.5) - lineWidth;
                    point.makeEdge();
                } else{  // no offset is needed if the point is in the center
                    xCoordinate = (int)(imageLength / trueGrid * j + startingX + 0.5);
                }
                point.setX(xCoordinate);
                if(xEdge){
                    point.makeEdge();
                }
                point.setY(yCoordinate);
                point.setCPX(xCP);
                point.setCPY(yCP);
                if(!(j == 0 && i == 0) && !(j == 0 && i == trueGrid) && !(j == trueGrid && i == 0) && !(j == trueGrid && i == trueGrid)){
                    gridPoints.add(point);
                }
                xCP  += increment;
            }
            yCP += increment;
        }
        // ************ IMPORTANT!!!**********
        // THIS FOR LOOP WILL MARK ALL GRIDPOINTS ON THE IMAGE!!!!
        // ONLY FOR TESTING
//        for(int i = 0; i < gridPoints.size(); i++){
//            Util.makeBlack(image, gridPoints.get(i).getX(),gridPoints.get(i).getY(), scanSize);
//        }
    }
    public ArrayList<Coordinates> findIntersections(int cornerMax, int edgeMax, int centerMax){
        // finding the intersections
        ArrayList <Coordinates> intersections = new ArrayList<Coordinates>();
        System.out.println("edge max: " + edgeMax + " center max: " + centerMax + "corner max: " + cornerMax);  // test print statement
        for(int i = 0; i < 4; i++){
            if(Util.totalRGB(image, gridPoints.get(i).getX(), gridPoints.get(i).getY(), scanSize) <= cornerMax){
                intersections.add(gridPoints.get(i));
            }
        }
        for(int i = 4; i < gridPoints.size(); i++){     // checking for intersections on the grid points
            if(gridPoints.get(i).onEdge()){
                if(Util.totalRGB(image, gridPoints.get(i).getX(), gridPoints.get(i).getY(), scanSize) <= edgeMax){   // normally is edgeMax
                    intersections.add(gridPoints.get(i));
                }
            } else if(Util.totalRGB(image, gridPoints.get(i).getX(), gridPoints.get(i).getY(), scanSize) <= centerMax){   // normally is centerMax
                intersections.add(gridPoints.get(i));
            }
        }
        // marking the points on the image
        for(int i = 0; i < intersections.size(); i++){
            Util.makeBlack(image, intersections.get(i).getX(), intersections.get(i).getY(), scanSize);
        }
        return intersections;
    }
    public ArrayList<Coordinates> findEdges(){
        ArrayList <Coordinates> intersections = new ArrayList<Coordinates>();
        int edgeMax = max.getEdgeAllowance();
        int centerMax = max.getCenterAllowance();
        int cornerMax = max.getCornerAllowance();
        System.out.println("edge max: " + edgeMax + " center max: " + centerMax + "corner max: " + cornerMax);  // test print statement
        for(int i = 4; i < gridPoints.size(); i++) {     // checking for intersections on the grid points
            if (gridPoints.get(i).onEdge()) {
                if (Util.totalRGB(image, gridPoints.get(i).getX(), gridPoints.get(i).getY(), scanSize) <= edgeMax) {   // normally is edgeMax
                    intersections.add(gridPoints.get(i));
                }
            }
        }
        // marking the points on the image
        for(int i = 0; i < intersections.size(); i++){
            Util.makeBlack(image, intersections.get(i).getX(), intersections.get(i).getY(), scanSize);
        }
        return intersections;
    }
    public ArrayList<Coordinates> findCorners(){
        // finding the intersections
        ArrayList <Coordinates> intersections = new ArrayList<Coordinates>();
        int edgeMax = max.getEdgeAllowance();
        int centerMax = max.getCenterAllowance();
        int cornerMax = max.getCornerAllowance();

        System.out.println("edge max: " + edgeMax + " center max: " + centerMax + "corner max: " + cornerMax);  // test print statement
        for(int i = 0; i < 4; i++){
            if(Util.totalRGB(image, gridPoints.get(i).getX(), gridPoints.get(i).getY(), scanSize) <= cornerMax){
                intersections.add(gridPoints.get(i));
            }
        }
        // marking the points on the image
        for(int i = 0; i < intersections.size(); i++){
            Util.makeBlack(image, intersections.get(i).getX(), intersections.get(i).getY(), scanSize);
        }
        System.out.println("corners found: " + intersections.size());
        return intersections;
    }
    public ArrayList<Coordinates> findCenters(){
        // finding the intersections
        ArrayList <Coordinates> intersections = new ArrayList<Coordinates>();
        int edgeMax = max.getEdgeAllowance();
        int centerMax = max.getCenterAllowance();
        int cornerMax = max.getCornerAllowance();
        System.out.println("edge max: " + edgeMax + " center max: " + centerMax + "corner max: " + cornerMax);  // test print statement
        int pointsFound = 0;
        System.out.println("Center Max is: " + centerMax);
        for(int i = 4; i < gridPoints.size(); i++) {     // checking for intersections on the grid points
            if (gridPoints.get(i).inCenter()) {
                if (Util.totalRGB(image, gridPoints.get(i).getX(), gridPoints.get(i).getY(), scanSize) <= centerMax) {
                    pointsFound++;
                    if(Util.totalRGB(image, gridPoints.get(i).getX(), gridPoints.get(i).getY(), scanSize) == 0){
                        System.out.println("********** ZERO POINT DETECTED **********");
                    }
//                    System.out.println("Point added. Total RGB: " + Util.totalRGB(image, gridPoints.get(i).getX(), gridPoints.get(i).getY(), scanSize) + " center max: " + centerMax);
                    intersections.add(gridPoints.get(i));
                }
            }
        }
        System.out.println("points found: " + pointsFound);
        // marking the points on the image
        for(int i = 0; i < intersections.size(); i++){
            Util.makeBlack(image, intersections.get(i).getX(), intersections.get(i).getY(), scanSize);
        }
        return intersections;
    }
    public void centerCornerOrEdge(){
        for(int i = 0; i < gridPoints.size(); i++){
            if(gridPoints.get(i).onCorner()){
                Util.makeRed(image, gridPoints.get(i).getX(), gridPoints.get(i).getY(), scanSize);
            } else if(gridPoints.get(i).onEdge()){
                Util.makeGreenSquare(image, gridPoints.get(i).getX(), gridPoints.get(i).getY(), scanSize);
            } else{
                Util.makeBlack(image, gridPoints.get(i).getX(), gridPoints.get(i).getY(), scanSize);
            }
        }
    }
    public void showGrid(){
        for (int i = 0; i < gridPoints.size(); i++) {
            Util.makeBlack(image, gridPoints.get(i).getX(), gridPoints.get(i).getY(), scanSize);
        }
    }
    public void showGridPixel(){
        for(int i = 0; i < gridPoints.size(); i++){
            Util.makeGreen(image, gridPoints.get(i).getX(), gridPoints.get(i).getY());
        }
    }
    public BufferedImage getImage(){
        return image;
    }

    public ArrayList getArray(){
        return coordinates;
    }
}
