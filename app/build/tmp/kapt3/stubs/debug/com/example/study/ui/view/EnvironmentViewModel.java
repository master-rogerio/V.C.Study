package com.example.study.ui.view;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\bJ\b\u0010\u0014\u001a\u00020\u0015H\u0002J\u000e\u0010\u0016\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\bJ\u000e\u0010\u0017\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\bJ\u000e\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0019\u001a\u00020\u001aJ\u000e\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\bR\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/example/study/ui/view/EnvironmentViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "allLocations", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/example/study/data/FavoriteLocation;", "getAllLocations", "()Lkotlinx/coroutines/flow/Flow;", "database", "Lcom/example/study/data/FlashcardDatabase;", "geofencingClient", "Lcom/google/android/gms/location/GeofencingClient;", "locationDao", "Lcom/example/study/data/FavoriteLocationDao;", "addGeofence", "", "location", "createGeofencePendingIntent", "Landroid/app/PendingIntent;", "delete", "insert", "removeGeofence", "locationId", "", "update", "app_debug"})
public final class EnvironmentViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.study.data.FlashcardDatabase database = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.study.data.FavoriteLocationDao locationDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.android.gms.location.GeofencingClient geofencingClient = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.example.study.data.FavoriteLocation>> allLocations = null;
    
    public EnvironmentViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.example.study.data.FavoriteLocation>> getAllLocations() {
        return null;
    }
    
    public final void insert(@org.jetbrains.annotations.NotNull()
    com.example.study.data.FavoriteLocation location) {
    }
    
    public final void update(@org.jetbrains.annotations.NotNull()
    com.example.study.data.FavoriteLocation location) {
    }
    
    public final void delete(@org.jetbrains.annotations.NotNull()
    com.example.study.data.FavoriteLocation location) {
    }
    
    public final void addGeofence(@org.jetbrains.annotations.NotNull()
    com.example.study.data.FavoriteLocation location) {
    }
    
    public final void removeGeofence(@org.jetbrains.annotations.NotNull()
    java.lang.String locationId) {
    }
    
    private final android.app.PendingIntent createGeofencePendingIntent() {
        return null;
    }
}