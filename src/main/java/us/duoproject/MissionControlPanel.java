package us.duoproject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MissionControlPanel extends JPanel {

    private static final int TRACK_SIZE = 30;
    private static final int BEACON_SIZE = 20;

    private final List<Marker> BEACONS = new ArrayList<>();

    private final int sizeX;
    private final int sizeY;
    private final int beaconCount;

    private float progress = 0.0F;

    public MissionControlPanel(int sizeX, int sizeY, int beaconCount) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.beaconCount = beaconCount;

        for (int i = 1; i <= beaconCount; i++) {
            BEACONS.add(new Marker(i, deltaX(i, BEACON_SIZE), deltaY(3), BEACON_SIZE));
        }
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(sizeX, sizeY);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        drawSensors(graphics);
        drawTracks(graphics);

    }

    private void drawSensors(Graphics graphics) {
        graphics.setColor(new Color(200, 200, 200));
        graphics.fillRect(0, deltaY(3), sizeX, BEACON_SIZE);
        for (int i = 0; i < beaconCount; i++) {
            BEACONS.get(i).paint(graphics);
        }
    }

    private void drawTracks(Graphics graphics) {
        // track background
        graphics.setColor(new Color(150, 150, 150));
        graphics.fillRect(0, deltaY(4) + TRACK_SIZE / 4, sizeX, TRACK_SIZE);

        // track foreground
        graphics.setColor(new Color(70, 139, 255));
        int w = (int) MathUtil.clampedMap(progress, 0, 1, 0, sizeX);
        graphics.fillRect(0, deltaY(4) + TRACK_SIZE / 4, w, TRACK_SIZE);
    }

    private int deltaY(int input) {
        return sizeY / 8 * input;
    }

    private int deltaX(int input, int size) {
        return (sizeX / (beaconCount + 1) * input) - size / 2;
    }

    public void triggerBeacon(int id, boolean triggered) {
        BEACONS.stream().filter(beacon -> beacon.getId() == id).findFirst().ifPresent(beacon -> beacon.setTriggered(triggered));
    }

    public void clearBoard() {
        for (int i = 1; i <= beaconCount; i++) {
            triggerBeacon(i, false);
        }
    }
}
