import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Main extends JFrame{
    private static String FILENAME;
    private static ArrayList<points> centroids;
    private static ArrayList<points> newCentroids;
    private static ArrayList<points> data;
    private static int iterations;
    private static Double prevJ;
    private static XYSeries series1;
    private static XYSeries series2;
    private static XYSeries series3;
    private static XYSeriesCollection dataset;


    private Main(){
        super("Scatter Plot");

        XYDataset DATA = dataset;
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Scatter Plot",
                "X-Axis", "Y-Axis", DATA);


        //Changes background color
        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint(Color.BLACK);


        // Create Panel
        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }
    // initializing centroids and filename
    static{
        dataset = new XYSeriesCollection();
        series1 = new XYSeries("Centroid 1");
        series2 = new XYSeries("Centroid 2");
        series3 = new XYSeries("Centroid 3");
        FILENAME= "resources/kmdata1.txt";
        centroids= newCentroids = new ArrayList<points>();            // arrraylist for dynamic centroids
        data = new ArrayList<points>();
//        centroids.add(new points(3,2));         // setting starting centroids
//        centroids.add(new points(6,4));

        centroids.add(new points(3,3));
        centroids.add(new points(6,2));
        centroids.add(new points(8,5));

        iterations =10;
        prevJ = 0d;
    }

    public static void main(String[] args) throws IOException {
        Main m = new Main();//ggcg
        m.setSize(640, 480);
        m.setLocationRelativeTo(null);
        m.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        m.setVisible(true);
        m.loadFiles(FILENAME);
        System.out.println("iterations " + iterations);
        for(int i = 0 ; i < iterations;i++) {
            System.out.println("1~");
            ArrayList<ArrayList<Double>> clusterAssign = m.clusterAssignment();   //assigns cluster
            System.out.println("2~");
            ArrayList<Integer> minIndex = m.moveCentroids(clusterAssign);         //moves centroids
            System.out.println("3~");
            Double J = m.computeJ(minIndex, clusterAssign);                       //computes Cost
            System.out.println("4~");
            m.fileWrite(minIndex, clusterAssign, J,i + 1);
            System.out.println("5~");
            centroids = newCentroids;
        }
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);
    }
    private void fileWrite(ArrayList<Integer> minIndex, ArrayList<ArrayList<Double>> clusterAssignment, Double J, int filenumber) throws IOException {

        String ca = "_ca.txt";
        String cm = "_cm.txt";
        /**
         * use ning naa sa ubos para di siya mo write sulod sa ca and cm na folder
         */
//        String iter = "iter";
//        FileWriter writer1 = new FileWriter(iter + filenumber + ca);
//        FileWriter writer2 = new FileWriter(iter + filenumber + cm);

        //---------------------START------------------//

        String iter1 = "resources/Answers/ca/iter";
        String iter2 = "resources/Answers/cm/iter";


        FileWriter writer1 = new FileWriter(iter1 + filenumber + ca);
        FileWriter writer2 = new FileWriter(iter2 + filenumber + cm);

        for(Integer i : minIndex){
            writer1.write(String.valueOf(i + 1) + "\n");
        }

        int i = 0;

        for(points p : newCentroids){
            writer2.write(p.getX() + " " + p.getY() + "\n");
            if(i == 0){
                series1.add(p.getX(),p.getY());
            }else if(i == 1){
                series2.add(p.getX(),p.getY());
            }else {
                series3.add(p.getX(),p.getY());
            }
            i++;
        }

        writer2.write("J = " + J + "\n");
        Double dJ = J - prevJ;
        writer2.write("dJ = " + dJ + "\n");
        prevJ = J;

        writer1.close();
        writer2.close();
    }

    private Double computeJ(ArrayList<Integer> minIndex, ArrayList<ArrayList<Double>> clusterAssignment){
        Double j = 0.0;
        for(int i = 0  ;i < minIndex.size(); i++){
            j += clusterAssignment.get(minIndex.get(i)).get(i);
        }
        j /= minIndex.size();
        System.out.println("J: " + j);
        return j;
    }

    //centroids moved internally
    private ArrayList<Integer> moveCentroids(ArrayList<ArrayList<Double>> clusterAssignment){
        int min;
        newCentroids = new ArrayList<>();
        ArrayList<Integer> minIndex = new ArrayList<Integer>();

        for(int i = 0 ;i <centroids.size();i++ ){
            newCentroids.add(new points());
        }

        for(int j = 0 ; j < data.size() ; j++) {
            min = 0;
            for (int i = 1; i < centroids.size(); i++) { //getting minimum cluster Cost
                if (clusterAssignment.get(min).get(j) > clusterAssignment.get(i).get(j)) {
                    min = i;
                }
            }
            minIndex.add(min);
            newCentroids.get(min).setX(newCentroids.get(min).getX() + data.get(j).getX());
            newCentroids.get(min).setY(newCentroids.get(min).getY() + data.get(j).getY());
        }

        //---------------new centroid location----------------//
        //just divided every point to 4
        int zeroOccurrences = Collections.frequency(minIndex, 0);
        int oneOccurrences = Collections.frequency(minIndex, 1);
        int twoOccurences = Collections.frequency(minIndex, 2);
        int[] occurences = {zeroOccurrences,oneOccurrences,twoOccurences};
        int i = 0;
        for(points p : newCentroids){
            p.setX(p.getX()/occurences[i]);
            p.setY(p.getY()/occurences[i]);
            i++;
        }

        //--------------------MIKE ORIGINAL VERSION-----------------//
//        for(int i = 0 ;i <centroids.size();i++ ){
//            newCentroids.get(i).setX(newCentroids.get(i).getX()*(centroids.size() / clusterAssignment.size()));
//            newCentroids.get(i).setY(newCentroids.get(i).getY()*(centroids.size() / clusterAssignment.size()));
//        }
        return minIndex;
    }

    private ArrayList<ArrayList<Double>> clusterAssignment(){
        ArrayList<ArrayList<Double>> clusterAssignment = new ArrayList<ArrayList<Double>>();

        //iterate for each cluster calculations
        for(int j = 0 ; j < centroids.size(); j++){
            ArrayList <Double> temp = new ArrayList<Double>();
            // iterate from top to down
            for(int i = 0; i < data.size();i++){
                temp.add(Math.sqrt((Math.pow(data.get(i).getX() - centroids.get(j).getX(),2)) +
                        (Math.pow(data.get(i).getY() - centroids.get(j).getY(),2))));
            }
            clusterAssignment.add(temp);
        }
        return clusterAssignment;
       // System.out.println(clusterAssignment.size());
    }

    private void loadFiles(String FILENAME){//loading files

        String currentString = "";
        String[] xY;
        FileReader fr;
        BufferedReader br;

        try{
            fr = new FileReader(FILENAME);
            br = new BufferedReader( fr);
            while((currentString = br.readLine())!= null) {
                xY = currentString.split(" ");
                data.add(new points(Double.parseDouble(xY[1]),Double.parseDouble(xY[2])));//TODO Don't hard code index 1 2
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}