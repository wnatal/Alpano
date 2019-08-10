package ch.epfl.alpano.gui;


/**
 * Provides some predefined panoramas in form of PanoramaUserParameters.
 * 
 * @author Natal Willisch (262092)
 *
 */
public interface PredefinedPanoramas {
    
    //common parameters
    final static int WIDTH = 2500;
    final static int HEIGHT = 800;
    final static int MAX_DISTANCE = 300;
    final static int SUPER_SAMPLING_EXPONENT = 0;
    
    //panoramas
    public final static PanoramaUserParameters NIESEN = new PanoramaUserParameters(
            76500, 467300, 600, 180, 110, MAX_DISTANCE, WIDTH, HEIGHT,
            SUPER_SAMPLING_EXPONENT);
    public final static PanoramaUserParameters ALPES_DU_JURA = new PanoramaUserParameters(
            68087, 470085, 1380, 162, 27, MAX_DISTANCE, WIDTH, HEIGHT,
            SUPER_SAMPLING_EXPONENT);
    public final static PanoramaUserParameters MONT_RACINE = new PanoramaUserParameters(
            68200, 470200, 1500, 135, 45, MAX_DISTANCE, WIDTH, HEIGHT,
            SUPER_SAMPLING_EXPONENT);
    public final static PanoramaUserParameters FINSTERAARHORN = new PanoramaUserParameters(
            81260, 465374, 4300, 205, 20, MAX_DISTANCE, WIDTH, HEIGHT,
            SUPER_SAMPLING_EXPONENT);
    public final static PanoramaUserParameters TOUR_DE_SAUVABELIN = new PanoramaUserParameters(
            66385, 465353, 700, 135, 100, MAX_DISTANCE, WIDTH, HEIGHT,
            SUPER_SAMPLING_EXPONENT);
    public final static PanoramaUserParameters PLAGE_DU_PELICAN = new PanoramaUserParameters(
            65728, 465132, 380, 135, 60, MAX_DISTANCE, WIDTH, HEIGHT,
            SUPER_SAMPLING_EXPONENT);
}