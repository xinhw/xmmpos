package com.rankway.controller.persistence.gen;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import com.rankway.controller.persistence.entity.PaymentItemEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PAYMENT_ITEM_ENTITY".
*/
public class PaymentItemEntityDao extends AbstractDao<PaymentItemEntity, Long> {

    public static final String TABLENAME = "PAYMENT_ITEM_ENTITY";

    /**
     * Properties of entity PaymentItemEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PosSerialChild = new Property(1, int.class, "posSerialChild", false, "POS_SERIAL_CHILD");
        public final static Property DishCode = new Property(2, String.class, "dishCode", false, "DISH_CODE");
        public final static Property DishName = new Property(3, String.class, "dishName", false, "DISH_NAME");
        public final static Property Price = new Property(4, int.class, "price", false, "PRICE");
        public final static Property Quantity = new Property(5, int.class, "quantity", false, "QUANTITY");
        public final static Property TransMoney = new Property(6, int.class, "transMoney", false, "TRANS_MONEY");
        public final static Property Timestamp = new Property(7, long.class, "timestamp", false, "TIMESTAMP");
        public final static Property PaymentTotalId = new Property(8, long.class, "paymentTotalId", false, "PAYMENT_TOTAL_ID");
    }

    private Query<PaymentItemEntity> paymentTotal_DishTransRecordDatasQuery;

    public PaymentItemEntityDao(DaoConfig config) {
        super(config);
    }
    
    public PaymentItemEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PAYMENT_ITEM_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"POS_SERIAL_CHILD\" INTEGER NOT NULL ," + // 1: posSerialChild
                "\"DISH_CODE\" TEXT," + // 2: dishCode
                "\"DISH_NAME\" TEXT," + // 3: dishName
                "\"PRICE\" INTEGER NOT NULL ," + // 4: price
                "\"QUANTITY\" INTEGER NOT NULL ," + // 5: quantity
                "\"TRANS_MONEY\" INTEGER NOT NULL ," + // 6: transMoney
                "\"TIMESTAMP\" INTEGER NOT NULL ," + // 7: timestamp
                "\"PAYMENT_TOTAL_ID\" INTEGER NOT NULL );"); // 8: paymentTotalId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PAYMENT_ITEM_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PaymentItemEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getPosSerialChild());
 
        String dishCode = entity.getDishCode();
        if (dishCode != null) {
            stmt.bindString(3, dishCode);
        }
 
        String dishName = entity.getDishName();
        if (dishName != null) {
            stmt.bindString(4, dishName);
        }
        stmt.bindLong(5, entity.getPrice());
        stmt.bindLong(6, entity.getQuantity());
        stmt.bindLong(7, entity.getTransMoney());
        stmt.bindLong(8, entity.getTimestamp());
        stmt.bindLong(9, entity.getPaymentTotalId());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PaymentItemEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getPosSerialChild());
 
        String dishCode = entity.getDishCode();
        if (dishCode != null) {
            stmt.bindString(3, dishCode);
        }
 
        String dishName = entity.getDishName();
        if (dishName != null) {
            stmt.bindString(4, dishName);
        }
        stmt.bindLong(5, entity.getPrice());
        stmt.bindLong(6, entity.getQuantity());
        stmt.bindLong(7, entity.getTransMoney());
        stmt.bindLong(8, entity.getTimestamp());
        stmt.bindLong(9, entity.getPaymentTotalId());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public PaymentItemEntity readEntity(Cursor cursor, int offset) {
        PaymentItemEntity entity = new PaymentItemEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // posSerialChild
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // dishCode
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // dishName
            cursor.getInt(offset + 4), // price
            cursor.getInt(offset + 5), // quantity
            cursor.getInt(offset + 6), // transMoney
            cursor.getLong(offset + 7), // timestamp
            cursor.getLong(offset + 8) // paymentTotalId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PaymentItemEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPosSerialChild(cursor.getInt(offset + 1));
        entity.setDishCode(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDishName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setPrice(cursor.getInt(offset + 4));
        entity.setQuantity(cursor.getInt(offset + 5));
        entity.setTransMoney(cursor.getInt(offset + 6));
        entity.setTimestamp(cursor.getLong(offset + 7));
        entity.setPaymentTotalId(cursor.getLong(offset + 8));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(PaymentItemEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(PaymentItemEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(PaymentItemEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "dishTransRecordDatas" to-many relationship of PaymentTotal. */
    public List<PaymentItemEntity> _queryPaymentTotal_DishTransRecordDatas(long paymentTotalId) {
        synchronized (this) {
            if (paymentTotal_DishTransRecordDatasQuery == null) {
                QueryBuilder<PaymentItemEntity> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.PaymentTotalId.eq(null));
                paymentTotal_DishTransRecordDatasQuery = queryBuilder.build();
            }
        }
        Query<PaymentItemEntity> query = paymentTotal_DishTransRecordDatasQuery.forCurrentThread();
        query.setParameter(0, paymentTotalId);
        return query.list();
    }

}
