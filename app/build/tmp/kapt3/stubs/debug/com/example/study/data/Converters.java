package com.example.study.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\b\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0019\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0007\u00a2\u0006\u0002\u0010\u000bJ\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0007J\u001a\u0010\u0010\u001a\n\u0012\u0004\u0012\u00020\u000f\u0018\u00010\u00112\b\u0010\u000e\u001a\u0004\u0018\u00010\rH\u0007J\u001a\u0010\u0012\u001a\n\u0012\u0004\u0012\u00020\r\u0018\u00010\u00112\b\u0010\u000e\u001a\u0004\u0018\u00010\rH\u0007J\u0019\u0010\u0013\u001a\u0004\u0018\u00010\n2\b\u0010\u000e\u001a\u0004\u0018\u00010\bH\u0007\u00a2\u0006\u0002\u0010\u0014J\u0010\u0010\u0015\u001a\u00020\u000f2\u0006\u0010\u000e\u001a\u00020\rH\u0007J\u001a\u0010\u0016\u001a\u0004\u0018\u00010\r2\u000e\u0010\u0017\u001a\n\u0012\u0004\u0012\u00020\u000f\u0018\u00010\u0011H\u0007J\u001a\u0010\u0018\u001a\u0004\u0018\u00010\r2\u000e\u0010\u0017\u001a\n\u0012\u0004\u0012\u00020\r\u0018\u00010\u0011H\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/example/study/data/Converters;", "", "()V", "flashcardTypeConverter", "Lcom/example/study/data/converter/FlashcardTypeConverter;", "listConverter", "Lcom/example/study/data/converter/ListConverter;", "dateToTimestamp", "", "date", "Ljava/util/Date;", "(Ljava/util/Date;)Ljava/lang/Long;", "fromFlashcardType", "", "value", "Lcom/example/study/data/FlashcardType;", "fromFlashcardTypeList", "", "fromStringList", "fromTimestamp", "(Ljava/lang/Long;)Ljava/util/Date;", "toFlashcardType", "toFlashcardTypeList", "list", "toStringList", "app_debug"})
public final class Converters {
    @org.jetbrains.annotations.NotNull()
    private final com.example.study.data.converter.FlashcardTypeConverter flashcardTypeConverter = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.study.data.converter.ListConverter listConverter = null;
    
    public Converters() {
        super();
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date fromTimestamp(@org.jetbrains.annotations.Nullable()
    java.lang.Long value) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long dateToTimestamp(@org.jetbrains.annotations.Nullable()
    java.util.Date date) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String fromFlashcardType(@org.jetbrains.annotations.NotNull()
    com.example.study.data.FlashcardType value) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.NotNull()
    public final com.example.study.data.FlashcardType toFlashcardType(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.String> fromStringList(@org.jetbrains.annotations.Nullable()
    java.lang.String value) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String toStringList(@org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> list) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.example.study.data.FlashcardType> fromFlashcardTypeList(@org.jetbrains.annotations.Nullable()
    java.lang.String value) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String toFlashcardTypeList(@org.jetbrains.annotations.Nullable()
    java.util.List<? extends com.example.study.data.FlashcardType> list) {
        return null;
    }
}