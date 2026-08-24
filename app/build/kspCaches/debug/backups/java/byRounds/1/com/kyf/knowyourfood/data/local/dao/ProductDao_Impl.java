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
import com.kyf.knowyourfood.data.local.entity.ProductEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class ProductDao_Impl implements ProductDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ProductEntity> __insertionAdapterOfProductEntity;

  public ProductDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfProductEntity = new EntityInsertionAdapter<ProductEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `products` (`barcode`,`name`,`brand`,`category`,`nutri_score`,`sugars_100g`,`fat_100g`,`sat_fat_100g`,`salt_100g`,`protein_100g`,`energy_kcal_100g`,`fiber_100g`,`ingredients_text`,`allergens_json`,`healthier_alternatives_json`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProductEntity entity) {
        statement.bindString(1, entity.getBarcode());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getBrand());
        statement.bindString(4, entity.getCategory());
        statement.bindString(5, entity.getNutriScore());
        statement.bindDouble(6, entity.getSugars100g());
        statement.bindDouble(7, entity.getFat100g());
        statement.bindDouble(8, entity.getSatFat100g());
        statement.bindDouble(9, entity.getSalt100g());
        statement.bindDouble(10, entity.getProtein100g());
        statement.bindDouble(11, entity.getEnergyKcal100g());
        statement.bindDouble(12, entity.getFiber100g());
        statement.bindString(13, entity.getIngredientsText());
        statement.bindString(14, entity.getAllergensJson());
        if (entity.getHealthierAlternativesJson() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getHealthierAlternativesJson());
        }
      }
    };
  }

  @Override
  public Object insertProduct(final ProductEntity product,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfProductEntity.insert(product);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ProductEntity>> getAllProducts() {
    final String _sql = "SELECT * FROM products";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"products"}, new Callable<List<ProductEntity>>() {
      @Override
      @NonNull
      public List<ProductEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBarcode = CursorUtil.getColumnIndexOrThrow(_cursor, "barcode");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNutriScore = CursorUtil.getColumnIndexOrThrow(_cursor, "nutri_score");
          final int _cursorIndexOfSugars100g = CursorUtil.getColumnIndexOrThrow(_cursor, "sugars_100g");
          final int _cursorIndexOfFat100g = CursorUtil.getColumnIndexOrThrow(_cursor, "fat_100g");
          final int _cursorIndexOfSatFat100g = CursorUtil.getColumnIndexOrThrow(_cursor, "sat_fat_100g");
          final int _cursorIndexOfSalt100g = CursorUtil.getColumnIndexOrThrow(_cursor, "salt_100g");
          final int _cursorIndexOfProtein100g = CursorUtil.getColumnIndexOrThrow(_cursor, "protein_100g");
          final int _cursorIndexOfEnergyKcal100g = CursorUtil.getColumnIndexOrThrow(_cursor, "energy_kcal_100g");
          final int _cursorIndexOfFiber100g = CursorUtil.getColumnIndexOrThrow(_cursor, "fiber_100g");
          final int _cursorIndexOfIngredientsText = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredients_text");
          final int _cursorIndexOfAllergensJson = CursorUtil.getColumnIndexOrThrow(_cursor, "allergens_json");
          final int _cursorIndexOfHealthierAlternativesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "healthier_alternatives_json");
          final List<ProductEntity> _result = new ArrayList<ProductEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductEntity _item;
            final String _tmpBarcode;
            _tmpBarcode = _cursor.getString(_cursorIndexOfBarcode);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpNutriScore;
            _tmpNutriScore = _cursor.getString(_cursorIndexOfNutriScore);
            final double _tmpSugars100g;
            _tmpSugars100g = _cursor.getDouble(_cursorIndexOfSugars100g);
            final double _tmpFat100g;
            _tmpFat100g = _cursor.getDouble(_cursorIndexOfFat100g);
            final double _tmpSatFat100g;
            _tmpSatFat100g = _cursor.getDouble(_cursorIndexOfSatFat100g);
            final double _tmpSalt100g;
            _tmpSalt100g = _cursor.getDouble(_cursorIndexOfSalt100g);
            final double _tmpProtein100g;
            _tmpProtein100g = _cursor.getDouble(_cursorIndexOfProtein100g);
            final double _tmpEnergyKcal100g;
            _tmpEnergyKcal100g = _cursor.getDouble(_cursorIndexOfEnergyKcal100g);
            final double _tmpFiber100g;
            _tmpFiber100g = _cursor.getDouble(_cursorIndexOfFiber100g);
            final String _tmpIngredientsText;
            _tmpIngredientsText = _cursor.getString(_cursorIndexOfIngredientsText);
            final String _tmpAllergensJson;
            _tmpAllergensJson = _cursor.getString(_cursorIndexOfAllergensJson);
            final String _tmpHealthierAlternativesJson;
            if (_cursor.isNull(_cursorIndexOfHealthierAlternativesJson)) {
              _tmpHealthierAlternativesJson = null;
            } else {
              _tmpHealthierAlternativesJson = _cursor.getString(_cursorIndexOfHealthierAlternativesJson);
            }
            _item = new ProductEntity(_tmpBarcode,_tmpName,_tmpBrand,_tmpCategory,_tmpNutriScore,_tmpSugars100g,_tmpFat100g,_tmpSatFat100g,_tmpSalt100g,_tmpProtein100g,_tmpEnergyKcal100g,_tmpFiber100g,_tmpIngredientsText,_tmpAllergensJson,_tmpHealthierAlternativesJson);
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
  public Object getProductByBarcode(final String barcode,
      final Continuation<? super ProductEntity> $completion) {
    final String _sql = "SELECT * FROM products WHERE barcode = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, barcode);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ProductEntity>() {
      @Override
      @Nullable
      public ProductEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBarcode = CursorUtil.getColumnIndexOrThrow(_cursor, "barcode");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNutriScore = CursorUtil.getColumnIndexOrThrow(_cursor, "nutri_score");
          final int _cursorIndexOfSugars100g = CursorUtil.getColumnIndexOrThrow(_cursor, "sugars_100g");
          final int _cursorIndexOfFat100g = CursorUtil.getColumnIndexOrThrow(_cursor, "fat_100g");
          final int _cursorIndexOfSatFat100g = CursorUtil.getColumnIndexOrThrow(_cursor, "sat_fat_100g");
          final int _cursorIndexOfSalt100g = CursorUtil.getColumnIndexOrThrow(_cursor, "salt_100g");
          final int _cursorIndexOfProtein100g = CursorUtil.getColumnIndexOrThrow(_cursor, "protein_100g");
          final int _cursorIndexOfEnergyKcal100g = CursorUtil.getColumnIndexOrThrow(_cursor, "energy_kcal_100g");
          final int _cursorIndexOfFiber100g = CursorUtil.getColumnIndexOrThrow(_cursor, "fiber_100g");
          final int _cursorIndexOfIngredientsText = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredients_text");
          final int _cursorIndexOfAllergensJson = CursorUtil.getColumnIndexOrThrow(_cursor, "allergens_json");
          final int _cursorIndexOfHealthierAlternativesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "healthier_alternatives_json");
          final ProductEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpBarcode;
            _tmpBarcode = _cursor.getString(_cursorIndexOfBarcode);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpNutriScore;
            _tmpNutriScore = _cursor.getString(_cursorIndexOfNutriScore);
            final double _tmpSugars100g;
            _tmpSugars100g = _cursor.getDouble(_cursorIndexOfSugars100g);
            final double _tmpFat100g;
            _tmpFat100g = _cursor.getDouble(_cursorIndexOfFat100g);
            final double _tmpSatFat100g;
            _tmpSatFat100g = _cursor.getDouble(_cursorIndexOfSatFat100g);
            final double _tmpSalt100g;
            _tmpSalt100g = _cursor.getDouble(_cursorIndexOfSalt100g);
            final double _tmpProtein100g;
            _tmpProtein100g = _cursor.getDouble(_cursorIndexOfProtein100g);
            final double _tmpEnergyKcal100g;
            _tmpEnergyKcal100g = _cursor.getDouble(_cursorIndexOfEnergyKcal100g);
            final double _tmpFiber100g;
            _tmpFiber100g = _cursor.getDouble(_cursorIndexOfFiber100g);
            final String _tmpIngredientsText;
            _tmpIngredientsText = _cursor.getString(_cursorIndexOfIngredientsText);
            final String _tmpAllergensJson;
            _tmpAllergensJson = _cursor.getString(_cursorIndexOfAllergensJson);
            final String _tmpHealthierAlternativesJson;
            if (_cursor.isNull(_cursorIndexOfHealthierAlternativesJson)) {
              _tmpHealthierAlternativesJson = null;
            } else {
              _tmpHealthierAlternativesJson = _cursor.getString(_cursorIndexOfHealthierAlternativesJson);
            }
            _result = new ProductEntity(_tmpBarcode,_tmpName,_tmpBrand,_tmpCategory,_tmpNutriScore,_tmpSugars100g,_tmpFat100g,_tmpSatFat100g,_tmpSalt100g,_tmpProtein100g,_tmpEnergyKcal100g,_tmpFiber100g,_tmpIngredientsText,_tmpAllergensJson,_tmpHealthierAlternativesJson);
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
  public Flow<ProductEntity> observeProductByBarcode(final String barcode) {
    final String _sql = "SELECT * FROM products WHERE barcode = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, barcode);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"products"}, new Callable<ProductEntity>() {
      @Override
      @Nullable
      public ProductEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBarcode = CursorUtil.getColumnIndexOrThrow(_cursor, "barcode");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNutriScore = CursorUtil.getColumnIndexOrThrow(_cursor, "nutri_score");
          final int _cursorIndexOfSugars100g = CursorUtil.getColumnIndexOrThrow(_cursor, "sugars_100g");
          final int _cursorIndexOfFat100g = CursorUtil.getColumnIndexOrThrow(_cursor, "fat_100g");
          final int _cursorIndexOfSatFat100g = CursorUtil.getColumnIndexOrThrow(_cursor, "sat_fat_100g");
          final int _cursorIndexOfSalt100g = CursorUtil.getColumnIndexOrThrow(_cursor, "salt_100g");
          final int _cursorIndexOfProtein100g = CursorUtil.getColumnIndexOrThrow(_cursor, "protein_100g");
          final int _cursorIndexOfEnergyKcal100g = CursorUtil.getColumnIndexOrThrow(_cursor, "energy_kcal_100g");
          final int _cursorIndexOfFiber100g = CursorUtil.getColumnIndexOrThrow(_cursor, "fiber_100g");
          final int _cursorIndexOfIngredientsText = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredients_text");
          final int _cursorIndexOfAllergensJson = CursorUtil.getColumnIndexOrThrow(_cursor, "allergens_json");
          final int _cursorIndexOfHealthierAlternativesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "healthier_alternatives_json");
          final ProductEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpBarcode;
            _tmpBarcode = _cursor.getString(_cursorIndexOfBarcode);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpNutriScore;
            _tmpNutriScore = _cursor.getString(_cursorIndexOfNutriScore);
            final double _tmpSugars100g;
            _tmpSugars100g = _cursor.getDouble(_cursorIndexOfSugars100g);
            final double _tmpFat100g;
            _tmpFat100g = _cursor.getDouble(_cursorIndexOfFat100g);
            final double _tmpSatFat100g;
            _tmpSatFat100g = _cursor.getDouble(_cursorIndexOfSatFat100g);
            final double _tmpSalt100g;
            _tmpSalt100g = _cursor.getDouble(_cursorIndexOfSalt100g);
            final double _tmpProtein100g;
            _tmpProtein100g = _cursor.getDouble(_cursorIndexOfProtein100g);
            final double _tmpEnergyKcal100g;
            _tmpEnergyKcal100g = _cursor.getDouble(_cursorIndexOfEnergyKcal100g);
            final double _tmpFiber100g;
            _tmpFiber100g = _cursor.getDouble(_cursorIndexOfFiber100g);
            final String _tmpIngredientsText;
            _tmpIngredientsText = _cursor.getString(_cursorIndexOfIngredientsText);
            final String _tmpAllergensJson;
            _tmpAllergensJson = _cursor.getString(_cursorIndexOfAllergensJson);
            final String _tmpHealthierAlternativesJson;
            if (_cursor.isNull(_cursorIndexOfHealthierAlternativesJson)) {
              _tmpHealthierAlternativesJson = null;
            } else {
              _tmpHealthierAlternativesJson = _cursor.getString(_cursorIndexOfHealthierAlternativesJson);
            }
            _result = new ProductEntity(_tmpBarcode,_tmpName,_tmpBrand,_tmpCategory,_tmpNutriScore,_tmpSugars100g,_tmpFat100g,_tmpSatFat100g,_tmpSalt100g,_tmpProtein100g,_tmpEnergyKcal100g,_tmpFiber100g,_tmpIngredientsText,_tmpAllergensJson,_tmpHealthierAlternativesJson);
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

  @Override
  public Flow<List<ProductEntity>> searchProducts(final String query, final String nutriScore,
      final String category) {
    final String _sql = "\n"
            + "        SELECT * FROM products \n"
            + "        WHERE (? = '' OR name LIKE '%' || ? || '%' OR brand LIKE '%' || ? || '%' OR category LIKE '%' || ? || '%' OR barcode LIKE '%' || ? || '%')\n"
            + "        AND (? = '' OR nutri_score = ?)\n"
            + "        AND (? = '' OR category = ?)\n"
            + "        ORDER BY name ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 9);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    _argIndex = 3;
    _statement.bindString(_argIndex, query);
    _argIndex = 4;
    _statement.bindString(_argIndex, query);
    _argIndex = 5;
    _statement.bindString(_argIndex, query);
    _argIndex = 6;
    _statement.bindString(_argIndex, nutriScore);
    _argIndex = 7;
    _statement.bindString(_argIndex, nutriScore);
    _argIndex = 8;
    _statement.bindString(_argIndex, category);
    _argIndex = 9;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"products"}, new Callable<List<ProductEntity>>() {
      @Override
      @NonNull
      public List<ProductEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBarcode = CursorUtil.getColumnIndexOrThrow(_cursor, "barcode");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNutriScore = CursorUtil.getColumnIndexOrThrow(_cursor, "nutri_score");
          final int _cursorIndexOfSugars100g = CursorUtil.getColumnIndexOrThrow(_cursor, "sugars_100g");
          final int _cursorIndexOfFat100g = CursorUtil.getColumnIndexOrThrow(_cursor, "fat_100g");
          final int _cursorIndexOfSatFat100g = CursorUtil.getColumnIndexOrThrow(_cursor, "sat_fat_100g");
          final int _cursorIndexOfSalt100g = CursorUtil.getColumnIndexOrThrow(_cursor, "salt_100g");
          final int _cursorIndexOfProtein100g = CursorUtil.getColumnIndexOrThrow(_cursor, "protein_100g");
          final int _cursorIndexOfEnergyKcal100g = CursorUtil.getColumnIndexOrThrow(_cursor, "energy_kcal_100g");
          final int _cursorIndexOfFiber100g = CursorUtil.getColumnIndexOrThrow(_cursor, "fiber_100g");
          final int _cursorIndexOfIngredientsText = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredients_text");
          final int _cursorIndexOfAllergensJson = CursorUtil.getColumnIndexOrThrow(_cursor, "allergens_json");
          final int _cursorIndexOfHealthierAlternativesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "healthier_alternatives_json");
          final List<ProductEntity> _result = new ArrayList<ProductEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductEntity _item;
            final String _tmpBarcode;
            _tmpBarcode = _cursor.getString(_cursorIndexOfBarcode);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpNutriScore;
            _tmpNutriScore = _cursor.getString(_cursorIndexOfNutriScore);
            final double _tmpSugars100g;
            _tmpSugars100g = _cursor.getDouble(_cursorIndexOfSugars100g);
            final double _tmpFat100g;
            _tmpFat100g = _cursor.getDouble(_cursorIndexOfFat100g);
            final double _tmpSatFat100g;
            _tmpSatFat100g = _cursor.getDouble(_cursorIndexOfSatFat100g);
            final double _tmpSalt100g;
            _tmpSalt100g = _cursor.getDouble(_cursorIndexOfSalt100g);
            final double _tmpProtein100g;
            _tmpProtein100g = _cursor.getDouble(_cursorIndexOfProtein100g);
            final double _tmpEnergyKcal100g;
            _tmpEnergyKcal100g = _cursor.getDouble(_cursorIndexOfEnergyKcal100g);
            final double _tmpFiber100g;
            _tmpFiber100g = _cursor.getDouble(_cursorIndexOfFiber100g);
            final String _tmpIngredientsText;
            _tmpIngredientsText = _cursor.getString(_cursorIndexOfIngredientsText);
            final String _tmpAllergensJson;
            _tmpAllergensJson = _cursor.getString(_cursorIndexOfAllergensJson);
            final String _tmpHealthierAlternativesJson;
            if (_cursor.isNull(_cursorIndexOfHealthierAlternativesJson)) {
              _tmpHealthierAlternativesJson = null;
            } else {
              _tmpHealthierAlternativesJson = _cursor.getString(_cursorIndexOfHealthierAlternativesJson);
            }
            _item = new ProductEntity(_tmpBarcode,_tmpName,_tmpBrand,_tmpCategory,_tmpNutriScore,_tmpSugars100g,_tmpFat100g,_tmpSatFat100g,_tmpSalt100g,_tmpProtein100g,_tmpEnergyKcal100g,_tmpFiber100g,_tmpIngredientsText,_tmpAllergensJson,_tmpHealthierAlternativesJson);
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
  public Flow<List<String>> getAllCategories() {
    final String _sql = "SELECT DISTINCT category FROM products ORDER BY category ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"products"}, new Callable<List<String>>() {
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

  @Override
  public Object getHealthierCategoryAlternatives(final String category, final String currentBarcode,
      final int limit, final Continuation<? super List<ProductEntity>> $completion) {
    final String _sql = "SELECT * FROM products WHERE category = ? AND barcode != ? ORDER BY CASE nutri_score WHEN 'A' THEN 1 WHEN 'B' THEN 2 WHEN 'C' THEN 3 WHEN 'D' THEN 4 ELSE 5 END ASC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    _argIndex = 2;
    _statement.bindString(_argIndex, currentBarcode);
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ProductEntity>>() {
      @Override
      @NonNull
      public List<ProductEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBarcode = CursorUtil.getColumnIndexOrThrow(_cursor, "barcode");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNutriScore = CursorUtil.getColumnIndexOrThrow(_cursor, "nutri_score");
          final int _cursorIndexOfSugars100g = CursorUtil.getColumnIndexOrThrow(_cursor, "sugars_100g");
          final int _cursorIndexOfFat100g = CursorUtil.getColumnIndexOrThrow(_cursor, "fat_100g");
          final int _cursorIndexOfSatFat100g = CursorUtil.getColumnIndexOrThrow(_cursor, "sat_fat_100g");
          final int _cursorIndexOfSalt100g = CursorUtil.getColumnIndexOrThrow(_cursor, "salt_100g");
          final int _cursorIndexOfProtein100g = CursorUtil.getColumnIndexOrThrow(_cursor, "protein_100g");
          final int _cursorIndexOfEnergyKcal100g = CursorUtil.getColumnIndexOrThrow(_cursor, "energy_kcal_100g");
          final int _cursorIndexOfFiber100g = CursorUtil.getColumnIndexOrThrow(_cursor, "fiber_100g");
          final int _cursorIndexOfIngredientsText = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredients_text");
          final int _cursorIndexOfAllergensJson = CursorUtil.getColumnIndexOrThrow(_cursor, "allergens_json");
          final int _cursorIndexOfHealthierAlternativesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "healthier_alternatives_json");
          final List<ProductEntity> _result = new ArrayList<ProductEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductEntity _item;
            final String _tmpBarcode;
            _tmpBarcode = _cursor.getString(_cursorIndexOfBarcode);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpNutriScore;
            _tmpNutriScore = _cursor.getString(_cursorIndexOfNutriScore);
            final double _tmpSugars100g;
            _tmpSugars100g = _cursor.getDouble(_cursorIndexOfSugars100g);
            final double _tmpFat100g;
            _tmpFat100g = _cursor.getDouble(_cursorIndexOfFat100g);
            final double _tmpSatFat100g;
            _tmpSatFat100g = _cursor.getDouble(_cursorIndexOfSatFat100g);
            final double _tmpSalt100g;
            _tmpSalt100g = _cursor.getDouble(_cursorIndexOfSalt100g);
            final double _tmpProtein100g;
            _tmpProtein100g = _cursor.getDouble(_cursorIndexOfProtein100g);
            final double _tmpEnergyKcal100g;
            _tmpEnergyKcal100g = _cursor.getDouble(_cursorIndexOfEnergyKcal100g);
            final double _tmpFiber100g;
            _tmpFiber100g = _cursor.getDouble(_cursorIndexOfFiber100g);
            final String _tmpIngredientsText;
            _tmpIngredientsText = _cursor.getString(_cursorIndexOfIngredientsText);
            final String _tmpAllergensJson;
            _tmpAllergensJson = _cursor.getString(_cursorIndexOfAllergensJson);
            final String _tmpHealthierAlternativesJson;
            if (_cursor.isNull(_cursorIndexOfHealthierAlternativesJson)) {
              _tmpHealthierAlternativesJson = null;
            } else {
              _tmpHealthierAlternativesJson = _cursor.getString(_cursorIndexOfHealthierAlternativesJson);
            }
            _item = new ProductEntity(_tmpBarcode,_tmpName,_tmpBrand,_tmpCategory,_tmpNutriScore,_tmpSugars100g,_tmpFat100g,_tmpSatFat100g,_tmpSalt100g,_tmpProtein100g,_tmpEnergyKcal100g,_tmpFiber100g,_tmpIngredientsText,_tmpAllergensJson,_tmpHealthierAlternativesJson);
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
  public Object getProductsByBarcodes(final List<String> barcodes,
      final Continuation<? super List<ProductEntity>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM products WHERE barcode IN (");
    final int _inputSize = barcodes.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : barcodes) {
      _statement.bindString(_argIndex, _item);
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ProductEntity>>() {
      @Override
      @NonNull
      public List<ProductEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBarcode = CursorUtil.getColumnIndexOrThrow(_cursor, "barcode");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNutriScore = CursorUtil.getColumnIndexOrThrow(_cursor, "nutri_score");
          final int _cursorIndexOfSugars100g = CursorUtil.getColumnIndexOrThrow(_cursor, "sugars_100g");
          final int _cursorIndexOfFat100g = CursorUtil.getColumnIndexOrThrow(_cursor, "fat_100g");
          final int _cursorIndexOfSatFat100g = CursorUtil.getColumnIndexOrThrow(_cursor, "sat_fat_100g");
          final int _cursorIndexOfSalt100g = CursorUtil.getColumnIndexOrThrow(_cursor, "salt_100g");
          final int _cursorIndexOfProtein100g = CursorUtil.getColumnIndexOrThrow(_cursor, "protein_100g");
          final int _cursorIndexOfEnergyKcal100g = CursorUtil.getColumnIndexOrThrow(_cursor, "energy_kcal_100g");
          final int _cursorIndexOfFiber100g = CursorUtil.getColumnIndexOrThrow(_cursor, "fiber_100g");
          final int _cursorIndexOfIngredientsText = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredients_text");
          final int _cursorIndexOfAllergensJson = CursorUtil.getColumnIndexOrThrow(_cursor, "allergens_json");
          final int _cursorIndexOfHealthierAlternativesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "healthier_alternatives_json");
          final List<ProductEntity> _result = new ArrayList<ProductEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductEntity _item_1;
            final String _tmpBarcode;
            _tmpBarcode = _cursor.getString(_cursorIndexOfBarcode);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpNutriScore;
            _tmpNutriScore = _cursor.getString(_cursorIndexOfNutriScore);
            final double _tmpSugars100g;
            _tmpSugars100g = _cursor.getDouble(_cursorIndexOfSugars100g);
            final double _tmpFat100g;
            _tmpFat100g = _cursor.getDouble(_cursorIndexOfFat100g);
            final double _tmpSatFat100g;
            _tmpSatFat100g = _cursor.getDouble(_cursorIndexOfSatFat100g);
            final double _tmpSalt100g;
            _tmpSalt100g = _cursor.getDouble(_cursorIndexOfSalt100g);
            final double _tmpProtein100g;
            _tmpProtein100g = _cursor.getDouble(_cursorIndexOfProtein100g);
            final double _tmpEnergyKcal100g;
            _tmpEnergyKcal100g = _cursor.getDouble(_cursorIndexOfEnergyKcal100g);
            final double _tmpFiber100g;
            _tmpFiber100g = _cursor.getDouble(_cursorIndexOfFiber100g);
            final String _tmpIngredientsText;
            _tmpIngredientsText = _cursor.getString(_cursorIndexOfIngredientsText);
            final String _tmpAllergensJson;
            _tmpAllergensJson = _cursor.getString(_cursorIndexOfAllergensJson);
            final String _tmpHealthierAlternativesJson;
            if (_cursor.isNull(_cursorIndexOfHealthierAlternativesJson)) {
              _tmpHealthierAlternativesJson = null;
            } else {
              _tmpHealthierAlternativesJson = _cursor.getString(_cursorIndexOfHealthierAlternativesJson);
            }
            _item_1 = new ProductEntity(_tmpBarcode,_tmpName,_tmpBrand,_tmpCategory,_tmpNutriScore,_tmpSugars100g,_tmpFat100g,_tmpSatFat100g,_tmpSalt100g,_tmpProtein100g,_tmpEnergyKcal100g,_tmpFiber100g,_tmpIngredientsText,_tmpAllergensJson,_tmpHealthierAlternativesJson);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
