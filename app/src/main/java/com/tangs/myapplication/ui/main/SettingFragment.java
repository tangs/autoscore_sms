package com.tangs.myapplication.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.tangs.auto_score_sms.R;
import com.tangs.auto_score_sms.databinding.FragmentSettingBinding;
import com.tangs.myapplication.ui.main.adapters.KArrayAdapter;
import com.tangs.myapplication.ui.main.adapters.RecordAdapter;
import com.tangs.myapplication.ui.main.data.config.Config;
import com.tangs.myapplication.ui.main.data.config.Server;
import com.tangs.myapplication.ui.main.utilities.Injection;
import com.tangs.myapplication.ui.main.utilities.StringHelper;
import com.tangs.myapplication.ui.main.viewmodels.SettingViewModel;
import com.tangs.myapplication.ui.main.viewmodels.SettingViewModelFactory;

import java.util.List;

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
        assert context != null;
        viewModel = new ViewModelProvider(context, mViewModelFactory).get(SettingViewModel.class);
        binding.setViewmodel(viewModel);
        setHasOptionsMenu(true);
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
        this.init();
        this.initToolbar();
    }

    public void initToolbar() {
        SwitchMaterial autoRefresh = binding.toolbar.findViewById(R.id.action_auto_refresh);
        SwitchMaterial dark = binding.toolbar.findViewById(R.id.action_dark);
        FragmentActivity context = this.getActivity();
        assert context != null;

        viewModel.getDarkMode().observe(this, val -> {
            if (val == dark.isChecked()) return;
            dark.setChecked(val);
        });
        viewModel.getAutoRefresh().observe(this, val -> {
            if (val == autoRefresh.isChecked()) return;
            autoRefresh.setChecked(val);
        });

        dark.setOnCheckedChangeListener((button, val) -> {
            viewModel.setDarkMode(val);
            context.recreate();
        });
        autoRefresh.setOnCheckedChangeListener((button, val) -> viewModel.setAutoRefresh(val));

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_clear) {
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Delete all records?")
                        .setPositiveButton(android.R.string.ok, (dialog, which) ->
                                disposable.add(viewModel.deleteAll()
                                .subscribeOn(Schedulers.io())
                                .subscribe()))
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
            }
            return false;
        });
    }

    private void initData() {
        disposable.add(viewModel.getRecords()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(records -> {
                            recordsAdapter.submitList(records);
                            recordsAdapter.notifyDataSetChanged();
                            if (viewModel.getAutoRefreshValue() && records.size() > 0) {
                                binding.records.smoothScrollToPosition(records.size() - 1);
                            }
                        }, throwable -> Log.e("user", "Unable to update username", throwable)
                ));
        ArrayAdapter<String> adapter = new KArrayAdapter<>(
                getContext(),
                R.layout.dropdown_menu_popup_item,
                getResources().getStringArray(R.array.platforms));
        binding.filledExposedDropdown.setAdapter(adapter);
        binding.records.setAdapter(recordsAdapter);
    }

    private void init() {
        viewModel.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (propertyId == BR.platform) {
                    viewModel.setHost("");
                    String platform = viewModel.getPlatform();
                    if (StringHelper.checkNullOrEmpty(platform)) return;
                    Server server = Config.getInstance(SettingFragment.this.getContext())
                            .geServer(platform);
                    if (server == null) return;
                    viewModel.setHost(server.url);
                }
            }
        });
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
    }

    private void getPhoneNumber() {
        FragmentActivity context = this.getActivity();
        assert context != null;
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("HardwareIds") String phoneNumber = tMgr.getLine1Number();
        String oldPhoneNumber = viewModel.getPhone();
        if (StringHelper.checkNullOrEmpty(phoneNumber)) return;
        if (phoneNumber.equals(oldPhoneNumber)) return;

        if (oldPhoneNumber == null || oldPhoneNumber.length() == 0) {
            viewModel.setPhone(phoneNumber);
        } else {
            new MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.get_phone_number_title)
                    .setPositiveButton(android.R.string.ok, (dialog, which) ->
                        viewModel.setPhone(phoneNumber))
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        }
    }
}
