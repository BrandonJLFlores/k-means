import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class ImageCompression {
    private static String FILENAME;
    private static ArrayList<Pixel> pixels;
    private static ArrayList<Pixel> centroids;
    private static ArrayList<Pixel> newCentroids;
    private static int iterations;
    private static int width,height;

    // initializing centroids and filename
    static{
        FILENAME= "resources/kmimg1.png";
        pixels = new ArrayList<>();
        centroids = newCentroids = new ArrayList<>();            // arrraylist for dynamic centroids
        iterations = 10;
    }

    public static void main(String[] args) throws IOException {
        ImageCompression m = new ImageCompression();//ggcg
        m.loadFiles(FILENAME);
        m.setCentroids();
        for(int i = 0 ; i < iterations; i++) {
            ArrayList<ArrayList<Double>> clusterAssign = m.clusterAssignment();
            ArrayList<Integer> minIndex = m.moveCentroids(clusterAssign);
            m.assignNew(minIndex);
            centroids = newCentroids;
            m.createImage();
        }
    }

    private void assignNew(ArrayList<Integer> min) {
        for(int i = 0; i < min.size(); i++){
            pixels.get(i).setR(newCentroids.get(min.get(i)).getR());
            pixels.get(i).setG(newCentroids.get(min.get(i)).getG());
            pixels.get(i).setB(newCentroids.get(min.get(i)).getB());
        }
    }

    //centroids moved internally
    private ArrayList<Integer> moveCentroids(ArrayList<ArrayList<Double>> clusterAssignment){
        int min;
        newCentroids = new ArrayList<>();
        ArrayList<Integer> minIndex = new ArrayList<>();

        for(int i = 0 ;i <centroids.size();i++ ){
            newCentroids.add(new Pixel());
        }

//        System.out.println("lol: " + newCentroids.get(0).getR());
        for(int j = 0 ; j < pixels.size() ; j++) {
            min = 0;
            for (int i = 1; i < centroids.size(); i++) { //getting minimum cluster Cost
                if (clusterAssignment.get(min).get(j) > clusterAssignment.get(i).get(j)) {
                    min = i;
                }
            }
            minIndex.add(min);

            int r = newCentroids.get(min).getR() + pixels.get(j).getR();
            int g = newCentroids.get(min).getG() + pixels.get(j).getG();
            int b = newCentroids.get(min).getB() + pixels.get(j).getB();

            newCentroids.get(min).setR(r);
            newCentroids.get(min).setG(g);
            newCentroids.get(min).setB(b);
        }

        //---------------new centroid location----------------//
        //just divided every point to ??
        ArrayList<Integer> occurences = new ArrayList<>();
        for(int i = 0; i < centroids.size(); i++){
            int occ = Collections.frequency(minIndex, i);
            occurences.add(occ);
        }
        int i = 0;
        int temp;
        for(Pixel p : newCentroids){
            temp = occurences.get(i);
            p.setR(p.getR()/temp);
            p.setG(p.getG()/temp);
            p.setB(p.getB()/temp);
            i++;
        }

        return minIndex;
    }

    private void loadFiles(String FILENAME) throws IOException {//loading files

        BufferedImage bi= ImageIO.read(new File(FILENAME));
        int[] pixel;
        width = bi.getWidth();
        height = bi.getHeight();

        for (int y = 0, z=0; y < bi.getHeight(); y++,z++) {
            for (int x = 0, i= 0; x < bi.getWidth(); x++,i++) {
                pixel = bi.getRaster().getPixel(x, y, new int[3]);
                pixels.add(new Pixel(pixel[0],pixel[1],pixel[2]));
            }
        }

    }

    private void createImage() throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0, i = 0; y < height; y++) {
            for (int x = 0; x < width; x++, i++) {

                Pixel px = pixels.get(i);
                Color c = new Color(px.getR(),px.getG(),px.getB());
                int rgb = c.getRGB();
                image.setRGB(x, y, rgb);
            }
        }

        File outputFile = new File("resources/Answers/output.png");
        ImageIO.write(image, "png", outputFile);
    }

    private void setCentroids() {
        ArrayList<Integer> list = new ArrayList<>();

        for (int i=0; i<16385; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        for(int i = 0; i < 16; i++){
            Integer index = list.get(i);
            centroids.add(pixels.get(index));
        }
    }

    private ArrayList<ArrayList<Double>> clusterAssignment(){
        //RGB ang gi cluster assign ani
        ArrayList<ArrayList<Double>> clusterAssignment = new ArrayList<>();

        //iterate for each cluster calculations
        for (Pixel centroid : centroids) {
            ArrayList<Double> temp = new ArrayList<>();
            // iterate from top to down
            for (Pixel pixel : pixels) {
                temp.add(Math.sqrt((Math.pow(pixel.getR() - centroid.getR(), 2)) +
                        (Math.pow(pixel.getG() - centroid.getG(), 2)) +
                        (Math.pow(pixel.getB() - centroid.getB(), 2))));
            }
            clusterAssignment.add(temp);
        }
        return clusterAssignment;

    }

}
