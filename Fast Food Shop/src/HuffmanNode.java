class HuffmanNode {
    char c;
    int frequency;
    HuffmanNode left;
    HuffmanNode right;

    HuffmanNode(char c, int frequency) {
        this.c = c;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }
}