package ru.shemplo.steganography;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ru.shemplo.steganography.fc.FileChooseFragment;
import ru.shemplo.steganography.util.T3;

public class SteganographyActivity extends AppCompatActivity {

    private Button openFileChooseButton;

    private ConstraintLayout postPreviewForm, postPreviewButtons;

    private SquareLayout previewImageSquare;

    private ImageView previewImage;
    private Bitmap bitmap;

    private EditText imageTextField;

    private Button saveImageButton, sendImageButton;

    private FragmentContainerView fileChooseFragment;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_steganography);

        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission (this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions (this, new String [] {permission}, 100);
        }

        /*
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission (getApplicationContext (), permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions (this, new String [] {permission}, 100);
        }
        */

        previewImageSquare = findViewById (R.id.preview_image_square);
        previewImage = findViewById (R.id.preview_image);

        fileChooseFragment = findViewById (R.id.file_choose_fragment);

        openFileChooseButton = findViewById (R.id.choose_image_button);
        openFileChooseButton.setOnClickListener (event -> {
            getSupportFragmentManager ().beginTransaction ()
                .setCustomAnimations (android.R.anim.fade_in, android.R.anim.fade_out)
                .add (R.id.file_choose_fragment, FileChooseFragment.class, null)
                .setReorderingAllowed (true)
                .addToBackStack (null)
                .commit ();
        });

        postPreviewButtons = findViewById (R.id.post_preview_buttons_layout);
        postPreviewForm = findViewById (R.id.post_preview_form_layout);
        imageTextField = findViewById (R.id.image_text_text_field);

        imageTextField.setOnFocusChangeListener ((__, hasFocus) -> {
            if (hasFocus) {
                //previewImageSquare.setVisibility (View.GONE);
            } else {
                //previewImageSquare.setVisibility (View.VISIBLE);
            }
        });
        imageTextField.setOnEditorActionListener ((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //previewImageSquare.setVisibility (View.VISIBLE);
            }

            Log.i ("SA", "Event code: " + actionId);
            return false;
        });

        saveImageButton = findViewById (R.id.save_image_button);
        saveImageButton.setOnClickListener (event -> {
            new ImageSaveTask ().execute (T3.of (getApplicationContext (), bitmap, imageTextField.getText ().toString ()));
        });
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager ();
        if (fm.getBackStackEntryCount () > 0) {
            fm.popBackStack ();
        } else {
            super.onBackPressed ();
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String [] permissions, @NonNull int [] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults [0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText (getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onImageChosen (Bitmap bitmap) {
        this.bitmap = bitmap.copy (Bitmap.Config.ARGB_8888, true);
        this.bitmap.setHasAlpha (true);

        Log.d ("SA", "Setting up given bitmap...");
        imageTextField.setText (SteganographyEngine.getInstance ().decode (this.bitmap));
        previewImage.setImageBitmap (this.bitmap);
        Log.d ("SA", "Given bitmap is set");

        postPreviewButtons.setVisibility (View.VISIBLE);
        previewImageSquare.setVisibility (View.VISIBLE);
        postPreviewForm.setVisibility (View.VISIBLE);
    }

}