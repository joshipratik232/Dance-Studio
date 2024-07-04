package com.example.apptest.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
    private final static int read_write_block_buffer = 1024;
    private final static String algo_image_encryptor = "AES/CBC/PKCS5Padding";
    private final static String algo_secret_key = "AES";

    public static void enc(String keystr, String specstr, InputStream in, OutputStream out)
            throws Exception {
        try {
            IvParameterSpec iv = new IvParameterSpec(specstr.getBytes("UTF-8"));
            SecretKeySpec keySpec = new SecretKeySpec(keystr.getBytes("UTF-8"),algo_secret_key);

            Cipher c = Cipher.getInstance(algo_image_encryptor);
            c.init(Cipher.ENCRYPT_MODE,keySpec,iv);
            out = new CipherOutputStream(out,c);
            int count=0;
            byte[] buffer = new byte[read_write_block_buffer];
            while ((count = in.read(buffer))>0){
                out.write(buffer,0,count);
            }
        }finally {
            out.close();
        }
    }
    public static void dec(String keystr, String specstr, InputStream in, OutputStream out)
            throws Exception {
        try {
            IvParameterSpec iv = new IvParameterSpec(specstr.getBytes("UTF-8"));
            SecretKeySpec keySpec = new SecretKeySpec(keystr.getBytes("UTF-8"),algo_secret_key);

            Cipher c = Cipher.getInstance(algo_image_encryptor);
            c.init(Cipher.DECRYPT_MODE,keySpec,iv);
            out = new CipherOutputStream(out,c);
            int count=0;
            byte[] buffer = new byte[read_write_block_buffer];
            while ((count = in.read(buffer))>0){
                out.write(buffer,0,count);
            }
        }finally {
            out.close();
        }
    }
}
