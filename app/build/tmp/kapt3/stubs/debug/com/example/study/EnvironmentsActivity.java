package com.example.study;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0017\u001a\u00020\u0018H\u0002J\b\u0010\u0019\u001a\u00020\u001aH\u0002J\b\u0010\u001b\u001a\u00020\u001aH\u0002J\b\u0010\u001c\u001a\u00020\u0018H\u0002J\u0012\u0010\u001d\u001a\u00020\u00182\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0014J\b\u0010 \u001a\u00020\u0018H\u0002J\b\u0010!\u001a\u00020\u0018H\u0002J\b\u0010\"\u001a\u00020\u0018H\u0002J\b\u0010#\u001a\u00020\u0018H\u0002J\b\u0010$\u001a\u00020\u0018H\u0002J\b\u0010%\u001a\u00020\u0018H\u0003R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u000b\u001a\u00020\f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000f\u0010\u0010\u001a\u0004\b\r\u0010\u000eR\u001a\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140\u00130\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006&"}, d2 = {"Lcom/example/study/EnvironmentsActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "adapter", "Lcom/example/study/adapter/LocationAdapter;", "binding", "Lcom/example/study/databinding/ActivityEnvironmentsBinding;", "fusedLocationClient", "Lcom/google/android/gms/location/FusedLocationProviderClient;", "geofenceHelper", "Lcom/example/study/util/GeofenceHelper;", "geofencePendingIntent", "Landroid/app/PendingIntent;", "getGeofencePendingIntent", "()Landroid/app/PendingIntent;", "geofencePendingIntent$delegate", "Lkotlin/Lazy;", "permissionLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "", "", "viewModel", "Lcom/example/study/ui/FlashcardViewModel;", "checkPermissionsAndObserve", "", "hasBackgroundLocationPermission", "", "hasFineLocationPermission", "observeLocations", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "requestBackgroundLocationPermission", "requestFineLocationPermission", "setupBottomNavigation", "setupFab", "setupRecyclerView", "showAddLocationDialog", "app_debug"})
public final class EnvironmentsActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.example.study.databinding.ActivityEnvironmentsBinding binding;
    private com.example.study.ui.FlashcardViewModel viewModel;
    private com.example.study.adapter.LocationAdapter adapter;
    private com.google.android.gms.location.FusedLocationProviderClient fusedLocationClient;
    private com.example.study.util.GeofenceHelper geofenceHelper;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy geofencePendingIntent$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String[]> permissionLauncher = null;
    
    public EnvironmentsActivity() {
        super();
    }
    
    private final android.app.PendingIntent getGeofencePendingIntent() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void checkPermissionsAndObserve() {
    }
    
    private final boolean hasFineLocationPermission() {
        return false;
    }
    
    private final boolean hasBackgroundLocationPermission() {
        return false;
    }
    
    private final void requestFineLocationPermission() {
    }
    
    private final void requestBackgroundLocationPermission() {
    }
    
    private final void setupRecyclerView() {
    }
    
    private final void observeLocations() {
    }
    
    private final void setupFab() {
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    private final void showAddLocationDialog() {
    }
    
    private final void setupBottomNavigation() {
    }
}