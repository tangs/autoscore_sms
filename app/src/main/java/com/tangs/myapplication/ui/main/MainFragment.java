package com.tangs.myapplication.ui.main;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tangs.myapplication.databinding.MainFragmentBinding;
import com.tangs.myapplication.ui.main.data.User;
import com.tangs.myapplication.ui.main.viewmodels.MainViewModel;
import com.tangs.myapplication.ui.main.viewmodels.ViewModelFactory;

import java.util.List;

import io.reactivex.Scheduler;
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
//        binding.setViewmodel(new MainViewModel());
        FragmentActivity context = this.getActivity();
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(context);
        viewModel = new ViewModelProvider(context, mViewModelFactory).get(MainViewModel.class);
        binding.setViewModel(viewModel);

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