package de.snaggly.bossmodellerfx.model.view;

import de.snaggly.bossmodellerfx.model.BOSSModel;

/**
 * A parents class for Models used in views.
 * Tracks information about the user specified X&Y coordinates to be restored when saved.
 *
 * @author Omar Emshani
 */
public abstract class ViewModel implements BOSSModel {
    private double xCoordinate;
    private double yCoordinate;

    public ViewModel() {
        this.xCoordinate = 10.0;
        this.yCoordinate = 10.0;
    }

    public ViewModel(double xCoordinate, double yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public double getXCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public double getYCoordinate() {
        return yCoordinate;
    }

    public void setYCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
}
