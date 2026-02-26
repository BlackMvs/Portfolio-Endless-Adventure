package gameExtended2D;

import game2D.TileMap;
import settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * An extended version of {@link TileMap} that supports {@link TileExtended} tiles,
 * automatic tile scaling, image assignment based on numeric codes, and logic
 * for assigning tile types (e.g., GROUND, PLATFORM, EMPTY).
 * <p>
 * It reads map files and constructs a tile grid with scaled images and gameplay types.
 */
public class TileMapExtended extends TileMap {

    protected TileExtended [][] tmap;
    private int platformEnds = 88;
    private int groundEnds = 181;

    /**
     * Constructs a TileMapExtended with specified platform and ground code thresholds.
     *
     * @param platformEnds the highest tile code considered a platform tile
     * @param groundEnds   the highest tile code considered a ground tile
     */
    public TileMapExtended(int platformEnds, int groundEnds){
        this.platformEnds = platformEnds;
        this.groundEnds = groundEnds;
    }//end constructor


    @Override
    public boolean loadMap(String folder, String mapfile) {
        // Create a full path to the tile map by sticking the folder and mapfile together
        String path = folder + "/mapLevels/" + mapfile;
        int row=0;

        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            String line="";
            String trimmed="";
            String [] vals;

            // First we need to clear out the old image map
            this.imagemap.clear();

            // Read the first line of the tile map to find out
            // the relevant dimensions of the map plus the tiles
            line = in.readLine();
            vals = line.split(" ");

            // Check that we read 4 values
            if (vals.length != 4) {
                System.err.println("Incorrect number of parameters in the TileMap header:" + vals.length);
                in.close();
                return false;
            }//end if

            // Read in the map dimensions
            this.mapWidth = Integer.parseInt(vals[0]);
            this.mapHeight = Integer.parseInt(vals[1]);

            //the original sizes
            int tileWidthOriginal = Integer.parseInt(vals[2]);
            int tileHeightOriginal = Integer.parseInt(vals[3]);

            //TODO add a less hardcoded way to scale the tiles to 32x32
            //hardcoded, we want the tiles to always stick to 32x32
            if (tileWidthOriginal == 16 && tileHeightOriginal == 16){
                //get the default tilesize that we want for the game
                tileHeightOriginal = Settings.getDefaultTileSize();
                tileWidthOriginal = Settings.getDefaultTileSize();
            }//end if

            this.tileWidth =  tileWidthOriginal * Settings.getTileScale();
            this.tileHeight = tileHeightOriginal * Settings.getTileScale();

            // Now look for the character assignments
            while ((line = in.readLine()) != null) {
                trimmed = line.trim();
                // Skip the current line if it's a comment
                if (trimmed.startsWith("//")) continue;
                // Break out of the loop if we find the map
                if (trimmed.startsWith("#map")) break;
                // Look for a character to image map
                if (trimmed.charAt(0) == '#') {
                    // Extract the character
                    String ch = "" + trimmed.charAt(1);
                    // and it's file name
                    String fileName = trimmed.substring(3,trimmed.length());

                    String folderPath = folder + "/tilesImages/" ;
//                    System.out.println("Tile images path is: " + folderPath);
//                    System.out.println("The filename is: " + fileName);
                    Image img  = new ImageIcon(folderPath + "" + fileName).getImage();
                    // Now add this character->image mapping to the map
                    if (img != null){
                        imagemap.put(ch,img);
                    }//end if
                    else{
                        System.err.println("Failed to load image '" + folder + "/" + fileName + "'");
                    }//end else
                }//end if
            }//end while loop

            // Check the map dimensione are at least > 0
            if ((mapWidth > 0) && (mapHeight > 0)) {
                this.tmap = new TileExtended[mapWidth][mapHeight];
            }//end if
            else {
                System.err.println("Incorrect image map dimensions.");
                trimmed = "";
            }//end else

            // Now read in the tile map structure
            if (trimmed.startsWith("#map")) {
                while ((line = in.readLine()) != null) {
                    if (line.trim().startsWith("//")) continue;

                    String[] rowTiles = line.split(",");
                    for (int col = 0; col < mapWidth && col < rowTiles.length; col++) {
                        String tileCode = rowTiles[col].trim(); //store the code of the String that we are currently checking
                        TileExtended tile = new TileExtended(rowTiles[col].trim(), col * tileWidth, row * tileHeight); //create a new tile
                        int code = Integer.parseInt(rowTiles[col].trim());
                        //check if the tile is supposed to be empty or anything else
                        if (code == -1) {
                            tile.setImage(createTransparentImage(tileWidth, tileHeight));
                            tile.setType(TileExtended.TileType.EMPTY);
                        }//end if
                        else {
                            //IMAGE
                            //set the image depending on the number, using this just for the file assignment
                            if ( code < 10){ //if it is under 10 we will need at add two zeros as the pictures have a 3 digit format
//                                tile.setImage(folder + "/tile00" + rowTiles[col].trim() + ".png");
                                tile.setImage(folder + "/tilesImages/" + "/tile00" + rowTiles[col].trim() + ".png", tileWidthOriginal, tileHeightOriginal);
                            }//end if
                            else if (code < 100){ //same for 100 but one zero
//                                tile.setImage(folder + "/tile0" + rowTiles[col].trim() + ".png");
                                tile.setImage(folder + "/tilesImages/" + "/tile0" + rowTiles[col].trim() + ".png", tileWidthOriginal, tileHeightOriginal);
                            }//end else if
                            else { //else add normally without requiring the extra 0
//                                tile.setImage(folder + "/tile" + rowTiles[col].trim() + ".png");
                                tile.setImage(folder + "/tilesImages/" + "/tile" + rowTiles[col].trim() + ".png", tileWidthOriginal, tileHeightOriginal);
                            }//end else

                            //TYPE
                            if(code >= 0 && code < this.platformEnds){
                                tile.setType(TileExtended.TileType.PLATFORM);
//                                System.out.println("Tile [" + col + "][" + row + "] is platform");
                            }//end if
                            else if (code < this.groundEnds){
                                tile.setType(TileExtended.TileType.GROUND);
//                                System.out.println("Tile [" + col + "][" + row + "] is ground");
                            }//end else if
                            else {
                                System.out.println("Error assigning the tile type!");
                            }//end else

                        }//end else
                        tmap[col][row] = tile; //add the tile to the map
                    }//end for loop
                    row++;
                    if (row >= mapHeight) break;
                }//end while loop
            }//end if
            in.close();
        }//end try
        catch (Exception e) {
            System.err.println("Failed to read in tile map '" + path + "':" + e);
            return false;
        }//end catch

        if (row != mapHeight) {
            System.err.println("Map failed to load. Incorrect rows in map");
            return false;
        }//end if
        return true;
    }//end loadMap method

    /**
     * Creates a transparent image with the specified width and height.
     * Used to visually represent empty tiles.
     *
     * @param width  the desired image width
     * @param height the desired image height
     * @return a transparent {@link Image}
     */
    private Image createTransparentImage(int width, int height) {
        BufferedImage transparentImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = transparentImg.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return transparentImg;
    }//end createTransparentImage method

    @Override
    public Image getTileImage(int x, int y){
        if (!valid(x, y)) return null;
        TileExtended t = tmap[x][y];
        if (t == null || t.getCode().equals(".")) return null;
        return t.getImage();  //Fetch image directly from the TileExtended instance
    }//end getTileImage method

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        for (int r=0; r<mapHeight; r++) {
            for (int c=0; c<mapWidth; c++){
                s.append(this.tmap[c][r].getCode() + ",");
            }//end nested for loop
            s.append('\n');
        }//end for loop
        return s.toString();
    }//end toString method

    @Override
    public TileExtended getTile(int x, int y) {
        if (!valid(x,y)) return null;
        return tmap[x][y];
    }//end getTile

}//end class
