package com.tangs.myapplication.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.tangs.myapplication.R;
import com.tangs.myapplication.databinding.MainFragmentBinding;
import com.tangs.myapplication.ui.main.data.User;
import com.tangs.myapplication.ui.main.viewmodels.MainViewModel;
import com.tangs.myapplication.ui.main.viewmodels.ViewModelFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainFragment extends Fragment {

    private final CompositeDisposable disposable = new CompositeDisposable();
    private MainFragmentBinding binding;
    private MainViewModel viewModel;

    private int uid = 0;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(inflater, container, false);
        FragmentActivity context = this.getActivity();
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(context);
        viewModel = new ViewModelProvider(context, mViewModelFactory).get(MainViewModel.class);
        binding.setViewModel(viewModel);

        String[] COUNTRIES = new String[] {"Item 1", "Item 2", "Item 3", "Item 4"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getContext(),
                        R.layout.dropdown_menu_popup_item,
                        COUNTRIES);

        binding.filledExposedDropdown.setAdapter(adapter);

        new Handler(this.getActivity().getMainLooper()).postDelayed(() -> {
            viewModel.setUsername("dddd");
        }, 1000);

        binding.button.setOnClickListener(view -> {
            binding.button.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
            User user = new User(binding.firstname.getText().toString(),
                    binding.lastname.getText().toString());
            disposable.add(viewModel.insertUser(user)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                binding.button.setEnabled(true);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }));
        });

        disposable.add(viewModel.getUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(users -> {
                    for (User user : users) {
                        Log.i("user", String.format("%s %s", user.firstName, user.lastName));
                    }
                }, throwable -> {
                    Log.e("user", "Unable to update username", throwable);
                }
        ));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposable.clear();
        binding = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

}