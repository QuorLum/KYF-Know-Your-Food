package com.kyf.knowyourfood.data.local;

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
import com.kyf.knowyourfood.data.local.dao.PlateDao;
import com.kyf.knowyourfood.data.local.dao.PlateDao_Impl;
import com.kyf.knowyourfood.data.local.dao.ProductDao;
import com.kyf.knowyourfood.data.local.dao.ProductDao_Impl;
import com.kyf.knowyourfood.data.local.dao.ProfileDao;
import com.kyf.knowyourfood.data.local.dao.ProfileDao_Impl;
import com.kyf.knowyourfood.data.local.dao.RawFoodDao;
import com.kyf.knowyourfood.data.local.dao.RawFoodDao_Impl;
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
public final class AppDatabase_Impl extends AppDatabase {
  private volatile ProfileDao _profileDao;

  private volatile ProductDao _productDao;

  private volatile RawFoodDao _rawFoodDao;

  private volatile PlateDao _plateDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `profiles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `avatar_path` TEXT, `age` INTEGER NOT NULL, `gender` TEXT NOT NULL, `weight` REAL NOT NULL, `height` REAL NOT NULL, `allergies_json` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `products` (`barcode` TEXT NOT NULL, `name` TEXT NOT NULL, `brand` TEXT NOT NULL, `category` TEXT NOT NULL, `nutri_score` TEXT NOT NULL, `sugars_100g` REAL NOT NULL, `fat_100g` REAL NOT NULL, `sat_fat_100g` REAL NOT NULL, `salt_100g` REAL NOT NULL, `protein_100g` REAL NOT NULL, `energy_kcal_100g` REAL NOT NULL, `fiber_100g` REAL NOT NULL, `ingredients_text` TEXT NOT NULL, `allergens_json` TEXT NOT NULL, `healthier_alternatives_json` TEXT, PRIMARY KEY(`barcode`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `raw_foods` (`fdc_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, `serving_g` REAL NOT NULL, `protein` REAL NOT NULL, `carbs` REAL NOT NULL, `fat` REAL NOT NULL, `fiber` REAL NOT NULL, `iron` REAL NOT NULL, `vit_c` REAL NOT NULL, `energy_kcal` REAL NOT NULL, `nutrients_json` TEXT NOT NULL, `source` TEXT NOT NULL, PRIMARY KEY(`fdc_id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `plate` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `profile_id` INTEGER NOT NULL, `food_id` INTEGER NOT NULL, `quantity_g` REAL NOT NULL, FOREIGN KEY(`profile_id`) REFERENCES `profiles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`food_id`) REFERENCES `raw_foods`(`fdc_id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_plate_profile_id` ON `plate` (`profile_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_plate_food_id` ON `plate` (`food_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '6e9c9e26c70565eebf0487ef33dbf4dc')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `profiles`");
        db.execSQL("DROP TABLE IF EXISTS `products`");
        db.execSQL("DROP TABLE IF EXISTS `raw_foods`");
        db.execSQL("DROP TABLE IF EXISTS `plate`");
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
        final HashMap<String, TableInfo.Column> _columnsProfiles = new HashMap<String, TableInfo.Column>(8);
        _columnsProfiles.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProfiles.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProfiles.put("avatar_path", new TableInfo.Column("avatar_path", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProfiles.put("age", new TableInfo.Column("age", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProfiles.put("gender", new TableInfo.Column("gender", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProfiles.put("weight", new TableInfo.Column("weight", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProfiles.put("height", new TableInfo.Column("height", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProfiles.put("allergies_json", new TableInfo.Column("allergies_json", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProfiles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesProfiles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoProfiles = new TableInfo("profiles", _columnsProfiles, _foreignKeysProfiles, _indicesProfiles);
        final TableInfo _existingProfiles = TableInfo.read(db, "profiles");
        if (!_infoProfiles.equals(_existingProfiles)) {
          return new RoomOpenHelper.ValidationResult(false, "profiles(com.kyf.knowyourfood.data.local.entity.ProfileEntity).\n"
                  + " Expected:\n" + _infoProfiles + "\n"
                  + " Found:\n" + _existingProfiles);
        }
        final HashMap<String, TableInfo.Column> _columnsProducts = new HashMap<String, TableInfo.Column>(15);
        _columnsProducts.put("barcode", new TableInfo.Column("barcode", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("brand", new TableInfo.Column("brand", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("nutri_score", new TableInfo.Column("nutri_score", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("sugars_100g", new TableInfo.Column("sugars_100g", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("fat_100g", new TableInfo.Column("fat_100g", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("sat_fat_100g", new TableInfo.Column("sat_fat_100g", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("salt_100g", new TableInfo.Column("salt_100g", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("protein_100g", new TableInfo.Column("protein_100g", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("energy_kcal_100g", new TableInfo.Column("energy_kcal_100g", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("fiber_100g", new TableInfo.Column("fiber_100g", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("ingredients_text", new TableInfo.Column("ingredients_text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("allergens_json", new TableInfo.Column("allergens_json", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("healthier_alternatives_json", new TableInfo.Column("healthier_alternatives_json", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProducts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesProducts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoProducts = new TableInfo("products", _columnsProducts, _foreignKeysProducts, _indicesProducts);
        final TableInfo _existingProducts = TableInfo.read(db, "products");
        if (!_infoProducts.equals(_existingProducts)) {
          return new RoomOpenHelper.ValidationResult(false, "products(com.kyf.knowyourfood.data.local.entity.ProductEntity).\n"
                  + " Expected:\n" + _infoProducts + "\n"
                  + " Found:\n" + _existingProducts);
        }
        final HashMap<String, TableInfo.Column> _columnsRawFoods = new HashMap<String, TableInfo.Column>(13);
        _columnsRawFoods.put("fdc_id", new TableInfo.Column("fdc_id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRawFoods.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRawFoods.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRawFoods.put("serving_g", new TableInfo.Column("serving_g", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRawFoods.put("protein", new TableInfo.Column("protein", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRawFoods.put("carbs", new TableInfo.Column("carbs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRawFoods.put("fat", new TableInfo.Column("fat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRawFoods.put("fiber", new TableInfo.Column("fiber", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRawFoods.put("iron", new TableInfo.Column("iron", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRawFoods.put("vit_c", new TableInfo.Column("vit_c", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRawFoods.put("energy_kcal", new TableInfo.Column("energy_kcal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRawFoods.put("nutrients_json", new TableInfo.Column("nutrients_json", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRawFoods.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRawFoods = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRawFoods = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRawFoods = new TableInfo("raw_foods", _columnsRawFoods, _foreignKeysRawFoods, _indicesRawFoods);
        final TableInfo _existingRawFoods = TableInfo.read(db, "raw_foods");
        if (!_infoRawFoods.equals(_existingRawFoods)) {
          return new RoomOpenHelper.ValidationResult(false, "raw_foods(com.kyf.knowyourfood.data.local.entity.RawFoodEntity).\n"
                  + " Expected:\n" + _infoRawFoods + "\n"
                  + " Found:\n" + _existingRawFoods);
        }
        final HashMap<String, TableInfo.Column> _columnsPlate = new HashMap<String, TableInfo.Column>(4);
        _columnsPlate.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlate.put("profile_id", new TableInfo.Column("profile_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlate.put("food_id", new TableInfo.Column("food_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlate.put("quantity_g", new TableInfo.Column("quantity_g", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPlate = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysPlate.add(new TableInfo.ForeignKey("profiles", "CASCADE", "NO ACTION", Arrays.asList("profile_id"), Arrays.asList("id")));
        _foreignKeysPlate.add(new TableInfo.ForeignKey("raw_foods", "CASCADE", "NO ACTION", Arrays.asList("food_id"), Arrays.asList("fdc_id")));
        final HashSet<TableInfo.Index> _indicesPlate = new HashSet<TableInfo.Index>(2);
        _indicesPlate.add(new TableInfo.Index("index_plate_profile_id", false, Arrays.asList("profile_id"), Arrays.asList("ASC")));
        _indicesPlate.add(new TableInfo.Index("index_plate_food_id", false, Arrays.asList("food_id"), Arrays.asList("ASC")));
        final TableInfo _infoPlate = new TableInfo("plate", _columnsPlate, _foreignKeysPlate, _indicesPlate);
        final TableInfo _existingPlate = TableInfo.read(db, "plate");
        if (!_infoPlate.equals(_existingPlate)) {
          return new RoomOpenHelper.ValidationResult(false, "plate(com.kyf.knowyourfood.data.local.entity.PlateItemEntity).\n"
                  + " Expected:\n" + _infoPlate + "\n"
                  + " Found:\n" + _existingPlate);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "6e9c9e26c70565eebf0487ef33dbf4dc", "38698d3504c8822e7169dcecb82965cc");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "profiles","products","raw_foods","plate");
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
      _db.execSQL("DELETE FROM `profiles`");
      _db.execSQL("DELETE FROM `products`");
      _db.execSQL("DELETE FROM `raw_foods`");
      _db.execSQL("DELETE FROM `plate`");
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
    _typeConvertersMap.put(ProfileDao.class, ProfileDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ProductDao.class, ProductDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RawFoodDao.class, RawFoodDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PlateDao.class, PlateDao_Impl.getRequiredConverters());
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
  public ProfileDao profileDao() {
    if (_profileDao != null) {
      return _profileDao;
    } else {
      synchronized(this) {
        if(_profileDao == null) {
          _profileDao = new ProfileDao_Impl(this);
        }
        return _profileDao;
      }
    }
  }

  @Override
  public ProductDao productDao() {
    if (_productDao != null) {
      return _productDao;
    } else {
      synchronized(this) {
        if(_productDao == null) {
          _productDao = new ProductDao_Impl(this);
        }
        return _productDao;
      }
    }
  }

  @Override
  public RawFoodDao rawFoodDao() {
    if (_rawFoodDao != null) {
      return _rawFoodDao;
    } else {
      synchronized(this) {
        if(_rawFoodDao == null) {
          _rawFoodDao = new RawFoodDao_Impl(this);
        }
        return _rawFoodDao;
      }
    }
  }

  @Override
  public PlateDao plateDao() {
    if (_plateDao != null) {
      return _plateDao;
    } else {
      synchronized(this) {
        if(_plateDao == null) {
          _plateDao = new PlateDao_Impl(this);
        }
        return _plateDao;
      }
    }
  }
}
