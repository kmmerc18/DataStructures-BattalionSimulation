import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * Settings object handles reading input from command line arguments passed into the main from Main.java.
 * It also processes input from the given text file using a Scanner, creating battalion objects and
 * determining if their deployment results in a battle; handles all battle logic, counts number of battles.
 * Outputs handled directly: verbose, median
 * Outputs created that are accessed in main: general evaluation, movie watcher
 */
public class Settings {
    private boolean verbose;    // true if verbose output specified
    private boolean median;     // true if median output specified
    private boolean genEval;    // true if general evaluation output specified
    private boolean watcher;    // true if watcher output specified
    private long currentTime;   // keeps track of highest timestamp to ensure time is moving forward
    private long order;         // counter for the order that battalions are deployed in
    private int generals;       // maximum number of generals expected
    private int planets;        // maximum number of planets expected
    private boolean format;     // pseudorandom (true) or deployment list (false) format of input text file
    private int battles;        // counter for the number of battles to take place in a day
    private ArrayList<planet> planetList;   // list of all planet objects that can be involved in deployments
    private ArrayList<general> generalList; // list of all general objects that can be involved in deployments

    // specify all of the long options for this program
    // these are flags the user picks from to set the above instance variables
    LongOpt[] longOptions = {
            new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v'),
            new LongOpt("median", LongOpt.NO_ARGUMENT, null, 'm'),
            new LongOpt("general-eval", LongOpt.NO_ARGUMENT, null, 'g'),
            new LongOpt("watcher", LongOpt.NO_ARGUMENT, null, 'w'),
    };
    /**
     * Settings takes the command line arguments when Main.java is run, as well
     * as the String contents of each line in the txt file passed to main in the format
     * of an ArrayList
     * @param args String[] of the command line arguments when Main.java is run
     **/
    // constructor
    public Settings(String[] args) {
        // initialize all instance variables
        this.verbose = false;
        this.median = false;
        this.genEval = false;
        this.watcher = false;
        this.battles = 0;
        this.order = 0;
        this.currentTime = 0;

        // create the object that processes command line arguments
        Getopt g = new Getopt("project2", args, "gmvw", this.longOptions);
        g.setOpterr(true);      // prints err messages for us when we make a mistake

        // process one command line argument at a time until there are none left
        // g.getopt() returns an int representing the short flag or -1 if we are done
        // used by getOpt to process command line arguments
        int choice;
        while ((choice = g.getopt()) != -1) {
            // we need to process the command line argument stored in choice
            // print an explanation of the program for the user (see printHelp() method)
            // if the user attempts to enter a flag not handled by a case above, alert them of an error and exit
            switch (choice) {
                case 'v' -> verbose = true;
                case 'm' -> median = true;
                case 'g' -> genEval = true;
                case 'w' -> watcher = true;
                case 'h' -> {
                    printHelp();
                    System.exit(0);
                }
                default -> {
                    System.err.println("Error: invalid option");
                    System.exit(1);
                }
            }
        }

        // scanner for the System.in, which has been redirected to a txt file
        Scanner in = new Scanner(System.in).useLocale(Locale.US);
        // process the first four lines of the text file
        in.nextLine();                          // skip comments on the first line
        if (in.nextLine().contains("PR")) {     // find the format of the battalion deployments in the file
            format = true;
        }
        this.generals = Integer.parseInt(in.nextLine().substring(14));  // parse number of general objects expected

        // create an ArrayList populated with the given number of general objects
        this.generalList = new ArrayList<>(generals);
        for(int i = 0; i < generals; i++) {
            this.generalList.add(new general(i));
        }

        this.planets  = Integer.parseInt(in.nextLine().substring(13));  // parse number of planet objects expected

        // create and ArrayList populated with the given number of planet objects
        this.planetList = new ArrayList<>(planets);
        for(int i = 0; i < planets; i++) {
            planet toAdd = new planet(i);
            planetList.add(toAdd);

            // if movie watcher output specified, make each planet keep track of movie watcher information
            toAdd.setMW(watcher);
        }

        if (format) {   // PR input format (otherwise default DL format)
            // parse the next few lines for the data needed to create multiple "random" battalions
            String[] data = in.nextLine().split("\\s");
            // seed for pseudorandom battalion generation
            int seed = Integer.parseInt(data[1]);
            // number of deployments for pseudorandom battalion generation
            int deployments = Integer.parseInt(in.nextLine().split("\\s")[1]);
            // arrival rate variable for the timestamps of pseudorandom battalion generation
            int arrivalRate = Integer.parseInt(in.nextLine().split("\\s")[1]);

            // create the multiple battalions in DL format
            P2Random rand = new P2Random();
            // redirect the System.in to the newly generated battalion information
            // this will now be in the same format as DL deployments
            in = rand.PRInit(seed, generals, planets, deployments, arrivalRate);
        }

        while (in.hasNextLine()) {
            // each remaining line contains a battalion deployment
            String line = in.nextLine();

            // a blank line indicates the end of the file; no more deployments
            if(line.isBlank()) {
                break;
            }

            // given a line in the text file, a new battalion object can be made
            // the battalion constructor processes the data in the line
            battalion bat = new battalion(line, this);

            // in the median output mode, information is printed at every change in timestamp
            if (median && bat.getTimestamp() != currentTime) {
                for(int i = 0; i < planets; i++) {
                    if(planetList.get(i).getMedian() != 0) {
                        System.out.println(("Median troops lost on planet " + i + " at time " +
                                currentTime + " is " + planetList.get(i).getMedian() + "."));
                    }
                }
            }
            currentTime = bat.getTimestamp();   // update the current time

            // check to see if battles occur and count them
            battles += battle(bat, bat.getPlanet());
        }
        if(median) {
            for (int i = 0; i < planets; i++) {
                if (planetList.get(i).getMedian() != 0) {
                    System.out.println(("Median troops lost on planet " + i + " at time " +
                            currentTime + " is " + planetList.get(i).getMedian()) + ".");
                }
            }
        }
    }

