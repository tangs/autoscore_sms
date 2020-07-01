package com.tangs.myapplication.ui.main.viewmodels;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tangs.myapplication.BR;
import com.tangs.myapplication.ui.main.UserDataSource;
import com.tangs.myapplication.ui.main.data.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class MainViewModel extends ViewModel implements Observable {
    // TODO: Implement the ViewModel
    private final UserDataSource dataSource;
    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();
    private MutableLiveData<String>  username;

    public MainViewModel(UserDataSource dataSource) {
        this.dataSource = dataSource;
        username = new MutableLiveData<String>();
        username.setValue("tangs");
    }

    public void setUsername(String username) {
        this.username.setValue(username);
        notifyPropertyChanged(BR.username);
    }

    @Bindable
    public String getUsername() {
        return username.getValue();
    }

    public Flowable<List<User>> getUsers() {
        return dataSource.getAll();
    }

    public Completable insertUser(User user) {
        return dataSource.insertAll(user);
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    /**
     * Notifies observers that all properties of this instance have changed.
     */
    void notifyChange() {
        callbacks.notifyCallbacks(this, 0, null);
    }

    /**
     * Notifies observers that a specific property has changed. The getter for the
     * property that changes should be marked with the @Bindable annotation to
     * generate a field in the BR class to be used as the fieldId parameter.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    void notifyPropertyChanged(int fieldId) {
        callbacks.notifyCallbacks(this, fieldId, null);
    }
}