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
    // CONFIRM WITH MR. BLICK
    // Max patterns is 176 as 80 of the patterns are chars (already set)
    private static final int MAX_PATTERNS = 176;
    private static final int COMPRESSED_BITS = 2;
    private static void compress() {
        TST TST = new TST();
        String data = BinaryStdIn.readString();
        int index = 0;
        String prefix = "";
        int prefixValue = 81;
        int counter = 0;
        int code;
        while (index < data.length()) {
            // Prefix = longest coded word that matches text @ index
            prefix = TST.getLongestPrefix(data.substring(index), index);
            // Get the code for this prefix
            code = TST.lookup(prefix);
            // If the code is associated with a prefix, write it out
            if (code != -1) {
                BinaryStdOut.write(code, COMPRESSED_BITS);
            }
            // If there isn't a code associated with the prefix, create one and write it out
            if (code == -1) {
                code = prefixValue + counter;
                counter++;
                TST.insert(prefix, code);
                BinaryStdOut.write(code, COMPRESSED_BITS);
                // IntelliJ suggestion: BinaryStdOut.write(data.substring(index), COMPRESSED_BITS);
            }
            // If possible, look ahead to the next character
            index += prefix.length();
            if (index < data.length() || counter < MAX_PATTERNS) {
                counter++;
                // Append that character to prefix
                String nextChar = prefix + data.charAt(index);
                // Associate prefix with the next code (if available)
                prefixValue += counter;
                TST.insert(nextChar, prefixValue);
            }
        }
        // Write out EOF and close
        BinaryStdOut.write(EOF, 8);
        BinaryStdOut.close();
    }

    // TODO: Complete the expand() method
    private static void expand() {

        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
