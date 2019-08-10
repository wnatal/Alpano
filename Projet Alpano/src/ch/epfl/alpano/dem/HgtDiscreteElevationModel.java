package ch.epfl.alpano.dem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel.MapMode;

import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

import static ch.epfl.alpano.Preconditions.checkArgument;
import static ch.epfl.alpano.dem.DiscreteElevationModel.sampleIndex;

/**
 * This class reads a file an provides its informations as a discrete elevation
 * model.
 * 
 * @author Natal Willisch (262092)
 *
 */
public final class HgtDiscreteElevationModel implements DiscreteElevationModel {

    private ShortBuffer b;
    private final Interval2D extent;
    private final int pointOfRefX;
    private final int pointOfRefY;
    private final int rowSize;
    
    private static final int SIZE = 25934402; //size of a valid file

    /**
     * The constructor takes a file-name as argument and checks the name and the
     * file about their validity and finally loads it in the memory to allow
     * access to the file as discrete elevation model.
     * 
     * @param file
     *            file-name
     * @throws IllegalArgumentException
     *             if anything is wrong with the file: name, size or
     *             accessibility
     */
    public HgtDiscreteElevationModel(File file) {
        
        String s = file.getName();
        
        checkArgument(s.length() == 11);
        
        char part1 = s.charAt(0);
        char part3 = s.charAt(3);
        double part2 = -1, part4 = -1;
        
        try {
            part2 = Integer.parseInt(s.substring(1, 3));
            part4 = Integer.parseInt(s.substring(4, 7));

        } catch (NumberFormatException e) {
            checkArgument(false);
        }
        
        String part5 = s.substring(7, 11);

        checkArgument(part1 == 'N' || part1 == 'S', "invalid file-name");
        checkArgument(part3 == 'E' || part3 == 'W', "invalid file-name");
        checkArgument(part2 >= 0, "invalid file-name");
        checkArgument(part4 >= 0, "invalid file-name");
        checkArgument(part5.equals(".hgt"), "invalid file-type");
        checkArgument(file.length() == SIZE, "invalid file-size");

        try (FileInputStream stream = new FileInputStream(file)) {
            b = stream.getChannel().map(MapMode.READ_ONLY, 0, file.length())
                    .asShortBuffer();
        } catch (IOException e) {
            checkArgument(false, "file not readable");
        }
        extent = new Interval2D(
                new Interval1D((int) (sampleIndex(Math.toRadians(part4)) + 0.5),
                        (int) (sampleIndex(Math.toRadians(part4 + 1)) + 0.5)),
                new Interval1D((int) (sampleIndex(Math.toRadians(part2)) + 0.5),
                        (int) (sampleIndex(Math.toRadians(part2 + 1)) + 0.5)));
        pointOfRefY = extent.iY().includedTo();
        pointOfRefX = extent.iX().includedFrom();
        rowSize = extent.iX().size();
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() {
        
        b = null;
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.alpano.dem.DiscreteElevationModel#extent()
     */
    @Override
    public Interval2D extent() {
        
        return extent;
        
    }

    /**
     * @see ch.epfl.alpano.dem.DiscreteElevationModel#elevationSample(int, int)
     * @throws IllegalArgumentException
     *             if the index doesn't correspond to a valid sample
     */
    @Override
    public double elevationSample(int x, int y) {
        
        checkArgument(extent.contains(x, y),
                "not a valid sample index");
        return b.get(x - pointOfRefX + rowSize * (pointOfRefY - y));
        
    }

}