    // instance methods
    private void printHelp() {
        System.out.println("Usage: java [options] main -v &| -m &| -g &| -w |-h" +
                "\nThis program simulates galactic warfare. " +
                "Generals will deploy troop battalions to various planets, which will fight battles.");
    }

    public boolean getVerbose() {
        return this.verbose;
    }
    public boolean getMedian() {
        return this.median;
    }
    public boolean getGenEval() {
        return this.genEval;
    }
    public boolean getWatcher() {
        return this.watcher;
    }
    public long getCurrentTime() {
        return this.currentTime;
    }
    public int getGenerals() {
        return this.generals;
    }
    public int getPlanets() {
        return this.planets;
    }
    public int getBattles() {
        return this.battles;
    }
    public long getOrder() {
        return this.order++;
    }
    public general getGeneral(int genNum) {
        return generalList.get(genNum);
    }
    public planet getInUse(int world) {
        return planetList.get(world);
    }

    /**
     * determines if the force sensitivity and jedi/sith qualities of a battalion are such that a battle occurs
     * @param b battalion being added to a planet
     * @param p planet a battalion is being added to
     * @return true if a battle occurs, false if not
     */
    public boolean battleCheck(battalion b, planet p) {
        int battle;
        // if the new battalion is sith and there is a jedi battalion on the planet
        if (b.getSith() && p.getJedi() != null) {
            battle = b.compareTo(p.getJedi());
        }
        // if the new battalion is jedi and there is a sith battalion on the planet
        else if (!b.getSith() && p.getSith() != null) {
            battle = p.getSith().compareTo(b);
        } else return false;

        // now see if the sith are force-sensitive enough to battle the jedi
        // compareTo method above returns 0 or 1 if the sith is powerful enough, -1 if not
        return battle >= 0;
    }

    /**
     * simulates a battle (or recursively a series of battles) between jedi and sith battalions on planet p, based on new battalion b
     * @param b battalion being added to a planet
     * @param p planet where battles may occur
     * @return returns the number of battles that the introduction of battalion b caused
     */
    public int battle(battalion b, planet p) {
        int battles2 = 0;   // a counter for the number of battles occurring in this round of recursion
        if (battleCheck(b, p)) {    // if a battle occurs between battalion b and another battalion on planet p
            battles2++;             // increment battle counter
            battalion sith;
            battalion jedi;

            // determine which battalion is sith and which is jedi
            if (b.getSith()) {
                sith = b;
                jedi = p.getJedi();
            } else {
                sith = p.getSith();
                jedi = b;
            }

            // face troops off 1-1
            long lostTroops = Math.min(jedi.getTroops(), sith.getTroops());
            sith.setTroops(sith.getTroops() - lostTroops);
            sith.getGeneral().removeTroops(lostTroops);
            jedi.setTroops(jedi.getTroops() - lostTroops);
            jedi.getGeneral().removeTroops(lostTroops);
            lostTroops *= 2;        // troops lost on both sides
            p.addLoss(lostTroops);

            // print output if -v
            if (this.getVerbose()) {
                System.out.printf("General %d's battalion attacked General %d's battalion on planet " +
                                "%d. %d troops were lost.\n", sith.getGeneral().getNum(),
                        jedi.getGeneral().getNum(), p.getPlanetNumber(), lostTroops);
            }

            // remove any battalion if they are destroyed
            if (sith.getTroops() == 0) {
                p.removeBattalion(sith);
            }
            if (jedi.getTroops() == 0) {
                p.removeBattalion(jedi);
            }

            // If newest battalion survives, do another battle (recursive call)
            if (b.getTroops() > 0) {
                battles2 += battle(b, p);
            }
        }
        // once all battles and resulting battles have been fought, return the total number of battles
        return battles2;
    }

    // prints the general evaluation output information if requested in main
    public void genEval() {
        System.out.println("---General Evaluation---");
        for(general g : generalList) {
            System.out.printf("General %d deployed %d Jedi troops and %d Sith troops, and %d/%d " +
                            "troops survived.\n", + g.getNum(), g.getJediDeployed(), g.getSithDeployed(), g.getTroopsAlive(),
                    (g.getSithDeployed()+g.getJediDeployed()));
        }
    }

    // prints the movie watcher output information if requested in main
    public void watchOut() {
        System.out.println("---Movie Watcher---");
        for(int n = 0; n < planets; n++) {
            System.out.println(String.format("A movie watcher would enjoy an ambush on planet " +
                            "%d with Sith at time %d and Jedi at time %d.", n,
                    this.getInUse(n).getWatcher().getAmbushSith(),
                    this.getInUse(n).getWatcher().getAmbushJedi()));
            System.out.println(String.format("A movie watcher would enjoy an attack on planet " +
                            "%d with Jedi at time %d and Sith at time %d.", n,
                    this.getInUse(n).getWatcher().getAttackJedi(),
                    this.getInUse(n).getWatcher().getAttackSith()));
        }
    }
}