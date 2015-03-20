package ua.com.glady.uacc.tools;

import android.content.res.Resources;

import java.io.InputStream;


/**
 * This class contains static functions that provides resources i/o
 * Created by Slava on 25.02.2015.
 */
public class ToolsRes {

    /**
     * Returns a text from given raw resource
     * @param resources - application resources
     * @param resId - raw resource id
     * @return text from specified resource
     */
    public static String getRawResAsString(Resources resources, int resId){
        try {
            InputStream in_s = resources.openRawResource(resId);
            byte[] b = new byte[in_s.available()];
            int result = in_s.read(b);
            if (result > -1)
                return new String(b);
            else
                return "";
        } catch (Exception e) {
            throw new Resources.NotFoundException();
        }
    }
}
