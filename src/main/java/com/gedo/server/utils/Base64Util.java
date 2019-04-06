package com.gedo.server.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
/**
 * Created by Gedo on 2019/4/3.
 */
public class Base64Util {

    private static Base64.Decoder decoder = Base64.getDecoder();

    private static Base64.Encoder encoder = Base64.getEncoder();


    public static String encrypt(String str) {
        String s = "";
        try {
            byte[] bt = str.getBytes("UTF-8");
            s = encoder.encodeToString(bt);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String decrypt(String str) {
        String s = "";
        try {
            byte[] bt = decoder.decode(str);
            s = new String(bt, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String getLogo() {
        String s = "IC4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4KICAgICAgICAgICAgICAgICAgICAgICBfb28wb29fCiAgICAgICAgICAgICAgICAgICAgICBvODg4ODg4OG8KICAgICAgICAgICAgICAgICAgICAgIDg4IiAuICI4OAogICAgICAgICAgICAgICAgICAgICAgKHwgLV8tIHwpCiAgICAgICAgICAgICAgICAgICAgICAwXCAgPSAgLzAKICAgICAgICAgICAgICAgICAgICBfX18vYC0tLSdcX19fCiAgICAgICAgICAgICAgICAgIC4nIFxcfCAgICAgfC8vICcuCiAgICAgICAgICAgICAgICAgLyBcXHx8fCAgOiAgfHx8Ly8gXAogICAgICAgICAgICAgICAgLyBffHx8fHwgLeWNjS18fHx8fC0gXAogICAgICAgICAgICAgICB8ICAgfCBcXFwgIC0gIC8vLyB8ICAgfAogICAgICAgICAgICAgICB8IFxffCAgJydcLS0tLycnICB8Xy8gfAogICAgICAgICAgICAgICBcICAuLVxfXyAgJy0nICBfX18vLS4gLwogICAgICAgICAgICAgX19fJy4gLicgIC8tLS4tLVwgIGAuIC4nX19fCiAgICAgICAgICAuIiIgJzwgIGAuX19fXF88fD5fL19fXy4nID4nICIiLgogICAgICAgICB8IHwgOiAgYC0gXGAuO2BcIF8gL2A7LmAvIC0gYCA6IHwgfAogICAgICAgICBcICBcIGBfLiAgIFxfIF9fXCAvX18gXy8gICAuLWAgLyAgLwogICAgID09PT09YC0uX19fX2AuX19fIFxfX19fXy9fX18uLWBfX18uLSc9PT09PQogICAgICAgICAgICAgICAgICAgICAgIGA9LS0tPScKLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4=";
        String ret = decrypt(s);
        return ret;
    }


}
