package com.kyf.knowyourfood.data.local.dao;

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
import com.kyf.knowyourfood.data.local.entity.PlateItemEntity;
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
public final class PlateDao_Impl implements PlateDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PlateItemEntity> __insertionAdapterOfPlateItemEntity;

  private final EntityDeletionOrUpdateAdapter<PlateItemEntity> __updateAdapterOfPlateItemEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeletePlateItemById;

  private final SharedSQLiteStatement __preparedStmtOfClearPlateForProfile;

  public PlateDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPlateItemEntity = new EntityInsertionAdapter<PlateItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `plate` (`id`,`profile_id`,`food_id`,`quantity_g`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PlateItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getProfileId());
        statement.bindLong(3, entity.getFoodId());
        statement.bindDouble(4, entity.getQuantityG());
      }
    };
    this.__updateAdapterOfPlateItemEntity = new EntityDeletionOrUpdateAdapter<PlateItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `plate` SET `id` = ?,`profile_id` = ?,`food_id` = ?,`quantity_g` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PlateItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getProfileId());
        statement.bindLong(3, entity.getFoodId());
        statement.bindDouble(4, entity.getQuantityG());
        statement.bindLong(5, entity.getId());
      }
    };
    this.__preparedStmtOfDeletePlateItemById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM plate WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearPlateForProfile = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM plate WHERE profile_id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdatePlateItem(final PlateItemEntity item,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPlateItemEntity.insertAndReturnId(item);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePlateItem(final PlateItemEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPlateItemEntity.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePlateItemById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePlateItemById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfDeletePlateItemById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearPlateForProfile(final long profileId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearPlateForProfile.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, profileId);
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
          __preparedStmtOfClearPlateForProfile.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PlateItemEntity>> getPlateItemsForProfile(final long profileId) {
    final String _sql = "SELECT * FROM plate WHERE profile_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, profileId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"plate"}, new Callable<List<PlateItemEntity>>() {
      @Override
      @NonNull
      public List<PlateItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProfileId = CursorUtil.getColumnIndexOrThrow(_cursor, "profile_id");
          final int _cursorIndexOfFoodId = CursorUtil.getColumnIndexOrThrow(_cursor, "food_id");
          final int _cursorIndexOfQuantityG = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity_g");
          final List<PlateItemEntity> _result = new ArrayList<PlateItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PlateItemEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpProfileId;
            _tmpProfileId = _cursor.getLong(_cursorIndexOfProfileId);
            final long _tmpFoodId;
            _tmpFoodId = _cursor.getLong(_cursorIndexOfFoodId);
            final double _tmpQuantityG;
            _tmpQuantityG = _cursor.getDouble(_cursorIndexOfQuantityG);
            _item = new PlateItemEntity(_tmpId,_tmpProfileId,_tmpFoodId,_tmpQuantityG);
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
  public Object getPlateItemsList(final long profileId,
      final Continuation<? super List<PlateItemEntity>> $completion) {
    final String _sql = "SELECT * FROM plate WHERE profile_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, profileId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PlateItemEntity>>() {
      @Override
      @NonNull
      public List<PlateItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProfileId = CursorUtil.getColumnIndexOrThrow(_cursor, "profile_id");
          final int _cursorIndexOfFoodId = CursorUtil.getColumnIndexOrThrow(_cursor, "food_id");
          final int _cursorIndexOfQuantityG = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity_g");
          final List<PlateItemEntity> _result = new ArrayList<PlateItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PlateItemEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpProfileId;
            _tmpProfileId = _cursor.getLong(_cursorIndexOfProfileId);
            final long _tmpFoodId;
            _tmpFoodId = _cursor.getLong(_cursorIndexOfFoodId);
            final double _tmpQuantityG;
            _tmpQuantityG = _cursor.getDouble(_cursorIndexOfQuantityG);
            _item = new PlateItemEntity(_tmpId,_tmpProfileId,_tmpFoodId,_tmpQuantityG);
            _result.add(_item);
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
  public Object getPlateItem(final long profileId, final long foodId,
      final Continuation<? super PlateItemEntity> $completion) {
    final String _sql = "SELECT * FROM plate WHERE profile_id = ? AND food_id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, profileId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, foodId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PlateItemEntity>() {
      @Override
      @Nullable
      public PlateItemEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProfileId = CursorUtil.getColumnIndexOrThrow(_cursor, "profile_id");
          final int _cursorIndexOfFoodId = CursorUtil.getColumnIndexOrThrow(_cursor, "food_id");
          final int _cursorIndexOfQuantityG = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity_g");
          final PlateItemEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpProfileId;
            _tmpProfileId = _cursor.getLong(_cursorIndexOfProfileId);
            final long _tmpFoodId;
            _tmpFoodId = _cursor.getLong(_cursorIndexOfFoodId);
            final double _tmpQuantityG;
            _tmpQuantityG = _cursor.getDouble(_cursorIndexOfQuantityG);
            _result = new PlateItemEntity(_tmpId,_tmpProfileId,_tmpFoodId,_tmpQuantityG);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
