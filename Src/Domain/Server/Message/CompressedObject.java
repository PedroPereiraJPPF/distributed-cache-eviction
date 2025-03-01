package Src.Domain.Server.Message;
import java.util.Map;

public class CompressedObject {
    private String[] values;
    private Map<Character, Integer> frequencyTable;

    public CompressedObject(String[] values, Map<Character, Integer> frequencyTable) {
        this.values = values;
        this.frequencyTable = frequencyTable;
    }

    public Map<Character, Integer> getFrequencyTable() {
        return this.frequencyTable;
    }

    public String[] getValues() {
        return this.values;
    }
}
