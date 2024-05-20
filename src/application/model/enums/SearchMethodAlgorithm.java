package application.model.enums;

public enum SearchMethodAlgorithm {
    LINEAR_SEARCH,
    BINARY_SEARCH,
    HASH_SEARCH;

    public String getDescription() {
        switch (this) {
            case LINEAR_SEARCH:
                return "Linear Search: Simple search through each element.";
            case BINARY_SEARCH:
                return "Binary Search: Efficient search for sorted data.";
            case HASH_SEARCH:
                return "Hash Search: Fast search using a hash table.";
            default:
                return "Unknown search method.";
        }
    }

    @Override
    public String toString() {
        return this.name() + ": " + this.getDescription();
    }
}

