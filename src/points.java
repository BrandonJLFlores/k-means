public class points {
    private Double x;
    private Double y;

    points(Double x, Double y){
        this.x = x;
        this.y = y;
    }
    points(int x, int y){
        this.x = (double)x;
        this.y = (double)y;
    }
    points(){
        this.x = 0.0;
        this.y = 0.0;
    }

    Double getY(){
        return y;
    }
    Double getX(){
        return x;
    }

    void setX(Double x){
     this.x = x;
    }
    void setY(Double y){
        this.y = y;
    }
}
