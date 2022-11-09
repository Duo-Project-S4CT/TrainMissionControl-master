package us.duoproject;

import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final String COM_PORT = "COM6";
    private static final int SENSOR_COUNT = 5;

    private static final int SIZE_X = 1200;
    private static final int SIZE_Y = 600;

    private static final AtomicBoolean DO_STOP = new AtomicBoolean(false);
    private static final AtomicBoolean DO_CALIBRATE = new AtomicBoolean(false);
    private static final AtomicInteger TOTAL_LENGTH = new AtomicInteger(0);

    @SuppressWarnings("StatementWithEmptyBody")
    public static void main(String[] args) {
        SerialPort port = SerialPort.getCommPort(COM_PORT);
        while (!port.openPort()) {
        } // continuously attempt to open port

        try {
            String name = "btspp://202103110461:1;authenticate=true";
            StreamConnection streamConnection = (StreamConnection) Connector.open(name);
            OutputStream outputStream = streamConnection.openOutputStream();

            new Timer().schedule(new TimerTask() {
                boolean previous;

                @Override
                public void run() {
                    try {
                        boolean current = DO_STOP.get();

                        if (previous != current) {
                            previous = current;
                            debug(current ? "STOP" : "MOVE");
                            outputStream.write(new byte[] { 1 });
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }, 0, 10);

            MissionControlPanel panel = new MissionControlPanel(SIZE_X, SIZE_Y, SENSOR_COUNT);
            JButton clibarteButton = new JButton(new AbstractAction("Calibrate") {
                @Override
                public void actionPerformed(ActionEvent event) {
                    DO_CALIBRATE.set(true);
                }
            });
            JButton startButton = new JButton(new AbstractAction("Start") {
                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        outputStream.write(new byte[] { 1 });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            startButton.setLocation(20, 10);
            clibarteButton.setLocation(10, 10);
            panel.add(clibarteButton);
            panel.add(startButton);

            SerialPortJsonReader reader = new SerialPortJsonReader.Builder(SerialPort.LISTENING_EVENT_DATA_RECEIVED)
                    .listener(data -> onJson(panel, data))
                    .delimiters("<", ">")
                    .build();
            port.addDataListener(reader);

            display(panel);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void onJson(MissionControlPanel panel, JsonObject object) {
        int distance = object.get("distance").getAsInt();
        if (DO_CALIBRATE.get()) {
            TOTAL_LENGTH.set(distance);
            DO_CALIBRATE.set(false);
        }

        float progress = (float) MathUtil.clampedMap(distance, TOTAL_LENGTH.get(), 0, 0, 1.0F);
        int trainRelativeToSensor = (int) MathUtil.clampedMap(progress, 0, 1.0F, 0, SENSOR_COUNT + 1) + 1;
        panel.setProgress(progress);
        panel.clearBoard();
        panel.revalidate();
        panel.repaint();

        List<Integer> sensors = object.get("sensors").getAsJsonArray().asList().stream().map(JsonElement::getAsInt)
                .toList();
        for (Integer sensor : sensors) {
            panel.triggerBeacon(sensor, true);
        }

        DO_STOP.set(sensors.contains(trainRelativeToSensor));
    }

    public static void display(MissionControlPanel panel) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setSize(SIZE_X, SIZE_Y);
        frame.setVisible(true);
    }

    private static synchronized void debug(Object object) {
        System.out.println(object);
    }
}
