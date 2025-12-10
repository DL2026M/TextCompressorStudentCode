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
    // TODO: Complete the compress() method
    private static void compress() {
        TST TST = new TST();
        String data = BinaryStdIn.readString();
        int index = 0;
        String prefix = "";
        int firstCode = 81;
        int counter = 0;
        while (index < data.length()) {
            // prefix = longest coded word that matches text @ index
            prefix = TST.getLongestPrefix(data, index);

            // get the code for this prefix

            //write out that code
            BinaryStdOut.write(prefix);
            //if possible, look ahead to the next character
            index += prefix.length();
            if ((index) < data.length()) {
                counter++;
                //append that character to prefix
                String nextString = prefix + data.charAt(index);
                //associate prefix with the next code (if available)
                TST.insert(nextString, firstCode + counter);
            }
            //write out EOF and close
            BinaryStdOut.write("EOF");
        }
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
