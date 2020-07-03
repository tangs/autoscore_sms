package com.tangs.myapplication.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tangs.myapplication.R;
import com.tangs.myapplication.databinding.RecordDetailFragmentBinding;
import com.tangs.myapplication.ui.main.data.Record;
import com.tangs.myapplication.ui.main.utilities.Injection;
import com.tangs.myapplication.ui.main.viewmodels.RecordDetailViewModel;
import com.tangs.myapplication.ui.main.viewmodels.RecordDetailViewModelFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RecordDetailFragment extends Fragment {

    private final CompositeDisposable disposable = new CompositeDisposable();
    private RecordDetailFragmentBinding binding;
    private RecordDetailViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = RecordDetailFragmentBinding.inflate(inflater, container, false);
        FragmentActivity context = this.getActivity();
        RecordDetailViewModelFactory viewModelFactory = Injection.provideRecordDetailViewModelFactory(context);
        viewModel = new ViewModelProvider(context, viewModelFactory).get(RecordDetailViewModel.class);
        int orderId = this.getArguments().getInt("orderId", -1);

        disposable.add(viewModel.getRecords()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(records -> {
                            for (Record record : records) {
                                if (record.orderId == orderId) {
                                    binding.setRecord(record);
                                    return;
                                }
                            }
                             Navigation.findNavController(binding.getRoot()).navigateUp();
                        }, throwable -> {
                            Log.e("user", "Unable to update username", throwable);
                        }
                ));
        initToolbar();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        disposable.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.initData();
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(view -> {
            Navigation.findNavController(view).navigateUp();
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_delete: {
                    new MaterialAlertDialogBuilder(this.getContext())
                            .setTitle("Delete this record?")
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                disposable.add(viewModel.deleteRecord(binding.getRecord().orderId)
                                        .subscribeOn(Schedulers.io())
                                        .subscribe());
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                    return true;
                }
            }
            return false;
        });
    }


    private void initData() {
        int orderId = this.getArguments().getInt("orderId", -1);
        this.binding.toolbar.setTitle("order:" + orderId);
    }
}
