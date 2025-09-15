package com.example.study.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FlashcardDatabase_Impl extends FlashcardDatabase {
  private volatile FlashcardDao _flashcardDao;

  private volatile DeckDao _deckDao;

  private volatile UserLocationDao _userLocationDao;

  private volatile FavoriteLocationDao _favoriteLocationDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(8) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `decks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `theme` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `flashcards` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `deckId` INTEGER NOT NULL, `type` TEXT NOT NULL, `front` TEXT NOT NULL, `back` TEXT NOT NULL, `clozeText` TEXT, `clozeAnswer` TEXT, `options` TEXT, `correctOptionIndex` INTEGER, `lastReviewed` INTEGER, `nextReviewDate` INTEGER, `easeFactor` REAL NOT NULL, `interval` INTEGER NOT NULL, `repetitions` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, FOREIGN KEY(`deckId`) REFERENCES `decks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_flashcards_deckId` ON `flashcards` (`deckId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_location` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `iconName` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `timestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `favorite_locations` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `address` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `radius` INTEGER NOT NULL, `isGeofenceActive` INTEGER NOT NULL, `iconName` TEXT NOT NULL, `preferredCardTypes` TEXT NOT NULL, `studySessionCount` INTEGER NOT NULL, `averagePerformance` REAL NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'b282f69337866e4d12933280890157ae')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `decks`");
        db.execSQL("DROP TABLE IF EXISTS `flashcards`");
        db.execSQL("DROP TABLE IF EXISTS `user_location`");
        db.execSQL("DROP TABLE IF EXISTS `favorite_locations`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsDecks = new HashMap<String, TableInfo.Column>(4);
        _columnsDecks.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecks.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecks.put("theme", new TableInfo.Column("theme", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecks.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDecks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDecks = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDecks = new TableInfo("decks", _columnsDecks, _foreignKeysDecks, _indicesDecks);
        final TableInfo _existingDecks = TableInfo.read(db, "decks");
        if (!_infoDecks.equals(_existingDecks)) {
          return new RoomOpenHelper.ValidationResult(false, "decks(com.example.study.data.Deck).\n"
                  + " Expected:\n" + _infoDecks + "\n"
                  + " Found:\n" + _existingDecks);
        }
        final HashMap<String, TableInfo.Column> _columnsFlashcards = new HashMap<String, TableInfo.Column>(15);
        _columnsFlashcards.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("deckId", new TableInfo.Column("deckId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("front", new TableInfo.Column("front", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("back", new TableInfo.Column("back", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("clozeText", new TableInfo.Column("clozeText", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("clozeAnswer", new TableInfo.Column("clozeAnswer", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("options", new TableInfo.Column("options", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("correctOptionIndex", new TableInfo.Column("correctOptionIndex", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("lastReviewed", new TableInfo.Column("lastReviewed", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("nextReviewDate", new TableInfo.Column("nextReviewDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("easeFactor", new TableInfo.Column("easeFactor", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("interval", new TableInfo.Column("interval", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("repetitions", new TableInfo.Column("repetitions", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFlashcards = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysFlashcards.add(new TableInfo.ForeignKey("decks", "CASCADE", "NO ACTION", Arrays.asList("deckId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesFlashcards = new HashSet<TableInfo.Index>(1);
        _indicesFlashcards.add(new TableInfo.Index("index_flashcards_deckId", false, Arrays.asList("deckId"), Arrays.asList("ASC")));
        final TableInfo _infoFlashcards = new TableInfo("flashcards", _columnsFlashcards, _foreignKeysFlashcards, _indicesFlashcards);
        final TableInfo _existingFlashcards = TableInfo.read(db, "flashcards");
        if (!_infoFlashcards.equals(_existingFlashcards)) {
          return new RoomOpenHelper.ValidationResult(false, "flashcards(com.example.study.data.Flashcard).\n"
                  + " Expected:\n" + _infoFlashcards + "\n"
                  + " Found:\n" + _existingFlashcards);
        }
        final HashMap<String, TableInfo.Column> _columnsUserLocation = new HashMap<String, TableInfo.Column>(6);
        _columnsUserLocation.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserLocation.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserLocation.put("iconName", new TableInfo.Column("iconName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserLocation.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserLocation.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserLocation.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserLocation = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserLocation = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserLocation = new TableInfo("user_location", _columnsUserLocation, _foreignKeysUserLocation, _indicesUserLocation);
        final TableInfo _existingUserLocation = TableInfo.read(db, "user_location");
        if (!_infoUserLocation.equals(_existingUserLocation)) {
          return new RoomOpenHelper.ValidationResult(false, "user_location(com.example.study.data.UserLocation).\n"
                  + " Expected:\n" + _infoUserLocation + "\n"
                  + " Found:\n" + _existingUserLocation);
        }
        final HashMap<String, TableInfo.Column> _columnsFavoriteLocations = new HashMap<String, TableInfo.Column>(11);
        _columnsFavoriteLocations.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavoriteLocations.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavoriteLocations.put("address", new TableInfo.Column("address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavoriteLocations.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavoriteLocations.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavoriteLocations.put("radius", new TableInfo.Column("radius", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavoriteLocations.put("isGeofenceActive", new TableInfo.Column("isGeofenceActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavoriteLocations.put("iconName", new TableInfo.Column("iconName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavoriteLocations.put("preferredCardTypes", new TableInfo.Column("preferredCardTypes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavoriteLocations.put("studySessionCount", new TableInfo.Column("studySessionCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavoriteLocations.put("averagePerformance", new TableInfo.Column("averagePerformance", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFavoriteLocations = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFavoriteLocations = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFavoriteLocations = new TableInfo("favorite_locations", _columnsFavoriteLocations, _foreignKeysFavoriteLocations, _indicesFavoriteLocations);
        final TableInfo _existingFavoriteLocations = TableInfo.read(db, "favorite_locations");
        if (!_infoFavoriteLocations.equals(_existingFavoriteLocations)) {
          return new RoomOpenHelper.ValidationResult(false, "favorite_locations(com.example.study.data.FavoriteLocation).\n"
                  + " Expected:\n" + _infoFavoriteLocations + "\n"
                  + " Found:\n" + _existingFavoriteLocations);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "b282f69337866e4d12933280890157ae", "bb342123abca0a8def1073898faeef2b");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "decks","flashcards","user_location","favorite_locations");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `decks`");
      _db.execSQL("DELETE FROM `flashcards`");
      _db.execSQL("DELETE FROM `user_location`");
      _db.execSQL("DELETE FROM `favorite_locations`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(FlashcardDao.class, FlashcardDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DeckDao.class, DeckDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UserLocationDao.class, UserLocationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FavoriteLocationDao.class, FavoriteLocationDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public FlashcardDao flashcardDao() {
    if (_flashcardDao != null) {
      return _flashcardDao;
    } else {
      synchronized(this) {
        if(_flashcardDao == null) {
          _flashcardDao = new FlashcardDao_Impl(this);
        }
        return _flashcardDao;
      }
    }
  }

  @Override
  public DeckDao deckDao() {
    if (_deckDao != null) {
      return _deckDao;
    } else {
      synchronized(this) {
        if(_deckDao == null) {
          _deckDao = new DeckDao_Impl(this);
        }
        return _deckDao;
      }
    }
  }

  @Override
  public UserLocationDao userLocationDao() {
    if (_userLocationDao != null) {
      return _userLocationDao;
    } else {
      synchronized(this) {
        if(_userLocationDao == null) {
          _userLocationDao = new UserLocationDao_Impl(this);
        }
        return _userLocationDao;
      }
    }
  }

  @Override
  public FavoriteLocationDao favoriteLocationDao() {
    if (_favoriteLocationDao != null) {
      return _favoriteLocationDao;
    } else {
      synchronized(this) {
        if(_favoriteLocationDao == null) {
          _favoriteLocationDao = new FavoriteLocationDao_Impl(this);
        }
        return _favoriteLocationDao;
      }
    }
  }
}
