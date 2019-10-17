package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.HashSet;
//This class controls our 2 players
public class Avatar {

    Position currentPos;
    TETile[][] frame;
    SSWorld world;
    HashSet<TETile> crossableTiles = new HashSet<>();
    TETile avatarTile;

    public Avatar(TETile[][] frame, SSWorld world, TETile avatarTile) {

        this.frame = frame;
        this.world = world;
        this.avatarTile = avatarTile;

        this.currentPos = this.world.randomPosition();
        frame[currentPos.x][currentPos.y] = Tileset.AVATAR;

        crossableTiles.add(Tileset.FLOOR);

    }

    public void moveAvatar(char movement) {

        TETile type;

        switch (movement) {
            case 'W':
                // move up
                type = frame[currentPos.x][currentPos.y + 1];
                if (crossableTiles.contains(type)) {
                    frame[currentPos.x][currentPos.y + 1] = avatarTile;
                    frame[currentPos.x][currentPos.y] = type; //COMMENTED FOR TESTING ONLY!!
                    currentPos = new Position(currentPos.x, currentPos.y + 1);
                }
                break;
            case 'D':
                // move right
                type = frame[currentPos.x + 1][currentPos.y];
                if (crossableTiles.contains(type)) {
                    frame[currentPos.x + 1][currentPos.y] = avatarTile;
                    frame[currentPos.x][currentPos.y] = type; //COMMENTED FOR TESTING ONLY!!
                    currentPos = new Position(currentPos.x + 1, currentPos.y);
                }
                break;
            case 'S':
                // move down
                type = frame[currentPos.x][currentPos.y - 1];
                if (crossableTiles.contains(type)) {
                    frame[currentPos.x][currentPos.y - 1] = avatarTile;
                    frame[currentPos.x][currentPos.y] = type; //COMMENTED FOR TESTING ONLY!!
                    currentPos = new Position(currentPos.x, currentPos.y - 1);
                }
                break;
            case 'A':
                // move left
                type = frame[currentPos.x - 1][currentPos.y];
                if (crossableTiles.contains(type)) {
                    frame[currentPos.x - 1][currentPos.y] = avatarTile;
                    frame[currentPos.x][currentPos.y] = type; //COMMENTED FOR TESTING ONLY!!
                    currentPos = new Position(currentPos.x - 1, currentPos.y);
                }
                break;
            default:
                break;
        }

    }

    private boolean checkValidTile(int x, int y) {

        return x >= 0 && y >= 0 && x < world.WORLDWIDTH && y < world.WORLDHEIGHT;

    }

}
