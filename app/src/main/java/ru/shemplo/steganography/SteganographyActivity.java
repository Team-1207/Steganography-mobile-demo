package ru.shemplo.steganography;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import ru.shemplo.steganography.fc.FileChooseFragment;
import ru.shemplo.steganography.util.T3;

public class SteganographyActivity extends AppCompatActivity {

    private ConstraintLayout postPreviewForm, postPreviewButtons;

    private SquareLayout previewImageSquare;

    private ImageView previewImage;
    private Bitmap bitmap;

    private EditText imageTextField;

    private InetAddress sendAddress;

    {
        try {
            sendAddress = Inet4Address.getByAddress (new byte [] {(byte) 192, (byte) 168, 1, 7});
        } catch (UnknownHostException uhe) { /* impossible */ }
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_steganography);

        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission (this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions (this, new String [] {permission}, 100);
        }

        permission = Manifest.permission.INTERNET;
        if (ContextCompat.checkSelfPermission (getApplicationContext (), permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions (this, new String [] {permission}, 100);
        }

        previewImageSquare = findViewById (R.id.preview_image_square);
        previewImage = findViewById (R.id.preview_image);

        Button openFileChooseButton = findViewById(R.id.choose_image_button);
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

        Button saveImageButton = findViewById(R.id.save_image_button);
        saveImageButton.setOnClickListener (event -> {
            new ImageSaveTask ().execute (T3.of (getApplicationContext (), bitmap, imageTextField.getText ().toString ()));
        });

        Button sendImageButton = findViewById(R.id.send_image_button);
        sendImageButton.setOnClickListener (event -> {
            new ImageSendTask ().execute (T3.of (bitmap, imageTextField.getText ().toString (), sendAddress));
        });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater ().inflate (R.menu.menu_top_toolbar, menu);
        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        if (item.getItemId () == R.id.params_item) {
            AlertDialog.Builder builder = new AlertDialog.Builder (this);
            builder.setTitle ("Image sending address");

            View view = LayoutInflater.from (this).inflate (R.layout.form_send_address_input_dialog, null, false);
            EditText input = view.findViewById (R.id.send_address_input);
            input.setText (sendAddress.toString ().substring (1));
            builder.setView (view);

            builder.setPositiveButton ("Set", (dialog, which) -> {
                new Thread (() -> {
                    try {
                        sendAddress = Inet4Address.getByName (input.getText ().toString ());
                    } catch (UnknownHostException uhe) {
                        Log.e ("SA", "Incorrect address, change will be ignored", uhe);
                    }
                }).start ();
            });

            builder.show ();
        }

        return super.onOptionsItemSelected (item);
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