package ch.epfl.alpano.gui;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.collections.FXCollections;
import ch.epfl.alpano.Azimuth;
import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.DiscreteElevationModel;
import ch.epfl.alpano.dem.HgtDiscreteElevationModel;
import ch.epfl.alpano.summit.GazetteerParser;
import ch.epfl.alpano.summit.Summit;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

/**
 * Main-class. Starts the application and creates the graphic userinterface.
 * 
 * @author Natal Willisch (262092)
 * 
 */
public final class Alpano extends Application {

    final static File HGT_FILE1 = new File("N45E006.hgt");
    final static File HGT_FILE2 = new File("N46E006.hgt");
    final static File HGT_FILE3 = new File("N45E007.hgt");
    final static File HGT_FILE4 = new File("N46E007.hgt");
    final static File HGT_FILE5 = new File("N45E008.hgt");
    final static File HGT_FILE6 = new File("N46E008.hgt");
    final static File HGT_FILE7 = new File("N45E009.hgt");
    final static File HGT_FILE8 = new File("N46E009.hgt");
    final static File SOMMETS_FILE = new File("alps.txt");
    final static PanoramaUserParameters STANDARD_PAN = PredefinedPanoramas.ALPES_DU_JURA;
    private DiscreteElevationModel dem;
    StringProperty information = new SimpleStringProperty();

    /**
     * Launches the application.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        DiscreteElevationModel dem1 = new HgtDiscreteElevationModel(HGT_FILE1),
                dem2 = new HgtDiscreteElevationModel(HGT_FILE2),
                dem3 = new HgtDiscreteElevationModel(HGT_FILE3),
                dem4 = new HgtDiscreteElevationModel(HGT_FILE4),
                dem5 = new HgtDiscreteElevationModel(HGT_FILE5),
                dem6 = new HgtDiscreteElevationModel(HGT_FILE6),
                dem7 = new HgtDiscreteElevationModel(HGT_FILE7),
                dem8 = new HgtDiscreteElevationModel(HGT_FILE8);

        dem = dem1.union(dem2).union(dem3.union(dem4))
                .union(dem5.union(dem6).union(dem7.union(dem8)));

        if (dem != null) {

            List<Summit> summits = GazetteerParser
                    .readSummitsFrom(SOMMETS_FILE);
            ContinuousElevationModel cem = new ContinuousElevationModel(dem);
            PanoramaParametersBean parameterBean = new PanoramaParametersBean(
                    STANDARD_PAN);
            PanoramaComputerBean computerBean = new PanoramaComputerBean(cem,
                    summits);

            BorderPane root = new BorderPane(
                    panoPane(computerBean, parameterBean),
                    menu(computerBean, parameterBean), null,
                    paramsGrid(parameterBean), null);
            Scene scene = new Scene(root);

            primaryStage.setTitle("Alpano");
            primaryStage.setScene(scene);
            primaryStage.show();

        }

    }

    /**
     * Closes the opened resources.
     * 
     * @see javafx.application.Application#stop()
     */
    @Override
    public void stop() throws Exception {

        dem.close();

    }
    
    
    //menubar
    @SuppressWarnings("unchecked")
    private MenuBar menu(PanoramaComputerBean computerBean,
            PanoramaParametersBean parameterBean) {
        Consumer<PanoramaUserParameters> set = param -> {

            parameterBean.observerLatitudeProperty()
                    .setValue(param.observerLatitude());
            parameterBean.observerLongitudeProperty()
                    .setValue(param.observerLongitude());
            parameterBean.observerelevationProperty()
                    .setValue(param.observerElevation());
            parameterBean.centerAzimuthProperty()
                    .setValue(param.centerAzimuth());
            parameterBean.horizontalFieldOfViewProperty()
                    .setValue(param.horizontalFieldOfView());
        };

        MenuBar bar = new MenuBar();
        Menu menuLocation = new Menu("Locations");

        BiConsumer<String, PanoramaUserParameters> add = (s, p) -> {
            MenuItem item = new MenuItem(s);

            item.setOnAction(e -> set.accept(p));
            menuLocation.getItems().add(item);
        };
        add.accept("Niesen", PredefinedPanoramas.NIESEN);
        add.accept("Alpes du Jura", PredefinedPanoramas.ALPES_DU_JURA);
        add.accept("Mont Racine", PredefinedPanoramas.MONT_RACINE);
        add.accept("Finsteraarhorn", PredefinedPanoramas.FINSTERAARHORN);

        Menu menuMode = new Menu("View-Modes");
        ToggleGroup group = new ToggleGroup();
        BiConsumer<String, Function<Panorama, ImagePainter>> addPainters = (s, p) -> {
            RadioMenuItem item = new RadioMenuItem(s);
            item.setToggleGroup(group);
            item.setUserData(p);
            item.setId(s);
            menuMode.getItems().add(item);

        };
        addPainters.accept("Rainbow", p->ImagePainter.rainbow(p));
        addPainters.accept("photorealistic", p->ImagePainter.photorealisticBlue(p));
        addPainters.accept("historic", p->ImagePainter.photorealisticHisto(p));
        addPainters.accept("night", p->ImagePainter.photorealisticNight(p));
        addPainters.accept("gray", p->ImagePainter.grayish(p));
        addPainters.accept("sketch", p->ImagePainter.draw(p));
        addPainters.accept("lig. de hauteur (d = 200m)", p->ImagePainter.layer(p));
        
        group.getToggles().get(0).setSelected(true);

        group.selectedToggleProperty().addListener((t, old, updated) -> {
            if (updated != null){
                computerBean.labelColorProperty().setValue(((RadioMenuItem)updated).getId()
                        =="night"?true:false);
                computerBean.imagePainterProperty()
                        .setValue((Function<Panorama, ImagePainter>) updated.getUserData());
                
            }
        });

        bar.getMenus().addAll(menuLocation, menuMode);

        return bar;
    }

