package us.duoproject;

import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class SerialPortJsonReader implements SerialPortDataListener {

    private final int event;
    private final Consumer<JsonObject> listener;
    private final String startDelimiter;
    private final String endDelimiter;

    private String partialData = "";

    public SerialPortJsonReader(int event, Consumer<JsonObject> listener, String startDelimiter, String endDelimiter) {
        this.event = event;
        this.listener = listener;
        this.startDelimiter = startDelimiter;
        this.endDelimiter = endDelimiter;
    }

    @Override
    public int getListeningEvents() {
        return event;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (listener == null) {
            throw new RuntimeException("No listener set for in SerialPortJsonReader.");
        }

        String rawData = new String(event.getReceivedData(), StandardCharsets.UTF_8)
                .replaceAll("\\n", "")
                .replaceAll("\\r", "")
                .trim();

        if (rawData.contains(startDelimiter)) {
            partialData = rawData;
        } else if (rawData.contains(endDelimiter)) {
            String completeData = (partialData + rawData)
                    .replace(startDelimiter, "")
                    .replace(endDelimiter, "");

            try {
                JsonObject jsonObject = JsonParser.parseString(completeData).getAsJsonObject();
                try {
                    listener.accept(jsonObject);
                } catch (Exception exception) {
                    System.err.println("An error occurred in a JSON listener.");
                    exception.printStackTrace(System.err);
                }
            } catch (Exception exception) {
//                System.err.println("Error while parsing serial port data to JSON; likely a formatting error.");
//                System.err.println("Is this correct: " + completeData);
//                exception.printStackTrace(System.err);
            } finally {
                partialData = "";
            }
        } else {
            partialData += rawData;
        }
    }

    public static class Builder {

        private final int event;
        private Consumer<JsonObject> listener;
        private String startDelimiter;
        private String endDelimiter;


        public Builder(int event) {
            this.event = event;
        }

        public Builder listener(Consumer<JsonObject> listener) {
            this.listener = listener;
            return this;
        }

        public Builder delimiters(String startDelimiter, String endDelimiter) {
            this.startDelimiter = startDelimiter;
            this.endDelimiter = endDelimiter;
            return this;
        }

        public SerialPortJsonReader build() {
            return new SerialPortJsonReader(event, listener, startDelimiter, endDelimiter);
        }
    }
}
