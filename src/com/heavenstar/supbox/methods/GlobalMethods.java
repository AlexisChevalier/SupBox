package com.heavenstar.supbox.methods;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GlobalMethods {

    /**
     * Classe non instanciable
     */
    private GlobalMethods() { }

    /**
     * Hash une chaine en MD5
     * @param pass non hashé
     * @return pass hashé en md5
     * @throws Exception
     */
    public static String cryptWithMD5(String pass) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] passBytes = pass.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuilder sb = new StringBuilder();
            for (byte aDigested : digested) {
                sb.append(Integer.toHexString(0xff & aDigested));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("MD5 Algorithm not found", e);
        }
    }
}
