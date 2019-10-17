package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class SSWorld {

    static final int WORLDWIDTH = 90;
    static final int WORLDHEIGHT = 45;
    TETile[][] ssWorld;
    HashSet<Room> rooms;
    HashMap<Position, Direction> openings;
    Random seed;


    public SSWorld(long userSeed) {

        this.seed = new Random(userSeed);
        this.ssWorld = new TETile[WORLDWIDTH][WORLDHEIGHT];
        this.rooms = new HashSet<>();
        this.openings = new HashMap<>();

        for (int i = 0; i < WORLDWIDTH; i++) {
            for (int j = 0; j < WORLDHEIGHT; j++) {
                ssWorld[i][j] = Tileset.NOTHING;
            }
        }

        boolean madeRoom = false;
        while (!madeRoom) {
            int xCoord = seed.nextInt(WORLDWIDTH);
            int yCoord = seed.nextInt(WORLDHEIGHT);
            Position potential = new Position(xCoord, yCoord);
            madeRoom = makeRoom(0, potential, pickRandomDirection());
        }

    }

    public Position randomPosition() {

        int numRooms = rooms.size();
        int roomNum = seed.nextInt(numRooms);
        int index = 0;
        for (Room room : rooms) {
            if (index == roomNum) {
                int chosenWidth = room.bottomLeft.x + 1 + seed.nextInt(room.width - 2);
                int chosenHeight = room.bottomLeft.y + 1 + seed.nextInt(room.height - 2);
                return new Position(chosenWidth, chosenHeight);
            }

            index += 1;
        }

        return new Position(1, 1);

    }

    // chooses a random direction
    private Direction pickRandomDirection() {

        int dirNum = seed.nextInt(4);
        switch (dirNum) {
            case 0:
                return Direction.BOTTOM;
            case 1:
                return Direction.LEFT;
            case 2:
                return Direction.TOP;
            case 3:
                return Direction.RIGHT;
            default:
                return Direction.BOTTOM;
        }

    }

    // makes a room given an opening to start with (randomly decides which edge it belongs to,
    // figures out where bottom left is, etc.)
    // direction is direction of hallway
    private boolean makeRoom(int roomNumber, Position opening, Direction direction) {

        boolean canMakeRoom = false;
        int counter = 0;

        int width = 0;
        int height = 0;
        Position bottomLeft = opening;

        while (!canMakeRoom && counter < 10) {

            width = seed.nextInt(8) + 6;
            height = seed.nextInt(8) + 6;
            bottomLeft = chooseBottomLeft(opening, direction, width, height);

            canMakeRoom = checkRoomFits(width, height, bottomLeft);

            counter += 1;

        }

        if (canMakeRoom) {
            Room newRoom = new Room(roomNumber, width, height, bottomLeft);
            newRoom.drawWalls();
            newRoom.fillRoom();
            newRoom.createOpenings();
            rooms.add(newRoom);
            return true;
        }
        return false;

    }

    private Position returnBottomLeft(Position opening, Direction side, int width, int height) {

        switch (side) {
            case BOTTOM:
                // choose bottom left corresponding to width
                int openPlaceB = seed.nextInt(width - 2) + 1;
                int bottomLeftXB = opening.x - openPlaceB;
                int bottomLeftYB = opening.y;
                return new Position(bottomLeftXB, bottomLeftYB);
            case LEFT:
                // choose bottom left corresponding to height
                int openPlaceL = seed.nextInt(height - 2) + 1;
                int bottomLeftXL = opening.x;
                int bottomLeftYL = opening.y - openPlaceL;
                return new Position(bottomLeftXL, bottomLeftYL);
            case TOP:
                // choose bottom left corresponding to width
                int openPlaceT = seed.nextInt(width - 2) + 1;
                int bottomLeftXT = opening.x - openPlaceT;
                int bottomLeftYT = opening.y - (height - 1);
                return new Position(bottomLeftXT, bottomLeftYT);
            case RIGHT:
                // choose bottom left corresponding to height
                int openPlaceR = seed.nextInt(height - 2) + 1;
                int bottomLeftXR = opening.x - (width - 1);
                int bottomLeftYR = opening.y - openPlaceR;
                return new Position(bottomLeftXR, bottomLeftYR);
            default:
                return returnBottomLeft(opening, Direction.RIGHT, width, height);
        }

    }

    private Position chooseBottomLeft(Position opening, Direction direction, int width, int height) {

        switch (direction) {

            case BOTTOM:
                // make room on bottom, left, or right
                Direction randomOpeningB = Direction.TOP;
                while (randomOpeningB == Direction.TOP) {
                    randomOpeningB = pickRandomDirection();
                }
                return returnBottomLeft(opening, randomOpeningB, width, height);
            case LEFT:
                // make room on left, top, or bottom
                Direction randomOpeningL = Direction.RIGHT;
                while (randomOpeningL == Direction.RIGHT) {
                    randomOpeningL = pickRandomDirection();
                }
                return returnBottomLeft(opening, randomOpeningL, width, height);
            case TOP:
                // make room on top, left, or right
                Direction randomOpeningT = Direction.BOTTOM;
                while (randomOpeningT == Direction.BOTTOM) {
                    randomOpeningT = pickRandomDirection();
                }
                return returnBottomLeft(opening, randomOpeningT, width, height);
            case RIGHT:
                // make room on right, top, or bottom
                Direction randomOpeningR = Direction.LEFT;
                while (randomOpeningR == Direction.LEFT) {
                    randomOpeningR = pickRandomDirection();
                }
                return returnBottomLeft(opening, randomOpeningR, width, height);
            default:
                return chooseBottomLeft(opening, Direction.BOTTOM, width, height);
        }

    }

    private boolean checkRoomFits(int width, int height, Position bottomLeft) {

        if (height < 4 || width < 4) {
            return false;
        }

        for (int i = bottomLeft.x; i < bottomLeft.x + width; i++) {
            for (int j = bottomLeft.y; j < bottomLeft.y + height; j++) {
                if (i >= (WORLDWIDTH - 0) || i < 0 || j >= (WORLDHEIGHT - 0) || j < 0) {
                    return false;
                }
                if (!ssWorld[i][j].equals(Tileset.NOTHING)) {
                    return false;
                }
            }
        }

        return true;

    }

    public TETile[][] returnTileArray() {
        return ssWorld;
    }


    private void makeHallway(Position start, Direction neededDirection) {
        if (!ssWorld[start.x][start.y].equals(Tileset.FLOOR)) {
            return;
        }
        ssWorld[start.x][start.y] = Tileset.FLOOR;
        if (neededDirection.equals(Direction.RIGHT)) {
            makeHallwayRight(start);
        } else if (neededDirection.equals(Direction.LEFT)) {
            makeHallwayLeft(start);
        } else if (neededDirection.equals(Direction.BOTTOM)) {
            makeHallwayBottom(start);
        } else {
            makeHallwayTop(start);
        }

    }


    private void makeHallwayRight(Position start) {
        // using seed to decide hallway width.
        int hallwayWidth = seed.nextInt(6) + 6;
        makeStraightHallwayRight(start, hallwayWidth);
    }

    // function that makes a straight hallway towards the right
    private void makeStraightHallwayRight(Position start, int width) {
        int xCoord = start.x;
        int yCoord = start.y;
        int helper = 0;
        // for loop to start making the hallway based on the given parameters
        for (int i = 1; i < width + 1; i += 1) {
            helper += 1;
            // check to make sure we are still in the bounds of our amazing world
            if ((xCoord + i) < WORLDWIDTH - 2) { // need to confirm if not <=
                // case 1: tile to the right of opening and 2 tiles above it are all nothing
                if (ssWorld[xCoord + i][yCoord].equals(Tileset.FLOOR)) {
                    return;
                }
                if (ssWorld[xCoord + i][yCoord].equals(Tileset.NOTHING)
                        && ssWorld[xCoord + i][yCoord + 1].equals(Tileset.NOTHING)
                        && ssWorld[xCoord + i][yCoord - 1].equals(Tileset.NOTHING)) {
                    ssWorld[xCoord + i][yCoord] = Tileset.FLOOR;
                    ssWorld[xCoord + i][yCoord + 1] = Tileset.WALL;
                    ssWorld[xCoord + i][yCoord - 1] = Tileset.WALL;
                } else if (ssWorld[xCoord + i][yCoord].equals(Tileset.WALL)
                        && ssWorld[xCoord + i][yCoord + 1].equals(Tileset.WALL)
                        && ssWorld[xCoord + i][yCoord - 1].equals(Tileset.WALL)) {
                    ssWorld[xCoord + i][yCoord] = Tileset.FLOOR;
                    break; // connecting and stopping
                } else if (ssWorld[xCoord + i][yCoord].equals(Tileset.NOTHING)) {
                    // case 3: in the case the tile on the right is nothing
                    ssWorld[xCoord + i][yCoord] = Tileset.FLOOR;
                    ssWorld[xCoord + i][yCoord + 1] = Tileset.WALL;
                    ssWorld[xCoord + i][yCoord - 1] = Tileset.WALL;
                } else {
                    ssWorld[xCoord + i][yCoord] = Tileset.FLOOR;
                    if (ssWorld[xCoord + i][yCoord + 1].equals(Tileset.WALL)) {
                        ssWorld[xCoord + i][yCoord + 1] = Tileset.FLOOR;
                        ssWorld[xCoord + i][yCoord - 1] = Tileset.WALL;
                        break;
                    }
                    if (ssWorld[xCoord + i][yCoord - 1].equals(Tileset.WALL)) {
                        ssWorld[xCoord + i][yCoord - 1] = Tileset.FLOOR;
                        ssWorld[xCoord + i][yCoord + 1] = Tileset.WALL;
                        break;
                    }
                    if (ssWorld[xCoord + i][yCoord + 1].equals(Tileset.NOTHING)) {
                        ssWorld[xCoord + i][yCoord + 1] = Tileset.WALL;
                        break;
                    }
                    if (ssWorld[xCoord + i][yCoord - 1].equals(Tileset.NOTHING)) {
                        ssWorld[xCoord + i][yCoord - 1] = Tileset.WALL;
                        break;
                    }
                }
            } else {
                ssWorld[xCoord + i][yCoord] = Tileset.WALL;
                break;
            }
        }

        if (helper == width) {
            boolean made = makeRoom(rooms.size(),
                    new Position(xCoord + width + 1, yCoord), Direction.LEFT);
            if (!made) {
                ssWorld[xCoord + width][yCoord] = Tileset.WALL;
            } else {
                ssWorld[xCoord + width + 1][yCoord] = Tileset.FLOOR;
            }
        }
    }

    private void makeHallwayLeft(Position start) {
        int hallwayWidth = seed.nextInt(6) + 6;
        makeStraightHallwayLeft(start, hallwayWidth);
    }

    // function that makes a straight hallway towards the left
    private void makeStraightHallwayLeft(Position start, int width) {
        int xCoord = start.x;
        int yCoord = start.y;
        // for loop to start making the hallway based on the given parameters
        int helper = 0;
        for (int i = 1; i < width + 1; i += 1) {
            helper += 1;
            // check to make sure we are still in the bounds of our amazing world
            if ((xCoord - i) >= 2) { // need to confirm if not >
                if (ssWorld[xCoord - i][yCoord].equals(Tileset.FLOOR)) {
                    return;
                }
                if (ssWorld[xCoord - i][yCoord].equals(Tileset.NOTHING)
                        && ssWorld[xCoord - i][yCoord + 1].equals(Tileset.NOTHING)
                        && ssWorld[xCoord - i][yCoord - 1].equals(Tileset.NOTHING)) {
                    ssWorld[xCoord - i][yCoord] = Tileset.FLOOR;
                    ssWorld[xCoord - i][yCoord + 1] = Tileset.WALL;
                    ssWorld[xCoord - i][yCoord - 1] = Tileset.WALL;
                } else if (ssWorld[xCoord - i][yCoord].equals(Tileset.WALL)
                        && ssWorld[xCoord - i][yCoord + 1].equals(Tileset.WALL)
                        && ssWorld[xCoord - i][yCoord - 1].equals(Tileset.WALL)) {
                    ssWorld[xCoord - i][yCoord] = Tileset.FLOOR;
                    break;
                } else if (ssWorld[xCoord - i][yCoord].equals(Tileset.NOTHING)) {
                    ssWorld[xCoord - i][yCoord] = Tileset.FLOOR;
                    ssWorld[xCoord - i][yCoord + 1] = Tileset.WALL;
                    ssWorld[xCoord - i][yCoord - 1] = Tileset.WALL;
                } else {
                    ssWorld[xCoord - i][yCoord] = Tileset.FLOOR;
                    if (ssWorld[xCoord - i][yCoord + 1].equals(Tileset.WALL)) {
                        ssWorld[xCoord - i][yCoord + 1] = Tileset.FLOOR;
                        ssWorld[xCoord - i][yCoord - 1] = Tileset.WALL;
                        break;
                    }
                    if (ssWorld[xCoord - i][yCoord - 1].equals(Tileset.WALL)) {
                        ssWorld[xCoord - i][yCoord - 1] = Tileset.FLOOR;
                        ssWorld[xCoord - i][yCoord + 1] = Tileset.WALL;
                        break;
                    }
                    if (ssWorld[xCoord - i][yCoord + 1].equals(Tileset.NOTHING)) {
                        ssWorld[xCoord - i][yCoord + 1] = Tileset.WALL;
                        break;
                    }
                    if (ssWorld[xCoord - i][yCoord - 1].equals(Tileset.NOTHING)) {
                        ssWorld[xCoord - i][yCoord - 1] = Tileset.WALL;
                        break;
                    }
                }
            } else {
                ssWorld[xCoord - i][yCoord] = Tileset.WALL;
                break;
            }
        }

        if (helper == width) {
            boolean made = makeRoom(rooms.size(),
                    new Position(xCoord - width - 1, yCoord), Direction.RIGHT);
            if (!made) {
                ssWorld[xCoord - width][yCoord] = Tileset.WALL;
            } else {
                ssWorld[xCoord - width - 1][yCoord] = Tileset.FLOOR;
            }
        }
    }

    private void makeHallwayBottom(Position start) {
        int hallwayHeight = seed.nextInt(6) + 6;
        makeStraightHallwayBottom(start, hallwayHeight);
    }

    // function that makes a straight hallway towards the bottom
    private void makeStraightHallwayBottom(Position start, int height) {
        int xCoord = start.x;
        int yCoord = start.y;
        // for loop to start making the hallway based on the given parameters
        int helper = 0;
        for (int i = 1; i < height + 1; i += 1) {
            helper += 1;
            // check to make sure we are still in the bounds of our amazing world
            if ((yCoord - i) >= 2) { // need to confirm if not >
                if (ssWorld[xCoord][yCoord - i].equals(Tileset.FLOOR)) {
                    return;
                }
                if (ssWorld[xCoord][yCoord - i].equals(Tileset.NOTHING)
                        && ssWorld[xCoord + 1][yCoord - i].equals(Tileset.NOTHING)
                        && ssWorld[xCoord - 1][yCoord - i].equals(Tileset.NOTHING)) {
                    ssWorld[xCoord][yCoord - i] = Tileset.FLOOR;
                    ssWorld[xCoord + 1][yCoord - i] = Tileset.WALL;
                    ssWorld[xCoord - 1][yCoord - i] = Tileset.WALL;
                } else if (ssWorld[xCoord][yCoord - i].equals(Tileset.WALL)
                        && ssWorld[xCoord + 1][yCoord - i].equals(Tileset.WALL)
                        && ssWorld[xCoord - 1][yCoord - i].equals(Tileset.WALL)) {
                    ssWorld[xCoord][yCoord - i] = Tileset.FLOOR;
                    break;
                } else if (ssWorld[xCoord][yCoord - i].equals(Tileset.NOTHING)) {
                    ssWorld[xCoord][yCoord - i] = Tileset.FLOOR;
                    ssWorld[xCoord + 1][yCoord - i] = Tileset.WALL;
                    ssWorld[xCoord - 1][yCoord - i] = Tileset.WALL;
                } else {
                    ssWorld[xCoord][yCoord - i] = Tileset.FLOOR;
                    if (ssWorld[xCoord + 1][yCoord - i].equals(Tileset.WALL)) {
                        ssWorld[xCoord + 1][yCoord - i] = Tileset.FLOOR;
                        ssWorld[xCoord - 1][yCoord - i] = Tileset.WALL;
                        break;
                    }
                    if (ssWorld[xCoord - 1][yCoord - i].equals(Tileset.WALL)) {
                        ssWorld[xCoord - 1][yCoord - i] = Tileset.FLOOR;
                        ssWorld[xCoord + 1][yCoord - i] = Tileset.WALL;
                        break;
                    }
                    if (ssWorld[xCoord + 1][yCoord - i].equals(Tileset.NOTHING)) {
                        ssWorld[xCoord + 1][yCoord - i] = Tileset.WALL;
                        break;
                    }
                    if (ssWorld[xCoord - 1][yCoord - i].equals(Tileset.NOTHING)) {
                        ssWorld[xCoord - 1][yCoord - i] = Tileset.WALL;
                        break;
                    }
                }
            } else {
                ssWorld[xCoord][yCoord - i] = Tileset.WALL;
                break;
            }
        }

        if (helper == height) {
            boolean made = makeRoom(rooms.size(),
                    new Position(xCoord, yCoord - height - 1), Direction.TOP);
            if (!made) {
                ssWorld[xCoord][yCoord - height] = Tileset.WALL;
            } else {
                ssWorld[xCoord][yCoord - height - 1] = Tileset.FLOOR;
            }
        }
    }


    private void makeHallwayTop(Position start) {
        int hallwayHeight = seed.nextInt(6) + 6;
        makeStraightHallwayTop(start, hallwayHeight);
    }

    // function that makes a straight hallway towards the top
    private void makeStraightHallwayTop(Position start, int height) {
        int xCoord = start.x;
        int yCoord = start.y;
        // for loop to start making the hallway based on the given parameters
        int helper = 0;
        for (int i = 1; i < height + 1; i += 1) {
            helper += 1;
            // check to make sure we are still in the bounds of our amazing world
            if ((yCoord + i) < WORLDHEIGHT - 2) { // need to confirm if not <=
                if (ssWorld[xCoord][yCoord + i].equals(Tileset.FLOOR)) {
                    return;
                }
                if (ssWorld[xCoord][yCoord + i].equals(Tileset.NOTHING)
                        && ssWorld[xCoord + 1][yCoord + i].equals(Tileset.NOTHING)
                        && ssWorld[xCoord - 1][yCoord + i].equals(Tileset.NOTHING)) {
                    ssWorld[xCoord][yCoord + i] = Tileset.FLOOR;
                    ssWorld[xCoord + 1][yCoord + i] = Tileset.WALL;
                    ssWorld[xCoord - 1][yCoord + i] = Tileset.WALL;
                } else if (ssWorld[xCoord][yCoord + i].equals(Tileset.WALL)
                        && ssWorld[xCoord + 1][yCoord + i].equals(Tileset.WALL)
                        && ssWorld[xCoord - 1][yCoord + i].equals(Tileset.WALL)) {
                    ssWorld[xCoord][yCoord + i] = Tileset.FLOOR;
                    break;
                } else if (ssWorld[xCoord][yCoord + i].equals(Tileset.NOTHING)) {
                    ssWorld[xCoord][yCoord + i] = Tileset.FLOOR;
                    ssWorld[xCoord + 1][yCoord + i] = Tileset.WALL;
                    ssWorld[xCoord - 1][yCoord + i] = Tileset.WALL;
                } else {
                    ssWorld[xCoord][yCoord + i] = Tileset.FLOOR;
                    if (ssWorld[xCoord + 1][yCoord + i].equals(Tileset.WALL)) {
                        ssWorld[xCoord + 1][yCoord + i] = Tileset.FLOOR;
                        ssWorld[xCoord - 1][yCoord + i] = Tileset.WALL;
                        break;
                    }
                    if (ssWorld[xCoord - 1][yCoord + i].equals(Tileset.WALL)) {
                        ssWorld[xCoord - 1][yCoord + i] = Tileset.FLOOR;
                        ssWorld[xCoord + 1][yCoord + i] = Tileset.WALL;
                        break;
                    }
                    if (ssWorld[xCoord + 1][yCoord + i].equals(Tileset.NOTHING)) {
                        ssWorld[xCoord + 1][yCoord + i] = Tileset.WALL;
                        break;
                    }
                    if (ssWorld[xCoord - 1][yCoord + i].equals(Tileset.NOTHING)) {
                        ssWorld[xCoord - 1][yCoord + i] = Tileset.WALL;
                        break;
                    }
                }
            } else {
                ssWorld[xCoord][yCoord + i] = Tileset.WALL;
                break;
            }
        }

        if (helper == height) {
            boolean made = makeRoom(rooms.size(),
                    new Position(xCoord, yCoord + height + 1), Direction.BOTTOM);
            if (!made) {
                ssWorld[xCoord][yCoord + height] = Tileset.WALL;
            } else {
                ssWorld[xCoord][yCoord + height + 1] = Tileset.FLOOR;
            }
        }

    }


    private class Room {

        private int height;
        private int width;
        private int numberOfOpenings;
        private Position bottomLeft;

        Room(int roomNumber, int width, int height, Position bottomLeft) {

            this.height = height;
            this.width = width;
            this.bottomLeft = bottomLeft;
            this.numberOfOpenings = seed.nextInt(3) + 5; // create up to 5 more openings per room

        }

        private void drawWalls() {

            for (int i = 0; i < width; i++) {
                ssWorld[bottomLeft.x + i][bottomLeft.y] = Tileset.WALL;
                ssWorld[bottomLeft.x + i][bottomLeft.y + (height - 1)] = Tileset.WALL;
            }

            for (int j = 0; j < height; j++) {
                ssWorld[bottomLeft.x][bottomLeft.y + j] = Tileset.WALL;
                ssWorld[bottomLeft.x + (width - 1)][bottomLeft.y + j] = Tileset.WALL;
            }

        }

        private void createOpenings() {
            int bottomCount = 0;
            int leftCount = 0;
            int rightCount = 0;
            int topCount = 0;
            for (int k = 0; k < numberOfOpenings; k = k + 1) {
                Direction edge = pickRandomDirection();
                switch (edge) {
                    case BOTTOM:
                        // create opening on bottom
                        int xCoord0 = seed.nextInt(width - 2) + bottomLeft.x + 1;
                        if (!ssWorld[xCoord0][bottomLeft.y].equals(Tileset.FLOOR)
                                && !ssWorld[xCoord0 + 1][bottomLeft.y].equals(Tileset.FLOOR)
                                && !ssWorld[xCoord0 - 1][bottomLeft.y].equals(Tileset.FLOOR)
                                && bottomLeft.y > 1) {
                            ssWorld[xCoord0][bottomLeft.y] = Tileset.FLOOR;
                            makeHallway(new Position(xCoord0, bottomLeft.y), Direction.BOTTOM);
                        } else {
                            bottomCount += 1;
                            if (bottomCount > 10) {
                                continue;
                            } else {
                                k = k - 1;
                            }
                        }
                        break;
                    case LEFT:
                        int yCoord0 = seed.nextInt(height - 2) + bottomLeft.y + 1;
                        if (!ssWorld[bottomLeft.x][yCoord0].equals(Tileset.FLOOR)
                                && !ssWorld[bottomLeft.x][yCoord0 + 1].equals(Tileset.FLOOR)
                                && !ssWorld[bottomLeft.x][yCoord0 - 1].equals(Tileset.FLOOR)
                                && bottomLeft.x > 1) {
                            ssWorld[bottomLeft.x][yCoord0] = Tileset.FLOOR;
                            makeHallway(new Position(bottomLeft.x, yCoord0), Direction.LEFT);
                        } else {
                            leftCount += 1;
                            if (leftCount > 10) {
                                continue;
                            } else {
                                k = k - 1;
                            }
                        }
                        break;
                    case TOP:
                        int xCoord1 = seed.nextInt(width - 2) + bottomLeft.x + 1;
                        if (!ssWorld[xCoord1][bottomLeft.y + (height - 1)].equals(Tileset.FLOOR)
                                && !ssWorld[xCoord1 + 1][bottomLeft.y + (height - 1)].equals(Tileset.FLOOR)
                                && !ssWorld[xCoord1 - 1][bottomLeft.y + (height - 1)].equals(Tileset.FLOOR)
                                && (WORLDHEIGHT - (bottomLeft.y + height) > 1)) {
                            ssWorld[xCoord1][bottomLeft.y + (height - 1)] = Tileset.FLOOR;
                            makeHallway(new Position(xCoord1, bottomLeft.y + (height - 1)),
                                    Direction.TOP);
                        } else {
                            topCount += 1;
                            if (topCount > 10) {
                                continue;
                            } else {
                                k = k - 1;
                            }
                        }
                        break;
                    case RIGHT:
                        int yCoord1 = seed.nextInt(height - 2) + bottomLeft.y + 1;
                        if (!ssWorld[bottomLeft.x + (width - 1)][yCoord1].equals(Tileset.FLOOR)
                                && !ssWorld[bottomLeft.x + (width - 1)][yCoord1 + 1].equals(Tileset.FLOOR)
                                && !ssWorld[bottomLeft.x + (width - 1)][yCoord1 - 1].equals(Tileset.FLOOR)
                                && (WORLDWIDTH - (bottomLeft.x + width) > 1)) {
                            ssWorld[bottomLeft.x + (width - 1)][yCoord1] = Tileset.FLOOR;
                            makeHallway(new Position(bottomLeft.x + (width - 1), yCoord1),
                                    Direction.RIGHT);
                        } else {
                            rightCount += 1;
                            if (rightCount > 10) {
                                continue;
                            } else {
                                k = k - 1;
                            }
                        }
                        break;
                    default:
                        this.createOpenings();
                        break;
                }
            }
        }

        private void fillRoom() {

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (!ssWorld[bottomLeft.x + i][bottomLeft.y + j].equals(Tileset.WALL)) {
                        ssWorld[bottomLeft.x + i][bottomLeft.y + j] = Tileset.FLOOR;
                    }
                }
            }
        }
    }
}
