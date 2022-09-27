package com.company;

public class MaxAllowance {
    private int centerAllowance;
    private int edgeAllowance;
    private int cornerAllowance;
    private int scanLength;
    private int scanArea;
    public MaxAllowance(int lineWidth, int change){
        this.scanLength = (int)(lineWidth * 1.5 + 0.5);
        this.scanArea = scanLength * scanLength;
        if(lineWidth == 1){
            centerAllowance = (200 + change) * scanArea;
            edgeAllowance = (275 + change) * scanArea;
            cornerAllowance = (175 + change) * scanArea;
        }
        if(lineWidth > 1 && lineWidth < 6){
            centerAllowance = (475 + change) * scanArea;
            edgeAllowance = (550 + change) * scanArea;
            cornerAllowance = (400 + change) * scanArea;
        }
        if(lineWidth > 5 && lineWidth < 11){
            centerAllowance = (350 + change) * scanArea;
            edgeAllowance = (425 + change) * scanArea;
            cornerAllowance = (250 + change) * scanArea;
        }
        if(lineWidth > 10 && lineWidth < 16){
            centerAllowance = (525 + change) * scanArea;
            edgeAllowance = (600 + change) * scanArea;
            cornerAllowance = (475 + change) * scanArea;
        }
        if(lineWidth > 15 && lineWidth < 21){
            centerAllowance = (550 + change) * scanArea;
            edgeAllowance = (625 + change) * scanArea;
            cornerAllowance = (500 + change) * scanArea;
        }
    }
    public int getScanLength(){
        return scanLength;
    }
    public int getCenterAllowance(){
        return centerAllowance;
    }
    public int getEdgeAllowance(){
        return edgeAllowance;
    }
    public int getCornerAllowance(){
        return cornerAllowance;
    }
}
