package com.rankway.controller.persistence.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.rankway.controller.persistence.entity.DishEntity;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DISH_ENTITY".
*/
public class DishEntityDao extends AbstractDao<DishEntity, Long> {

    public static final String TABLENAME = "DISH_ENTITY";

    /**
     * Properties of entity DishEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DishCode = new Property(1, String.class, "dishCode", false, "DISH_CODE");
        public final static Property DishName = new Property(2, String.class, "dishName", false, "DISH_NAME");
        public final static Property Price = new Property(3, int.class, "price", false, "PRICE");
        public final static Property Status = new Property(4, String.class, "status", false, "STATUS");
        public final static Property TypeId = new Property(5, long.class, "typeId", false, "TYPE_ID");
        public final static Property Timestamp = new Property(6, long.class, "timestamp", false, "TIMESTAMP");
    }

    private Query<DishEntity> dishTypeEntity_DishsQuery;

    public DishEntityDao(DaoConfig config) {
        super(config);
    }
    
    public DishEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DISH_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"DISH_CODE\" TEXT," + // 1: dishCode
                "\"DISH_NAME\" TEXT," + // 2: dishName
                "\"PRICE\" INTEGER NOT NULL ," + // 3: price
                "\"STATUS\" TEXT," + // 4: status
                "\"TYPE_ID\" INTEGER NOT NULL ," + // 5: typeId
                "\"TIMESTAMP\" INTEGER NOT NULL );"); // 6: timestamp
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DISH_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DishEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String dishCode = entity.getDishCode();
        if (dishCode != null) {
            stmt.bindString(2, dishCode);
        }
 
        String dishName = entity.getDishName();
        if (dishName != null) {
            stmt.bindString(3, dishName);
        }
        stmt.bindLong(4, entity.getPrice());
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(5, status);
        }
        stmt.bindLong(6, entity.getTypeId());
        stmt.bindLong(7, entity.getTimestamp());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DishEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String dishCode = entity.getDishCode();
        if (dishCode != null) {
            stmt.bindString(2, dishCode);
        }
 
        String dishName = entity.getDishName();
        if (dishName != null) {
            stmt.bindString(3, dishName);
        }
        stmt.bindLong(4, entity.getPrice());
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(5, status);
        }
        stmt.bindLong(6, entity.getTypeId());
        stmt.bindLong(7, entity.getTimestamp());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public DishEntity readEntity(Cursor cursor, int offset) {
        DishEntity entity = new DishEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // dishCode
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // dishName
            cursor.getInt(offset + 3), // price
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // status
            cursor.getLong(offset + 5), // typeId
            cursor.getLong(offset + 6) // timestamp
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DishEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDishCode(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDishName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPrice(cursor.getInt(offset + 3));
        entity.setStatus(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setTypeId(cursor.getLong(offset + 5));
        entity.setTimestamp(cursor.getLong(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(DishEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(DishEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(DishEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "dishs" to-many relationship of DishTypeEntity. */
    public List<DishEntity> _queryDishTypeEntity_Dishs(long typeId) {
        synchronized (this) {
            if (dishTypeEntity_DishsQuery == null) {
                QueryBuilder<DishEntity> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.TypeId.eq(null));
                dishTypeEntity_DishsQuery = queryBuilder.build();
            }
        }
        Query<DishEntity> query = dishTypeEntity_DishsQuery.forCurrentThread();
        query.setParameter(0, typeId);
        return query.list();
    }

}
