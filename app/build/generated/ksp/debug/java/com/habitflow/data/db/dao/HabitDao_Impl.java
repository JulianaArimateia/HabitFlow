package com.habitflow.data.db.dao;

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
import com.habitflow.data.db.entity.Habit;
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
public final class HabitDao_Impl implements HabitDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Habit> __insertionAdapterOfHabit;

  private final EntityDeletionOrUpdateAdapter<Habit> __updateAdapterOfHabit;

  private final SharedSQLiteStatement __preparedStmtOfSoftDelete;

  public HabitDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfHabit = new EntityInsertionAdapter<Habit>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `habits` (`id`,`nome`,`categoria`,`frequencia`,`diasSemana`,`horarioPreferencial`,`dataCriacao`,`ativo`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Habit entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNome());
        statement.bindString(3, entity.getCategoria());
        statement.bindString(4, entity.getFrequencia());
        statement.bindString(5, entity.getDiasSemana());
        statement.bindString(6, entity.getHorarioPreferencial());
        statement.bindLong(7, entity.getDataCriacao());
        final int _tmp = entity.getAtivo() ? 1 : 0;
        statement.bindLong(8, _tmp);
      }
    };
    this.__updateAdapterOfHabit = new EntityDeletionOrUpdateAdapter<Habit>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `habits` SET `id` = ?,`nome` = ?,`categoria` = ?,`frequencia` = ?,`diasSemana` = ?,`horarioPreferencial` = ?,`dataCriacao` = ?,`ativo` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Habit entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNome());
        statement.bindString(3, entity.getCategoria());
        statement.bindString(4, entity.getFrequencia());
        statement.bindString(5, entity.getDiasSemana());
        statement.bindString(6, entity.getHorarioPreferencial());
        statement.bindLong(7, entity.getDataCriacao());
        final int _tmp = entity.getAtivo() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfSoftDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE habits SET ativo = 0 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Habit habit, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfHabit.insertAndReturnId(habit);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Habit habit, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfHabit.handle(habit);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object softDelete(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSoftDelete.acquire();
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
          __preparedStmtOfSoftDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Habit>> getAllActive() {
    final String _sql = "SELECT * FROM habits WHERE ativo = 1 ORDER BY dataCriacao ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"habits"}, new Callable<List<Habit>>() {
      @Override
      @NonNull
      public List<Habit> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNome = CursorUtil.getColumnIndexOrThrow(_cursor, "nome");
          final int _cursorIndexOfCategoria = CursorUtil.getColumnIndexOrThrow(_cursor, "categoria");
          final int _cursorIndexOfFrequencia = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencia");
          final int _cursorIndexOfDiasSemana = CursorUtil.getColumnIndexOrThrow(_cursor, "diasSemana");
          final int _cursorIndexOfHorarioPreferencial = CursorUtil.getColumnIndexOrThrow(_cursor, "horarioPreferencial");
          final int _cursorIndexOfDataCriacao = CursorUtil.getColumnIndexOrThrow(_cursor, "dataCriacao");
          final int _cursorIndexOfAtivo = CursorUtil.getColumnIndexOrThrow(_cursor, "ativo");
          final List<Habit> _result = new ArrayList<Habit>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Habit _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNome;
            _tmpNome = _cursor.getString(_cursorIndexOfNome);
            final String _tmpCategoria;
            _tmpCategoria = _cursor.getString(_cursorIndexOfCategoria);
            final String _tmpFrequencia;
            _tmpFrequencia = _cursor.getString(_cursorIndexOfFrequencia);
            final String _tmpDiasSemana;
            _tmpDiasSemana = _cursor.getString(_cursorIndexOfDiasSemana);
            final String _tmpHorarioPreferencial;
            _tmpHorarioPreferencial = _cursor.getString(_cursorIndexOfHorarioPreferencial);
            final long _tmpDataCriacao;
            _tmpDataCriacao = _cursor.getLong(_cursorIndexOfDataCriacao);
            final boolean _tmpAtivo;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAtivo);
            _tmpAtivo = _tmp != 0;
            _item = new Habit(_tmpId,_tmpNome,_tmpCategoria,_tmpFrequencia,_tmpDiasSemana,_tmpHorarioPreferencial,_tmpDataCriacao,_tmpAtivo);
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
  public Object getById(final long id, final Continuation<? super Habit> $completion) {
    final String _sql = "SELECT * FROM habits WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Habit>() {
      @Override
      @Nullable
      public Habit call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNome = CursorUtil.getColumnIndexOrThrow(_cursor, "nome");
          final int _cursorIndexOfCategoria = CursorUtil.getColumnIndexOrThrow(_cursor, "categoria");
          final int _cursorIndexOfFrequencia = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencia");
          final int _cursorIndexOfDiasSemana = CursorUtil.getColumnIndexOrThrow(_cursor, "diasSemana");
          final int _cursorIndexOfHorarioPreferencial = CursorUtil.getColumnIndexOrThrow(_cursor, "horarioPreferencial");
          final int _cursorIndexOfDataCriacao = CursorUtil.getColumnIndexOrThrow(_cursor, "dataCriacao");
          final int _cursorIndexOfAtivo = CursorUtil.getColumnIndexOrThrow(_cursor, "ativo");
          final Habit _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNome;
            _tmpNome = _cursor.getString(_cursorIndexOfNome);
            final String _tmpCategoria;
            _tmpCategoria = _cursor.getString(_cursorIndexOfCategoria);
            final String _tmpFrequencia;
            _tmpFrequencia = _cursor.getString(_cursorIndexOfFrequencia);
            final String _tmpDiasSemana;
            _tmpDiasSemana = _cursor.getString(_cursorIndexOfDiasSemana);
            final String _tmpHorarioPreferencial;
            _tmpHorarioPreferencial = _cursor.getString(_cursorIndexOfHorarioPreferencial);
            final long _tmpDataCriacao;
            _tmpDataCriacao = _cursor.getLong(_cursorIndexOfDataCriacao);
            final boolean _tmpAtivo;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAtivo);
            _tmpAtivo = _tmp != 0;
            _result = new Habit(_tmpId,_tmpNome,_tmpCategoria,_tmpFrequencia,_tmpDiasSemana,_tmpHorarioPreferencial,_tmpDataCriacao,_tmpAtivo);
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
  public Object getAllActiveOnce(final Continuation<? super List<Habit>> $completion) {
    final String _sql = "SELECT * FROM habits WHERE ativo = 1 ORDER BY dataCriacao ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Habit>>() {
      @Override
      @NonNull
      public List<Habit> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNome = CursorUtil.getColumnIndexOrThrow(_cursor, "nome");
          final int _cursorIndexOfCategoria = CursorUtil.getColumnIndexOrThrow(_cursor, "categoria");
          final int _cursorIndexOfFrequencia = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencia");
          final int _cursorIndexOfDiasSemana = CursorUtil.getColumnIndexOrThrow(_cursor, "diasSemana");
          final int _cursorIndexOfHorarioPreferencial = CursorUtil.getColumnIndexOrThrow(_cursor, "horarioPreferencial");
          final int _cursorIndexOfDataCriacao = CursorUtil.getColumnIndexOrThrow(_cursor, "dataCriacao");
          final int _cursorIndexOfAtivo = CursorUtil.getColumnIndexOrThrow(_cursor, "ativo");
          final List<Habit> _result = new ArrayList<Habit>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Habit _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNome;
            _tmpNome = _cursor.getString(_cursorIndexOfNome);
            final String _tmpCategoria;
            _tmpCategoria = _cursor.getString(_cursorIndexOfCategoria);
            final String _tmpFrequencia;
            _tmpFrequencia = _cursor.getString(_cursorIndexOfFrequencia);
            final String _tmpDiasSemana;
            _tmpDiasSemana = _cursor.getString(_cursorIndexOfDiasSemana);
            final String _tmpHorarioPreferencial;
            _tmpHorarioPreferencial = _cursor.getString(_cursorIndexOfHorarioPreferencial);
            final long _tmpDataCriacao;
            _tmpDataCriacao = _cursor.getLong(_cursorIndexOfDataCriacao);
            final boolean _tmpAtivo;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAtivo);
            _tmpAtivo = _tmp != 0;
            _item = new Habit(_tmpId,_tmpNome,_tmpCategoria,_tmpFrequencia,_tmpDiasSemana,_tmpHorarioPreferencial,_tmpDataCriacao,_tmpAtivo);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
