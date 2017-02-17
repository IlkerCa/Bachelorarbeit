package model;
 
public class Data {
 
    private double x = 0;
    private int numClusters = 0;
 
    public Data(double x)
    {
        this.setX(x);
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getX()  {
        return this.x;
    }
    
    public void setCluster(int n) {
        this.numClusters = n;
    }
    
    public int getCluster() {
        return this.numClusters;
    }
    
    public static double distance(Data p, Data centroid) {
        return Math.sqrt(Math.pow((centroid.getX() - p.getX()), 2));
    }
   
}