public class general {
    // instance variables
    private long sithDeployed;  // number of sith troops deployed
    private long jediDeployed;  // number of jedi troops deployed
    private long troopsAlive;   // number of total troops still alive
    private long genNum;        // number of general

    // constructor

    /**
     * creates a general object that tracks how many troops the specified number general has deployed for each side,
     * and how many are still alive
     * @param genNum the number of the general represented by the general object
     */
    public general(long genNum) {
        this.sithDeployed = 0;
        this.jediDeployed = 0;
        this.troopsAlive = 0;
        this.genNum = genNum;
    }

    // instance methods
    public long getNum() {
        return this.genNum;
    }
    public long getJediDeployed() {
        return this.jediDeployed;
    }
    public long getSithDeployed() {
        return this.sithDeployed;
    }
    public long getTroopsAlive() {
        return this.troopsAlive;
    }

    //adds the designated number of troops to the sith or jedi count, as well as to the current number alive
    public void addTroops(boolean areSith, long troops) {
        // add the new troops to their respective count and to the number of troops alive
        if(areSith) sithDeployed += troops;
        else jediDeployed += troops;
        troopsAlive += troops;
    }
    // removes the number of troops from the count of those alive
    public void removeTroops(long troops) {
        troopsAlive -= troops;
    }
}
