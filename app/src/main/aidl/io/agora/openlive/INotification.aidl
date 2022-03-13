// INotification.aidl
package io.agora.openlive;

// Declare any non-default types here with import statements

interface INotification {
    void onError(int error);
    void onTokenWillExpire();
}