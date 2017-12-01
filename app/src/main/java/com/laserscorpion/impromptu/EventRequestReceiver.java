package com.laserscorpion.impromptu;

import java.util.Collection;

public interface EventRequestReceiver {

    void onEventsReceived(Collection<EventDetails> events);

    void onRequestFailed(String reason);

}
