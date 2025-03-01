package Src.Domain.Server.Message;

import Src.Domain.Server.Message.RuffmanTree.Node;
import Src.Domain.Server.Message.RuffmanTree.RuffmanTree;
import java.util.Map;
import java.util.HashMap;

public class CompressionManager {
    public static CompressedObject codifyParameter(String[] values) {
        String value = "";

        for (String v : values) {
            if (v != null) {
                value += v;
            }
        }

        Map<Character, Integer> frequencyTable = createFrequencyTable(value);
        RuffmanTree ruffmanTree = new RuffmanTree(frequencyTable);

        CompressedObject compressedValue = new CompressedObject(codifyParameter(values, ruffmanTree.getcodesTable()), frequencyTable);

        return compressedValue;
    }

    private static String codifyParameter(String value, Map<Character, String> codesTable) {
        if (value == null) {
            return null;
        }

        String encodedValue = "";

        for (char c : value.toCharArray()) {
            encodedValue += codesTable.get(c);
        }

        return encodedValue;
    }

    private static String[] codifyParameter(String[] values, Map<Character, String> codesTables) {
        String[] codifiedValues = new String[values.length];
        
        for (int i = 0; i < values.length; i++) {
            codifiedValues[i] = values[i] == null ? null : codifyParameter(values[i], codesTables);
        }

        return codifiedValues;
    }

    public static String decodeParameter(String value, Map<Character, Integer> frequencyTable) {
        RuffmanTree ruffmanTree = new RuffmanTree(frequencyTable);
        Node current = ruffmanTree.getNode();
        String message = "";

        for (char c : value.toCharArray()) {
            if (c == '0') {
                current = current.left;
            } else {
                current = current.right;
            }

            if (current.left == null && current.right == null) {
                message += current.getCharacter();
                current = ruffmanTree.getNode();
            }
        }

        return message;
    }

    public static Map<Character, Integer> createFrequencyTable(String value) {
        Map<Character, Integer> frequencyTable = new HashMap<>();

        for (char index : value.toCharArray()) {
            if (frequencyTable.get(index) == null) {
                frequencyTable.put(index, 1);
            } else {
                frequencyTable.put(index, frequencyTable.get(index) + 1);
            }
        }   

        return frequencyTable;
    }
}