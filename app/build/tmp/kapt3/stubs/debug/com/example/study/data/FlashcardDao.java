package com.example.study.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\fH\'J\u0014\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\fH\'J\u0018\u0010\u000f\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0010\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u001c\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\f2\u0006\u0010\u0012\u001a\u00020\u0013H\'J$\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\f2\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u0013H\'J\u000e\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\fH\'J\u001c\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\f2\u0006\u0010\b\u001a\u00020\tH\'J\u001c\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\f2\u0006\u0010\b\u001a\u00020\tH\'J\u0016\u0010\u0019\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u001a\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u001b"}, d2 = {"Lcom/example/study/data/FlashcardDao;", "", "delete", "", "flashcard", "Lcom/example/study/data/Flashcard;", "(Lcom/example/study/data/Flashcard;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAllForDeck", "deckId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllFlashcardsByCreation", "Lkotlinx/coroutines/flow/Flow;", "", "getAllFlashcardsByReview", "getById", "id", "getDueFlashcards", "date", "Ljava/util/Date;", "getDueFlashcardsForDeck", "getFlashcardCount", "", "getFlashcardsForDeckByCreation", "getFlashcardsForDeckByReview", "insert", "update", "app_debug"})
@androidx.room.Dao()
public abstract interface FlashcardDao {
    
    @androidx.room.Query(value = "SELECT * FROM flashcards ORDER BY nextReviewDate ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.study.data.Flashcard>> getAllFlashcardsByReview();
    
    @androidx.room.Query(value = "SELECT * FROM flashcards ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.study.data.Flashcard>> getAllFlashcardsByCreation();
    
    @androidx.room.Query(value = "SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY nextReviewDate ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.study.data.Flashcard>> getFlashcardsForDeckByReview(long deckId);
    
    @androidx.room.Query(value = "SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.study.data.Flashcard>> getFlashcardsForDeckByCreation(long deckId);
    
    @androidx.room.Query(value = "SELECT * FROM flashcards WHERE nextReviewDate <= :date OR nextReviewDate IS NULL")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.study.data.Flashcard>> getDueFlashcards(@org.jetbrains.annotations.NotNull()
    java.util.Date date);
    
    @androidx.room.Query(value = "SELECT * FROM flashcards WHERE deckId = :deckId AND (nextReviewDate <= :date OR nextReviewDate IS NULL)")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.study.data.Flashcard>> getDueFlashcardsForDeck(long deckId, @org.jetbrains.annotations.NotNull()
    java.util.Date date);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.example.study.data.Flashcard flashcard, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.example.study.data.Flashcard flashcard, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.example.study.data.Flashcard flashcard, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM flashcards WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.study.data.Flashcard> $completion);
    
    @androidx.room.Query(value = "DELETE FROM flashcards WHERE deckId = :deckId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllForDeck(long deckId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM flashcards")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Integer> getFlashcardCount();
}