package com.example.application_template_jmvvm.Helpers;

import android.util.Log;

import java.io.UnsupportedEncodingException;

import CTOS.CtKMS2Exception;
import CTOS.CtKMS2FixedKey;
import CTOS.CtKMS2TR31;

public class KeyInjectHelper {
    private static final String TAG = "KeyInjectHelper";

    public static byte[] encryptData(int keyset, int keyindex, byte[] data)
    {
        CtKMS2FixedKey enckey = new CtKMS2FixedKey();
        try {
            enckey.setCipherMethod(CtKMS2FixedKey.DATA_ENCRYPT_METHOD_ECB);
            enckey.setInputData(data, 0, data.length);
            enckey.selectKey(keyset,keyindex);
            enckey.dataEncrypt();
        } catch (CtKMS2Exception e) {
            Log.e("KMS_test", "Encryption error! ret = " + String.format("0x%4X", e.getError()));
        }
        return enckey.getOutpuData();
    }

    public static byte[] decryptData(int keyset, int keyindex, byte[] data)
    {
        CtKMS2FixedKey deckey = new CtKMS2FixedKey();
        try {
            deckey.setCipherMethod(CtKMS2FixedKey.DATA_ENCRYPT_METHOD_ECB);
            deckey.setInputData(data, 0, data.length);
            deckey.selectKey(keyset,keyindex);
            deckey.dataDecrypt();
        } catch (CtKMS2Exception e) {
            Log.e("KMS_test", "Decryption error! ret = " + String.format("0x%4X", e.getError()));
        }
        return deckey.getOutpuData();
    }

    public static int injectEncryptedKey(int KEK_keyset, int KEK_keyindex, int keyset, int keyindex, byte[] encKeyData)
    {
        CtKMS2TR31 tr31_e = new CtKMS2TR31();
        try {
            tr31_e.selectKey(KEK_keyset, KEK_keyindex);
            tr31_e.setKeyLocation(keyset, keyindex);
            tr31_e.setTR31KeyBlock(encKeyData);
            tr31_e.writeKey();
        } catch (CtKMS2Exception e) {
            Log.e("KMS_test", "Injection error! ret = " + String.format("0x%4X", e.getError()));
            return e.getError();
        }
        return 0;
    }

    public static void exampleKey( ){

        Log.d(TAG, "Inject Key");

        String TEB_key_en = "B0080D0TB00N00006D0818BA19BCC93355006A58F4D8504480356918AC3F96668C67B9FFC386B3CC";

        byte[] bytes_e = TEB_key_en.getBytes();
        int ret =  injectEncryptedKey(0xCFFF, 0x0000, 0x0A03, 0x0000, bytes_e);
        if(ret != 0)
        {
            final int retf = ret;
            Log.d(TAG, "Write key 0A02 FAIL ret  = " + retf);
        }

        byte[] encData =  encryptData(0x0A03, 0x0000, "Deneme text.....".getBytes());

        final byte[] decData = decryptData( 0x0A03, 0x0000, encData);
        String str = "";
        try {
            str = new String(decData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final String strstr = str;
        Log.d(TAG, "Dec data : " + strstr);
    }
}
