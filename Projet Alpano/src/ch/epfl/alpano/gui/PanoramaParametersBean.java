package ch.epfl.alpano.gui;

import javafx.beans.property.*;
import static javafx.application.Platform.runLater;

import java.util.Objects;

/**
 * Represents the parameters shown in the user-interface.
 * 
 * @author Natal Willisch (262092)
 * 
 */
public final class PanoramaParametersBean {

    private final ObjectProperty<PanoramaUserParameters> parameters;
    private final ObjectProperty<Integer> observerLongitudeProperty;
    private final ObjectProperty<Integer> observerLatitudeProperty;
    private final ObjectProperty<Integer> observerelevationProperty;
    private final ObjectProperty<Integer> centerAzimuthProperty;
    private final ObjectProperty<Integer> horizontalFieldOfViewProperty;
    private final ObjectProperty<Integer> maxDistanceProperty;
    private final ObjectProperty<Integer> widthProperty;
    private final ObjectProperty<Integer> heightProperty;
    private final ObjectProperty<Integer> superSamplingExponentProperty;

    /**
     * Constructs a PanoramaParametersBean. Initialize all parameters that the class
     * provides based on the given parameters.
     * 
     * @param parameters
     *            PanoramaUserParameters
     * @throws NullPointerException
     *             if parameters is null
     */
    public PanoramaParametersBean(PanoramaUserParameters parameters) {

        this.parameters = new SimpleObjectProperty<>(
                Objects.requireNonNull(parameters));

        observerLongitudeProperty = new SimpleObjectProperty<>(
                parameters.observerLongitude());
        observerLatitudeProperty = new SimpleObjectProperty<>(
                parameters.observerLatitude());
        observerelevationProperty = new SimpleObjectProperty<>(
                parameters.observerElevation());
        centerAzimuthProperty = new SimpleObjectProperty<>(
                parameters.centerAzimuth());
        horizontalFieldOfViewProperty = new SimpleObjectProperty<>(
                parameters.horizontalFieldOfView());
        maxDistanceProperty = new SimpleObjectProperty<>(
                parameters.maxDistance());
        widthProperty = new SimpleObjectProperty<>(parameters.width());
        heightProperty = new SimpleObjectProperty<>(parameters.height());
        superSamplingExponentProperty = new SimpleObjectProperty<>(
                parameters.superSamplingExponent());

        observerLongitudeProperty.addListener(
                (b, o, n) -> runLater(this::synchronizeParameters));
        observerLatitudeProperty.addListener(
                (b, o, n) -> runLater(this::synchronizeParameters));
        observerelevationProperty.addListener(
                (b, o, n) -> runLater(this::synchronizeParameters));
        centerAzimuthProperty.addListener(
                (b, o, n) -> runLater(this::synchronizeParameters));
        horizontalFieldOfViewProperty.addListener(
                (b, o, n) -> runLater(this::synchronizeParameters));
        maxDistanceProperty.addListener(
                (b, o, n) -> runLater(this::synchronizeParameters));
        widthProperty.addListener(
                (b, o, n) -> runLater(this::synchronizeParameters));
        heightProperty.addListener(
                (b, o, n) -> runLater(this::synchronizeParameters));
        superSamplingExponentProperty.addListener(
                (b, o, n) -> runLater(this::synchronizeParameters));

    }

    /**
     * Returns a property (read only) that contains all the parameters.
     * 
     * @return PanoramuserParameters-property
     */
    ReadOnlyObjectProperty<PanoramaUserParameters> parametersProperty() {

        return parameters;

    }

    /**
     * Returns a property that contains the longitude.
     * 
     * @return the longitude
     */
    ObjectProperty<Integer> observerLongitudeProperty() {

        return observerLongitudeProperty;

    }

    /**
     * Returns a property that contains the latitude.
     * 
     * @return the latitude
     */
    ObjectProperty<Integer> observerLatitudeProperty() {

        return observerLatitudeProperty;

    }

    /**
     * Returns a property that contains the observers elevation.
     * 
     * @return the observers elevation
     */
    ObjectProperty<Integer> observerelevationProperty() {

        return observerelevationProperty;

    }

    /**
     * Returns a property that contains the center-azimuth.
     * 
     * @return the center-azimuth
     */
    ObjectProperty<Integer> centerAzimuthProperty() {

        return centerAzimuthProperty;

    }

    /**
     * Returns a property that contains the horizontal field of view.
     * 
     * @return the horizontal field of view
     */
    ObjectProperty<Integer> horizontalFieldOfViewProperty() {

        return horizontalFieldOfViewProperty;

    }

    /**
     * Returns a property that contains the maximal distance.
     * 
     * @return the maximal distance
     */
    ObjectProperty<Integer> maxDistanceProperty() {

        return maxDistanceProperty;

    }

    /**
     * Returns a property that contains the width of the panorama.
     * 
     * @return the width of the panorama
     */
    ObjectProperty<Integer> widthProperty() {

        return widthProperty;

    }

    /**
     * Returns a property that contains the height of the panorama.
     * 
     * @return the height of the panorama
     */
    ObjectProperty<Integer> heightProperty() {

        return heightProperty;

    }

    /**
     * Returns a property that contains the supersampling exponent.
     * 
     * @return the supersampling exponent
     */
    ObjectProperty<Integer> superSamplingExponentProperty() {

        return superSamplingExponentProperty;

    }

    /**
     * synchronize and corrects all the properties.
     */
    private void synchronizeParameters() {

        parameters.setValue(
                new PanoramaUserParameters(observerLongitudeProperty.getValue(),
                        observerLatitudeProperty.getValue(),
                        observerelevationProperty.getValue(),
                        centerAzimuthProperty.getValue(),
                        horizontalFieldOfViewProperty.getValue(),
                        maxDistanceProperty.getValue(),
                        widthProperty.getValue(), heightProperty.getValue(),
                        superSamplingExponentProperty.getValue()));
        
        PanoramaUserParameters content = parameters.getValue();
        observerLongitudeProperty.setValue(content.observerLongitude());
        observerLatitudeProperty.setValue(content.observerLatitude());
        observerelevationProperty.setValue(content.observerElevation());
        centerAzimuthProperty.setValue(content.centerAzimuth());
        horizontalFieldOfViewProperty.setValue(content.horizontalFieldOfView());
        maxDistanceProperty.setValue(content.maxDistance());
        widthProperty.setValue(content.width());
        heightProperty.setValue(content.height());
        superSamplingExponentProperty.setValue(content.superSamplingExponent());

    }

}
