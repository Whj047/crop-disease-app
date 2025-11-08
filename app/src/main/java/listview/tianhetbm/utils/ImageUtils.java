\
package listview.tianhetbm.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import androidx.exifinterface.media.ExifInterface;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

    public static Bitmap loadBitmapFromUri(Context ctx, Uri uri, int reqWidth, int reqHeight) throws IOException {
        ContentResolver resolver = ctx.getContentResolver();
        InputStream is = resolver.openInputStream(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        if (is != null) is.close();

        int inSampleSize = 1;
        int height = options.outHeight;
        int width = options.outWidth;
        while ((height / inSampleSize) > reqHeight || (width / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        is = resolver.openInputStream(uri);
        Bitmap bmp = BitmapFactory.decodeStream(is, null, options);
        if (is != null) is.close();

        // Try read EXIF rotation (if from file path)
        try {
            ExifInterface exif = new ExifInterface(resolver.openInputStream(uri));
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            bmp = rotateBitmap(bmp, orientation);
        } catch (Exception ignore) {}

        return bmp;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        if (bitmap == null) return null;
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap;
        }
        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return rotated;
    }

    public static Bitmap resizeCenterCrop(Bitmap src, int size) {
        int width = src.getWidth();
        int height = src.getHeight();
        float scale = (float) size / Math.min(width, height);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap scaled = Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
        int x = (scaled.getWidth() - size) / 2;
        int y = (scaled.getHeight() - size) / 2;
        Bitmap out = Bitmap.createBitmap(scaled, x, y, size, size);
        if (scaled != src) scaled.recycle();
        return out;
    }
}
