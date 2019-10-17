package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

public class InputProcessor {

    char command1;
    private String seedString = "";
    long seedNumber = 0;
    char command2;
    InputType type;
    Processor processor;

    public InputProcessor(InputType type, String inputString) {

        this.type = type;

        switch (type) {
            case STRING:
                // process string
                processor = new StringProcessor(inputString);
                break;
            case KEYBOARD:
                // process keyboard
                processor = new KeyboardProcessor();
                break;
        }

    }


    private class StringProcessor implements Processor {

        private String moves = "";
        int index = 0;

        public StringProcessor(String inputString) {

            int index = 1;

            if (inputString.charAt(0) == ':' && inputString.charAt(1) == 'Q') {
                command1 = 'Q';
                index = 2;
            } else {
                command1 = inputString.charAt(0);
            }

            while(inputString.charAt(index) != 'S') {
                seedString += inputString.charAt(index);
                index += 1;
            }

            command2 = inputString.charAt(index);
            index += 1;

            for (int i = 0; i < seedString.length(); i++) {
                int nextDigit = Character.getNumericValue(seedString.charAt(i));
                seedNumber += Math.pow(10, seedString.length() - 1 - i) * nextDigit;
            }

            for (int i = index; i < inputString.length(); i++) {
                moves += inputString.charAt(i);
            }

        }

        public char getNextKey() {
            int temp = index;
            index += 1;
            return moves.charAt(temp);
        }

        public boolean hasNextKey() {
            return index < moves.length();
        }

    }

    private class KeyboardProcessor implements Processor {

        private static final boolean PRINT_MOVES = true;

        public KeyboardProcessor() {

            char first = getNextKey();
            while (first == ':') {
                char second = getNextKey();
                if (second == 'Q') {
                    first = 'Q';
                } else {
                    first = second;
                }
            }

            command1 = first;

            if (command1 != 'L') {
                char next = getNextKey();
                while (next != 'S') {
                    seedString += next;
                    next = getNextKey();
                }

                command2 = next;

                for (int i = 0; i < seedString.length(); i++) {
                    int nextDigit = Character.getNumericValue(seedString.charAt(i));
                    seedNumber += Math.pow(10, seedString.length() - 1 - i) * nextDigit;
                }
            } else {
                command2 = 'S';
            }


        }

        public char getNextKey() {

            while (true) {
                if (StdDraw.hasNextKeyTyped()) {
                    char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                    if (PRINT_MOVES) {
                        System.out.print(c);
                    }
                    return c;
                } else {
                    return 0;
                }
            }

        }

        public boolean hasNextKey() {
            return true;
        }

    }

}
