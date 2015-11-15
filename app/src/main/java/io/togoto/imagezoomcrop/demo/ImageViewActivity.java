package io.togoto.imagezoomcrop.demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * @author GT
 */
public class ImageViewActivity extends Activity implements PicModeSelectDialogFragment.IPicModeSelectListener {

    public static final String TAG = "ImageViewActivity";
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;
    private String imgUri;

    private Button mBtnUpdatePic;
    private ImageView mImageView;
    private CardView mCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        mBtnUpdatePic = (Button) findViewById(R.id.btnUpdatePic);
        mImageView = (ImageView) findViewById(R.id.iv_user_pic);
        mCardView = (CardView) findViewById(R.id.cv_image_container);
        initCardView(); //Resize card view according to activity dimension
        mBtnUpdatePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProfilePicDialog();
            }
        });

        checkPermissions();
    }

    @SuppressLint("InlinedApi")
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1234);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == REQUEST_CODE_UPDATE_PIC) {
            if (resultCode == RESULT_OK) {
                String imagePath = result.getStringExtra(GOTOConstants.IntentExtras.IMAGE_PATH);
                showCroppedImage(imagePath);
            } else if (resultCode == RESULT_CANCELED) {
                //TODO : Handle case
            } else {
                String errorMsg = result.getStringExtra(ImageCropActivity.ERROR_MSG);
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showCroppedImage(String mImagePath) {
        if (mImagePath != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(mImagePath);
            mImageView.setImageBitmap(myBitmap);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //--------Private methods --------

    private void initCardView() {
        mCardView.setPreventCornerOverlap(false);
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        //We are implementing this only for portrait mode so width will be always less
        int w = displayMetrics.widthPixels;
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mCardView.getLayoutParams();
        int leftMargin = lp.leftMargin;
        int topMargin = lp.topMargin;
        int rightMargin = lp.rightMargin;
        int paddingLeft = mCardView.getPaddingLeft();
        int paddingRight = mCardView.getPaddingLeft();
        int ch = w - leftMargin - rightMargin + paddingLeft + paddingRight;
        mCardView.getLayoutParams().height = ch;
    }


    private void showAddProfilePicDialog() {
        PicModeSelectDialogFragment dialogFragment = new PicModeSelectDialogFragment();
        dialogFragment.setiPicModeSelectListener(this);
        dialogFragment.show(getFragmentManager(), "picModeSelector");
    }

    private void actionProfilePic(String action) {
        Intent intent = new Intent(this, ImageCropActivity.class);
        intent.putExtra("ACTION", action);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC);
    }


    @Override
    public void onPicModeSelected(String mode) {
        String action = mode.equalsIgnoreCase(GOTOConstants.PicModes.CAMERA) ? GOTOConstants.IntentExtras.ACTION_CAMERA : GOTOConstants.IntentExtras.ACTION_GALLERY;
        actionProfilePic(action);
    }
}
