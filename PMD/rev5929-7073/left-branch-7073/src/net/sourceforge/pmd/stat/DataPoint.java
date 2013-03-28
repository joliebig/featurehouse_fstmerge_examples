
package net.sourceforge.pmd.stat;

import net.sourceforge.pmd.ast.SimpleNode;

import java.util.Random;


public class DataPoint implements Comparable<DataPoint> {

    private SimpleNode node;
    private int random;
    private double score;
    private String message;

    
    public DataPoint() {
        super();
        
        
        Random rand = new Random();
        random = rand.nextInt(11061973);
    }

    public int compareTo(DataPoint rhs) {
        Double lhsScore = new Double(score);
        Double rhsScore = new Double(rhs.getScore());
        if (lhsScore.doubleValue() != rhsScore.doubleValue()) {
            return lhsScore.compareTo(rhsScore);
        }
        return random - rhs.random;
    }

    public SimpleNode getNode() {
        return node;
    }

    public void setNode(SimpleNode node) {
        this.node = node;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
