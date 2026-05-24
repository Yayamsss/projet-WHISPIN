// Creating a shortcut for tuple<double, int, int> type
public class Details{
    private double value;
    private int i;
    private int j;

    public Details(double value, int i, int j){
        this.value = value;
        this.i = i;
        this.j = j;
    }

    public double getValue(){
        return this.value;
    }

    public int getI(){
        return this.i;
    }

    public int getJ(){
        return this.j;
    }
}

// public record Details(double value, int i, int j){}