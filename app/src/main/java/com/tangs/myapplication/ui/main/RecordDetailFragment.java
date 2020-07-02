package com.tangs.myapplication.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.tangs.myapplication.R;
import com.tangs.myapplication.databinding.RecordDetailFragmentBinding;
import com.tangs.myapplication.ui.main.data.Record;

public class RecordDetailFragment extends Fragment {

    private RecordDetailFragmentBinding binding;
//    private final Record record;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = RecordDetailFragmentBinding.inflate(inflater, container, false);
        FragmentActivity context = this.getActivity();
//        binding.setRecord();

//        setHasOptionsMenu(true);
        return binding.getRoot();
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//
//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
////        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.main_action, menu);
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
