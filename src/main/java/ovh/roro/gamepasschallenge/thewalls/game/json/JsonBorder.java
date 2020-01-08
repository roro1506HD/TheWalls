package ovh.roro.gamepasschallenge.thewalls.game.json;

import com.google.gson.annotations.SerializedName;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public final class JsonBorder {

    @SerializedName("center_x")
    private double centerX;

    @SerializedName("center_z")
    private double centerZ;

    @SerializedName("start_size")
    private double startSize;

    @SerializedName("end_size")
    private double endSize;

    @SerializedName("shrink_start")
    private int shrinkStart;

    @SerializedName("shrink_time")
    private int shrinkTime;

    public double getCenterX() {
        return this.centerX;
    }

    public double getCenterZ() {
        return this.centerZ;
    }

    public double getStartSize() {
        return this.startSize;
    }

    public double getEndSize() {
        return this.endSize;
    }

    public int getShrinkStart() {
        return this.shrinkStart;
    }

    public int getShrinkTime() {
        return this.shrinkTime;
    }
}
