package ch.epfl.alpano;

import static ch.epfl.alpano.Math2.*;

import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.ElevationProfile;

/**
 * The PanoramaComputer calculates with the given information (parameters) the
 * sampled informations for a panorama from the given continuous elevation-model
 * and creates with the help of the subclass Panorama.Build a corresponding
 * panorama-object.
 * 
 * @author Natal Willisch (262092)
 *
 */
public final class PanoramaComputer {
    
    private final static double K = 0.13;
    private final static double COMPENSATOR = (1 - K) / (2
            * Distance.EARTH_RADIUS);
    private final static double RESEARCH_INTERVAL = 64;
    private final static double DISTANCE_EPSILON = 4;

    private final ContinuousElevationModel dem;

    /**
     * Construct a new panorma-computer that contains a continuous
     * elevation-model.
     * 
     * @param dem
     *            a continuous elevation model of the landscape
     * @throws NullPointerException
     *             if {@code dem} is null
     */
    public PanoramaComputer(ContinuousElevationModel dem) {
        
        this.dem = Objects.requireNonNull(dem,
                "ContinousElevationModel is null");
        
    }

    /**
     * Calculates from its CEM the demanded panorama.
     * 
     * @param parameters
     *            parameters that define view, position, direction etc. of the
     *            wanted panorama
     * @return the demanded panorama
     * @throws NullPointerException
     *             if {@code parameters} is null
     */
    public Panorama computePanorama(PanoramaParameters parameters) {
        
        double distanceLowerBound, d;
        GeoPoint position;
        ElevationProfile profile;
        Panorama.Builder build = new Panorama.Builder(parameters);
        DoubleUnaryOperator distanceFunc;
        for (int x = 0; x < parameters.width(); x++) {
            d = 0;
            distanceLowerBound = 0;
            profile = new ElevationProfile(dem, parameters.observerPosition(),
                    parameters.azimuthForX(x), parameters.maxDistance());
            
            for (int y = parameters.height() - 1; y >= 0; y--) {
                distanceFunc = rayToGroundDistance(profile,
                        parameters.observerElevation(),
                        Math.tan(parameters.altitudeForY(y)));
                distanceLowerBound = firstIntervalContainingRoot(distanceFunc,
                        d, parameters.maxDistance() - RESEARCH_INTERVAL, RESEARCH_INTERVAL);
                
                if (distanceLowerBound < Double.POSITIVE_INFINITY) {
                    d = improveRoot(distanceFunc, distanceLowerBound,
                            distanceLowerBound + RESEARCH_INTERVAL,
                            DISTANCE_EPSILON);
                    position = profile.positionAt(d);

                    build.setDistanceAt(x, y, (float) (d/Math.cos(parameters.altitudeForY(y))))
                            .setElevationAt(x, y,
                                    (float) dem.elevationAt(position))
                            .setLatitudeAt(x, y, (float) position.latitude())
                            .setLongitudeAt(x, y, (float) position.longitude())
                            .setSlopeAt(x, y, (float) dem.slopeAt(position));
                } else 
                    break;
            }
        }
        return build.build();

    }

    /**
     * Unary function to find a zero between an imagined light ray and the
     * ground (of the elevation model).
     * 
     * @param profile
     *            the elevation profile that represents the elevation model in a
     *            certain direction
     * @param ray0
     *            the initial height of the observer
     * @param raySlope
     *            the "view angle" (of the ray)
     * @return returns the difference between the ray and the ground at a given
     *         point
     */
    public static DoubleUnaryOperator rayToGroundDistance(
            ElevationProfile profile, double ray0, double raySlope) {
        
        return (x) -> ray0 + x * raySlope - profile.elevationAt(x)
                + COMPENSATOR * sq(x);

    }
}
