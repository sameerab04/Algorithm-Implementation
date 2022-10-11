
import java.security.SecureRandom;

public class Add128 implements SymCipher {
    byte[] key;

      //create a byte array of size 128 and fill with random bytes
    public Add128()  {
        key = new byte[128];
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);
    }
    //set instance variable to incoming byte array
    public Add128 (byte[] keyBytes){
        key = keyBytes;
    }

    // Return an array of bytes that represent the key for the cipher
    public byte [] getKey(){
        return key;
    }

    // Encode the string using the key and return the result as an array of
    // bytes.  Note that you will need to convert the String to an array of bytes
    // prior to encrypting it.  Also note that String S could have an arbitrary
    // length, so your cipher may have to "wrap" when encrypting (remember that
    // it is a block cipher)
    public byte [] encode(String S){
        System.out.println("Original String: " + S);
        //converting string to byte array
        byte[] stringBytes = S.getBytes();
        System.out.print("Corresponding Byte Array: " );
        for(byte b: stringBytes){
            System.out.print(b + " ");
        }
        byte [] res = new byte[stringBytes.length];
        int keyLength = key.length;
        int keyPos = 0;
        //loop through stringBytes
        for(int i=0; i<stringBytes.length; i++){
          //check for wrapping
            if(i % keyLength == 0){
                keyPos = 0;
            }
            //add current location of stringBytes and key to get encrypted value
            res[i] = (byte) (stringBytes[i] + key[keyPos]);
            keyPos ++;
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
    public String decode(byte [] bytes){
        System.out.print("Received Byte Array: ");
        for(byte b: bytes){
            System.out.print(b + " ");
        }
        System.out.println();
        byte[] res = new byte[bytes.length];
        int keyPos = 0;
        //loop through bytes
        for(int i=0; i < bytes.length; i++){
          //check for wrapping
            if(i % key.length == 0) {
                keyPos = 0;
            }
            //subtract current location of stringBytes and key to get decrypted value
            res[i] = (byte) (bytes[i] - key[keyPos]);
            keyPos++;
        }

        System.out.print("Decrypted Array Bytes: ");
        for(byte b: res){
            System.out.print(b + " ");
        }
        System.out.println();
        String resultString = new String(res);
        System.out.println("Corresponding String: "+ resultString);
        return resultString;
    }
}
