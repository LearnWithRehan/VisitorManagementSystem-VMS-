package com.example.visitormanagementsys;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;

public class FileUtils {

    public static File getFileFromUri(Context context, Uri uri) {

        String path = "";
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

            if (idx != -1)
                path = cursor.getString(idx);

            cursor.close();
        }

        if (path == null || path.isEmpty()) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                File file = new File(context.getCacheDir(), "image.jpg");

                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

                return file;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new File(path);
    }
}
