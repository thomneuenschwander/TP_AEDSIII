package application.model.enums;

public enum PatternMatchingAlgorithm {
    KMP,
    BOYER_MOORE,
    NAIVE;

    public String getDescription() {
        switch (this) {
            case KMP:
                return "Knuth-Morris-Pratt: Efficient for fixed patterns.";
            case BOYER_MOORE:
                return "Boyer-Moore: Efficient for large alphabets.";
            case NAIVE:
                return "Naive: Simple but inefficient.";
            default:
                return "Unknown algorithm.";
        }
    }
    
    @Override
    public String toString() {
        return this.name() + ": " + this.getDescription();
    }
}
