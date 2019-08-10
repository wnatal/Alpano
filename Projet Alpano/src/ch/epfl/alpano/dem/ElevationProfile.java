package ch.epfl.alpano.dem;

import static java.lang.Math.*;

import java.util.Objects;

import static ch.epfl.alpano.Preconditions.checkArgument;

import ch.epfl.alpano.Azimuth;
import ch.epfl.alpano.Distance;
import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.Math2;

/**
 * This class creates a elevation-profile from a certain location in a certain
 * direction from a {@link ContinuousElevationModel} and gives access to
 * informations about the points along this profile.
 * 
 * @author Natal Willisch (262092)
 */
public final class ElevationProfile {

    private static final int DETAIL = 4096;
    private static final int SCALE = 12; // 2 ^ SCALE = DETAIL

    private final ContinuousElevationModel elevationModel;
    private final double length;
    private final GeoPoint[] positions;

    /**
     * Creates a profile.
     * 
     * @param elevationModel
     *            a {@link ContinuousElevationModel} to which this panorama will
     *            refer
     * @param origin
     *            the point of origin
     * @param azimuth
     *            the direction (a canonical azimuth)
     * @param length
     *            the length of the profile
     * @throws IllegalArgumentException
     *             if the {@code azimuth} is not canonical or the {@code length}
     *             not positive
     * @throws NullPointerException
     *             if the {@code origin} or the {@code elevationModel} is null
     */
    public ElevationProfile(ContinuousElevationModel elevationModel,
            GeoPoint origin, double azimuth, double length) {

        checkArgument(Azimuth.isCanonical(azimuth), "Azimuth is not canonical");
        checkArgument(length > 0,
                "length of the elevation profile is not strictly positive");

        this.elevationModel = Objects.requireNonNull(elevationModel,
                "elevationModel is null");
        this.length = length;

        int arrayLength = (int) scalb(length, -SCALE) + 2;

        positions = new GeoPoint[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            positions[i] = positionAtPresice(i * DETAIL, azimuth,
                    Objects.requireNonNull(origin, "origin is null"));
        }

    }

    /**
     * Returns the elevation at a given position in the profile or throws an
     * exception.
     * 
     * @param x
     *            position
     * @return the elevation
     * @throws IllegalArgumentException
     *             if the position is out of the boundaries of the
     *             elevation-profile
     */
    public double elevationAt(double x) {

        return elevationModel.elevationAt(positionAt(x));

    }

    /**
     * Returns the coordinates for a given position in the profile or throws an
     * exception.
     * 
     * @param x
     *            position
     * @return coordinates of that position
     * @throws IllegalArgumentException
     *             if the position is out of the boundaries of the
     *             elevation-profile
     */
    public GeoPoint positionAt(double x) {

        checkArgument(x >= 0 && x <= length,
                "position is not within the boundaries of the elevation profile");

        double pos = scalb(x, -SCALE);
        int posInt = (int) pos;
        double relPos = pos - posInt;
        GeoPoint x1 = positions[posInt];
        GeoPoint x2 = positions[posInt + 1];
        double lo = Math2.lerp(x1.longitude(), x2.longitude(), relPos);
        double la = Math2.lerp(x1.latitude(), x2.latitude(), relPos);

        return new GeoPoint(lo, la);

    }

    /**
     * Returns the slope at a given position of the profile or throws an
     * exception.
     * 
     * @param x
     *            position
     * @return the slope
     * @throws IllegalArgumentException
     *             if the position is out of the boundaries of the
     *             elevation-profile
     */
    public double slopeAt(double x) {

        return elevationModel.slopeAt(positionAt(x));

    }

    // private

    /**
     * Calculates the precise position for a distance.
     * 
     * @param x
     * @return position
     */
    private GeoPoint positionAtPresice(double x, double azimuth, GeoPoint origin) {

        x = Distance.toRadians(x);

        double lo0 = origin.longitude();
        double la0 = origin.latitude();
        double lo, la;

        la = asin(sin(la0) * cos(x)
                + cos(la0) * sin(x) * cos(Azimuth.toMath(azimuth)));
        lo = Math2.floorMod((lo0
                - asin(sin(Azimuth.toMath(azimuth)) * sin(x) / cos(la)) + PI),
                Math2.PI2) - PI;
        return new GeoPoint(lo, la);

    }

}
