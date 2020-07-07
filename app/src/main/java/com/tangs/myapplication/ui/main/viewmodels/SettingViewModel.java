package com.tangs.myapplication.ui.main.viewmodels;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tangs.auto_score_sms.BR;
import com.tangs.myapplication.ui.main.RecordDataSource;
import com.tangs.myapplication.ui.main.data.LocalSharedPreferences;
import com.tangs.myapplication.ui.main.data.Record;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class SettingViewModel extends ViewModel implements Observable {

    private final RecordDataSource dataSource;
    private final LocalSharedPreferences localSharedPreferences;
    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    private MutableLiveData<String> phone = new MutableLiveData<>();
    private MutableLiveData<String> platform = new MutableLiveData<>();
    private MutableLiveData<String> host = new MutableLiveData<>();
    private MutableLiveData<Boolean> darkMode = new MutableLiveData<>();
    private MutableLiveData<Boolean> autoRefresh = new MutableLiveData<>();

    public SettingViewModel(RecordDataSource dataSource, LocalSharedPreferences localSharedPreferences) {
        this.dataSource = dataSource;
        this.localSharedPreferences = localSharedPreferences;
        this.setHost(localSharedPreferences.getHost());
        this.setPhone(localSharedPreferences.getPhoneNumber());
        this.setPlatform(localSharedPreferences.getPlatform());
        this.setAutoRefresh(localSharedPreferences.isAutoRefresh(false));
        this.setDarkMode(localSharedPreferences.isDarkMode(false));
    }

    public void setPhone(String phone) {
        localSharedPreferences.setPhoneNumber(phone);
        this.phone.setValue(phone);
        notifyPropertyChanged(BR.phone);
    }

    @Bindable
    public String getPhone() {
        return phone.getValue();
    }

    public void setPlatform(String platform) {
        localSharedPreferences.setPlatform(platform);
        this.platform.setValue(platform);
        notifyPropertyChanged(BR.platform);
    }

    @Bindable
    public String getPlatform() {
        return platform.getValue();
    }

    public void setHost(String host) {
        localSharedPreferences.setHost(host);
        this.host.setValue(host);
        notifyPropertyChanged(BR.host);
    }

    @Bindable
    public String getHost() {
        return host.getValue();
    }

    public void setDarkMode(boolean isDark) {
        localSharedPreferences.setDarkMode(isDark);
        this.darkMode.setValue(isDark);
    }

    public LiveData<Boolean> getDarkMode() {
        return darkMode;
    }

    public void setAutoRefresh(boolean autoRefresh) {
        localSharedPreferences.setAutoRefresh(autoRefresh);
        this.autoRefresh.setValue(autoRefresh);
    }

    public LiveData<Boolean> getAutoRefresh() {
        return autoRefresh;
    }

    public boolean getAutoRefreshValue() {
        return autoRefresh.getValue();
    }

    public Flowable<List<Record>> getRecords() {
        return dataSource.getAll();
    }

    public Completable insertRecord(Record record) {
        return dataSource.insert(record);
    }

    public Completable deleteRecord(Record record) {
        return dataSource.delete(record);
    }

    public Completable deleteAll() {
        return dataSource.deleteAll();
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