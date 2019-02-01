package in.abongcher.tbec;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by abongcher on 22/5/17.
 */
public class Utility {

    public static Bitmap convertToBitmap(byte[] b){
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

}
