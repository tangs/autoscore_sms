package com.tangs.myapplication.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.List;


public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;
    private SettingViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        FragmentActivity context = this.getActivity();
        SettingViewModelFactory mViewModelFactory = Injection.provideSettingViewModelFactory(context);
        viewModel = new ViewModelProvider(context, mViewModelFactory).get(SettingViewModel.class);
        binding.setViewmodel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.init();
        this.initPlatforms();
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
//        binding.dataList.setAdapter();
        List<Record> list = new ArrayList<>();
        for (int i = 0; i < 1000; ++i)
            list.add(new Record(i, "www.tangs.com", "a=" + i));
        RecordAdapter adapter = new RecordAdapter();
        adapter.submitList(list);
        binding.records.setAdapter(adapter);
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
