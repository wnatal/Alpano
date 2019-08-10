package ch.epfl.alpano.gui;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.List;
import java.util.function.Function;

import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.gui.PanoramaRenderer;
import ch.epfl.alpano.summit.Summit;
import javafx.collections.FXCollections;

/**
 * Represents the panorama shown in the user-interface.
 * 
 * @author Natal Willisch (262092)
 * 
 */
/**
 * @author Natal
 *
 */
public final class PanoramaComputerBean {

    private final ObjectProperty<PanoramaUserParameters> parameters;
    private final ObjectProperty<Panorama> panorama;
    private final ObjectProperty<Image> image;
    private final ObjectProperty<ObservableList<Node>> labels;

    private final ObjectProperty<Function<Panorama, ImagePainter>> imagePainterProperty;
    private final BooleanProperty labelColorProperty;
    private final ObservableList<Node> labelList;
    private final PanoramaComputer computer;
    private final Labelizer labelMaker;

    private boolean change;

    /**
     * @param mnt
     *            a continuous elevation-model
     * @param summits
     *            list of all summits
     * @throws NullPointerException
     *             if a parameter is null
     */
    public PanoramaComputerBean(ContinuousElevationModel mnt,
            List<Summit> summits) {

        this.parameters = new SimpleObjectProperty<>(null);
        panorama = new SimpleObjectProperty<>(null);
        image = new SimpleObjectProperty<>(null);
        computer = new PanoramaComputer(mnt);
        labelList = FXCollections.observableArrayList();
        labels = new SimpleObjectProperty<>(
                FXCollections.unmodifiableObservableList(labelList));
        labelMaker = new Labelizer(mnt, summits);
        parameters.addListener((b, o, n) -> synchronizeParameters());
        imagePainterProperty = new SimpleObjectProperty<>(p -> ImagePainter.rainbow(p));
        imagePainterProperty.addListener((b, o, n) -> draw());
        labelColorProperty = new SimpleBooleanProperty(false);
    }

    /**
     * Returns a property (read only) that contains the parameters that
     * corresponds with the shown panorama-picture.
     * 
     * @return PanoramauserParameters-property
     */
    ObjectProperty<PanoramaUserParameters> parametersProperty() {

        return parameters;

    }

    /**
     * Returns the parameters that correspond with the shown panorama-picture.
     * 
     * @return PanoramauserParameters
     */
    PanoramaUserParameters getParameters() {

        return parameters.getValue();

    }

    /**
     * Sets the parameters that create the panorama-picture.
     * 
     * @param newParameters
     *            the new PanoramaUserParameters
     */
    void setParameters(PanoramaUserParameters newParameters) {

        parameters.setValue(newParameters);

    }

    /**
     * Returns a property (read only) that contains the panorama that
     * corresponds with the shown panorama-picture.
     * 
     * @return the Panorama-property
     */
    ReadOnlyObjectProperty<Panorama> panoramaProperty() {

        return panorama;

    }

    /**
     * Returns the panorama that corresponds with the shown panorama-picture.
     * 
     * @return the Panorama
     */
    Panorama getPanorama() {

        return panorama.getValue();

    }

    /**
     * Returns the property that contains the panorama-image.
     * 
     * @return the panorama-image-property
     */
    ReadOnlyObjectProperty<Image> imageProperty() {

        return image;

    }

    /**
     * Returns the panorama-image
     * 
     * @return the panorama-image
     */
    Image getImage() {

        return image.getValue();

    }

    /**
     * Returns a property with the labels that fit to the calculated panorama.
     * 
     * @return the labels-property
     */
    ReadOnlyObjectProperty<ObservableList<Node>> labelsProperty() {

        return labels;

    }

    /**
     * Returns the labels that fit to the calculated panorama.
     * 
     * @return the labels
     */
    ObservableList<Node> getLabels() {

        return labels.getValue();

    }

    /**
     * returns a Property that contains a Function that takes a panorama as argument and returns a ImagePainter.
     * 
     * @return Property that contains a Function that takes a panorama as argument and returns a ImagePainter
     */
    ObjectProperty<Function<Panorama, ImagePainter>> imagePainterProperty() {

        return imagePainterProperty;

    }

    /**
     * returns a BooleanProperty that indicates the color of the labels
     * 
     * @return a BooleanProperty that indicates the color of the labels
     */
    BooleanProperty labelColorProperty() {

        return labelColorProperty;

    }

    /**
     * Synchronizes all the properties. (calculates the panorama and renders the
     * corresponding image)
     */
    private void synchronizeParameters() {
        panorama.setValue(
                computer.computePanorama(getParameters().panoramaParameters()));
        labelList.setAll(
                labelMaker.labels(getParameters().panoramaDisplayParameters()));
        change = false;
        draw();

    }

    //draw image
    private void draw() {
        if (getPanorama() != null) {

            boolean bool = false;
            if (labelColorProperty.getValue())
                bool = true;

            if (bool)
                labelList.forEach(n -> {
                    if (n instanceof Text)
                        ((Text) n).setFill(Color.WHITE);
                    if (n instanceof Line)
                        ((Line) n).setStroke(Color.WHITE);
                });
            else if (change)
                labelList.forEach(n -> {
                    if (n instanceof Text)
                        ((Text) n).setFill(Color.BLACK);
                    if (n instanceof Line)
                        ((Line) n).setStroke(Color.BLACK);
                });
            change = bool;

            image.setValue(PanoramaRenderer.renderPanorama(getPanorama(),
                    imagePainterProperty.getValue().apply(getPanorama())));
        }

    }

}
