package ru.shemplo.steganography;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import ru.shemplo.steganography.fc.FileChooseFragment;

public class SteganographyActivity extends AppCompatActivity {

    private Button openFileChooseButton;

    private ConstraintLayout postPreviewForm;
    private SquareLayout previewImageSquare;
    private ImageView previewImage;
    private EditText imageTextField;

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

        postPreviewForm = findViewById (R.id.post_preview_form_layout);
        imageTextField = findViewById (R.id.image_text_text_field);
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
        imageTextField.setText (SteganographyEngine.getInstance ().decode (bitmap));
        previewImage.setImageBitmap (bitmap);

        previewImageSquare.setVisibility (View.VISIBLE);
        postPreviewForm.setVisibility (View.VISIBLE);
    }

}