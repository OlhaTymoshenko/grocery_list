package ua.com.amicablesoft.android.grocerylist.ui.profile;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ua.com.amicablesoft.android.grocerylist.R;
import ua.com.amicablesoft.android.grocerylist.service.FileUploadService;
import ua.com.amicablesoft.android.grocerylist.ui.common.ImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserPhotoActivity extends AppCompatActivity {
    private String currentPhotoPath;
    private File image;
    private int width;
    private int height;
    private View progressView;
    private View photoFormView;
    private ImageView imageView;
    private BroadcastReceiver receiver;
    static final int REQUEST_IMAGE_CAPTURE = 0;
    static final int MY_PERMISSIONS_REQUEST = 1;
    static final int PICK_IMAGE = 1;
    public static final String BROADCAST_ACTION_2 = "close activity";
    public static final String FILE_PATH = "filePath";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_photo);
        imageView = (ImageView) findViewById(R.id.user_photo);

        if (savedInstanceState != null) {
            currentPhotoPath = savedInstanceState.getString(FILE_PATH);
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imageView.setImageBitmap(bitmap);
            image = new File(currentPhotoPath);
        } else {
            getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    ImageLoader imageLoader = new ImageLoader(getApplicationContext());
                    imageLoader.loadImage(getString(R.string.picasso_url), getWindow().getDecorView().getWidth(), 0, imageView);
                    getWindow().getDecorView().removeOnLayoutChangeListener(this);
                }
            });
        }

        TextView textViewTakePicture = (TextView) findViewById(R.id.take_picture_text_view);
        assert textViewTakePicture != null;
        textViewTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                    takePicture();
                } else {
                    ActivityCompat.requestPermissions(UserPhotoActivity.this,
                            new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
                }
            }
        });
        TextView textViewSelectPicture = (TextView) findViewById(R.id.select_picture_text_view);
        assert textViewSelectPicture != null;
        textViewSelectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture();
            }
        });

        TextView textViewDone = (TextView) findViewById(R.id.done_text_view);
        assert textViewDone != null;
        textViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image != null) {
                    showProgress(true);
                    Intent serviceIntent = new Intent(UserPhotoActivity.this, FileUploadService.class);
                    serviceIntent.putExtra("image", image);
                    startService(serviceIntent);
                } else {
                    finish();
                }
            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showProgress(false);
                finish();
            }
        };

        IntentFilter filter = new IntentFilter(BROADCAST_ACTION_2);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(FILE_PATH, currentPhotoPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK && data != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                ImageView imageView = (ImageView) findViewById(R.id.user_photo);
                assert imageView != null;
                imageView.setImageBitmap(bitmap);
            }
        }
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                File photo = null;
                try {
                    photo = createImageFile();
                    OutputStream outputStream = new FileOutputStream(photo);
                    byte[] buf = new byte[1024 * 1024];
                    int len;
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    assert inputStream != null;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                    outputStream.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ImageView imageView = (ImageView) findViewById(R.id.user_photo);
                assert imageView != null;
                width = imageView.getWidth();
                height = imageView.getHeight();
                new BitmapWorkerTask(imageView).execute(photo);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePicture();
        } else {
            Toast.makeText(getApplicationContext(), "This action requires permissions",
                    Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void selectPicture() {
        Intent selectPictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectPictureIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(selectPictureIntent, "Select picture"), PICK_IMAGE);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(File imageFile, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getPath(), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imageFile.getPath(), options);
    }

    class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewWeakReference;
        private File data = null;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewWeakReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(File... params) {
            data = params[0];
            return decodeSampledBitmapFromFile(data, width, height);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                final ImageView imageView = imageViewWeakReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            photoFormView = findViewById(R.id.photo_form);
            assert photoFormView != null;
            photoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            photoFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    photoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView = findViewById(R.id.send_photo_progress);
            assert progressView != null;
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            photoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
