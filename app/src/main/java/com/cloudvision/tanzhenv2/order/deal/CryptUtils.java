package com.cloudvision.tanzhenv2.order.deal;

import com.cloudvision.tanzhenv2.order.constants.Constants;

import java.io.IOException;
import java.nio.charset.Charset;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 数据加密，用于http的请求内容
 * 
 * Created by 谭智文.
 */
public class CryptUtils {
    private static final CryptUtils instance = new CryptUtils();
    public static String XOR = "03";


    public static CryptUtils getInstance() {
        return instance;
    }

    public String encryptXOR(String content) {
        if (content == null || content.length() == 0)
            return content;

        byte[] KEY = Constants.PWD_KEY.getBytes(Charset
                .forName(Constants.CHARSET));

        byte[] encrypt = content.getBytes(Charset.forName(Constants.CHARSET));

        byte[] newByte = new byte[encrypt.length];

        for (int i = 0; i < newByte.length; i++) {
            newByte[i] = (byte) (encrypt[i] ^ KEY[i % KEY.length]);
        }

        return XOR + new BASE64Encoder().encode(newByte);
    }

    public String descryptXOR(String content) {

        try {
            byte[] encrypted = new BASE64Decoder().decodeBuffer(content.substring(2));

            byte[] KEY = Constants.PWD_KEY.getBytes(Charset
                    .forName(Constants.CHARSET));

            byte[] newByte = new byte[encrypted.length];

            for (int i = 0; i < newByte.length; i++) {
                newByte[i] = (byte) (encrypted[i] ^ KEY[i % KEY.length]);
            }
            return new String(newByte, Charset.forName(Constants.CHARSET));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
