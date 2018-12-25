package ayit5902.gmail.com.camerabasic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BitmapResizer {
    public static Bitmap shrinkBitmap(Context ctx, Uri uri, int width, int height) {
        InputStream input;
        try {
            input = ctx.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }

        if (!input.markSupported()) { // InputStream doesn't support mark(). so wrap it into BufferedInputStream & use that
            input = new BufferedInputStream(input);
        }

        try {
            input.mark(input.available()); // input.isavailable() gives size of input stream
        } catch (IOException e) {
            e.printStackTrace();
        }

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;

        try {
            input.reset(); // Resetting input stream
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmpFactoryOptions);
        bitmap=rotateBitmapIfRequired(ctx,bitmap);
        return bitmap;
    }

    private static Bitmap rotateBitmapIfRequired(Context ctx, Bitmap bitmap) {
        File imagePath = new File(ctx.getFilesDir(), "images");
        File imgFile = new File(imagePath, "default_image.jpg");

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                bitmap=rotateImage(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                bitmap=rotateImage(bitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                bitmap=rotateImage(bitmap, 270);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                break;
        }
        return  bitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
