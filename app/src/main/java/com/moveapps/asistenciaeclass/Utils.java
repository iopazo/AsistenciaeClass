package com.moveapps.asistenciaeclass;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;

/**
 * Created by iopazog on 13-09-14.
 */
public class Utils {

    //Llaves de API eClass
    public static final String API_URL = "http://api.eclass.cl";
    public static final String KEY = "oVajeICNg9OL0LnJaIuu3MrVf31td76aq2tompdtaJKRo7F+inacmOmGtsuk4dLYw7uavNLhzNKwmZY=";
    public static final String SECRET = "2vTOVNrYmXf8ZywdpFD4";


    public static boolean validarRut(String rut) {

        boolean validacion = false;
        try {
            rut =  rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            char dv = rut.charAt(rut.length() - 1);

            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                validacion = true;
            }

        } catch (java.lang.NumberFormatException e) {
        } catch (Exception e) {
        }
        return validacion;
    }

    static public String formatear(String rut){
        int cont=0;
        String format;
        if(rut.length() == 0){
            return "";
        }else{
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            format = "-"+rut.substring(rut.length()-1);
            for(int i = rut.length()-2;i>=0;i--){
                format = rut.substring(i, i+1)+format;
                cont++;
                if(cont == 3 && i != 0){
                    format = format;
                    cont = 0;
                }
            }
            return format;
        }
    }

    static public String MD5(String md5) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    static public HashMap<String, String> putData(String id, String nombre) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("id", id.toString());
        item.put("nombre", nombre);
        return item;
    }

    static public int convertDpToPixel(float dp, Resources resources) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }
}
