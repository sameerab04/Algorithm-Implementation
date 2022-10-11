/*
Sameera Boppana - ssb40@pitt.edu
4254417
Ramirez 1501 - section 1020
Recitation - 1320
*/
/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/
public class LZWmod {
    private static final int R = 256; // number of input chars
    private static int L; // number of codewords = 2^W
    private static int W; // codeword width
    private static final int MIN = 9;
    private static final int MAX = 16;
    private static char reset_flag = 0;

    public static void compress() {
        W = MIN;
        L = (int) Math.pow(2, W);

        char character;

        TST<Integer> st = new TST<Integer>();
        StringBuilder s = new StringBuilder();

        // initalizing dict
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R + 1;

        boolean isMax = false;
        //adding reset flag to outputted file
        BinaryStdOut.write(reset_flag);

        // reading first char
        s.append(BinaryStdIn.readChar());


        while (!BinaryStdIn.isEmpty()) {
            // getting next char

            String prefix = "";
            //if there is room to add or at the max - get next char
            if (code < L || isMax) {
                character = BinaryStdIn.readChar();
                s.append(character);
                prefix = st.longestPrefixOf(s);

                if (st.get(s) == null) {

                    int encodedVal = st.get(prefix);

                    BinaryStdOut.write(encodedVal, W);
                    //if not at the max - add to the dictionary
                    if (!isMax) {
                        st.put(s, code++);
                    }
                    //reset strinbuilder
                    s.delete(0, s.length() - 1);

                }
              //either need to increment or reset
            } else {
                //check to increment bit size
                if (W < MAX) {
                    W++;
                    L = (int) Math.pow(2, W);
                } else {
                    //cannot increment anymore - check to reset
                    if(reset_flag == 'r'){
                        st = new TST<Integer> ();
                        W = MIN;
                        L =  (int) Math.pow(2, W);
                        for ( int i = 0; i < R; i++)
                            st.put("" + (char) i, i);
                        code = R + 1;
                        isMax = false;
                    }else {
                      //at the max codeword but do not reset  or add anything to dict
                        isMax = true;
                    }
                }
            }
        }
        BinaryStdOut.write(st.get(s), W);
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }

    public static void expand() {
        W = MIN;
        L = (int) Math.pow(2, MIN);
        int array_size = (int) Math.pow(2, MAX);
        String[] st = new String[array_size];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";

        //read in reset_flag
        reset_flag = BinaryStdIn.readChar();
        boolean isMax = false;
        int codeword = BinaryStdIn.readInt(W);
        String currentVal = st[codeword];

        while (true) {

            BinaryStdOut.write(currentVal);
            codeword = BinaryStdIn.readInt(W);

            //EOF
            if (codeword == R) {
                break;
            }
            String nextVal = st[codeword];

            //special case hack
            if (i == codeword) {
                nextVal = currentVal + currentVal.charAt(0);
            }

            //if at the end of possible codewords and not at the max - add to dict
            if (i < L - 1 && !isMax) {
                st[i++] = currentVal + nextVal.charAt(0);
            }
            currentVal = nextVal;

            //if at the end of possible codewords and at the max
            //need to add the last entry before resetting
            if(i == L - 1 && W == MAX){
              //check to see reset_flag - if 'r' then read in min num of codeword bytes
              //else read in current codeword bytes
                if(reset_flag == 'r'){
                    codeword = BinaryStdIn.readInt(MIN);
                }else{
                    codeword = BinaryStdIn.readInt(W);
                }
                //EOF
                if (codeword == R) {
                    break;
                }
                nextVal = st[codeword];

                //special case hack
                if (i == codeword) {
                    nextVal = currentVal + currentVal.charAt(0);
                }
                //add the very last element into the array
                st[i++] = currentVal + nextVal.charAt(0);

                BinaryStdOut.write(currentVal);

                currentVal = nextVal;
                //check to reset the array
                if(reset_flag == 'r'){
                    st = new String[array_size];
                    for (i = 0; i < R; i++)
                        st[i] = "" + (char) i;
                    st[i++] = "";

                    W = MIN;
                    L = (int) Math.pow(2, MIN);
                }else{
                    //do not reset or add anything else to array
                    isMax = true;
                }
            }
            //at the end of possible codewords but not at max - increment
            if(i == L -1 && W < MAX){
                W++;
                L = (int) Math.pow(2, W);
            }
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {

        if (args[0].equals("-")){
            if(args[1].equals("r")){
                reset_flag = 'r';
            }else if(args[1].equals("n")){
                reset_flag = 'n';
            }
            compress();
        }

        else if (args[0].equals("+"))
            expand();
        else
            throw new RuntimeException("Illegal command line argument");
    }

}
