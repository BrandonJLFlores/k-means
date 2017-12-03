import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Main {
    private static String FILENAME;
    private static ArrayList<points> centroids;
    private static ArrayList<points> newCentroids;
    private static ArrayList<points> data;
    private static int iterations;

    // initializing centroids and filename
    static{
        FILENAME= "resources/test.txt";
        centroids= newCentroids = new ArrayList<points>();            // arrraylist for dynamic centroids
        data = new ArrayList<points>();
        centroids.add(new points(3,2));         // setting starting centroids
        centroids.add(new points(6,4));
        //centroids.add(new points(8,5));
        iterations =1;
    }

    public static void main(String[] args){
        Main m = new Main();//ggcg
        m.loadFiles(FILENAME);
        System.out.println(iterations);
        for(int i = 0 ; i < iterations;i++) {
            System.out.println("1~");
            ArrayList<ArrayList<Double>> clusterAssign = m.clusterAssignment();   //assigns cluster
            System.out.println("2~");
            ArrayList<Integer> minIndex = m.moveCentroids(clusterAssign);         //moves centroids
            System.out.println("3~");
            Double J = m.computeJ(minIndex, clusterAssign);                       //computes Cost
            System.out.println("4~");
            m.fileWrite(clusterAssign,i);
            System.out.println("5~");
            centroids = newCentroids;
        }
    }
    private void fileWrite(ArrayList<ArrayList<Double>> clusterAssignment, int filenumber){
        System.out.println(clusterAssignment.size());
        System.out.println(clusterAssignment.get(0).size());
        System.out.println(centroids.size());
        /*        try {
            PrintWriter writer = new PrintWriter("iter"+filenumber+"_ca.txt", "UTF-8");
            for(int i = 0 ; i < clusterAssignment.size(); i++) {
                for (int j = 0 ; j < centroids.; j++) {
                    writer.println(clusterAssignment().get(i).get(j));
                }
            }
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

    private Double computeJ(ArrayList<Integer> minIndex, ArrayList<ArrayList<Double>> clusterAssignment){
        Double j = 0.0;
        for(int i = 0  ;i < minIndex.size(); i++){
            j += clusterAssignment.get(minIndex.get(i)).get(i);
        }
        j /= minIndex.size();
        System.out.println(j);
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
        for(int i = 0 ;i <centroids.size();i++ ){
            newCentroids.get(i).setX(newCentroids.get(i).getX()*(centroids.size() / clusterAssignment.size()));
            newCentroids.get(i).setY(newCentroids.get(i).getY()*(centroids.size() / clusterAssignment.size()));
        }
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