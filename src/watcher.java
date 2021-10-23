public class watcher {
    private battalion attackJedi = new battalion();
    private battalion attackSith = new battalion();
    private battalion potentialAttackJedi = new battalion();

    private battalion ambushJedi = new battalion();
    private battalion ambushSith = new battalion();
    private battalion potentialAmbushSith = new battalion();

    private State attackState = State.NONE;
    private State ambushState = State.NONE;

    public void addDeployment(battalion b) {
        processAttack(b);
        processAmbush(b);
    }

    private void processAmbush(battalion b) {
        if(!b.getSith()) {
            switch(ambushState) {
                // this is the code to handle a jedi during ambush processing
                case SITH:
                    // if we see a jedi and its force is worse than the sith, we have a battle to track
                    if(ambushSith.compareTo(b) >= 0) {   // we have a stronger sith than new jedi
                        ambushJedi = b;
                        ambushState = State.BOTH;
                    }
                    break;

                case BOTH:  // if the new jedi is weaker, it is ambushed instead
                    if(ambushJedi.compareTo(b) > 0) {   // will only return 1 or -1 because both jedi
                        ambushJedi = b;
                    }
                    break;

                case POTENTIAL: // there is already a potential ambushSith waiting to ambush
                    // if the new potential ambush is more dramatic than the current ambush, update values
                    if((potentialAmbushSith.getForceSens() - b.getForceSens())
                            > (ambushSith.getForceSens() - ambushJedi.getForceSens())) {
                        ambushJedi = b;
                        ambushSith = potentialAmbushSith;
                        ambushState = State.BOTH;
                    }
                    break;

                default:    // cases we don't care about: ambush state = none
                    break;
            }
        } else {
            switch(ambushState) {   // check to see what state we're in
                // this is the code to handle a sith during ambush processing
                case NONE:
                    ambushSith = b;             // update ambush sith
                    ambushState = State.SITH;   // update ambush status to sith
                    break;

                case SITH:  // if there's already a sith down
                    if (ambushSith.compareTo(b) < 1) {  // only return 1 or -1 because both sith
                        // stronger than ambushSith; update ambushSith
                        ambushSith = b;
                    }
                    break;

                case BOTH:
                    if(ambushSith.compareTo(b) < 1) { // stronger than ambushSith but hasn't ambushed anything yet
                        potentialAmbushSith = b;
                        ambushState = State.POTENTIAL;
                    }
                    break;

                case POTENTIAL:
                    if(potentialAmbushSith.compareTo(b) < 1) {  // new strongest potential sith
                        potentialAmbushSith = b;
                    }
                    break;

            }
        }
    }

    private void processAttack(battalion b) {
        if(b.getSith()) {
            // this is the code to handle a sith during attack processing
            switch(attackState) {
                case JEDI:
                    // if we see a sith and its force is better than the jedi, we have a battle to track
                    if(attackJedi.compareTo(b) < 1) {  // we have a stronger sith
                        attackSith = b;
                        attackState = State.BOTH;
                    }
                    break;

                case BOTH:  // if the new sith is stronger, it fights the jedi
                    if(attackSith.compareTo(b) < 1) {
                        attackSith = b;
                    }
                    break;

                case POTENTIAL: // there is already a potential attackJedi waiting to be attacked
                    // if the new potential attack is more dramatic than the current attack, update values
                    if((b.getForceSens() - potentialAttackJedi.getForceSens()) >
                            (attackSith.getForceSens() - attackJedi.getForceSens())) {
                        attackSith = b;
                        attackJedi = potentialAttackJedi;
                        attackState = State.BOTH;
                    }
                    break;

                default:
                    // cases we don't care about: attackState = NONE
                    break;
            }

        } else {                    // this is code for jedi
            switch(attackState) {   // check to see which state we're in
                case NONE:
                    attackJedi = b;             // update attack jedi
                    attackState = State.JEDI;   // update our state to be JEDI
                    break;

                case JEDI:
                    if(attackJedi.compareTo(b) > 0) {
                        attackJedi = b;     // if the jedi is weaker, update the attackJedi
                    }
                    break;

                case BOTH:
                    if(attackJedi.compareTo(b) > 0) {
                        potentialAttackJedi = b;        // if the new jedi is weaker, update potential
                        attackState = State.POTENTIAL;  // change state to reflect potential update
                    }
                    break;

                case POTENTIAL:
                    if(potentialAttackJedi.compareTo(b) > 0) {
                        potentialAttackJedi = b;    // if new jedi is weaker, update potential
                    }
                    break;

                default:
                    // any case we don't consider does nothing
                    break;
            }
        }
    }

    private enum State {
        NONE,
        JEDI,
        BOTH,
        POTENTIAL,
        SITH,
    }

    public long getAmbushJedi() {
        if(ambushSith.getTimestamp() == -1) return -1;
        else return this.ambushJedi.getTimestamp();
    }
    public long getAttackJedi() {
        if(attackSith.getTimestamp() == -1) return -1;
        else return this.attackJedi.getTimestamp();
    }
    public long getAttackSith() {
        if(attackJedi.getTimestamp() == -1) return -1;
        else return this.attackSith.getTimestamp();
    }
    public long getAmbushSith() {
        if(ambushJedi.getTimestamp() == -1) return -1;
        else return this.ambushSith.getTimestamp();
    }
}
