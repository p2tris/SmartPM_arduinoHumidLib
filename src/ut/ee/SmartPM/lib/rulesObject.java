package ut.ee.SmartPM.lib;

public class rulesObject<Low, High, Name> {
    private Low low;
    private High high;
    private Name n;
    
    public rulesObject(Low low, High high, Name n){
        this.low = low;
        this.high = high;
        this.n = n;
    }
    
    public Low getLow(){ return low; }
    public High getHigh(){ return high; }
    public Name getName(){ return n; }
    public void setLow(Low low){ this.low = low; }
    public void setHigh(High high){ this.high = high; }
    public void setName(Name n){ this.n = n; }
}