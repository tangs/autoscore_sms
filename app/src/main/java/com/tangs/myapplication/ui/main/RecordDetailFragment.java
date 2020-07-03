package com.tangs.myapplication.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.tangs.myapplication.databinding.RecordDetailFragmentBinding;

public class RecordDetailFragment extends Fragment {

    private RecordDetailFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = RecordDetailFragmentBinding.inflate(inflater, container, false);
        binding.toolbar.setNavigationOnClickListener(view -> {
            Navigation.findNavController(view).navigateUp();
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.init();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        int orderId = this.getArguments().getInt("orderId", -1);
        this.binding.toolbar.setTitle("order:" + orderId);
    }
}