    //image area
    private StackPane panoPane(PanoramaComputerBean computerBean,
            PanoramaParametersBean parameterBean) {

        Pane labelsPane = new Pane();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(4444),
                labelsPane);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250),
                labelsPane);
        PauseTransition wait = new PauseTransition(Duration.seconds(2));

        {
            labelsPane.setMouseTransparent(true);
            labelsPane.prefWidthProperty().bind(parameterBean.widthProperty());
            labelsPane.prefHeightProperty()
                    .bind(parameterBean.heightProperty());

            Bindings.bindContent(labelsPane.getChildren(),
                    computerBean.getLabels());

            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.08);
            fadeIn.setToValue(1.0);

            wait.setOnFinished(e -> fadeOut.play());

            computerBean.panoramaProperty()
                    .addListener(e -> wait.playFromStart());
        }

        ImageView panoView = new ImageView();

        {
            panoView.imageProperty().bind(computerBean.imageProperty());
            panoView.fitWidthProperty().bind(parameterBean.widthProperty());
            panoView.fitHeightProperty().bind(parameterBean.heightProperty());
            panoView.setPreserveRatio(true);
            panoView.setSmooth(true);
            panoView.setOnMouseMoved(e -> {

                Panorama panorama = computerBean.getPanorama();
                PanoramaUserParameters parameters = computerBean
                        .getParameters();

                Point pos = mousPosConverter(e, parameters, parameterBean);
                int x = pos.x;
                int y = pos.y;

                double azimuth = parameters.panoramaParameters().azimuthForX(x);
                String information = String.format(
                        "Position : %.4f°N %.4f°E\nDistance : %.1f km\nAltitude : %.0f m\nAzimut : %.1f (%s)\tElévation : %.1f°",
                        Math.toDegrees(panorama.latitudeAt(x, y)),
                        Math.toDegrees(panorama.longitudeAt(x, y)),
                        panorama.distanceAt(x, y) / 1000,
                        panorama.elevationAt(x, y), Math.toDegrees(azimuth),
                        Azimuth.toOctantString(azimuth, "N", "E", "S", "W"),
                        Math.toDegrees(parameters.panoramaParameters()
                                .altitudeForY(y)));
                this.information.setValue(information);

            });

            panoView.setOnMouseClicked(e -> {

                Panorama panorama = computerBean.getPanorama();
                PanoramaUserParameters parameters = computerBean
                        .getParameters();

                Point pos = mousPosConverter(e, parameters, parameterBean);
                int x = pos.x;
                int y = pos.y;

                double longitude = Math.toDegrees(panorama.longitudeAt(x, y));
                double latitude = Math.toDegrees(panorama.latitudeAt(x, y));

                String qy = String.format((Locale) null,
                        "mlat=%.2f&mlon=%.2f, args", latitude, longitude);
                String fg = String.format((Locale) null, "map=15/%.2f/%.2f",
                        latitude, longitude);

                try {
                    URI osmURI = new URI("http", "www.openstreetmap.org", "/",
                            qy, fg);
                    java.awt.Desktop.getDesktop().browse(osmURI);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }

            });
        }

        StackPane panoGroup = new StackPane();
        panoGroup.getChildren().addAll(panoView, labelsPane);

        ScrollPane panoScrollPane = new ScrollPane();
        panoScrollPane.setContent(panoGroup);
        panoScrollPane.setOnMouseMoved(e -> {

            fadeOut.stop();
            fadeIn.play();
            labelsPane.setOpacity(1);
            wait.playFromStart();
        });

        // update Notice:

        String message = "Cliquez ici pour mettre le dessin à jour.";
        Text updateText = new Text(message);
        updateText.setFont(new Font(40.));
        updateText.setTextAlignment(TextAlignment.CENTER);

        StackPane updateNotice = new StackPane(updateText);

        {
            updateNotice.setBackground(new Background(new BackgroundFill(
                    new Color(1, 1, 1, 0.9), CornerRadii.EMPTY, Insets.EMPTY)));

            BooleanExpression isNotEqual = computerBean.parametersProperty()
                    .isNotEqualTo(parameterBean.parametersProperty());
            updateNotice.visibleProperty().bind(isNotEqual);
            updateNotice.setCursor(Cursor.HAND);

            /*
             * Set cursor to waiting during the calculation:
             * 
             * As the cursor sets only after an event, I must separate the
             * cursor.set and the calculation in two different events (the
             * alternative would be a thread for the calculation). It works
             * properly, because the mousereleased-event fires always from the
             * same position where the mouse is pressed down, even I would move
             * the mouse out of the panorama-area, so after a mousepressed-event
             * a mousereleased event has to follow. (But in the case the mouse
             * sleeps on the mouse, there could be a long time between the two
             * events).
             * 
             * Therefore (as always both event happen together), the
             * booleanProperty waiting is not really necessary, but ensures a
             * proper workflow if the behavior of the events-firing should ever
             * change.
             */

            BooleanProperty waiting = new SimpleBooleanProperty(false);
            updateNotice.setOnMousePressed(e -> {
                waiting.setValue(true);
                updateText.setText("un moment...");
                updateNotice.setCursor(Cursor.WAIT);
            });
            updateNotice.setOnMouseReleased(e -> {
                if (waiting.getValue()) {
                    computerBean.setParameters(
                            parameterBean.parametersProperty().getValue());
                    waiting.setValue(false);
                }
                updateText.setText(
                        "Les paramètres du panorama ont changé.\n" + message);
                updateNotice.setCursor(Cursor.HAND);
            });
        }

        return new StackPane(panoScrollPane, updateNotice);

    }

    
    private GridPane paramsGrid(PanoramaParametersBean parameterBean) {

        GridPane gridPane = new GridPane();

        gridPane.setHgap(10);
        gridPane.setVgap(3);
        gridPane.setPadding(new Insets(5));

        {
            List<Node> nodeList = new ArrayList<>();

            StringConverter<Integer> stringConverterFP4 = new FixedPointStringConverter(
                    4);
            StringConverter<Integer> stringConverterFP0 = new FixedPointStringConverter(
                    0);
            int columnCount7 = 7;
            int columnCount4 = 4;
            int columnCount3 = 3;
            nodesCreator("Latitude (°) :", stringConverterFP4,
                    parameterBean.observerLatitudeProperty(), columnCount7,
                    nodeList);
            nodesCreator("Longitude (°) :", stringConverterFP4,
                    parameterBean.observerLongitudeProperty(), columnCount7,
                    nodeList);
            nodesCreator("Altitude (m) :", stringConverterFP0,
                    parameterBean.observerelevationProperty(), columnCount4,
                    nodeList);
            nodesCreator("Azimut (°) :", stringConverterFP0,
                    parameterBean.centerAzimuthProperty(), columnCount3,
                    nodeList);
            nodesCreator("Angle de vue (°) :", stringConverterFP0,
                    parameterBean.horizontalFieldOfViewProperty(), columnCount3,
                    nodeList);
            nodesCreator("Visibilité (km) :", stringConverterFP0,
                    parameterBean.maxDistanceProperty(), columnCount3,
                    nodeList);
            nodesCreator("Largeur (px) :", stringConverterFP0,
                    parameterBean.widthProperty(), columnCount4, nodeList);
            nodesCreator("Hauteur (px) :", stringConverterFP0,
                    parameterBean.heightProperty(), columnCount4, nodeList);

            nodeList.add(newText("Suréchantillonnage :"));
            ChoiceBox<Integer> choiceBox = new ChoiceBox<>(
                    FXCollections.observableArrayList(0, 1, 2));
            StringConverter<Integer> converter = new LabeledListStringConverter(
                    "non", "2×", "4×");

            choiceBox.setConverter(converter);
            choiceBox.valueProperty().bindBidirectional(
                    parameterBean.superSamplingExponentProperty());
            nodeList.add(choiceBox);

            for (int i = 0; i < nodeList.size(); i++) {
                Node n = nodeList.get(i);

                gridPane.add(n, i % 6, i / 6);

                if (i % 2 == 0)
                    GridPane.setHalignment(n, HPos.RIGHT);
            }
        }

        {
            TextArea informationBox = new TextArea();
            informationBox.setEditable(false);
            informationBox.setPrefRowCount(2);
            informationBox.textProperty().bind(information);
            informationBox.prefWidthProperty().bind(gridPane.widthProperty());

            gridPane.add(informationBox, 6, 0, 6, 3);
        }

        return gridPane;

    }

    private TextField newTextField(StringConverter<Integer> stringConverter,
            ObjectProperty<Integer> property, int columnCount) {

        TextField textField = new TextField();

        TextFormatter<Integer> formatter = new TextFormatter<>(stringConverter);
        formatter.valueProperty().bindBidirectional(property);
        textField.setTextFormatter(formatter);
        textField.setAlignment(Pos.CENTER_RIGHT);
        textField.setPrefColumnCount(columnCount);
        return textField;

    }

    private Text newText(String string) {

        return new Text(string);

    }

    private Point mousPosConverter(MouseEvent e, PanoramaUserParameters calc,
            PanoramaParametersBean shown) {

        double x_pixel = e.getX();
        double y_pixel = e.getY();

        return new Point(
                (int) (x_pixel / shown.widthProperty().getValue()
                        * calc.panoramaParameters().width()),
                (int) (y_pixel / shown.heightProperty().getValue()
                        * calc.panoramaParameters().height()));

    }

    private void nodesCreator(String text, StringConverter<Integer> converter,
            ObjectProperty<Integer> property, int columnCount,
            List<Node> nodeList) {

        nodeList.add(newText(text));
        nodeList.add(newTextField(converter, property, columnCount));

    }

}
