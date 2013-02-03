/**
 * 
 */

package sg.gov.nhb.ihuayu.activity.aes;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author lixingwang
 */
public class AESUtils {

    public static String encription(String key, String text) {
        byte[] ivraw = key.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(ivraw, "AES");

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            return new String(Base64.encode(encrypted));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
