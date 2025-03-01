package Src.Domain.Server.Message;
import java.util.Date;

public class Message {
    private String[] values;
    private CompressedObject codifiedValues;

    public Message() {
        this.values = new String[4];
    }

    public Message(String name, String description) {
        this.values = new String[4];

        this.values[0] = null;
        this.values[1] = name;
        this.values[2] = description;
        this.values[3] = null;

        this.codifiedValues = CompressionManager.codifyParameter(values);
    }

    public Message(String name, String description, Date requestTime) {
        this.values = new String[4];

        this.values[0] = null;
        this.values[1] = name;
        this.values[2] = description;
        this.values[3] = requestTime.toString();

        this.codifiedValues = CompressionManager.codifyParameter(values);
    }

    public Message(int code, String name, String description, Date requestTime) {
        this.values = new String[4];

        this.values[0] = String.valueOf(code);
        this.values[1] = name;
        this.values[2] = description;
        this.values[3] = requestTime.toString();

        this.codifiedValues = CompressionManager.codifyParameter(values);
    }

    public CompressedObject getData() {
        return this.codifiedValues;
    }

    public void setCode(int code) {
        this.values[0] = String.valueOf(code);

        this.codifiedValues = CompressionManager.codifyParameter(values);
    }

    public void setName(String name) {
        this.values[1] = name;

        this.codifiedValues = CompressionManager.codifyParameter(values);
    }

    public void setDescription(String description) {
        this.values[2] = description;

        this.codifiedValues = CompressionManager.codifyParameter(values);
    }

    public void setRequestTime(Date requestTime) {
        this.values[3] = requestTime.toString();

        this.codifiedValues = CompressionManager.codifyParameter(this.values);
    }
}
