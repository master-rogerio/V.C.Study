package com.example.study.util;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001c\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\rJ\u000e\u0010\u000e\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/example/study/util/GeofenceHelper;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "geofencingClient", "Lcom/google/android/gms/location/GeofencingClient;", "addGeofences", "", "locations", "", "Lcom/example/study/data/FavoriteLocation;", "pendingIntent", "Landroid/app/PendingIntent;", "removeGeofences", "app_debug"})
public final class GeofenceHelper {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.android.gms.location.GeofencingClient geofencingClient = null;
    
    public GeofenceHelper(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    public final void addGeofences(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.study.data.FavoriteLocation> locations, @org.jetbrains.annotations.NotNull()
    android.app.PendingIntent pendingIntent) {
    }
    
    public final void removeGeofences(@org.jetbrains.annotations.NotNull()
    android.app.PendingIntent pendingIntent) {
    }
}