package com.example.study.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \t2\u00020\u0001:\u0001\tB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&\u00a8\u0006\n"}, d2 = {"Lcom/example/study/data/FlashcardDatabase;", "Landroidx/room/RoomDatabase;", "()V", "deckDao", "Lcom/example/study/data/DeckDao;", "flashcardDao", "Lcom/example/study/data/FlashcardDao;", "userLocationDao", "Lcom/example/study/data/UserLocationDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {com.example.study.data.Deck.class, com.example.study.data.Flashcard.class, com.example.study.data.UserLocation.class}, version = 5, exportSchema = false)
@androidx.room.TypeConverters(value = {com.example.study.data.Converters.class})
public abstract class FlashcardDatabase extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.example.study.data.FlashcardDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.study.data.FlashcardDatabase.Companion Companion = null;
    
    public FlashcardDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.study.data.FlashcardDao flashcardDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.study.data.DeckDao deckDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.study.data.UserLocationDao userLocationDao();
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/example/study/data/FlashcardDatabase$Companion;", "", "()V", "INSTANCE", "Lcom/example/study/data/FlashcardDatabase;", "getDatabase", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.study.data.FlashcardDatabase getDatabase(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}