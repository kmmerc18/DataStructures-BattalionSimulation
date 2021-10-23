import java.util.Collections;
import java.util.PriorityQueue;

public class planet {
    // instance variables
    private int planetNumber;
    private PriorityQueue<battalion> jedis;
    private PriorityQueue<battalion> siths;
    private medianCalc med;
    private boolean movieWatcher;
    private watcher watcher;

    /**
     * creates a planet object that knows what sith/jedi battalions are on it, the median number of casualties per
     * battle, the most force-sensitive sith battalion to arrive, the least force-sensitive jedi battalion ot arrive,
     * the most recent of the most force-sensitive sith, and the most recent of the most force-sensitive jedi
     * @param planetNumber the number of the planet the planet object represents
     */
    public planet(int planetNumber) {
        // gives access to the least force-sensitive jedi on a planet
        this.jedis = new PriorityQueue<>();
        // gives access to the most force-sensitive siths on a planet
        this.siths = new PriorityQueue<>(Collections.reverseOrder());
        this.planetNumber = planetNumber;
        this.med = new medianCalc();
        this.movieWatcher = false;
        this.watcher = new watcher();
    }

    // instance methods
    public void addBattalion(battalion b) {
        if (b.getSith()) siths.add(b);
        else this.jedis.add(b);
        if (movieWatcher) {
            this.watcher.addDeployment(b);
        }
    }

    public void removeBattalion(battalion b) {
        if(b.getSith()) this.siths.remove(b);
        else this.jedis.remove(b);
    }
    public void addLoss(long loss) {
        this.med.addLoss(loss);
    }
    public battalion getJedi() {
        return jedis.peek();
    }
    public battalion getSith() {
        return siths.peek();
    }
    public int getPlanetNumber() {
        return this.planetNumber;
    }
    public long getMedian() {
        return this.med.calculateMed();
    }
    public watcher getWatcher() {
        return this.watcher;
    }
    public void setMW(boolean b) {
        this.movieWatcher = b;
    }
}
