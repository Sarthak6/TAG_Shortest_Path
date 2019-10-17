package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 90;
    public static final int HEIGHT = 45;



    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {

        drawMenuScreen();
        InputProcessor in = new InputProcessor(InputType.KEYBOARD, "");
        TETile[][] finalWorldFrame = startNewGame(in);

    }

    private void drawMenuScreen() {

        StdDraw.text(0.5, 0.8, "CS61B: THE GAME");
        StdDraw.text(0.5, 0.4, "NEW GAME (N)");
        StdDraw.text(0.5, 0.35, "LOAD PREVIOUS GAME (L)");
        StdDraw.text(0.5, 0.3, "QUIT (Q)");
        StdDraw.text(0.5, 0.1, "AFTER PRESSING N WRITE A SEED # THEN PRESS S");

    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // to-do: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        input = input.toUpperCase();
        InputProcessor in = new InputProcessor(InputType.STRING, input);

        return startNewGame(in);
    }

    private TETile[][] startNewGame(InputProcessor in) {

        SSWorld newWorld = new SSWorld(in.seedNumber);
        TETile[][] finalWorldFrame = newWorld.returnTileArray();
        Avatar av = new Avatar(finalWorldFrame, newWorld, Tileset.AVATAR);

        if (in.type == InputType.KEYBOARD) {
            ter.initialize(WIDTH + 10, HEIGHT + 5, 0, 1);
            ter.renderFrame(finalWorldFrame);
        }

        Processor processor = in.processor;

        while (processor.hasNextKey()) {
            char nextMove = processor.getNextKey();
            if (nextMove == 'Q') {
                // save the game
                // quit the game
                System.exit(0);
            }
            av.moveAvatar(nextMove);
            ter.renderFrame(finalWorldFrame);
        }

        return finalWorldFrame;

    }


}