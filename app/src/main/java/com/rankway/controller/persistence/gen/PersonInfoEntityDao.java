package com.rankway.controller.persistence.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.rankway.controller.persistence.entity.PersonInfoEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PERSON_INFO_ENTITY".
*/
public class PersonInfoEntityDao extends AbstractDao<PersonInfoEntity, Long> {

    public static final String TABLENAME = "PERSON_INFO_ENTITY";

    /**
     * Properties of entity PersonInfoEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Cardno = new Property(1, int.class, "cardno", false, "CARDNO");
        public final static Property Gsno = new Property(2, String.class, "gsno", false, "GSNO");
        public final static Property Gname = new Property(3, String.class, "gname", false, "GNAME");
        public final static Property Gsex = new Property(4, String.class, "gsex", false, "GSEX");
        public final static Property Gdeptname = new Property(5, String.class, "gdeptname", false, "GDEPTNAME");
        public final static Property DeptId = new Property(6, String.class, "deptId", false, "DEPT_ID");
        public final static Property StatusId = new Property(7, int.class, "StatusId", false, "STATUS_ID");
        public final static Property Gno = new Property(8, String.class, "gno", false, "GNO");
        public final static Property Timestamp = new Property(9, long.class, "timestamp", false, "TIMESTAMP");
    }


    public PersonInfoEntityDao(DaoConfig config) {
        super(config);
    }
    
    public PersonInfoEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PERSON_INFO_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"CARDNO\" INTEGER NOT NULL ," + // 1: cardno
                "\"GSNO\" TEXT," + // 2: gsno
                "\"GNAME\" TEXT," + // 3: gname
                "\"GSEX\" TEXT," + // 4: gsex
                "\"GDEPTNAME\" TEXT," + // 5: gdeptname
                "\"DEPT_ID\" TEXT," + // 6: deptId
                "\"STATUS_ID\" INTEGER NOT NULL ," + // 7: StatusId
                "\"GNO\" TEXT," + // 8: gno
                "\"TIMESTAMP\" INTEGER NOT NULL );"); // 9: timestamp
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PERSON_INFO_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PersonInfoEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getCardno());
 
        String gsno = entity.getGsno();
        if (gsno != null) {
            stmt.bindString(3, gsno);
        }
 
        String gname = entity.getGname();
        if (gname != null) {
            stmt.bindString(4, gname);
        }
 
        String gsex = entity.getGsex();
        if (gsex != null) {
            stmt.bindString(5, gsex);
        }
 
        String gdeptname = entity.getGdeptname();
        if (gdeptname != null) {
            stmt.bindString(6, gdeptname);
        }
 
        String deptId = entity.getDeptId();
        if (deptId != null) {
            stmt.bindString(7, deptId);
        }
        stmt.bindLong(8, entity.getStatusId());
 
        String gno = entity.getGno();
        if (gno != null) {
            stmt.bindString(9, gno);
        }
        stmt.bindLong(10, entity.getTimestamp());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PersonInfoEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getCardno());
 
        String gsno = entity.getGsno();
        if (gsno != null) {
            stmt.bindString(3, gsno);
        }
 
        String gname = entity.getGname();
        if (gname != null) {
            stmt.bindString(4, gname);
        }
 
        String gsex = entity.getGsex();
        if (gsex != null) {
            stmt.bindString(5, gsex);
        }
 
        String gdeptname = entity.getGdeptname();
        if (gdeptname != null) {
            stmt.bindString(6, gdeptname);
        }
 
        String deptId = entity.getDeptId();
        if (deptId != null) {
            stmt.bindString(7, deptId);
        }
        stmt.bindLong(8, entity.getStatusId());
 
        String gno = entity.getGno();
        if (gno != null) {
            stmt.bindString(9, gno);
        }
        stmt.bindLong(10, entity.getTimestamp());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public PersonInfoEntity readEntity(Cursor cursor, int offset) {
        PersonInfoEntity entity = new PersonInfoEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // cardno
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // gsno
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // gname
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // gsex
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // gdeptname
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // deptId
            cursor.getInt(offset + 7), // StatusId
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // gno
            cursor.getLong(offset + 9) // timestamp
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PersonInfoEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCardno(cursor.getInt(offset + 1));
        entity.setGsno(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setGname(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setGsex(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setGdeptname(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setDeptId(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setStatusId(cursor.getInt(offset + 7));
        entity.setGno(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setTimestamp(cursor.getLong(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(PersonInfoEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(PersonInfoEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(PersonInfoEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
