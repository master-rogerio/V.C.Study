package com.example.study.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
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
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FavoriteLocationDao_Impl implements FavoriteLocationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FavoriteLocation> __insertionAdapterOfFavoriteLocation;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<FavoriteLocation> __deletionAdapterOfFavoriteLocation;

  private final EntityDeletionOrUpdateAdapter<FavoriteLocation> __updateAdapterOfFavoriteLocation;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public FavoriteLocationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFavoriteLocation = new EntityInsertionAdapter<FavoriteLocation>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `favorite_locations` (`id`,`name`,`address`,`latitude`,`longitude`,`radius`,`isGeofenceActive`,`iconName`,`preferredCardTypes`,`studySessionCount`,`averagePerformance`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FavoriteLocation entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getAddress() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getAddress());
        }
        statement.bindDouble(4, entity.getLatitude());
        statement.bindDouble(5, entity.getLongitude());
        statement.bindLong(6, entity.getRadius());
        final int _tmp = entity.isGeofenceActive() ? 1 : 0;
        statement.bindLong(7, _tmp);
        if (entity.getIconName() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getIconName());
        }
        final String _tmp_1 = __converters.toFlashcardTypeList(entity.getPreferredCardTypes());
        if (_tmp_1 == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, _tmp_1);
        }
        statement.bindLong(10, entity.getStudySessionCount());
        statement.bindDouble(11, entity.getAveragePerformance());
      }
    };
    this.__deletionAdapterOfFavoriteLocation = new EntityDeletionOrUpdateAdapter<FavoriteLocation>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `favorite_locations` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FavoriteLocation entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfFavoriteLocation = new EntityDeletionOrUpdateAdapter<FavoriteLocation>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `favorite_locations` SET `id` = ?,`name` = ?,`address` = ?,`latitude` = ?,`longitude` = ?,`radius` = ?,`isGeofenceActive` = ?,`iconName` = ?,`preferredCardTypes` = ?,`studySessionCount` = ?,`averagePerformance` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FavoriteLocation entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getAddress() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getAddress());
        }
        statement.bindDouble(4, entity.getLatitude());
        statement.bindDouble(5, entity.getLongitude());
        statement.bindLong(6, entity.getRadius());
        final int _tmp = entity.isGeofenceActive() ? 1 : 0;
        statement.bindLong(7, _tmp);
        if (entity.getIconName() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getIconName());
        }
        final String _tmp_1 = __converters.toFlashcardTypeList(entity.getPreferredCardTypes());
        if (_tmp_1 == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, _tmp_1);
        }
        statement.bindLong(10, entity.getStudySessionCount());
        statement.bindDouble(11, entity.getAveragePerformance());
        if (entity.getId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getId());
        }
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM favorite_locations WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM favorite_locations";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final FavoriteLocation favoriteLocation,
      final Continuation<? super Long> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFavoriteLocation.insertAndReturnId(favoriteLocation);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object delete(final FavoriteLocation favoriteLocation,
      final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFavoriteLocation.handle(favoriteLocation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object update(final FavoriteLocation favoriteLocation,
      final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFavoriteLocation.handle(favoriteLocation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object deleteById(final String id, final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        if (id == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, id);
        }
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
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, arg1);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> arg0) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
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
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, arg0);
  }

  @Override
  public LiveData<List<FavoriteLocation>> getAllFavoriteLocations() {
    final String _sql = "SELECT `favorite_locations`.`id` AS `id`, `favorite_locations`.`name` AS `name`, `favorite_locations`.`address` AS `address`, `favorite_locations`.`latitude` AS `latitude`, `favorite_locations`.`longitude` AS `longitude`, `favorite_locations`.`radius` AS `radius`, `favorite_locations`.`isGeofenceActive` AS `isGeofenceActive`, `favorite_locations`.`iconName` AS `iconName`, `favorite_locations`.`preferredCardTypes` AS `preferredCardTypes`, `favorite_locations`.`studySessionCount` AS `studySessionCount`, `favorite_locations`.`averagePerformance` AS `averagePerformance` FROM favorite_locations";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"favorite_locations"}, false, new Callable<List<FavoriteLocation>>() {
      @Override
      @Nullable
      public List<FavoriteLocation> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfAddress = 2;
          final int _cursorIndexOfLatitude = 3;
          final int _cursorIndexOfLongitude = 4;
          final int _cursorIndexOfRadius = 5;
          final int _cursorIndexOfIsGeofenceActive = 6;
          final int _cursorIndexOfIconName = 7;
          final int _cursorIndexOfPreferredCardTypes = 8;
          final int _cursorIndexOfStudySessionCount = 9;
          final int _cursorIndexOfAveragePerformance = 10;
          final List<FavoriteLocation> _result = new ArrayList<FavoriteLocation>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FavoriteLocation _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final int _tmpRadius;
            _tmpRadius = _cursor.getInt(_cursorIndexOfRadius);
            final boolean _tmpIsGeofenceActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsGeofenceActive);
            _tmpIsGeofenceActive = _tmp != 0;
            final String _tmpIconName;
            if (_cursor.isNull(_cursorIndexOfIconName)) {
              _tmpIconName = null;
            } else {
              _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            }
            final List<FlashcardType> _tmpPreferredCardTypes;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfPreferredCardTypes)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfPreferredCardTypes);
            }
            _tmpPreferredCardTypes = __converters.fromFlashcardTypeList(_tmp_1);
            final int _tmpStudySessionCount;
            _tmpStudySessionCount = _cursor.getInt(_cursorIndexOfStudySessionCount);
            final double _tmpAveragePerformance;
            _tmpAveragePerformance = _cursor.getDouble(_cursorIndexOfAveragePerformance);
            _item = new FavoriteLocation(_tmpId,_tmpName,_tmpAddress,_tmpLatitude,_tmpLongitude,_tmpRadius,_tmpIsGeofenceActive,_tmpIconName,_tmpPreferredCardTypes,_tmpStudySessionCount,_tmpAveragePerformance);
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
  public Flow<List<FavoriteLocation>> getAllFavoriteLocationsFlow() {
    final String _sql = "SELECT `favorite_locations`.`id` AS `id`, `favorite_locations`.`name` AS `name`, `favorite_locations`.`address` AS `address`, `favorite_locations`.`latitude` AS `latitude`, `favorite_locations`.`longitude` AS `longitude`, `favorite_locations`.`radius` AS `radius`, `favorite_locations`.`isGeofenceActive` AS `isGeofenceActive`, `favorite_locations`.`iconName` AS `iconName`, `favorite_locations`.`preferredCardTypes` AS `preferredCardTypes`, `favorite_locations`.`studySessionCount` AS `studySessionCount`, `favorite_locations`.`averagePerformance` AS `averagePerformance` FROM favorite_locations";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"favorite_locations"}, new Callable<List<FavoriteLocation>>() {
      @Override
      @NonNull
      public List<FavoriteLocation> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfAddress = 2;
          final int _cursorIndexOfLatitude = 3;
          final int _cursorIndexOfLongitude = 4;
          final int _cursorIndexOfRadius = 5;
          final int _cursorIndexOfIsGeofenceActive = 6;
          final int _cursorIndexOfIconName = 7;
          final int _cursorIndexOfPreferredCardTypes = 8;
          final int _cursorIndexOfStudySessionCount = 9;
          final int _cursorIndexOfAveragePerformance = 10;
          final List<FavoriteLocation> _result = new ArrayList<FavoriteLocation>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FavoriteLocation _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final int _tmpRadius;
            _tmpRadius = _cursor.getInt(_cursorIndexOfRadius);
            final boolean _tmpIsGeofenceActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsGeofenceActive);
            _tmpIsGeofenceActive = _tmp != 0;
            final String _tmpIconName;
            if (_cursor.isNull(_cursorIndexOfIconName)) {
              _tmpIconName = null;
            } else {
              _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            }
            final List<FlashcardType> _tmpPreferredCardTypes;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfPreferredCardTypes)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfPreferredCardTypes);
            }
            _tmpPreferredCardTypes = __converters.fromFlashcardTypeList(_tmp_1);
            final int _tmpStudySessionCount;
            _tmpStudySessionCount = _cursor.getInt(_cursorIndexOfStudySessionCount);
            final double _tmpAveragePerformance;
            _tmpAveragePerformance = _cursor.getDouble(_cursorIndexOfAveragePerformance);
            _item = new FavoriteLocation(_tmpId,_tmpName,_tmpAddress,_tmpLatitude,_tmpLongitude,_tmpRadius,_tmpIsGeofenceActive,_tmpIconName,_tmpPreferredCardTypes,_tmpStudySessionCount,_tmpAveragePerformance);
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
  public Object getAllFavoriteLocationsSync(
      final Continuation<? super List<FavoriteLocation>> arg0) {
    final String _sql = "SELECT `favorite_locations`.`id` AS `id`, `favorite_locations`.`name` AS `name`, `favorite_locations`.`address` AS `address`, `favorite_locations`.`latitude` AS `latitude`, `favorite_locations`.`longitude` AS `longitude`, `favorite_locations`.`radius` AS `radius`, `favorite_locations`.`isGeofenceActive` AS `isGeofenceActive`, `favorite_locations`.`iconName` AS `iconName`, `favorite_locations`.`preferredCardTypes` AS `preferredCardTypes`, `favorite_locations`.`studySessionCount` AS `studySessionCount`, `favorite_locations`.`averagePerformance` AS `averagePerformance` FROM favorite_locations";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FavoriteLocation>>() {
      @Override
      @NonNull
      public List<FavoriteLocation> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfAddress = 2;
          final int _cursorIndexOfLatitude = 3;
          final int _cursorIndexOfLongitude = 4;
          final int _cursorIndexOfRadius = 5;
          final int _cursorIndexOfIsGeofenceActive = 6;
          final int _cursorIndexOfIconName = 7;
          final int _cursorIndexOfPreferredCardTypes = 8;
          final int _cursorIndexOfStudySessionCount = 9;
          final int _cursorIndexOfAveragePerformance = 10;
          final List<FavoriteLocation> _result = new ArrayList<FavoriteLocation>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FavoriteLocation _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final int _tmpRadius;
            _tmpRadius = _cursor.getInt(_cursorIndexOfRadius);
            final boolean _tmpIsGeofenceActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsGeofenceActive);
            _tmpIsGeofenceActive = _tmp != 0;
            final String _tmpIconName;
            if (_cursor.isNull(_cursorIndexOfIconName)) {
              _tmpIconName = null;
            } else {
              _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            }
            final List<FlashcardType> _tmpPreferredCardTypes;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfPreferredCardTypes)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfPreferredCardTypes);
            }
            _tmpPreferredCardTypes = __converters.fromFlashcardTypeList(_tmp_1);
            final int _tmpStudySessionCount;
            _tmpStudySessionCount = _cursor.getInt(_cursorIndexOfStudySessionCount);
            final double _tmpAveragePerformance;
            _tmpAveragePerformance = _cursor.getDouble(_cursorIndexOfAveragePerformance);
            _item = new FavoriteLocation(_tmpId,_tmpName,_tmpAddress,_tmpLatitude,_tmpLongitude,_tmpRadius,_tmpIsGeofenceActive,_tmpIconName,_tmpPreferredCardTypes,_tmpStudySessionCount,_tmpAveragePerformance);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg0);
  }

  @Override
  public Object getFavoriteLocationById(final String id,
      final Continuation<? super FavoriteLocation> arg1) {
    final String _sql = "SELECT * FROM favorite_locations WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FavoriteLocation>() {
      @Override
      @Nullable
      public FavoriteLocation call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfRadius = CursorUtil.getColumnIndexOrThrow(_cursor, "radius");
          final int _cursorIndexOfIsGeofenceActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isGeofenceActive");
          final int _cursorIndexOfIconName = CursorUtil.getColumnIndexOrThrow(_cursor, "iconName");
          final int _cursorIndexOfPreferredCardTypes = CursorUtil.getColumnIndexOrThrow(_cursor, "preferredCardTypes");
          final int _cursorIndexOfStudySessionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "studySessionCount");
          final int _cursorIndexOfAveragePerformance = CursorUtil.getColumnIndexOrThrow(_cursor, "averagePerformance");
          final FavoriteLocation _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final int _tmpRadius;
            _tmpRadius = _cursor.getInt(_cursorIndexOfRadius);
            final boolean _tmpIsGeofenceActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsGeofenceActive);
            _tmpIsGeofenceActive = _tmp != 0;
            final String _tmpIconName;
            if (_cursor.isNull(_cursorIndexOfIconName)) {
              _tmpIconName = null;
            } else {
              _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            }
            final List<FlashcardType> _tmpPreferredCardTypes;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfPreferredCardTypes)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfPreferredCardTypes);
            }
            _tmpPreferredCardTypes = __converters.fromFlashcardTypeList(_tmp_1);
            final int _tmpStudySessionCount;
            _tmpStudySessionCount = _cursor.getInt(_cursorIndexOfStudySessionCount);
            final double _tmpAveragePerformance;
            _tmpAveragePerformance = _cursor.getDouble(_cursorIndexOfAveragePerformance);
            _result = new FavoriteLocation(_tmpId,_tmpName,_tmpAddress,_tmpLatitude,_tmpLongitude,_tmpRadius,_tmpIsGeofenceActive,_tmpIconName,_tmpPreferredCardTypes,_tmpStudySessionCount,_tmpAveragePerformance);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
