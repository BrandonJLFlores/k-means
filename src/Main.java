import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Main{
    private static String FILENAME;
    private static ArrayList<points> centroids;
    private static ArrayList<points> newCentroids;
    private static ArrayList<points> data;
    private static int iterations;
    private static Double prevJ;

    // initializing centroids and filename
    static{
        FILENAME= "resources/kmdata1.txt"; //todo <----- change file name if you want to try other test files
        centroids= newCentroids = new ArrayList<>();            // arrraylist for dynamic centroids
        data = new ArrayList<>();

        centroids.add(new points(3,3));
        centroids.add(new points(6,2));
        centroids.add(new points(8,5));

        iterations =10;
        prevJ = 0d;
    }

    public static void main(String[] args) throws IOException {
        Main m = new Main();//ggcg
        m.loadFiles(FILENAME);
        for(int i = 0 ; i < iterations;i++) {
            ArrayList<ArrayList<Double>> clusterAssign = m.clusterAssignment();   //assigns cluster
            ArrayList<Integer> minIndex = m.moveCentroids(clusterAssign);         //moves centroids
            Double J = m.computeJ(minIndex, clusterAssign);                       //computes Cost
            m.fileWrite(minIndex, J,i + 1);
            centroids = newCentroids;
        }

    }
    private void fileWrite(ArrayList<Integer> minIndex, Double J, int filenumber) throws IOException {

        String ca = "_ca.txt";
        String cm = "_cm.txt";
        //---------------------START------------------//

        String iter1 = "resources/Answers/ca/iter";
        String iter2 = "resources/Answers/cm/iter";


        FileWriter writer1 = new FileWriter(iter1 + filenumber + ca);
        FileWriter writer2 = new FileWriter(iter2 + filenumber + cm);

        for(Integer i : minIndex){
            writer1.write(String.valueOf(i + 1) + "\n");
        }

        for(points p : newCentroids){
            writer2.write(p.getX() + " " + p.getY() + "\n");

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
        ArrayList<Integer> occurences = new ArrayList<>();
        for(int i = 0; i < centroids.size(); i++){
            int occ = Collections.frequency(minIndex, i);
            occurences.add(occ);
        }
        int i = 0;
        int temp;
        for(points p : newCentroids){
            temp = occurences.get(i);
            p.setX(p.getX()/temp);
            p.setY(p.getY()/temp);
            i++;
        }
        return minIndex;
    }




    private ArrayList<ArrayList<Double>> clusterAssignment(){
        ArrayList<ArrayList<Double>> clusterAssignment = new ArrayList<ArrayList<Double>>();

        //iterate for each cluster calculations
        for (points centroid : centroids) {
            ArrayList<Double> temp = new ArrayList<Double>();
            // iterate from top to down
            for (points aData : data) {
                temp.add(Math.sqrt((Math.pow(aData.getX() - centroid.getX(), 2)) +
                        (Math.pow(aData.getY() - centroid.getY(), 2))));
            }
            clusterAssignment.add(temp);
        }
        return clusterAssignment;
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