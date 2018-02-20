package com.simon.blockchain.util;

import java.security.*;
import java.util.Base64;

/**
 * Created by simon on 2018/2/8.
 */
public class CryptologyUtil {
    public static String applySha256(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
            for(int i=0;i<hash.length;i++){
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1){
                    hexString.append(0);
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    /**
     * 使用私钥签名
     * @param privateKey
     * @param data
     * @return
     */
    public static byte[] applyECDSASig(PrivateKey privateKey,String data){
        Signature dsa;
        try{
            dsa = Signature.getInstance("ECDSA","BC");
            dsa.initSign(privateKey);
            byte[] strByte = data.getBytes();
            dsa.update(strByte);
            return dsa.sign();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用公钥验证签名
     * @param publicKey
     * @param data
     * @param signature
     * @return
     */
    public static boolean verifyECDSASig(PublicKey publicKey,String data,byte[] signature){
        try {
            Signature edcsaVerify = Signature.getInstance("ECDSA","BC");
            edcsaVerify.initVerify(publicKey);
            edcsaVerify.update(data.getBytes());
            return edcsaVerify.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String getStringFromKey(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

}
