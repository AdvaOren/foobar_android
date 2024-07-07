package daos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;

/**
 * Provides type conversion methods for converting between Bitmap and byte array.
 */
public class Converters {
    /**
     * Converts a Bitmap image to a byte array.
     *
     * @param bitmap The Bitmap image to be converted.
     * @return The byte array representing the Bitmap image.
     */
    @TypeConverter
    public static byte[] fromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Converts a byte array to a Bitmap image.
     *
     * @param byteArray The byte array representing the Bitmap image.
     * @return The Bitmap image converted from the byte array.
     */
    @TypeConverter
    public static Bitmap toBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
