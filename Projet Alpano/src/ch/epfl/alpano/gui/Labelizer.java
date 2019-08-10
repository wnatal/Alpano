package ch.epfl.alpano.gui;

import static ch.epfl.alpano.Math2.firstIntervalContainingRoot;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.shape.Line;
import javafx.scene.transform.*;
import ch.epfl.alpano.Math2;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.ElevationProfile;
import ch.epfl.alpano.summit.Summit;

/**
 * Provides the labels for the panorama-view in form of formatted nodes that are
 * ready to be insert in the javafx-scene.
 * 
 * @author Natal Willisch (262092)
 *
 */

public final class Labelizer {

    private final static double RESEARCH_INTERVAL = 64;
    private final static double TOLERANCE = 200;
    private final static int MIN_SPACE = 170;
    private final static int FRAME_DISTANCE = 19;
    private final static int FRAME_DISTANCE_POS = FRAME_DISTANCE + 1;
    private final static int TEXT_SUMMIT_D = 22;

    private final ContinuousElevationModel mnt;
    private final List<Summit> summits;

    /**
     * Constructor. Takes the basic general informations needed to calculate
     * later the labels-list.
     * 
     * @param mnt
     *            ContinousElevationModel
     * @param summits
     *            list of Summit
     * @throws NullPointerException
     *             if one of both arguments is null
     */
    public Labelizer(ContinuousElevationModel mnt, List<Summit> summits) {

        this.mnt = Objects.requireNonNull(mnt);
        this.summits = Objects.requireNonNull(summits);

    }

    /**
     * Provides the list of label nodes (lines and the information texts)
     * 
     * @param parameters
     *            PanoramaParameters
     * @return list of the nodes that describe the labels (text + line)
     */
    public List<Node> labels(PanoramaParameters parameters) {

        List<Node> etiquettes = new ArrayList<>();
        List<SummitPosition> inFrame = new LinkedList<>();

        for (Summit x : summits) {

            double distance = parameters.observerPosition()
                    .distanceTo(x.position());

            if (distance <= parameters.maxDistance()) {

                double azimuth = parameters.observerPosition()
                        .azimuthTo(x.position());
                double horizontalTolerance = parameters.horizontalFieldOfView()
                        / 2;

                if (Math.abs(Math2.angularDistance(azimuth,
                        parameters.centerAzimuth())) <= horizontalTolerance) {

                    ElevationProfile profile = new ElevationProfile(mnt,
                            parameters.observerPosition(), azimuth, distance);
                    double slope = (-PanoramaComputer
                            .rayToGroundDistance(profile,
                                    parameters.observerElevation(), 0)
                            .applyAsDouble(distance)) / distance;
                    double altitude = Math.atan(slope);
                    double verticalTolerance = parameters.verticalFieldOfView()
                            / 2;

                    if (altitude >= -verticalTolerance
                            && altitude <= verticalTolerance) {

                        int verticalPixelPos = (int) Math
                                .round(parameters.yForAltitude(altitude));

                        int horizontalPixelPos = (int) Math
                                .round(parameters.xForAzimuth(azimuth));

                        if (verticalPixelPos >= MIN_SPACE
                                && horizontalPixelPos >= FRAME_DISTANCE_POS
                                && horizontalPixelPos <= parameters.width()
                                        - FRAME_DISTANCE_POS) {

                            DoubleUnaryOperator distanceFunc = PanoramaComputer
                                    .rayToGroundDistance(profile,
                                            parameters.observerElevation(),
                                            slope);
                            double foundDistance = firstIntervalContainingRoot(
                                    distanceFunc, 0, distance,
                                    RESEARCH_INTERVAL);

                            if (foundDistance > distance - TOLERANCE) {
                                inFrame.add(new SummitPosition(x,
                                        verticalPixelPos, horizontalPixelPos));
                            }
                        }
                    }
                }
            }
        }

        Collections.sort(inFrame);
        BitSet bs = new BitSet(parameters.width());
        int textPosition = 0;

        bs.set(0, FRAME_DISTANCE_POS);
        bs.set(parameters.width() - FRAME_DISTANCE, parameters.width());

        if (inFrame.size() > 0) {

            int highestMountain = inFrame.get(0).verticalPos();
            textPosition = highestMountain - TEXT_SUMMIT_D;

        }

        for (SummitPosition elem : inFrame) {

            if (!bs.get(elem.horizontalPos())) {

                bs.set(elem.horizontalPos() - FRAME_DISTANCE,
                        elem.horizontalPos() + FRAME_DISTANCE);

                Text t = new Text(elem.summit().name() + " ("
                        + elem.summit().elevation() + " m)");
                t.getTransforms().addAll(
                        new Translate(elem.horizontalPos(), textPosition),
                        new Rotate(-60, 0, 0));
                etiquettes.add(t);

                Line l = new Line(elem.horizontalPos(), elem.verticalPos(),
                        elem.horizontalPos(), textPosition + 1);
                etiquettes.add(l);

            }

        }

        return etiquettes;

    }

    /**
     * Small class that stores the position of the summit and the summit itself.
     * The class is comparable, so a list of SummitPosition can be sorted.
     *
     */
    private final class SummitPosition implements Comparable<SummitPosition> {

        private final Summit summit;
        private final Integer verticalPos;
        private final Integer horizontalPos;

        SummitPosition(Summit summit, int verticalPos, int horizontalPos) {

            this.summit = summit;
            this.verticalPos = verticalPos;
            this.horizontalPos = horizontalPos;

        }

        Summit summit() {

            return summit;

        }

        int verticalPos() {

            return verticalPos;

        }

        int horizontalPos() {

            return horizontalPos;

        }

        /**
         * Sorts first by vertical position and than by the elevation of the
         * summit (higher first).
         * 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(SummitPosition arg0) {
            int comp = verticalPos.compareTo(arg0.verticalPos());
            if (comp == 0)
                comp = -Integer.compare(summit.elevation(),
                        arg0.summit().elevation());
            return comp;
        }

    }
}
