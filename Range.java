// class for checking values in ranges

public class Range {
    private final Double low;
    private final Double high;

    public Range(Double low, Double high){
        this.low = low;
        this.high = high;
    }

    public boolean contains(Double number){
        return (number >= low && number <= high);
    }
}