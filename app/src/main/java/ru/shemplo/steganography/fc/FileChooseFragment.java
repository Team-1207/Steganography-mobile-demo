package ru.shemplo.steganography.fc;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.shemplo.steganography.R;
import ru.shemplo.steganography.SteganographyActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FileChooseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FileChooseFragment extends Fragment {

    public FileChooseFragment () {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment FileChooseFragment.
     */
    public static FileChooseFragment newInstance () {
        FileChooseFragment fragment = new FileChooseFragment ();
        Bundle args = new Bundle ();

        fragment.setArguments (args);
        return fragment;
    }

    private RecyclerView imagesRecycler;

    @Override
    public View onCreateView (
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate (R.layout.fragment_file_choose, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);

        //Log.i ("FCF", getContext().toString());
        imagesRecycler = view.findViewById (R.id.images_recycler);
        imagesRecycler.setLayoutManager (new LinearLayoutManager (getContext ()));
        imagesRecycler.setAdapter (new ImagesListAdapter (this));
        //imagesRecycler.setItemAnimator (null);
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView ();

        Log.d ("FCF", "Fragment should be destroyed");
        imagesRecycler.setAdapter (null);
        imagesRecycler = null;
    }

    public void onImageChosen (Bitmap bitmap) {
        ((SteganographyActivity) getActivity ()).onImageChosen (bitmap);
        getParentFragmentManager ().beginTransaction ().remove (this).commit ();
    }

}