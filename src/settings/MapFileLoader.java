package settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * MapFileLoader is responsible for scanning a given directory
 * and storing the names of all .txt map files it finds.
 * <p>
 * It provides methods to retrieve the full list of map file names,
 * select individual map names by index, or pick one at random.
 * <p>
 * Example usage:
 * <pre>
 *     MapFileLoader loader = new MapFileLoader("maps");
 *     String firstMap = loader.getMapFileName(0);
 * </pre>
 */
public class MapFileLoader {
    private List<String> mapFileNames;
    private Random random;

    /**
     * Constructs a MapFileLoader and immediately loads all .txt from the folder specified
     *
     * @param folderPath The path to the directory containing map files.
     */
    public MapFileLoader(String folderPath) {
        mapFileNames = new ArrayList<>();
        this.random = new Random();
        loadMapFileNames(folderPath);
    }//end constructor

    /**
     * Scans the given folder for all files ending in ".txt"
     * and adds their names to the internal list.
     *
     * @param folderPath The directory to scan for .txt files.
     */
    private void loadMapFileNames(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files != null) {
            for (File file : files) {
                mapFileNames.add(file.getName());
//                System.out.println("Map file is: " + file);
            }//end for loop
            Collections.sort(mapFileNames);
        }//end if
    }//end loadMapFileNames

    /**
     * Returns a list of all map file names found.
     *
     * @return A List of map file names (e.g. "level1.txt", "cave-map.txt").
     */
    public List<String> getMapFileNames() {
        return mapFileNames;
    }//end getMapFileNames

    /**
     * Returns the map file name at the given index.
     *
     * @param index The index of the desired map name in the list.
     * @return The map file name if the index is valid, or null if out of bounds.
     */
    public String getMapFileName(int index) {
        if (index >= 0 && index < mapFileNames.size()) {
            return mapFileNames.get(index);
        }//end if
        return null;
    }//end getMapFileName

    /**
     * Returns a random map file name from the list.
     *
     * @return A randomly chosen map file name, or null if the list is empty.
     */
    public String getRandomMapFileName() {
        if (mapFileNames.isEmpty()) {
            return null;
        }//end if
        int index = random.nextInt(mapFileNames.size());
        if (index==0){
            index += 1;
        }//end if
        return mapFileNames.get(index);
    }//end getRandomMapFileName

}//end class
