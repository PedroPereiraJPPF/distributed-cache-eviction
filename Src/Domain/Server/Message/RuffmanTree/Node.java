package Src.Domain.Server.Message.RuffmanTree;

public class Node {
    Integer frequency;
    Character character;
    public Node left, right;

    public Node() {
        this.frequency = null;
        this.character = null;
        this.left = this.right = null; 
    }

    public Node(Character character, Integer frequency) {
        this.frequency = frequency;
        this.character = character;
        this.left = this.right = null;
    }

    public Node(Character character, Integer frequency, Node left, Node right) {
        this.frequency = frequency;
        this.character = character;
        this.left = left;
        this.right = right;
    }

    public Character getCharacter() {
        return this.character;
    }

    public Integer getFrequency() {
        return this.frequency;
    }
}
