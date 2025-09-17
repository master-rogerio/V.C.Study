package com.example.study.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000F\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001aN\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u0018\u0010\b\u001a\u0014\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bH\u0007\u001a,\u0010\f\u001a\u0010\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000e\u0018\u00010\r2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0007H\u0082@\u00a2\u0006\u0002\u0010\u0012\u001a2\u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u000f\u001a\u00020\u00102\u0018\u0010\u0016\u001a\u0014\u0012\u0004\u0012\u00020\u0017\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\tH\u0003\u001a&\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\u000eH\u0082@\u00a2\u0006\u0002\u0010\u001b\u00a8\u0006\u001c"}, d2 = {"LocationPickerDialog", "", "isVisible", "", "initialLocation", "Lcom/google/android/gms/maps/model/LatLng;", "initialAddress", "", "onLocationSelected", "Lkotlin/Function2;", "onDismiss", "Lkotlin/Function0;", "geocodeAddress", "Lkotlin/Pair;", "", "context", "Landroid/content/Context;", "address", "(Landroid/content/Context;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCurrentLocation", "fusedLocationClient", "Lcom/google/android/gms/location/FusedLocationProviderClient;", "onLocationReceived", "Landroid/location/Location;", "reverseGeocode", "latitude", "longitude", "(Landroid/content/Context;DDLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class LocationPickerKt {
    
    @androidx.compose.runtime.Composable()
    public static final void LocationPickerDialog(boolean isVisible, @org.jetbrains.annotations.Nullable()
    com.google.android.gms.maps.model.LatLng initialLocation, @org.jetbrains.annotations.NotNull()
    java.lang.String initialAddress, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super com.google.android.gms.maps.model.LatLng, ? super java.lang.String, kotlin.Unit> onLocationSelected, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    private static final void getCurrentLocation(com.google.android.gms.location.FusedLocationProviderClient fusedLocationClient, android.content.Context context, kotlin.jvm.functions.Function2<? super android.location.Location, ? super java.lang.String, kotlin.Unit> onLocationReceived) {
    }
    
    private static final java.lang.Object geocodeAddress(android.content.Context context, java.lang.String address, kotlin.coroutines.Continuation<? super kotlin.Pair<java.lang.Double, java.lang.Double>> $completion) {
        return null;
    }
    
    private static final java.lang.Object reverseGeocode(android.content.Context context, double latitude, double longitude, kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
}