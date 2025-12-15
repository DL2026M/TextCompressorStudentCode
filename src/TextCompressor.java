/******************************************************************************
 *  Compilation:  javac TextCompressor.java
 *  Execution:    java TextCompressor - < input.txt   (compress)
 *  Execution:    java TextCompressor + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   abra.txt
 *                jabberwocky.txt
 *                shakespeare.txt
 *                virus.txt
 *
 *  % java DumpBinary 0 < abra.txt
 *  136 bits
 *
 *  % java TextCompressor - < abra.txt | java DumpBinary 0
 *  104 bits    (when using 8-bit codes)
 *
 *  % java DumpBinary 0 < alice.txt
 *  1104064 bits
 *  % java TextCompressor - < alice.txt | java DumpBinary 0
 *  480760 bits
 *  = 43.54% compression ratio!
 ******************************************************************************/

/**
 *  The {@code TextCompressor} class provides static methods for compressing
 *  and expanding natural language through textfile input.
 *
 *  @author Zach Blick, David Lutch
 */
public class TextCompressor {
    private static final int EOF = 0x80;
    private static final int COMPRESSED_BITS = 12;
    private static final int MAX_PATTERNS = (1 << COMPRESSED_BITS);
    private static final int STANDARD_ASCII = 128;

    private static void compress() {
        TST TST = new TST();
        String data = BinaryStdIn.readString();
        int index = 0;
        String prefix = "";
        int prefixValue = EOF + 1;
        int code;

        for (char c = 0; c < STANDARD_ASCII; c++) {
            String ascii = "" + c;
            TST.insert(ascii, (int) c);
        }

        while (index < data.length()) {
            // Prefix = longest coded word that matches text @ index
            prefix = TST.getLongestPrefix(data, index);
            // Get the code for this prefix
            code = TST.lookup(prefix);
            BinaryStdOut.write(code, COMPRESSED_BITS);

            index += prefix.length();
            if (index < data.length() && prefixValue < MAX_PATTERNS) {
                // Append that character to prefix
                String nextString = prefix + data.charAt(index);
                // Associate prefix with the next code (if available)
                TST.insert(nextString, prefixValue);
                prefixValue++;
            }
        }
        // Write out EOF and close
        BinaryStdOut.write(EOF, COMPRESSED_BITS);
        BinaryStdOut.close();
    }

    private static void expand() {
        String[] Map = new String[MAX_PATTERNS];
        int index = STANDARD_ASCII + 1;
        // Might change to 128
       for (int i = 0; i < STANDARD_ASCII; i++) {
            Map[i] = "" + (char) i;
        }
        int nextPrefix = BinaryStdIn.readInt(COMPRESSED_BITS);
        int prefix = 0;
        //BinaryStdOut.write(prefix, COMPRESSED_BITS);
        while (nextPrefix != EOF) {
            // This is the code we are going to look at right now
            prefix = nextPrefix;
            BinaryStdOut.write(Map[prefix]);
            // Getting the next prefix/code
            nextPrefix = BinaryStdIn.readInt(COMPRESSED_BITS);
            // Edge case: When expanding, if we see a code that doesn't exist yet
            if (Map[nextPrefix] == null) {
                // Its string is given to us by appending to our current prefix, p: New String = p + p's first letter
                String edgeCase = Map[prefix] + Map[prefix].charAt(0);
                Map[nextPrefix] = edgeCase;
            }
            else if (index < MAX_PATTERNS) {
                String addedString = Map[prefix] + Map[nextPrefix].charAt(0);
                Map[index] = addedString;
            }
            index++;
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
