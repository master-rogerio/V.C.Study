package com.example.study.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FlashcardDao_Impl implements FlashcardDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Flashcard> __insertionAdapterOfFlashcard;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<Flashcard> __deletionAdapterOfFlashcard;

  private final EntityDeletionOrUpdateAdapter<Flashcard> __updateAdapterOfFlashcard;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllForDeck;

  public FlashcardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFlashcard = new EntityInsertionAdapter<Flashcard>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `flashcards` (`id`,`deckId`,`type`,`front`,`back`,`frontImageUrl`,`frontAudioUrl`,`backImageUrl`,`backAudioUrl`,`clozeText`,`clozeAnswer`,`options`,`correctOptionIndex`,`lastReviewed`,`nextReviewDate`,`easeFactor`,`interval`,`repetitions`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Flashcard entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDeckId());
        final String _tmp = __converters.fromFlashcardType(entity.getType());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        if (entity.getFront() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getFront());
        }
        if (entity.getBack() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getBack());
        }
        if (entity.getFrontImageUrl() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getFrontImageUrl());
        }
        if (entity.getFrontAudioUrl() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getFrontAudioUrl());
        }
        if (entity.getBackImageUrl() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getBackImageUrl());
        }
        if (entity.getBackAudioUrl() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getBackAudioUrl());
        }
        if (entity.getClozeText() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getClozeText());
        }
        if (entity.getClozeAnswer() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getClozeAnswer());
        }
        final String _tmp_1 = __converters.toStringList(entity.getOptions());
        if (_tmp_1 == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, _tmp_1);
        }
        if (entity.getCorrectOptionIndex() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getCorrectOptionIndex());
        }
        final Long _tmp_2 = __converters.dateToTimestamp(entity.getLastReviewed());
        if (_tmp_2 == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, _tmp_2);
        }
        final Long _tmp_3 = __converters.dateToTimestamp(entity.getNextReviewDate());
        if (_tmp_3 == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, _tmp_3);
        }
        statement.bindDouble(16, entity.getEaseFactor());
        statement.bindLong(17, entity.getInterval());
        statement.bindLong(18, entity.getRepetitions());
        statement.bindLong(19, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfFlashcard = new EntityDeletionOrUpdateAdapter<Flashcard>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `flashcards` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Flashcard entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfFlashcard = new EntityDeletionOrUpdateAdapter<Flashcard>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `flashcards` SET `id` = ?,`deckId` = ?,`type` = ?,`front` = ?,`back` = ?,`frontImageUrl` = ?,`frontAudioUrl` = ?,`backImageUrl` = ?,`backAudioUrl` = ?,`clozeText` = ?,`clozeAnswer` = ?,`options` = ?,`correctOptionIndex` = ?,`lastReviewed` = ?,`nextReviewDate` = ?,`easeFactor` = ?,`interval` = ?,`repetitions` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Flashcard entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDeckId());
        final String _tmp = __converters.fromFlashcardType(entity.getType());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        if (entity.getFront() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getFront());
        }
        if (entity.getBack() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getBack());
        }
        if (entity.getFrontImageUrl() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getFrontImageUrl());
        }
        if (entity.getFrontAudioUrl() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getFrontAudioUrl());
        }
        if (entity.getBackImageUrl() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getBackImageUrl());
        }
        if (entity.getBackAudioUrl() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getBackAudioUrl());
        }
        if (entity.getClozeText() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getClozeText());
        }
        if (entity.getClozeAnswer() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getClozeAnswer());
        }
        final String _tmp_1 = __converters.toStringList(entity.getOptions());
        if (_tmp_1 == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, _tmp_1);
        }
        if (entity.getCorrectOptionIndex() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getCorrectOptionIndex());
        }
        final Long _tmp_2 = __converters.dateToTimestamp(entity.getLastReviewed());
        if (_tmp_2 == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, _tmp_2);
        }
        final Long _tmp_3 = __converters.dateToTimestamp(entity.getNextReviewDate());
        if (_tmp_3 == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, _tmp_3);
        }
        statement.bindDouble(16, entity.getEaseFactor());
        statement.bindLong(17, entity.getInterval());
        statement.bindLong(18, entity.getRepetitions());
        statement.bindLong(19, entity.getCreatedAt());
        statement.bindLong(20, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllForDeck = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM flashcards WHERE deckId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Flashcard flashcard, final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFlashcard.insert(flashcard);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object delete(final Flashcard flashcard, final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFlashcard.handle(flashcard);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object update(final Flashcard flashcard, final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFlashcard.handle(flashcard);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object deleteAllForDeck(final long deckId, final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllForDeck.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, deckId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllForDeck.release(_stmt);
        }
      }
    }, arg1);
  }

  @Override
  public Flow<List<Flashcard>> getAllFlashcardsByReview() {
    final String _sql = "SELECT `flashcards`.`id` AS `id`, `flashcards`.`deckId` AS `deckId`, `flashcards`.`type` AS `type`, `flashcards`.`front` AS `front`, `flashcards`.`back` AS `back`, `flashcards`.`frontImageUrl` AS `frontImageUrl`, `flashcards`.`frontAudioUrl` AS `frontAudioUrl`, `flashcards`.`backImageUrl` AS `backImageUrl`, `flashcards`.`backAudioUrl` AS `backAudioUrl`, `flashcards`.`clozeText` AS `clozeText`, `flashcards`.`clozeAnswer` AS `clozeAnswer`, `flashcards`.`options` AS `options`, `flashcards`.`correctOptionIndex` AS `correctOptionIndex`, `flashcards`.`lastReviewed` AS `lastReviewed`, `flashcards`.`nextReviewDate` AS `nextReviewDate`, `flashcards`.`easeFactor` AS `easeFactor`, `flashcards`.`interval` AS `interval`, `flashcards`.`repetitions` AS `repetitions`, `flashcards`.`createdAt` AS `createdAt` FROM flashcards ORDER BY nextReviewDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flashcards"}, new Callable<List<Flashcard>>() {
      @Override
      @NonNull
      public List<Flashcard> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfDeckId = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfFront = 3;
          final int _cursorIndexOfBack = 4;
          final int _cursorIndexOfFrontImageUrl = 5;
          final int _cursorIndexOfFrontAudioUrl = 6;
          final int _cursorIndexOfBackImageUrl = 7;
          final int _cursorIndexOfBackAudioUrl = 8;
          final int _cursorIndexOfClozeText = 9;
          final int _cursorIndexOfClozeAnswer = 10;
          final int _cursorIndexOfOptions = 11;
          final int _cursorIndexOfCorrectOptionIndex = 12;
          final int _cursorIndexOfLastReviewed = 13;
          final int _cursorIndexOfNextReviewDate = 14;
          final int _cursorIndexOfEaseFactor = 15;
          final int _cursorIndexOfInterval = 16;
          final int _cursorIndexOfRepetitions = 17;
          final int _cursorIndexOfCreatedAt = 18;
          final List<Flashcard> _result = new ArrayList<Flashcard>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Flashcard _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDeckId;
            _tmpDeckId = _cursor.getLong(_cursorIndexOfDeckId);
            final FlashcardType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            _tmpType = __converters.toFlashcardType(_tmp);
            final String _tmpFront;
            if (_cursor.isNull(_cursorIndexOfFront)) {
              _tmpFront = null;
            } else {
              _tmpFront = _cursor.getString(_cursorIndexOfFront);
            }
            final String _tmpBack;
            if (_cursor.isNull(_cursorIndexOfBack)) {
              _tmpBack = null;
            } else {
              _tmpBack = _cursor.getString(_cursorIndexOfBack);
            }
            final String _tmpFrontImageUrl;
            if (_cursor.isNull(_cursorIndexOfFrontImageUrl)) {
              _tmpFrontImageUrl = null;
            } else {
              _tmpFrontImageUrl = _cursor.getString(_cursorIndexOfFrontImageUrl);
            }
            final String _tmpFrontAudioUrl;
            if (_cursor.isNull(_cursorIndexOfFrontAudioUrl)) {
              _tmpFrontAudioUrl = null;
            } else {
              _tmpFrontAudioUrl = _cursor.getString(_cursorIndexOfFrontAudioUrl);
            }
            final String _tmpBackImageUrl;
            if (_cursor.isNull(_cursorIndexOfBackImageUrl)) {
              _tmpBackImageUrl = null;
            } else {
              _tmpBackImageUrl = _cursor.getString(_cursorIndexOfBackImageUrl);
            }
            final String _tmpBackAudioUrl;
            if (_cursor.isNull(_cursorIndexOfBackAudioUrl)) {
              _tmpBackAudioUrl = null;
            } else {
              _tmpBackAudioUrl = _cursor.getString(_cursorIndexOfBackAudioUrl);
            }
            final String _tmpClozeText;
            if (_cursor.isNull(_cursorIndexOfClozeText)) {
              _tmpClozeText = null;
            } else {
              _tmpClozeText = _cursor.getString(_cursorIndexOfClozeText);
            }
            final String _tmpClozeAnswer;
            if (_cursor.isNull(_cursorIndexOfClozeAnswer)) {
              _tmpClozeAnswer = null;
            } else {
              _tmpClozeAnswer = _cursor.getString(_cursorIndexOfClozeAnswer);
            }
            final List<String> _tmpOptions;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfOptions)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
            }
            _tmpOptions = __converters.fromStringList(_tmp_1);
            final Integer _tmpCorrectOptionIndex;
            if (_cursor.isNull(_cursorIndexOfCorrectOptionIndex)) {
              _tmpCorrectOptionIndex = null;
            } else {
              _tmpCorrectOptionIndex = _cursor.getInt(_cursorIndexOfCorrectOptionIndex);
            }
            final Date _tmpLastReviewed;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfLastReviewed)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfLastReviewed);
            }
            _tmpLastReviewed = __converters.fromTimestamp(_tmp_2);
            final Date _tmpNextReviewDate;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfNextReviewDate)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfNextReviewDate);
            }
            _tmpNextReviewDate = __converters.fromTimestamp(_tmp_3);
            final float _tmpEaseFactor;
            _tmpEaseFactor = _cursor.getFloat(_cursorIndexOfEaseFactor);
            final int _tmpInterval;
            _tmpInterval = _cursor.getInt(_cursorIndexOfInterval);
            final int _tmpRepetitions;
            _tmpRepetitions = _cursor.getInt(_cursorIndexOfRepetitions);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Flashcard(_tmpId,_tmpDeckId,_tmpType,_tmpFront,_tmpBack,_tmpFrontImageUrl,_tmpFrontAudioUrl,_tmpBackImageUrl,_tmpBackAudioUrl,_tmpClozeText,_tmpClozeAnswer,_tmpOptions,_tmpCorrectOptionIndex,_tmpLastReviewed,_tmpNextReviewDate,_tmpEaseFactor,_tmpInterval,_tmpRepetitions,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Flashcard>> getAllFlashcardsByCreation() {
    final String _sql = "SELECT `flashcards`.`id` AS `id`, `flashcards`.`deckId` AS `deckId`, `flashcards`.`type` AS `type`, `flashcards`.`front` AS `front`, `flashcards`.`back` AS `back`, `flashcards`.`frontImageUrl` AS `frontImageUrl`, `flashcards`.`frontAudioUrl` AS `frontAudioUrl`, `flashcards`.`backImageUrl` AS `backImageUrl`, `flashcards`.`backAudioUrl` AS `backAudioUrl`, `flashcards`.`clozeText` AS `clozeText`, `flashcards`.`clozeAnswer` AS `clozeAnswer`, `flashcards`.`options` AS `options`, `flashcards`.`correctOptionIndex` AS `correctOptionIndex`, `flashcards`.`lastReviewed` AS `lastReviewed`, `flashcards`.`nextReviewDate` AS `nextReviewDate`, `flashcards`.`easeFactor` AS `easeFactor`, `flashcards`.`interval` AS `interval`, `flashcards`.`repetitions` AS `repetitions`, `flashcards`.`createdAt` AS `createdAt` FROM flashcards ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flashcards"}, new Callable<List<Flashcard>>() {
      @Override
      @NonNull
      public List<Flashcard> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfDeckId = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfFront = 3;
          final int _cursorIndexOfBack = 4;
          final int _cursorIndexOfFrontImageUrl = 5;
          final int _cursorIndexOfFrontAudioUrl = 6;
          final int _cursorIndexOfBackImageUrl = 7;
          final int _cursorIndexOfBackAudioUrl = 8;
          final int _cursorIndexOfClozeText = 9;
          final int _cursorIndexOfClozeAnswer = 10;
          final int _cursorIndexOfOptions = 11;
          final int _cursorIndexOfCorrectOptionIndex = 12;
          final int _cursorIndexOfLastReviewed = 13;
          final int _cursorIndexOfNextReviewDate = 14;
          final int _cursorIndexOfEaseFactor = 15;
          final int _cursorIndexOfInterval = 16;
          final int _cursorIndexOfRepetitions = 17;
          final int _cursorIndexOfCreatedAt = 18;
          final List<Flashcard> _result = new ArrayList<Flashcard>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Flashcard _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDeckId;
            _tmpDeckId = _cursor.getLong(_cursorIndexOfDeckId);
            final FlashcardType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            _tmpType = __converters.toFlashcardType(_tmp);
            final String _tmpFront;
            if (_cursor.isNull(_cursorIndexOfFront)) {
              _tmpFront = null;
            } else {
              _tmpFront = _cursor.getString(_cursorIndexOfFront);
            }
            final String _tmpBack;
            if (_cursor.isNull(_cursorIndexOfBack)) {
              _tmpBack = null;
            } else {
              _tmpBack = _cursor.getString(_cursorIndexOfBack);
            }
            final String _tmpFrontImageUrl;
            if (_cursor.isNull(_cursorIndexOfFrontImageUrl)) {
              _tmpFrontImageUrl = null;
            } else {
              _tmpFrontImageUrl = _cursor.getString(_cursorIndexOfFrontImageUrl);
            }
            final String _tmpFrontAudioUrl;
            if (_cursor.isNull(_cursorIndexOfFrontAudioUrl)) {
              _tmpFrontAudioUrl = null;
            } else {
              _tmpFrontAudioUrl = _cursor.getString(_cursorIndexOfFrontAudioUrl);
            }
            final String _tmpBackImageUrl;
            if (_cursor.isNull(_cursorIndexOfBackImageUrl)) {
              _tmpBackImageUrl = null;
            } else {
              _tmpBackImageUrl = _cursor.getString(_cursorIndexOfBackImageUrl);
            }
            final String _tmpBackAudioUrl;
            if (_cursor.isNull(_cursorIndexOfBackAudioUrl)) {
              _tmpBackAudioUrl = null;
            } else {
              _tmpBackAudioUrl = _cursor.getString(_cursorIndexOfBackAudioUrl);
            }
            final String _tmpClozeText;
            if (_cursor.isNull(_cursorIndexOfClozeText)) {
              _tmpClozeText = null;
            } else {
              _tmpClozeText = _cursor.getString(_cursorIndexOfClozeText);
            }
            final String _tmpClozeAnswer;
            if (_cursor.isNull(_cursorIndexOfClozeAnswer)) {
              _tmpClozeAnswer = null;
            } else {
              _tmpClozeAnswer = _cursor.getString(_cursorIndexOfClozeAnswer);
            }
            final List<String> _tmpOptions;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfOptions)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
            }
            _tmpOptions = __converters.fromStringList(_tmp_1);
            final Integer _tmpCorrectOptionIndex;
            if (_cursor.isNull(_cursorIndexOfCorrectOptionIndex)) {
              _tmpCorrectOptionIndex = null;
            } else {
              _tmpCorrectOptionIndex = _cursor.getInt(_cursorIndexOfCorrectOptionIndex);
            }
            final Date _tmpLastReviewed;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfLastReviewed)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfLastReviewed);
            }
            _tmpLastReviewed = __converters.fromTimestamp(_tmp_2);
            final Date _tmpNextReviewDate;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfNextReviewDate)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfNextReviewDate);
            }
            _tmpNextReviewDate = __converters.fromTimestamp(_tmp_3);
            final float _tmpEaseFactor;
            _tmpEaseFactor = _cursor.getFloat(_cursorIndexOfEaseFactor);
            final int _tmpInterval;
            _tmpInterval = _cursor.getInt(_cursorIndexOfInterval);
            final int _tmpRepetitions;
            _tmpRepetitions = _cursor.getInt(_cursorIndexOfRepetitions);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Flashcard(_tmpId,_tmpDeckId,_tmpType,_tmpFront,_tmpBack,_tmpFrontImageUrl,_tmpFrontAudioUrl,_tmpBackImageUrl,_tmpBackAudioUrl,_tmpClozeText,_tmpClozeAnswer,_tmpOptions,_tmpCorrectOptionIndex,_tmpLastReviewed,_tmpNextReviewDate,_tmpEaseFactor,_tmpInterval,_tmpRepetitions,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Flashcard>> getFlashcardsForDeckByReview(final long deckId) {
    final String _sql = "SELECT * FROM flashcards WHERE deckId = ? ORDER BY nextReviewDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, deckId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flashcards"}, new Callable<List<Flashcard>>() {
      @Override
      @NonNull
      public List<Flashcard> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeckId = CursorUtil.getColumnIndexOrThrow(_cursor, "deckId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfFront = CursorUtil.getColumnIndexOrThrow(_cursor, "front");
          final int _cursorIndexOfBack = CursorUtil.getColumnIndexOrThrow(_cursor, "back");
          final int _cursorIndexOfFrontImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "frontImageUrl");
          final int _cursorIndexOfFrontAudioUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "frontAudioUrl");
          final int _cursorIndexOfBackImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "backImageUrl");
          final int _cursorIndexOfBackAudioUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "backAudioUrl");
          final int _cursorIndexOfClozeText = CursorUtil.getColumnIndexOrThrow(_cursor, "clozeText");
          final int _cursorIndexOfClozeAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "clozeAnswer");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfCorrectOptionIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctOptionIndex");
          final int _cursorIndexOfLastReviewed = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewed");
          final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewDate");
          final int _cursorIndexOfEaseFactor = CursorUtil.getColumnIndexOrThrow(_cursor, "easeFactor");
          final int _cursorIndexOfInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "interval");
          final int _cursorIndexOfRepetitions = CursorUtil.getColumnIndexOrThrow(_cursor, "repetitions");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Flashcard> _result = new ArrayList<Flashcard>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Flashcard _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDeckId;
            _tmpDeckId = _cursor.getLong(_cursorIndexOfDeckId);
            final FlashcardType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            _tmpType = __converters.toFlashcardType(_tmp);
            final String _tmpFront;
            if (_cursor.isNull(_cursorIndexOfFront)) {
              _tmpFront = null;
            } else {
              _tmpFront = _cursor.getString(_cursorIndexOfFront);
            }
            final String _tmpBack;
            if (_cursor.isNull(_cursorIndexOfBack)) {
              _tmpBack = null;
            } else {
              _tmpBack = _cursor.getString(_cursorIndexOfBack);
            }
            final String _tmpFrontImageUrl;
            if (_cursor.isNull(_cursorIndexOfFrontImageUrl)) {
              _tmpFrontImageUrl = null;
            } else {
              _tmpFrontImageUrl = _cursor.getString(_cursorIndexOfFrontImageUrl);
            }
            final String _tmpFrontAudioUrl;
            if (_cursor.isNull(_cursorIndexOfFrontAudioUrl)) {
              _tmpFrontAudioUrl = null;
            } else {
              _tmpFrontAudioUrl = _cursor.getString(_cursorIndexOfFrontAudioUrl);
            }
            final String _tmpBackImageUrl;
            if (_cursor.isNull(_cursorIndexOfBackImageUrl)) {
              _tmpBackImageUrl = null;
            } else {
              _tmpBackImageUrl = _cursor.getString(_cursorIndexOfBackImageUrl);
            }
            final String _tmpBackAudioUrl;
            if (_cursor.isNull(_cursorIndexOfBackAudioUrl)) {
              _tmpBackAudioUrl = null;
            } else {
              _tmpBackAudioUrl = _cursor.getString(_cursorIndexOfBackAudioUrl);
            }
            final String _tmpClozeText;
            if (_cursor.isNull(_cursorIndexOfClozeText)) {
              _tmpClozeText = null;
            } else {
              _tmpClozeText = _cursor.getString(_cursorIndexOfClozeText);
            }
            final String _tmpClozeAnswer;
            if (_cursor.isNull(_cursorIndexOfClozeAnswer)) {
              _tmpClozeAnswer = null;
            } else {
              _tmpClozeAnswer = _cursor.getString(_cursorIndexOfClozeAnswer);
            }
            final List<String> _tmpOptions;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfOptions)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
            }
            _tmpOptions = __converters.fromStringList(_tmp_1);
            final Integer _tmpCorrectOptionIndex;
            if (_cursor.isNull(_cursorIndexOfCorrectOptionIndex)) {
              _tmpCorrectOptionIndex = null;
            } else {
              _tmpCorrectOptionIndex = _cursor.getInt(_cursorIndexOfCorrectOptionIndex);
            }
            final Date _tmpLastReviewed;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfLastReviewed)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfLastReviewed);
            }
            _tmpLastReviewed = __converters.fromTimestamp(_tmp_2);
            final Date _tmpNextReviewDate;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfNextReviewDate)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfNextReviewDate);
            }
            _tmpNextReviewDate = __converters.fromTimestamp(_tmp_3);
            final float _tmpEaseFactor;
            _tmpEaseFactor = _cursor.getFloat(_cursorIndexOfEaseFactor);
            final int _tmpInterval;
            _tmpInterval = _cursor.getInt(_cursorIndexOfInterval);
            final int _tmpRepetitions;
            _tmpRepetitions = _cursor.getInt(_cursorIndexOfRepetitions);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Flashcard(_tmpId,_tmpDeckId,_tmpType,_tmpFront,_tmpBack,_tmpFrontImageUrl,_tmpFrontAudioUrl,_tmpBackImageUrl,_tmpBackAudioUrl,_tmpClozeText,_tmpClozeAnswer,_tmpOptions,_tmpCorrectOptionIndex,_tmpLastReviewed,_tmpNextReviewDate,_tmpEaseFactor,_tmpInterval,_tmpRepetitions,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Flashcard>> getFlashcardsForDeckByCreation(final long deckId) {
    final String _sql = "SELECT * FROM flashcards WHERE deckId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, deckId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flashcards"}, new Callable<List<Flashcard>>() {
      @Override
      @NonNull
      public List<Flashcard> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeckId = CursorUtil.getColumnIndexOrThrow(_cursor, "deckId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfFront = CursorUtil.getColumnIndexOrThrow(_cursor, "front");
          final int _cursorIndexOfBack = CursorUtil.getColumnIndexOrThrow(_cursor, "back");
          final int _cursorIndexOfFrontImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "frontImageUrl");
          final int _cursorIndexOfFrontAudioUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "frontAudioUrl");
          final int _cursorIndexOfBackImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "backImageUrl");
          final int _cursorIndexOfBackAudioUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "backAudioUrl");
          final int _cursorIndexOfClozeText = CursorUtil.getColumnIndexOrThrow(_cursor, "clozeText");
          final int _cursorIndexOfClozeAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "clozeAnswer");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfCorrectOptionIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctOptionIndex");
          final int _cursorIndexOfLastReviewed = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewed");
          final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewDate");
          final int _cursorIndexOfEaseFactor = CursorUtil.getColumnIndexOrThrow(_cursor, "easeFactor");
          final int _cursorIndexOfInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "interval");
          final int _cursorIndexOfRepetitions = CursorUtil.getColumnIndexOrThrow(_cursor, "repetitions");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Flashcard> _result = new ArrayList<Flashcard>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Flashcard _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDeckId;
            _tmpDeckId = _cursor.getLong(_cursorIndexOfDeckId);
            final FlashcardType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            _tmpType = __converters.toFlashcardType(_tmp);
            final String _tmpFront;
            if (_cursor.isNull(_cursorIndexOfFront)) {
              _tmpFront = null;
            } else {
              _tmpFront = _cursor.getString(_cursorIndexOfFront);
            }
            final String _tmpBack;
            if (_cursor.isNull(_cursorIndexOfBack)) {
              _tmpBack = null;
            } else {
              _tmpBack = _cursor.getString(_cursorIndexOfBack);
            }
            final String _tmpFrontImageUrl;
            if (_cursor.isNull(_cursorIndexOfFrontImageUrl)) {
              _tmpFrontImageUrl = null;
            } else {
              _tmpFrontImageUrl = _cursor.getString(_cursorIndexOfFrontImageUrl);
            }
            final String _tmpFrontAudioUrl;
            if (_cursor.isNull(_cursorIndexOfFrontAudioUrl)) {
              _tmpFrontAudioUrl = null;
            } else {
              _tmpFrontAudioUrl = _cursor.getString(_cursorIndexOfFrontAudioUrl);
            }
            final String _tmpBackImageUrl;
            if (_cursor.isNull(_cursorIndexOfBackImageUrl)) {
              _tmpBackImageUrl = null;
            } else {
              _tmpBackImageUrl = _cursor.getString(_cursorIndexOfBackImageUrl);
            }
            final String _tmpBackAudioUrl;
            if (_cursor.isNull(_cursorIndexOfBackAudioUrl)) {
              _tmpBackAudioUrl = null;
            } else {
              _tmpBackAudioUrl = _cursor.getString(_cursorIndexOfBackAudioUrl);
            }
            final String _tmpClozeText;
            if (_cursor.isNull(_cursorIndexOfClozeText)) {
              _tmpClozeText = null;
            } else {
              _tmpClozeText = _cursor.getString(_cursorIndexOfClozeText);
            }
            final String _tmpClozeAnswer;
            if (_cursor.isNull(_cursorIndexOfClozeAnswer)) {
              _tmpClozeAnswer = null;
            } else {
              _tmpClozeAnswer = _cursor.getString(_cursorIndexOfClozeAnswer);
            }
            final List<String> _tmpOptions;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfOptions)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
            }
            _tmpOptions = __converters.fromStringList(_tmp_1);
            final Integer _tmpCorrectOptionIndex;
            if (_cursor.isNull(_cursorIndexOfCorrectOptionIndex)) {
              _tmpCorrectOptionIndex = null;
            } else {
              _tmpCorrectOptionIndex = _cursor.getInt(_cursorIndexOfCorrectOptionIndex);
            }
            final Date _tmpLastReviewed;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfLastReviewed)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfLastReviewed);
            }
            _tmpLastReviewed = __converters.fromTimestamp(_tmp_2);
            final Date _tmpNextReviewDate;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfNextReviewDate)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfNextReviewDate);
            }
            _tmpNextReviewDate = __converters.fromTimestamp(_tmp_3);
            final float _tmpEaseFactor;
            _tmpEaseFactor = _cursor.getFloat(_cursorIndexOfEaseFactor);
            final int _tmpInterval;
            _tmpInterval = _cursor.getInt(_cursorIndexOfInterval);
            final int _tmpRepetitions;
            _tmpRepetitions = _cursor.getInt(_cursorIndexOfRepetitions);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Flashcard(_tmpId,_tmpDeckId,_tmpType,_tmpFront,_tmpBack,_tmpFrontImageUrl,_tmpFrontAudioUrl,_tmpBackImageUrl,_tmpBackAudioUrl,_tmpClozeText,_tmpClozeAnswer,_tmpOptions,_tmpCorrectOptionIndex,_tmpLastReviewed,_tmpNextReviewDate,_tmpEaseFactor,_tmpInterval,_tmpRepetitions,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Flashcard>> getDueFlashcards(final Date date) {
    final String _sql = "SELECT * FROM flashcards WHERE nextReviewDate <= ? OR nextReviewDate IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final Long _tmp = __converters.dateToTimestamp(date);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, _tmp);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flashcards"}, new Callable<List<Flashcard>>() {
      @Override
      @NonNull
      public List<Flashcard> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeckId = CursorUtil.getColumnIndexOrThrow(_cursor, "deckId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfFront = CursorUtil.getColumnIndexOrThrow(_cursor, "front");
          final int _cursorIndexOfBack = CursorUtil.getColumnIndexOrThrow(_cursor, "back");
          final int _cursorIndexOfFrontImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "frontImageUrl");
          final int _cursorIndexOfFrontAudioUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "frontAudioUrl");
          final int _cursorIndexOfBackImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "backImageUrl");
          final int _cursorIndexOfBackAudioUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "backAudioUrl");
          final int _cursorIndexOfClozeText = CursorUtil.getColumnIndexOrThrow(_cursor, "clozeText");
          final int _cursorIndexOfClozeAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "clozeAnswer");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfCorrectOptionIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctOptionIndex");
          final int _cursorIndexOfLastReviewed = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewed");
          final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewDate");
          final int _cursorIndexOfEaseFactor = CursorUtil.getColumnIndexOrThrow(_cursor, "easeFactor");
          final int _cursorIndexOfInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "interval");
          final int _cursorIndexOfRepetitions = CursorUtil.getColumnIndexOrThrow(_cursor, "repetitions");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Flashcard> _result = new ArrayList<Flashcard>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Flashcard _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDeckId;
            _tmpDeckId = _cursor.getLong(_cursorIndexOfDeckId);
            final FlashcardType _tmpType;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfType);
            }
            _tmpType = __converters.toFlashcardType(_tmp_1);
            final String _tmpFront;
            if (_cursor.isNull(_cursorIndexOfFront)) {
              _tmpFront = null;
            } else {
              _tmpFront = _cursor.getString(_cursorIndexOfFront);
            }
            final String _tmpBack;
            if (_cursor.isNull(_cursorIndexOfBack)) {
              _tmpBack = null;
            } else {
              _tmpBack = _cursor.getString(_cursorIndexOfBack);
            }
            final String _tmpFrontImageUrl;
            if (_cursor.isNull(_cursorIndexOfFrontImageUrl)) {
              _tmpFrontImageUrl = null;
            } else {
              _tmpFrontImageUrl = _cursor.getString(_cursorIndexOfFrontImageUrl);
            }
            final String _tmpFrontAudioUrl;
            if (_cursor.isNull(_cursorIndexOfFrontAudioUrl)) {
              _tmpFrontAudioUrl = null;
            } else {
              _tmpFrontAudioUrl = _cursor.getString(_cursorIndexOfFrontAudioUrl);
            }
            final String _tmpBackImageUrl;
            if (_cursor.isNull(_cursorIndexOfBackImageUrl)) {
              _tmpBackImageUrl = null;
            } else {
              _tmpBackImageUrl = _cursor.getString(_cursorIndexOfBackImageUrl);
            }
            final String _tmpBackAudioUrl;
            if (_cursor.isNull(_cursorIndexOfBackAudioUrl)) {
              _tmpBackAudioUrl = null;
            } else {
              _tmpBackAudioUrl = _cursor.getString(_cursorIndexOfBackAudioUrl);
            }
            final String _tmpClozeText;
            if (_cursor.isNull(_cursorIndexOfClozeText)) {
              _tmpClozeText = null;
            } else {
              _tmpClozeText = _cursor.getString(_cursorIndexOfClozeText);
            }
            final String _tmpClozeAnswer;
            if (_cursor.isNull(_cursorIndexOfClozeAnswer)) {
              _tmpClozeAnswer = null;
            } else {
              _tmpClozeAnswer = _cursor.getString(_cursorIndexOfClozeAnswer);
            }
            final List<String> _tmpOptions;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfOptions)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfOptions);
            }
            _tmpOptions = __converters.fromStringList(_tmp_2);
            final Integer _tmpCorrectOptionIndex;
            if (_cursor.isNull(_cursorIndexOfCorrectOptionIndex)) {
              _tmpCorrectOptionIndex = null;
            } else {
              _tmpCorrectOptionIndex = _cursor.getInt(_cursorIndexOfCorrectOptionIndex);
            }
            final Date _tmpLastReviewed;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfLastReviewed)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfLastReviewed);
            }
            _tmpLastReviewed = __converters.fromTimestamp(_tmp_3);
            final Date _tmpNextReviewDate;
            final Long _tmp_4;
            if (_cursor.isNull(_cursorIndexOfNextReviewDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfNextReviewDate);
            }
            _tmpNextReviewDate = __converters.fromTimestamp(_tmp_4);
            final float _tmpEaseFactor;
            _tmpEaseFactor = _cursor.getFloat(_cursorIndexOfEaseFactor);
            final int _tmpInterval;
            _tmpInterval = _cursor.getInt(_cursorIndexOfInterval);
            final int _tmpRepetitions;
            _tmpRepetitions = _cursor.getInt(_cursorIndexOfRepetitions);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Flashcard(_tmpId,_tmpDeckId,_tmpType,_tmpFront,_tmpBack,_tmpFrontImageUrl,_tmpFrontAudioUrl,_tmpBackImageUrl,_tmpBackAudioUrl,_tmpClozeText,_tmpClozeAnswer,_tmpOptions,_tmpCorrectOptionIndex,_tmpLastReviewed,_tmpNextReviewDate,_tmpEaseFactor,_tmpInterval,_tmpRepetitions,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Flashcard>> getDueFlashcardsForDeck(final long deckId, final Date date) {
    final String _sql = "SELECT * FROM flashcards WHERE deckId = ? AND (nextReviewDate <= ? OR nextReviewDate IS NULL)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, deckId);
    _argIndex = 2;
    final Long _tmp = __converters.dateToTimestamp(date);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, _tmp);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flashcards"}, new Callable<List<Flashcard>>() {
      @Override
      @NonNull
      public List<Flashcard> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeckId = CursorUtil.getColumnIndexOrThrow(_cursor, "deckId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfFront = CursorUtil.getColumnIndexOrThrow(_cursor, "front");
          final int _cursorIndexOfBack = CursorUtil.getColumnIndexOrThrow(_cursor, "back");
          final int _cursorIndexOfFrontImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "frontImageUrl");
          final int _cursorIndexOfFrontAudioUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "frontAudioUrl");
          final int _cursorIndexOfBackImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "backImageUrl");
          final int _cursorIndexOfBackAudioUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "backAudioUrl");
          final int _cursorIndexOfClozeText = CursorUtil.getColumnIndexOrThrow(_cursor, "clozeText");
          final int _cursorIndexOfClozeAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "clozeAnswer");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfCorrectOptionIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctOptionIndex");
          final int _cursorIndexOfLastReviewed = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewed");
          final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewDate");
          final int _cursorIndexOfEaseFactor = CursorUtil.getColumnIndexOrThrow(_cursor, "easeFactor");
          final int _cursorIndexOfInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "interval");
          final int _cursorIndexOfRepetitions = CursorUtil.getColumnIndexOrThrow(_cursor, "repetitions");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Flashcard> _result = new ArrayList<Flashcard>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Flashcard _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDeckId;
            _tmpDeckId = _cursor.getLong(_cursorIndexOfDeckId);
            final FlashcardType _tmpType;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfType);
            }
            _tmpType = __converters.toFlashcardType(_tmp_1);
            final String _tmpFront;
            if (_cursor.isNull(_cursorIndexOfFront)) {
              _tmpFront = null;
            } else {
              _tmpFront = _cursor.getString(_cursorIndexOfFront);
            }
            final String _tmpBack;
            if (_cursor.isNull(_cursorIndexOfBack)) {
              _tmpBack = null;
            } else {
              _tmpBack = _cursor.getString(_cursorIndexOfBack);
            }
            final String _tmpFrontImageUrl;
            if (_cursor.isNull(_cursorIndexOfFrontImageUrl)) {
              _tmpFrontImageUrl = null;
            } else {
              _tmpFrontImageUrl = _cursor.getString(_cursorIndexOfFrontImageUrl);
            }
            final String _tmpFrontAudioUrl;
            if (_cursor.isNull(_cursorIndexOfFrontAudioUrl)) {
              _tmpFrontAudioUrl = null;
            } else {
              _tmpFrontAudioUrl = _cursor.getString(_cursorIndexOfFrontAudioUrl);
            }
            final String _tmpBackImageUrl;
            if (_cursor.isNull(_cursorIndexOfBackImageUrl)) {
              _tmpBackImageUrl = null;
            } else {
              _tmpBackImageUrl = _cursor.getString(_cursorIndexOfBackImageUrl);
            }
            final String _tmpBackAudioUrl;
            if (_cursor.isNull(_cursorIndexOfBackAudioUrl)) {
              _tmpBackAudioUrl = null;
            } else {
              _tmpBackAudioUrl = _cursor.getString(_cursorIndexOfBackAudioUrl);
            }
            final String _tmpClozeText;
            if (_cursor.isNull(_cursorIndexOfClozeText)) {
              _tmpClozeText = null;
            } else {
              _tmpClozeText = _cursor.getString(_cursorIndexOfClozeText);
            }
            final String _tmpClozeAnswer;
            if (_cursor.isNull(_cursorIndexOfClozeAnswer)) {
              _tmpClozeAnswer = null;
            } else {
              _tmpClozeAnswer = _cursor.getString(_cursorIndexOfClozeAnswer);
            }
            final List<String> _tmpOptions;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfOptions)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfOptions);
            }
            _tmpOptions = __converters.fromStringList(_tmp_2);
            final Integer _tmpCorrectOptionIndex;
            if (_cursor.isNull(_cursorIndexOfCorrectOptionIndex)) {
              _tmpCorrectOptionIndex = null;
            } else {
              _tmpCorrectOptionIndex = _cursor.getInt(_cursorIndexOfCorrectOptionIndex);
            }
            final Date _tmpLastReviewed;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfLastReviewed)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfLastReviewed);
            }
            _tmpLastReviewed = __converters.fromTimestamp(_tmp_3);
            final Date _tmpNextReviewDate;
            final Long _tmp_4;
            if (_cursor.isNull(_cursorIndexOfNextReviewDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfNextReviewDate);
            }
            _tmpNextReviewDate = __converters.fromTimestamp(_tmp_4);
            final float _tmpEaseFactor;
            _tmpEaseFactor = _cursor.getFloat(_cursorIndexOfEaseFactor);
            final int _tmpInterval;
            _tmpInterval = _cursor.getInt(_cursorIndexOfInterval);
            final int _tmpRepetitions;
            _tmpRepetitions = _cursor.getInt(_cursorIndexOfRepetitions);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Flashcard(_tmpId,_tmpDeckId,_tmpType,_tmpFront,_tmpBack,_tmpFrontImageUrl,_tmpFrontAudioUrl,_tmpBackImageUrl,_tmpBackAudioUrl,_tmpClozeText,_tmpClozeAnswer,_tmpOptions,_tmpCorrectOptionIndex,_tmpLastReviewed,_tmpNextReviewDate,_tmpEaseFactor,_tmpInterval,_tmpRepetitions,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final long id, final Continuation<? super Flashcard> arg1) {
    final String _sql = "SELECT * FROM flashcards WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Flashcard>() {
      @Override
      @Nullable
      public Flashcard call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeckId = CursorUtil.getColumnIndexOrThrow(_cursor, "deckId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfFront = CursorUtil.getColumnIndexOrThrow(_cursor, "front");
          final int _cursorIndexOfBack = CursorUtil.getColumnIndexOrThrow(_cursor, "back");
          final int _cursorIndexOfFrontImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "frontImageUrl");
          final int _cursorIndexOfFrontAudioUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "frontAudioUrl");
          final int _cursorIndexOfBackImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "backImageUrl");
          final int _cursorIndexOfBackAudioUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "backAudioUrl");
          final int _cursorIndexOfClozeText = CursorUtil.getColumnIndexOrThrow(_cursor, "clozeText");
          final int _cursorIndexOfClozeAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "clozeAnswer");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfCorrectOptionIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctOptionIndex");
          final int _cursorIndexOfLastReviewed = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewed");
          final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewDate");
          final int _cursorIndexOfEaseFactor = CursorUtil.getColumnIndexOrThrow(_cursor, "easeFactor");
          final int _cursorIndexOfInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "interval");
          final int _cursorIndexOfRepetitions = CursorUtil.getColumnIndexOrThrow(_cursor, "repetitions");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final Flashcard _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDeckId;
            _tmpDeckId = _cursor.getLong(_cursorIndexOfDeckId);
            final FlashcardType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            _tmpType = __converters.toFlashcardType(_tmp);
            final String _tmpFront;
            if (_cursor.isNull(_cursorIndexOfFront)) {
              _tmpFront = null;
            } else {
              _tmpFront = _cursor.getString(_cursorIndexOfFront);
            }
            final String _tmpBack;
            if (_cursor.isNull(_cursorIndexOfBack)) {
              _tmpBack = null;
            } else {
              _tmpBack = _cursor.getString(_cursorIndexOfBack);
            }
            final String _tmpFrontImageUrl;
            if (_cursor.isNull(_cursorIndexOfFrontImageUrl)) {
              _tmpFrontImageUrl = null;
            } else {
              _tmpFrontImageUrl = _cursor.getString(_cursorIndexOfFrontImageUrl);
            }
            final String _tmpFrontAudioUrl;
            if (_cursor.isNull(_cursorIndexOfFrontAudioUrl)) {
              _tmpFrontAudioUrl = null;
            } else {
              _tmpFrontAudioUrl = _cursor.getString(_cursorIndexOfFrontAudioUrl);
            }
            final String _tmpBackImageUrl;
            if (_cursor.isNull(_cursorIndexOfBackImageUrl)) {
              _tmpBackImageUrl = null;
            } else {
              _tmpBackImageUrl = _cursor.getString(_cursorIndexOfBackImageUrl);
            }
            final String _tmpBackAudioUrl;
            if (_cursor.isNull(_cursorIndexOfBackAudioUrl)) {
              _tmpBackAudioUrl = null;
            } else {
              _tmpBackAudioUrl = _cursor.getString(_cursorIndexOfBackAudioUrl);
            }
            final String _tmpClozeText;
            if (_cursor.isNull(_cursorIndexOfClozeText)) {
              _tmpClozeText = null;
            } else {
              _tmpClozeText = _cursor.getString(_cursorIndexOfClozeText);
            }
            final String _tmpClozeAnswer;
            if (_cursor.isNull(_cursorIndexOfClozeAnswer)) {
              _tmpClozeAnswer = null;
            } else {
              _tmpClozeAnswer = _cursor.getString(_cursorIndexOfClozeAnswer);
            }
            final List<String> _tmpOptions;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfOptions)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
            }
            _tmpOptions = __converters.fromStringList(_tmp_1);
            final Integer _tmpCorrectOptionIndex;
            if (_cursor.isNull(_cursorIndexOfCorrectOptionIndex)) {
              _tmpCorrectOptionIndex = null;
            } else {
              _tmpCorrectOptionIndex = _cursor.getInt(_cursorIndexOfCorrectOptionIndex);
            }
            final Date _tmpLastReviewed;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfLastReviewed)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfLastReviewed);
            }
            _tmpLastReviewed = __converters.fromTimestamp(_tmp_2);
            final Date _tmpNextReviewDate;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfNextReviewDate)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfNextReviewDate);
            }
            _tmpNextReviewDate = __converters.fromTimestamp(_tmp_3);
            final float _tmpEaseFactor;
            _tmpEaseFactor = _cursor.getFloat(_cursorIndexOfEaseFactor);
            final int _tmpInterval;
            _tmpInterval = _cursor.getInt(_cursorIndexOfInterval);
            final int _tmpRepetitions;
            _tmpRepetitions = _cursor.getInt(_cursorIndexOfRepetitions);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new Flashcard(_tmpId,_tmpDeckId,_tmpType,_tmpFront,_tmpBack,_tmpFrontImageUrl,_tmpFrontAudioUrl,_tmpBackImageUrl,_tmpBackAudioUrl,_tmpClozeText,_tmpClozeAnswer,_tmpOptions,_tmpCorrectOptionIndex,_tmpLastReviewed,_tmpNextReviewDate,_tmpEaseFactor,_tmpInterval,_tmpRepetitions,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @Override
  public Flow<Integer> getFlashcardCount() {
    final String _sql = "SELECT COUNT(*) FROM flashcards";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flashcards"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
