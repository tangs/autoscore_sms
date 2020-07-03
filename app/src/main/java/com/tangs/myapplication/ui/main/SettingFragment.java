package com.tangs.myapplication.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.tangs.myapplication.R;
import com.tangs.myapplication.databinding.FragmentSettingBinding;
import com.tangs.myapplication.ui.main.adapters.KArrayAdapter;
import com.tangs.myapplication.ui.main.adapters.RecordAdapter;
import com.tangs.myapplication.ui.main.data.Record;
import com.tangs.myapplication.ui.main.viewmodels.SettingViewModel;
import com.tangs.myapplication.ui.main.viewmodels.SettingViewModelFactory;

import java.util.Date;
import java.util.List;
import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class SettingFragment extends Fragment {

    private final CompositeDisposable disposable = new CompositeDisposable();
    private FragmentSettingBinding binding;
    private SettingViewModel viewModel;

    private RecordAdapter recordsAdapter = new RecordAdapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        FragmentActivity context = this.getActivity();
        SettingViewModelFactory mViewModelFactory = Injection.provideSettingViewModelFactory(context);
        viewModel = new ViewModelProvider(context, mViewModelFactory).get(SettingViewModel.class);
        binding.setViewmodel(viewModel);
        setHasOptionsMenu(true);
        disposable.add(viewModel.getRecords()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(records -> {
                            recordsAdapter.submitList(records);
                            recordsAdapter.notifyDataSetChanged();
                            if (records.size() > 0)
//                                binding.records.getLayoutManager().scrollToPosition(records.size() - 1);
                                binding.records.smoothScrollToPosition(records.size() - 1);
                        }, throwable -> {
                            Log.e("user", "Unable to update username", throwable);
                        }
                ));
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
        this.init();
        this.initPlatforms();
        this.initToolbar();
    }

    public void initToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_clear: {
                    new MaterialAlertDialogBuilder(SettingFragment.this.getContext())
                            .setTitle("Delete all records?")
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                disposable.add(viewModel.deleteAll()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {

                                        }));
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                    return true;
                }
            }
            return false;
        });
    }

    private void initPlatforms() {
        ArrayAdapter<String> adapter = new KArrayAdapter<String>(
                getContext(),
                R.layout.dropdown_menu_popup_item,
                getResources().getStringArray(R.array.platforms));
        binding.filledExposedDropdown.setAdapter(adapter);
    }

    private void init() {
        binding.getPhoneNumber.setOnClickListener(view -> {
            int id = new Random(new Date().getTime()).nextInt();
            disposable.add(viewModel.insertRecord(new Record(id, "host", "params"))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {

                    }));
            if (true) return;
            if (XXPermissions.hasPermission(this.getContext(), Permission.READ_PHONE_NUMBERS)) {
                getPhoneNumber();
                return;
            }
            XXPermissions
                    .with(this.getActivity())
                    .permission(Permission.READ_PHONE_NUMBERS)
                    .request(new OnPermission() {
                        @Override
                        public void hasPermission(List<String> granted, boolean all) {
                            getPhoneNumber();
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean quick) {
                            Toast.makeText(SettingFragment.this.getContext(),
                                    "Can't get permission.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
        binding.records.setAdapter(recordsAdapter);
    }

    private void getPhoneNumber() {
        if (ActivityCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        TelephonyManager tMgr = (TelephonyManager) this.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = tMgr.getLine1Number();
        String oldPhoneNumber = viewModel.getPhone();
        if (phoneNumber == null || phoneNumber.length() == 0) return;
        if (oldPhoneNumber != null && phoneNumber.equals(oldPhoneNumber)) return;

        if (oldPhoneNumber == null || oldPhoneNumber.length() == 0) {
            viewModel.setPhone(phoneNumber);
        } else {
            new MaterialAlertDialogBuilder(SettingFragment.this.getContext())
                    .setTitle("Replace phone number?")
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        viewModel.setPhone(phoneNumber);
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        }
    }
}
