package ayit5902.gmail.com.camerabasic;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import static android.support.v4.content.FileProvider.getUriForFile;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Uri selectedPhotoPath;
    private ImageView takePictureImageView;
    private static final int TAKE_PHOTO_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePictureImageView = (ImageView) findViewById(R.id.imageView);
    }

    @Override
    public void onClick(View v) {
        takePictureWithCamera();
    }

    private void takePictureWithCamera() {
        // 1 akses kamera
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 2 menyiapkan file
        File imagePath = new File(getFilesDir(), "images");
        File newFile = new File(imagePath, "default_image.jpg");
        if (newFile.exists()) {
            newFile.delete();
        } else {
            newFile.getParentFile().mkdirs();
        }
        selectedPhotoPath = getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".fileprovider", newFile);

        // 3
        captureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, selectedPhotoPath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            ClipData clip = ClipData.newUri(getContentResolver(), "A photo", selectedPhotoPath);
            captureIntent.setClipData(clip);
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        startActivityForResult(captureIntent, TAKE_PHOTO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            setImageViewWithImage();
        }
    }

    private void setImageViewWithImage() {
        takePictureImageView.post(new Runnable() {
            @Override
            public void run() {
                Bitmap pictureBitmap = BitmapResizer.shrinkBitmap(
                        MainActivity.this,
                        selectedPhotoPath,
                        takePictureImageView.getWidth(),
                        takePictureImageView.getHeight()
                );
                takePictureImageView.setImageBitmap(pictureBitmap);
            }
        });
    }
}
