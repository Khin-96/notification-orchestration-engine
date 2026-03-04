package com.notification.engine.provider;

import com.notification.engine.model.NotificationChannel;

public interface ChannelProvider {
    NotificationChannel getChannel();
    boolean send(String recipient, String title, String message);
}
