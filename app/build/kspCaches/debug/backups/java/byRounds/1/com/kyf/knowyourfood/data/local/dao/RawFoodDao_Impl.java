package com.kyf.knowyourfood.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.kyf.knowyourfood.data.local.entity.RawFoodEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class RawFoodDao_Impl implements RawFoodDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RawFoodEntity> __insertionAdapterOfRawFoodEntity;

  public RawFoodDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRawFoodEntity = new EntityInsertionAdapter<RawFoodEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `raw_foods` (`fdc_id`,`name`,`category`,`serving_g`,`protein`,`carbs`,`fat`,`fiber`,`iron`,`vit_c`,`energy_kcal`,`nutrients_json`,`source`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RawFoodEntity entity) {
        statement.bindLong(1, entity.getFdcId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getCategory());
        statement.bindDouble(4, entity.getServingG());
        statement.bindDouble(5, entity.getProtein());
        statement.bindDouble(6, entity.getCarbs());
        statement.bindDouble(7, entity.getFat());
        statement.bindDouble(8, entity.getFiber());
        statement.bindDouble(9, entity.getIron());
        statement.bindDouble(10, entity.getVitC());
        statement.bindDouble(11, entity.getEnergyKcal());
        statement.bindString(12, entity.getNutrientsJson());
        statement.bindString(13, entity.getSource());
      }
    };
  }

  @Override
  public Object insertRawFood(final RawFoodEntity food,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRawFoodEntity.insert(food);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RawFoodEntity>> getAllRawFoods() {
    final String _sql = "SELECT * FROM raw_foods ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"raw_foods"}, new Callable<List<RawFoodEntity>>() {
      @Override
      @NonNull
      public List<RawFoodEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFdcId = CursorUtil.getColumnIndexOrThrow(_cursor, "fdc_id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "serving_g");
          final int _cursorIndexOfProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "protein");
          final int _cursorIndexOfCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "carbs");
          final int _cursorIndexOfFat = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfFiber = CursorUtil.getColumnIndexOrThrow(_cursor, "fiber");
          final int _cursorIndexOfIron = CursorUtil.getColumnIndexOrThrow(_cursor, "iron");
          final int _cursorIndexOfVitC = CursorUtil.getColumnIndexOrThrow(_cursor, "vit_c");
          final int _cursorIndexOfEnergyKcal = CursorUtil.getColumnIndexOrThrow(_cursor, "energy_kcal");
          final int _cursorIndexOfNutrientsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "nutrients_json");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final List<RawFoodEntity> _result = new ArrayList<RawFoodEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RawFoodEntity _item;
            final long _tmpFdcId;
            _tmpFdcId = _cursor.getLong(_cursorIndexOfFdcId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final double _tmpServingG;
            _tmpServingG = _cursor.getDouble(_cursorIndexOfServingG);
            final double _tmpProtein;
            _tmpProtein = _cursor.getDouble(_cursorIndexOfProtein);
            final double _tmpCarbs;
            _tmpCarbs = _cursor.getDouble(_cursorIndexOfCarbs);
            final double _tmpFat;
            _tmpFat = _cursor.getDouble(_cursorIndexOfFat);
            final double _tmpFiber;
            _tmpFiber = _cursor.getDouble(_cursorIndexOfFiber);
            final double _tmpIron;
            _tmpIron = _cursor.getDouble(_cursorIndexOfIron);
            final double _tmpVitC;
            _tmpVitC = _cursor.getDouble(_cursorIndexOfVitC);
            final double _tmpEnergyKcal;
            _tmpEnergyKcal = _cursor.getDouble(_cursorIndexOfEnergyKcal);
            final String _tmpNutrientsJson;
            _tmpNutrientsJson = _cursor.getString(_cursorIndexOfNutrientsJson);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            _item = new RawFoodEntity(_tmpFdcId,_tmpName,_tmpCategory,_tmpServingG,_tmpProtein,_tmpCarbs,_tmpFat,_tmpFiber,_tmpIron,_tmpVitC,_tmpEnergyKcal,_tmpNutrientsJson,_tmpSource);
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
  public Object getRawFoodById(final long fdcId,
      final Continuation<? super RawFoodEntity> $completion) {
    final String _sql = "SELECT * FROM raw_foods WHERE fdc_id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, fdcId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RawFoodEntity>() {
      @Override
      @Nullable
      public RawFoodEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFdcId = CursorUtil.getColumnIndexOrThrow(_cursor, "fdc_id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "serving_g");
          final int _cursorIndexOfProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "protein");
          final int _cursorIndexOfCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "carbs");
          final int _cursorIndexOfFat = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfFiber = CursorUtil.getColumnIndexOrThrow(_cursor, "fiber");
          final int _cursorIndexOfIron = CursorUtil.getColumnIndexOrThrow(_cursor, "iron");
          final int _cursorIndexOfVitC = CursorUtil.getColumnIndexOrThrow(_cursor, "vit_c");
          final int _cursorIndexOfEnergyKcal = CursorUtil.getColumnIndexOrThrow(_cursor, "energy_kcal");
          final int _cursorIndexOfNutrientsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "nutrients_json");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final RawFoodEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpFdcId;
            _tmpFdcId = _cursor.getLong(_cursorIndexOfFdcId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final double _tmpServingG;
            _tmpServingG = _cursor.getDouble(_cursorIndexOfServingG);
            final double _tmpProtein;
            _tmpProtein = _cursor.getDouble(_cursorIndexOfProtein);
            final double _tmpCarbs;
            _tmpCarbs = _cursor.getDouble(_cursorIndexOfCarbs);
            final double _tmpFat;
            _tmpFat = _cursor.getDouble(_cursorIndexOfFat);
            final double _tmpFiber;
            _tmpFiber = _cursor.getDouble(_cursorIndexOfFiber);
            final double _tmpIron;
            _tmpIron = _cursor.getDouble(_cursorIndexOfIron);
            final double _tmpVitC;
            _tmpVitC = _cursor.getDouble(_cursorIndexOfVitC);
            final double _tmpEnergyKcal;
            _tmpEnergyKcal = _cursor.getDouble(_cursorIndexOfEnergyKcal);
            final String _tmpNutrientsJson;
            _tmpNutrientsJson = _cursor.getString(_cursorIndexOfNutrientsJson);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            _result = new RawFoodEntity(_tmpFdcId,_tmpName,_tmpCategory,_tmpServingG,_tmpProtein,_tmpCarbs,_tmpFat,_tmpFiber,_tmpIron,_tmpVitC,_tmpEnergyKcal,_tmpNutrientsJson,_tmpSource);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRawFoodsByIds(final List<Long> fdcIds,
      final Continuation<? super List<RawFoodEntity>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM raw_foods WHERE fdc_id IN (");
    final int _inputSize = fdcIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (long _item : fdcIds) {
      _statement.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RawFoodEntity>>() {
      @Override
      @NonNull
      public List<RawFoodEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFdcId = CursorUtil.getColumnIndexOrThrow(_cursor, "fdc_id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "serving_g");
          final int _cursorIndexOfProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "protein");
          final int _cursorIndexOfCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "carbs");
          final int _cursorIndexOfFat = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfFiber = CursorUtil.getColumnIndexOrThrow(_cursor, "fiber");
          final int _cursorIndexOfIron = CursorUtil.getColumnIndexOrThrow(_cursor, "iron");
          final int _cursorIndexOfVitC = CursorUtil.getColumnIndexOrThrow(_cursor, "vit_c");
          final int _cursorIndexOfEnergyKcal = CursorUtil.getColumnIndexOrThrow(_cursor, "energy_kcal");
          final int _cursorIndexOfNutrientsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "nutrients_json");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final List<RawFoodEntity> _result = new ArrayList<RawFoodEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RawFoodEntity _item_1;
            final long _tmpFdcId;
            _tmpFdcId = _cursor.getLong(_cursorIndexOfFdcId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final double _tmpServingG;
            _tmpServingG = _cursor.getDouble(_cursorIndexOfServingG);
            final double _tmpProtein;
            _tmpProtein = _cursor.getDouble(_cursorIndexOfProtein);
            final double _tmpCarbs;
            _tmpCarbs = _cursor.getDouble(_cursorIndexOfCarbs);
            final double _tmpFat;
            _tmpFat = _cursor.getDouble(_cursorIndexOfFat);
            final double _tmpFiber;
            _tmpFiber = _cursor.getDouble(_cursorIndexOfFiber);
            final double _tmpIron;
            _tmpIron = _cursor.getDouble(_cursorIndexOfIron);
            final double _tmpVitC;
            _tmpVitC = _cursor.getDouble(_cursorIndexOfVitC);
            final double _tmpEnergyKcal;
            _tmpEnergyKcal = _cursor.getDouble(_cursorIndexOfEnergyKcal);
            final String _tmpNutrientsJson;
            _tmpNutrientsJson = _cursor.getString(_cursorIndexOfNutrientsJson);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            _item_1 = new RawFoodEntity(_tmpFdcId,_tmpName,_tmpCategory,_tmpServingG,_tmpProtein,_tmpCarbs,_tmpFat,_tmpFiber,_tmpIron,_tmpVitC,_tmpEnergyKcal,_tmpNutrientsJson,_tmpSource);
            _result.add(_item_1);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RawFoodEntity>> searchRawFoods(final String query, final String category) {
    final String _sql = "\n"
            + "        SELECT * FROM raw_foods \n"
            + "        WHERE (? = '' OR name LIKE '%' || ? || '%')\n"
            + "        AND (? = '' OR category = ?)\n"
            + "        ORDER BY name ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    _argIndex = 3;
    _statement.bindString(_argIndex, category);
    _argIndex = 4;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"raw_foods"}, new Callable<List<RawFoodEntity>>() {
      @Override
      @NonNull
      public List<RawFoodEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFdcId = CursorUtil.getColumnIndexOrThrow(_cursor, "fdc_id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "serving_g");
          final int _cursorIndexOfProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "protein");
          final int _cursorIndexOfCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "carbs");
          final int _cursorIndexOfFat = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfFiber = CursorUtil.getColumnIndexOrThrow(_cursor, "fiber");
          final int _cursorIndexOfIron = CursorUtil.getColumnIndexOrThrow(_cursor, "iron");
          final int _cursorIndexOfVitC = CursorUtil.getColumnIndexOrThrow(_cursor, "vit_c");
          final int _cursorIndexOfEnergyKcal = CursorUtil.getColumnIndexOrThrow(_cursor, "energy_kcal");
          final int _cursorIndexOfNutrientsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "nutrients_json");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final List<RawFoodEntity> _result = new ArrayList<RawFoodEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RawFoodEntity _item;
            final long _tmpFdcId;
            _tmpFdcId = _cursor.getLong(_cursorIndexOfFdcId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final double _tmpServingG;
            _tmpServingG = _cursor.getDouble(_cursorIndexOfServingG);
            final double _tmpProtein;
            _tmpProtein = _cursor.getDouble(_cursorIndexOfProtein);
            final double _tmpCarbs;
            _tmpCarbs = _cursor.getDouble(_cursorIndexOfCarbs);
            final double _tmpFat;
            _tmpFat = _cursor.getDouble(_cursorIndexOfFat);
            final double _tmpFiber;
            _tmpFiber = _cursor.getDouble(_cursorIndexOfFiber);
            final double _tmpIron;
            _tmpIron = _cursor.getDouble(_cursorIndexOfIron);
            final double _tmpVitC;
            _tmpVitC = _cursor.getDouble(_cursorIndexOfVitC);
            final double _tmpEnergyKcal;
            _tmpEnergyKcal = _cursor.getDouble(_cursorIndexOfEnergyKcal);
            final String _tmpNutrientsJson;
            _tmpNutrientsJson = _cursor.getString(_cursorIndexOfNutrientsJson);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            _item = new RawFoodEntity(_tmpFdcId,_tmpName,_tmpCategory,_tmpServingG,_tmpProtein,_tmpCarbs,_tmpFat,_tmpFiber,_tmpIron,_tmpVitC,_tmpEnergyKcal,_tmpNutrientsJson,_tmpSource);
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
  public Flow<List<String>> getAllProduceCategories() {
    final String _sql = "SELECT DISTINCT category FROM raw_foods ORDER BY category ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"raw_foods"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
