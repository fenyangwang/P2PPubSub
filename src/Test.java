import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            String str = "127.0.0.1:8009";
            byte[] bytes = md.digest(str.getBytes());
            /*for (byte b : bytes) {
                System.out.print(b + " ");
            }*/
            System.out.println();
            byte b = bytes[bytes.length - 1];
            int num = ((int)b) & (7);
            System.out.println(num);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
