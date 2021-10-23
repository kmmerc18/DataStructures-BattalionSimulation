public class battalion implements Comparable<battalion> {
    // instance variables
    private long timestamp;     // when battalion is deployed
    private general general;    // general deploying battalion
    private boolean sith;       // true if sith
    private long forceSens;     // force sensitivity of battalion
    private long troops;        // number of troops in battalion
    private planet planet;      // planet object battalion is deployed ot
    private long order;         // number in order of battalion deployments

    /**
     * Given a string (a line from a text file accessed in Settings), battalion object created; also contains
     * the information to compare two battalion objects based on their force sensitivity and jedi/sith association
     * @param line a string from a text file passed in from Settings containing battalion information
     * @param settings a Settings object containing an ArrayList of general objects, int current timestamp,
     *                 ArrayList of planet objects
     */
    public battalion(String line, Settings settings) {
        String[] data = line.split("\\s");  // data in line is separated by spaces
        this.order = settings.getOrder();          // keep track of which battalions were deployed first

        // parse the timestamp and check validity
        this.timestamp = Integer.parseInt(data[0]);
        // currentTime begins at -1; this ensures time is valid and non-reversing
        if (timestamp < settings.getCurrentTime()) {
            System.err.println("Timestamp invalid");
            System.exit(1);
        }

        // parse sith or jedi
        this.sith = (data[1].compareTo("SITH") == 0);

        // parse the general ID and check validity
        int generalID = Integer.parseInt(data[2].substring(1));
        if (generalID < 0 || generalID > settings.getGenerals() - 1) {
            System.err.println("General " + generalID + " invalid");
            System.exit(1);
        }
        this.general = settings.getGeneral(generalID);

        // parse the planet ID and check validity
        int world = Integer.parseInt(data[3].substring(1));
        if (world < 0 || world > settings.getPlanets() - 1) {
            System.err.println("Planet " + world + " invalid");
            System.exit(1);
        }
        // add the planet to the battalion data
        planet plan = settings.getInUse(world);
        this.planet = plan;

        // parse the force sensitivity and check validity
        int force = Integer.parseInt(data[4].substring(1));
        if (force < 1) {
            System.err.println("Force sensitivity " + force + " invalid");
            System.exit(1);
        }
        this.forceSens = force;

        // parse the number of troops and check validity
        int troops = Integer.parseInt(data[5].substring(1));
        if (troops < 1) {
            System.err.println("Number of troops " + troops + " invalid");
            System.exit(1);
        }
        this.troops = troops;
        this.general.addTroops(sith, troops);

        // add the battalion to it's planet object's list of battalion objects
        plan.addBattalion(this);
    }

    // default constructor for all -1 values (for use initializing watcher())
    public battalion() {
        this.timestamp = -1;
        this.general = null;
        this.sith = true;
        this.forceSens = -1;
        this.troops = -1;
        this.planet = null;
        this.order = -1;
    }

    // instance methods
    // accessors/setters
    public general getGeneral() {
        return this.general;
    }
    public boolean getSith() {
       return this.sith;
    }
    public long getForceSens() {
        return this.forceSens;
    }
    public long getTroops() {
        return this.troops;
    }
    public planet getPlanet() {
        return this.planet;
    }
    public long getOrder() {
        return this.order;
    }
    public long getTimestamp() {
        return this.timestamp;
    }
    public void setTroops(long t) {
        this.troops = t;
    }

    @Override
    public int compareTo(battalion b) {
        if(this.getForceSens() < b.getForceSens()) return -1;       // battalion weaker than given battalion
        else if(this.getForceSens() > b.getForceSens()) return 1;   // battalion stronger than given battalion
        else {                                                      // both battalions equal force sensitivity
            if(this.getSith() && b.getSith()) {         // if both are sith, the stronger is the later deployment
                if(this.getOrder() < b.getOrder()) return 1;
                else if(this.getOrder() > b.getOrder()) return -1;
                else return 0;  // vacuous; battalions cannot have the same place in deployment order
            } else if(!this.getSith() && !b.getSith()) {   // if both are jedi, the stronger is the earlier deployment
                if(this.getOrder() < b.getOrder()) return -1;
                else if(this.getOrder() > b.getOrder()) return 1;
                else return 0;  // vacuous; battalions cannot have the same place in deployment order
            }
            else return 0;  // if not both sith or both jedi, then they can have equal force sensitivity
        }
    }
}
