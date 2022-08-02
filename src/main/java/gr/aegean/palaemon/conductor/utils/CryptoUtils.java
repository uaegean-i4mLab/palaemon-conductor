package gr.aegean.palaemon.conductor.utils;

import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

@Component
public class CryptoUtils {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public PrivateKey getPrivateKey(){
        return this.privateKey;
    }

    public CryptoUtils() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File publicKeyFile = new File(EnvUtils.getEnvVar("PUBLIC_ENCRYPTION_KEY_PATH","public.key"));
        byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        this.publicKey = keyFactory.generatePublic(publicKeySpec);

        File privateKeyFile = new File(EnvUtils.getEnvVar("PRIVATE_ENCRYPTION_KEY_PATH","private.key"));
        byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        this.privateKey = keyFactory.generatePrivate(privateKeySpec);
    }


    public String encryptBase64(String toEncrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
        byte[] secretMessageBytes = toEncrypt.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
        return Base64.getEncoder().encodeToString(encryptedMessageBytes);
    }

    public String decryptBase64Message(String encryptedMessage) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher decryptCipher = Cipher.getInstance("RSA");
        byte[] encryptedMessageBytes = Base64.getDecoder().decode(encryptedMessage);
        decryptCipher.init(Cipher.DECRYPT_MODE, this.getPrivateKey());
        byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
        return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
    }


    public static SecretKey getSymmetricKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
    }

}
