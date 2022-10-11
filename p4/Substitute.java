
import java.util.Random;

public class Substitute implements SymCipher {
    byte[] key;
    int[] inverse;

    public Substitute() {
        key = new byte[256];
        inverse = new int[256];
        //instantiate key to all possible 256 byte values
        for (int i = 0; i < key.length; i++) {
            key[i] = (byte) i;
        }
        //randomly swap values
        Random rand = new Random();
        for (int i = 0; i < key.length; i++) {
            int randomIndexToSwap = rand.nextInt(key.length);
            byte temp = key[randomIndexToSwap];
            key[randomIndexToSwap] = key[i];
            key[i] = temp;
        }
        //creates inverse array
        for (int i = 0; i < key.length; i++) {
            int intVal = 0;
            //if byte value is negative add 256 to get positive index
            if (key[i] < 0) {
                int adjIndex = key[i] + 256;
                inverse[adjIndex] = i;
            } else {
                inverse[key[i] & 0xff] = i;
            }

        }
    }

    public Substitute(byte[] keyBytes) {
      //set instance variable key to incoming byte array
        key = keyBytes;
        inverse = new int[256];
        //create inverse array
        for (int i = 0; i < key.length; i++) {
            if (key[i] < 0) {
                int adjIndex = key[i] + 256;
                inverse[adjIndex] = i;
            } else {
                inverse[key[i] & 0xff] = i;
            }
        }
    }

    // Return an array of bytes that represent the key for the cipher
    public byte[] getKey() {
        return key;
    }

    // Encode the string using the key and return the result as an array of
    // bytes.  Note that you will need to convert the String to an array of bytes
    // prior to encrypting it.  Also note that String S could have an arbitrary
    // length, so your cipher may have to "wrap" when encrypting (remember that
    // it is a block cipher)
    public byte[] encode(String S) {
        System.out.println("Original String: " + S);
        //create corresponding byte array
        byte[] stringBytes = S.getBytes();
        System.out.print("Corresponding Byte Array: " );
        for(byte b: stringBytes){
            System.out.print(b + " ");
        }
        byte[] res = new byte[stringBytes.length];
        int intVal = 0;
        //loop through stringByte array
        for (int i = 0; i < stringBytes.length; i++) {
            //if byte value is negative add 256
            if (stringBytes[i] < 0) {
                int adjIndex = stringBytes[i] + 256;
                res[i] = stringBytes[adjIndex];
            } else {
                res[i] = key[stringBytes[i]];
            }
        }
        System.out.println();
        System.out.print("Encrypted Byte Array: " );
        for(byte b: res){
            System.out.print(b + " ");
        }
        System.out.println();
        return res;
    }


    // Decrypt the array of bytes and generate and return the corresponding String.
    public String decode(byte[] bytes) {
        System.out.print("Received Byte Array: ");
        for(byte b: bytes){
            System.out.print(b + " ");
        }
        System.out.println();
        byte[] decodedVals = new byte[bytes.length];
        //loop through bytes
        for(int i=0; i<bytes.length; i++){
          //if negative add 256 to get positive index
            if(bytes[i] < 0){
                int adjIndex = bytes[i] + 256;
                decodedVals[i] = (byte) inverse[adjIndex];
            }else{
                decodedVals[i] = (byte) inverse[bytes[i]];
            }
        }
        System.out.print("Decrypted Array Bytes: ");
        for(byte b: decodedVals){
            System.out.print(b + " ");
        }
        System.out.println();
        //convert decodedVals to corresponding string
        String resultString = new String(decodedVals);
        System.out.println("Corresponding String: "+ resultString);
        return resultString;
    }
}
