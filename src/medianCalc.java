import java.util.Collections;
import java.util.PriorityQueue;

public class medianCalc {
    /**
     * Class used to calculate the median of a given list of longs by sorting the data into
     * a min priority queue and a max priority queue.
     */
    private long med;
    PriorityQueue<Long> left;   // lower values
    PriorityQueue<Long> right;  // higher values

    public medianCalc() {
        this.med = 0;
        this.left = new PriorityQueue<>(Collections.reverseOrder());    // the Max PriorityQueue
        this.right = new PriorityQueue<>();                             // the Min PriorityQueue
    }

    /**
     * adds a new value to the priority queues, then reassigns values to ensure neither priority queue contains
     * more than one long more than the other
     * @param loss a long to add to the priority queues
     */
    public void addLoss(long loss) {
        // sort the data into the left (lower number) queue and the right (higher number) queue
        if (left.peek() != null && left.peek() < loss) {
            // if the data is too large for left, add it to right
            right.add(loss);
        } else {
            // if the data is small enough for left, add it to left
            left.add(loss);
        }
        if (Math.abs(left.size() - right.size()) > 1) {
            // the size difference between the two queues is too great; balance them out
            if (left.size() < right.size()) {
                left.add(right.remove());
            } else {
                right.add(left.remove());
            }
        }
    }

    /**
     * Calculates the median of the longs across both priority queues
     * If there are an odd number of longs, the median is the top of the large queue
     * If there are an even number of longs, the median is the average of the top of both queues
     * @return long that is the median of the two priority queues
     */
    public long calculateMed() {
        // if both queues are empty
        if(left.size() == 0 && right.size() == 0) {
            return 0;
        }

        // if the data set has an even number of elements, then the median is the average of the two middle elements
        if (left.size() == right.size()) {
            med = (left.peek() + right.peek()) / 2;
        } else {
            // if there is an odd number of data, then the top of the larger queue is the median
            PriorityQueue<Long> larger;
            if (left.size() > right.size())
                larger = left;
            else larger = right;
            med = larger.peek();
        }
        return med;
    }
}
