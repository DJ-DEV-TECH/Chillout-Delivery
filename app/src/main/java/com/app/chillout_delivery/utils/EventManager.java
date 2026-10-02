package com.app.chillout_delivery.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.chillout_delivery.model.EventModel;

public class EventManager {
    private static EventManager instance;

    private final MutableLiveData<EventModel> eventLiveData = new MutableLiveData<>();

    private EventManager() {}

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public void sendEvent(EventModel event) {
        eventLiveData.postValue(event);
    }

    public LiveData<EventModel> getEvents() {
        return eventLiveData;
    }

    public void clearEvent() {
        eventLiveData.postValue(null);
    }
}
