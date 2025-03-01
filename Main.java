import Src.View.Client.ClientView;
import Utils.Logger;

public class Main {
    public static void main(String[] args) {
        try {
            new ClientView().start();
        } catch(Exception e) {
            new Logger("Logs/ErrorLogs.log").error(e.getMessage() + " - " + e.getClass());
        }
    }
}
