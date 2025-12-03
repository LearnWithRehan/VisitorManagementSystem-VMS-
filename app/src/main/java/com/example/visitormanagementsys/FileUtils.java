package com.example.visitormanagementsys;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {
    public static String getPath(Context context, Uri uri) {
        try {
            InputStream input = context.getContentResolver().openInputStream(uri);
            File temp = new File(context.getCacheDir(), "img_" + System.currentTimeMillis());
            FileOutputStream out = new FileOutputStream(temp);

            byte[] buf = new byte[1024];
            int len;
            while ((len = input.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            input.close();
            return temp.getAbsolutePath();

        } catch (Exception e) {
            return null;
        }
    }
}
