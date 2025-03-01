package Utils;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private String logFilePath;
    private SimpleDateFormat dateFormat;

    public Logger(String logFilePath) {
        this.logFilePath = logFilePath;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
        File logFile = new File(logFilePath);
        File parentDir = logFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String level, String message) {
        String timestamp = dateFormat.format(new Date());
        String logMessage = String.format("[%s] %s: %s", timestamp, level, message);
        writeLog(logMessage);
    }

    public void info(String message) {
        log("INFO", message);
    }

    public void warning(String message) {
        log("WARNING", message);
    }

    public void error(String message) {
        log("ERROR", message);
    }

    private void writeLog(String logMessage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int countOperation(String searchString) {
        int count = 0;
    
        try (BufferedReader reader = new BufferedReader(new FileReader(this.logFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String logMessage = line.substring(line.indexOf("INFO: ") + 6).trim();
    
                if (logMessage.length() >= searchString.length()) {
                    boolean match = true;
    
                    for (int i = 0; i < searchString.length(); i++) {
                        if (logMessage.charAt(i) != searchString.charAt(i)) {
                            match = false; 
                            break;
                        }
                    }
    
                    if (match) {
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return count;
    }

}

