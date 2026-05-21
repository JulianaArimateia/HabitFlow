package com.habitflow.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.habitflow.data.db.entity.HabitLog;
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
public final class HabitLogDao_Impl implements HabitLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<HabitLog> __insertionAdapterOfHabitLog;

  private final EntityDeletionOrUpdateAdapter<HabitLog> __deletionAdapterOfHabitLog;

  public HabitLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfHabitLog = new EntityInsertionAdapter<HabitLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `habit_logs` (`id`,`habitId`,`dataHora`,`concluido`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final HabitLog entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getHabitId());
        statement.bindLong(3, entity.getDataHora());
        final int _tmp = entity.getConcluido() ? 1 : 0;
        statement.bindLong(4, _tmp);
      }
    };
    this.__deletionAdapterOfHabitLog = new EntityDeletionOrUpdateAdapter<HabitLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `habit_logs` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final HabitLog entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final HabitLog log, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfHabitLog.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final HabitLog log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfHabitLog.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<HabitLog>> getLogsForHabit(final long habitId) {
    final String _sql = "SELECT * FROM habit_logs WHERE habitId = ? ORDER BY dataHora DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, habitId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"habit_logs"}, new Callable<List<HabitLog>>() {
      @Override
      @NonNull
      public List<HabitLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHabitId = CursorUtil.getColumnIndexOrThrow(_cursor, "habitId");
          final int _cursorIndexOfDataHora = CursorUtil.getColumnIndexOrThrow(_cursor, "dataHora");
          final int _cursorIndexOfConcluido = CursorUtil.getColumnIndexOrThrow(_cursor, "concluido");
          final List<HabitLog> _result = new ArrayList<HabitLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HabitLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpHabitId;
            _tmpHabitId = _cursor.getLong(_cursorIndexOfHabitId);
            final long _tmpDataHora;
            _tmpDataHora = _cursor.getLong(_cursorIndexOfDataHora);
            final boolean _tmpConcluido;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfConcluido);
            _tmpConcluido = _tmp != 0;
            _item = new HabitLog(_tmpId,_tmpHabitId,_tmpDataHora,_tmpConcluido);
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
  public Object getCompletedOnDate(final long habitId, final long startOfDay, final long endOfDay,
      final Continuation<? super List<HabitLog>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM habit_logs\n"
            + "        WHERE habitId = ?\n"
            + "          AND dataHora >= ?\n"
            + "          AND dataHora < ?\n"
            + "          AND concluido = 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, habitId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endOfDay);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<HabitLog>>() {
      @Override
      @NonNull
      public List<HabitLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHabitId = CursorUtil.getColumnIndexOrThrow(_cursor, "habitId");
          final int _cursorIndexOfDataHora = CursorUtil.getColumnIndexOrThrow(_cursor, "dataHora");
          final int _cursorIndexOfConcluido = CursorUtil.getColumnIndexOrThrow(_cursor, "concluido");
          final List<HabitLog> _result = new ArrayList<HabitLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HabitLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpHabitId;
            _tmpHabitId = _cursor.getLong(_cursorIndexOfHabitId);
            final long _tmpDataHora;
            _tmpDataHora = _cursor.getLong(_cursorIndexOfDataHora);
            final boolean _tmpConcluido;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfConcluido);
            _tmpConcluido = _tmp != 0;
            _item = new HabitLog(_tmpId,_tmpHabitId,_tmpDataHora,_tmpConcluido);
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
  public Object getLastCompleted(final long habitId, final int limit,
      final Continuation<? super List<HabitLog>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM habit_logs\n"
            + "        WHERE habitId = ?\n"
            + "          AND concluido = 1\n"
            + "        ORDER BY dataHora DESC\n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, habitId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<HabitLog>>() {
      @Override
      @NonNull
      public List<HabitLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHabitId = CursorUtil.getColumnIndexOrThrow(_cursor, "habitId");
          final int _cursorIndexOfDataHora = CursorUtil.getColumnIndexOrThrow(_cursor, "dataHora");
          final int _cursorIndexOfConcluido = CursorUtil.getColumnIndexOrThrow(_cursor, "concluido");
          final List<HabitLog> _result = new ArrayList<HabitLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HabitLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpHabitId;
            _tmpHabitId = _cursor.getLong(_cursorIndexOfHabitId);
            final long _tmpDataHora;
            _tmpDataHora = _cursor.getLong(_cursorIndexOfDataHora);
            final boolean _tmpConcluido;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfConcluido);
            _tmpConcluido = _tmp != 0;
            _item = new HabitLog(_tmpId,_tmpHabitId,_tmpDataHora,_tmpConcluido);
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
  public Object getAllCompletedSince(final long since,
      final Continuation<? super List<HabitLog>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM habit_logs\n"
            + "        WHERE dataHora >= ?\n"
            + "          AND concluido = 1\n"
            + "        ORDER BY dataHora DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<HabitLog>>() {
      @Override
      @NonNull
      public List<HabitLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHabitId = CursorUtil.getColumnIndexOrThrow(_cursor, "habitId");
          final int _cursorIndexOfDataHora = CursorUtil.getColumnIndexOrThrow(_cursor, "dataHora");
          final int _cursorIndexOfConcluido = CursorUtil.getColumnIndexOrThrow(_cursor, "concluido");
          final List<HabitLog> _result = new ArrayList<HabitLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HabitLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpHabitId;
            _tmpHabitId = _cursor.getLong(_cursorIndexOfHabitId);
            final long _tmpDataHora;
            _tmpDataHora = _cursor.getLong(_cursorIndexOfDataHora);
            final boolean _tmpConcluido;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfConcluido);
            _tmpConcluido = _tmp != 0;
            _item = new HabitLog(_tmpId,_tmpHabitId,_tmpDataHora,_tmpConcluido);
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
  public Object getAllCompletedOnDate(final long startOfDay, final long endOfDay,
      final Continuation<? super List<HabitLog>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM habit_logs\n"
            + "        WHERE dataHora >= ?\n"
            + "          AND dataHora < ?\n"
            + "          AND concluido = 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<HabitLog>>() {
      @Override
      @NonNull
      public List<HabitLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHabitId = CursorUtil.getColumnIndexOrThrow(_cursor, "habitId");
          final int _cursorIndexOfDataHora = CursorUtil.getColumnIndexOrThrow(_cursor, "dataHora");
          final int _cursorIndexOfConcluido = CursorUtil.getColumnIndexOrThrow(_cursor, "concluido");
          final List<HabitLog> _result = new ArrayList<HabitLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HabitLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpHabitId;
            _tmpHabitId = _cursor.getLong(_cursorIndexOfHabitId);
            final long _tmpDataHora;
            _tmpDataHora = _cursor.getLong(_cursorIndexOfDataHora);
            final boolean _tmpConcluido;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfConcluido);
            _tmpConcluido = _tmp != 0;
            _item = new HabitLog(_tmpId,_tmpHabitId,_tmpDataHora,_tmpConcluido);
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
  public Object getAllCompletedForHabit(final long habitId,
      final Continuation<? super List<HabitLog>> $completion) {
    final String _sql = "SELECT * FROM habit_logs WHERE habitId = ? AND concluido = 1 ORDER BY dataHora DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, habitId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<HabitLog>>() {
      @Override
      @NonNull
      public List<HabitLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHabitId = CursorUtil.getColumnIndexOrThrow(_cursor, "habitId");
          final int _cursorIndexOfDataHora = CursorUtil.getColumnIndexOrThrow(_cursor, "dataHora");
          final int _cursorIndexOfConcluido = CursorUtil.getColumnIndexOrThrow(_cursor, "concluido");
          final List<HabitLog> _result = new ArrayList<HabitLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HabitLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpHabitId;
            _tmpHabitId = _cursor.getLong(_cursorIndexOfHabitId);
            final long _tmpDataHora;
            _tmpDataHora = _cursor.getLong(_cursorIndexOfDataHora);
            final boolean _tmpConcluido;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfConcluido);
            _tmpConcluido = _tmp != 0;
            _item = new HabitLog(_tmpId,_tmpHabitId,_tmpDataHora,_tmpConcluido);
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
