package com.example.costumeapp.utils;

import android.content.Context;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utilitaire pour la gestion des fichiers (images).
 * Permet de copier une image depuis un URI "content://" vers un fichier
 * temporaire
 * utilisable par Retrofit pour l'upload.
 */
public class FileUtils {
    public static File getFileFromUri(Context context, Uri uri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            // Création d'un fichier temporaire dans le cache de l'app
            File file = new File(context.getCacheDir(), "upload_image_" + System.currentTimeMillis() + ".jpg");
            try (OutputStream outputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[2048];
                int read;
                // Lecture par morceaux depuis l'InputStream vers le fichier local
                while (inputStream != null && (read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
