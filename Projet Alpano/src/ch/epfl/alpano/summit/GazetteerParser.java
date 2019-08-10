package ch.epfl.alpano.summit;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import ch.epfl.alpano.GeoPoint;

/**
 * This class reads a files that describe the summits of the mountains, for that
 * this class only provides one public method that read the file in a list of
 * {@link Summit}s.
 *
 */
public final class GazetteerParser {

    // private constructor
    private GazetteerParser() {
    }

    /**
     * Read a file in a list of {@link Summit}s.
     * 
     * @param file
     *            a file
     * @return a list of {@link Summit}s
     * @throws IOException
     *             If something is wrong with the content of the file or the
     *             opening of the file fails.
     * @see GazetteerParser#summit for more details
     */
    public static List<Summit> readSummitsFrom(File file) throws IOException {

        List<Summit> list = new ArrayList<Summit>();
        String line = null;

        try (BufferedReader s = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.US_ASCII))) {
            while (null != (line = s.readLine())) {
                list.add(summit(line));
            }
        } catch (FileNotFoundException e) {
            throw new IOException();
        }

        return Collections.unmodifiableList(list);

    }

    /**
     * Reads a line of a file and extracts the data.
     * 
     * @param line
     *            a line of a file as a String
     * @return data in form of a {@link Summit}-object
     * @throws IOException
     *             if the content is not as expected
     */
    private static Summit summit(String line) throws IOException {

        double longitude = 0, latitude = 0;
        int elevation = 0;

        try {
            String longitudeDMS = line.substring(0, 9);
            String latitudeDMS = line.substring(10, 18);

            longitude = angel(longitudeDMS);
            latitude = angel(latitudeDMS);
            elevation = Integer.parseInt(line.substring(19, 24).trim());

            String name = line.substring(36);

            if (name.trim().length() < 1)
                throw new IOException();

            return new Summit(name, new GeoPoint(longitude, latitude),
                    elevation);

        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new IOException();
        }

    }

    /**
     * @param angle
     *            angle in deg:min:sec-format
     * @return angle as simple radian
     * @throws IOException
     *             if the format of the angle is not valid
     */
    private static double angel(String angle) throws IOException {

        String[] dms = angle.split(":");

        if (dms.length != 3)
            throw new IOException();

        int[] dmsInInt = new int[3];

        dmsInInt[0] = Integer.parseInt(dms[0].trim());
        for (int i = 1; i <= 2; i++) {
            dmsInInt[i] = Integer.parseInt(dms[i]);
            if (dmsInInt[i] < 0)
                throw new IOException();
        }
        return Math.toRadians(
                dmsInInt[0] + (dmsInInt[1] + dmsInInt[2] / 60.) / 60.);
        
    }

}
