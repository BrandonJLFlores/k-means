public class Pixel{
    private int R,G,B;


    Pixel(){
        this.R = 0;
        this.G = 0;
        this.B = 0;
    }

    Pixel(int R, int G, int B) {
        this.R = R;
        this.G = G;
        this.B = B;

    }

    public int getR() {
        return R;
    }

    public void setR(int r) {
        R = r;
    }

    public int getG() {
        return G;
    }

    public void setG(int g) {
        G = g;
    }

    public int getB() {
        return B;
    }

    public void setB(int b) {
        B = b;
    }
}
