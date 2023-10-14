package com.rankway.controller.persistence.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.rankway.controller.persistence.entity.PaymentRecordEntity;
import com.rankway.controller.persistence.entity.PaymentTotal;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.internal.SqlUtils;

import java.util.ArrayList;
import java.util.List;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PAYMENT_RECORD_ENTITY".
*/
public class PaymentRecordEntityDao extends AbstractDao<PaymentRecordEntity, Long> {

    public static final String TABLENAME = "PAYMENT_RECORD_ENTITY";

    /**
     * Properties of entity PaymentRecordEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property AuditNo = new Property(1, int.class, "auditNo", false, "AUDIT_NO");
        public final static Property PosNo = new Property(2, String.class, "posNo", false, "POS_NO");
        public final static Property Postype = new Property(3, int.class, "postype", false, "POSTYPE");
        public final static Property Payway = new Property(4, int.class, "payway", false, "PAYWAY");
        public final static Property UserCode = new Property(5, String.class, "userCode", false, "USER_CODE");
        public final static Property Cardno = new Property(6, int.class, "cardno", false, "CARDNO");
        public final static Property Remain = new Property(7, float.class, "remain", false, "REMAIN");
        public final static Property Amount = new Property(8, float.class, "amount", false, "AMOUNT");
        public final static Property Balance = new Property(9, float.class, "balance", false, "BALANCE");
        public final static Property Typeid = new Property(10, int.class, "typeid", false, "TYPEID");
        public final static Property TransTime = new Property(11, java.util.Date.class, "transTime", false, "TRANS_TIME");
        public final static Property CardSNO = new Property(12, String.class, "cardSNO", false, "CARD_SNO");
        public final static Property WorkNo = new Property(13, String.class, "workNo", false, "WORK_NO");
        public final static Property WorkName = new Property(14, String.class, "workName", false, "WORK_NAME");
        public final static Property UserId = new Property(15, String.class, "userId", false, "USER_ID");
        public final static Property QrType = new Property(16, int.class, "qrType", false, "QR_TYPE");
        public final static Property SystemId = new Property(17, int.class, "systemId", false, "SYSTEM_ID");
        public final static Property UploadFlag = new Property(18, int.class, "uploadFlag", false, "UPLOAD_FLAG");
        public final static Property UploadTime = new Property(19, java.util.Date.class, "uploadTime", false, "UPLOAD_TIME");
        public final static Property PaymentTotalId = new Property(20, long.class, "paymentTotalId", false, "PAYMENT_TOTAL_ID");
    }

    private DaoSession daoSession;


    public PaymentRecordEntityDao(DaoConfig config) {
        super(config);
    }
    
    public PaymentRecordEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PAYMENT_RECORD_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"AUDIT_NO\" INTEGER NOT NULL ," + // 1: auditNo
                "\"POS_NO\" TEXT," + // 2: posNo
                "\"POSTYPE\" INTEGER NOT NULL ," + // 3: postype
                "\"PAYWAY\" INTEGER NOT NULL ," + // 4: payway
                "\"USER_CODE\" TEXT," + // 5: userCode
                "\"CARDNO\" INTEGER NOT NULL ," + // 6: cardno
                "\"REMAIN\" REAL NOT NULL ," + // 7: remain
                "\"AMOUNT\" REAL NOT NULL ," + // 8: amount
                "\"BALANCE\" REAL NOT NULL ," + // 9: balance
                "\"TYPEID\" INTEGER NOT NULL ," + // 10: typeid
                "\"TRANS_TIME\" INTEGER," + // 11: transTime
                "\"CARD_SNO\" TEXT," + // 12: cardSNO
                "\"WORK_NO\" TEXT," + // 13: workNo
                "\"WORK_NAME\" TEXT," + // 14: workName
                "\"USER_ID\" TEXT," + // 15: userId
                "\"QR_TYPE\" INTEGER NOT NULL ," + // 16: qrType
                "\"SYSTEM_ID\" INTEGER NOT NULL ," + // 17: systemId
                "\"UPLOAD_FLAG\" INTEGER NOT NULL ," + // 18: uploadFlag
                "\"UPLOAD_TIME\" INTEGER," + // 19: uploadTime
                "\"PAYMENT_TOTAL_ID\" INTEGER NOT NULL );"); // 20: paymentTotalId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PAYMENT_RECORD_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PaymentRecordEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getAuditNo());
 
        String posNo = entity.getPosNo();
        if (posNo != null) {
            stmt.bindString(3, posNo);
        }
        stmt.bindLong(4, entity.getPostype());
        stmt.bindLong(5, entity.getPayway());
 
        String userCode = entity.getUserCode();
        if (userCode != null) {
            stmt.bindString(6, userCode);
        }
        stmt.bindLong(7, entity.getCardno());
        stmt.bindDouble(8, entity.getRemain());
        stmt.bindDouble(9, entity.getAmount());
        stmt.bindDouble(10, entity.getBalance());
        stmt.bindLong(11, entity.getTypeid());
 
        java.util.Date transTime = entity.getTransTime();
        if (transTime != null) {
            stmt.bindLong(12, transTime.getTime());
        }
 
        String cardSNO = entity.getCardSNO();
        if (cardSNO != null) {
            stmt.bindString(13, cardSNO);
        }
 
        String workNo = entity.getWorkNo();
        if (workNo != null) {
            stmt.bindString(14, workNo);
        }
 
        String workName = entity.getWorkName();
        if (workName != null) {
            stmt.bindString(15, workName);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(16, userId);
        }
        stmt.bindLong(17, entity.getQrType());
        stmt.bindLong(18, entity.getSystemId());
        stmt.bindLong(19, entity.getUploadFlag());
 
        java.util.Date uploadTime = entity.getUploadTime();
        if (uploadTime != null) {
            stmt.bindLong(20, uploadTime.getTime());
        }
        stmt.bindLong(21, entity.getPaymentTotalId());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PaymentRecordEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getAuditNo());
 
        String posNo = entity.getPosNo();
        if (posNo != null) {
            stmt.bindString(3, posNo);
        }
        stmt.bindLong(4, entity.getPostype());
        stmt.bindLong(5, entity.getPayway());
 
        String userCode = entity.getUserCode();
        if (userCode != null) {
            stmt.bindString(6, userCode);
        }
        stmt.bindLong(7, entity.getCardno());
        stmt.bindDouble(8, entity.getRemain());
        stmt.bindDouble(9, entity.getAmount());
        stmt.bindDouble(10, entity.getBalance());
        stmt.bindLong(11, entity.getTypeid());
 
        java.util.Date transTime = entity.getTransTime();
        if (transTime != null) {
            stmt.bindLong(12, transTime.getTime());
        }
 
        String cardSNO = entity.getCardSNO();
        if (cardSNO != null) {
            stmt.bindString(13, cardSNO);
        }
 
        String workNo = entity.getWorkNo();
        if (workNo != null) {
            stmt.bindString(14, workNo);
        }
 
        String workName = entity.getWorkName();
        if (workName != null) {
            stmt.bindString(15, workName);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(16, userId);
        }
        stmt.bindLong(17, entity.getQrType());
        stmt.bindLong(18, entity.getSystemId());
        stmt.bindLong(19, entity.getUploadFlag());
 
        java.util.Date uploadTime = entity.getUploadTime();
        if (uploadTime != null) {
            stmt.bindLong(20, uploadTime.getTime());
        }
        stmt.bindLong(21, entity.getPaymentTotalId());
    }

    @Override
    protected final void attachEntity(PaymentRecordEntity entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public PaymentRecordEntity readEntity(Cursor cursor, int offset) {
        PaymentRecordEntity entity = new PaymentRecordEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // auditNo
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // posNo
            cursor.getInt(offset + 3), // postype
            cursor.getInt(offset + 4), // payway
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // userCode
            cursor.getInt(offset + 6), // cardno
            cursor.getFloat(offset + 7), // remain
            cursor.getFloat(offset + 8), // amount
            cursor.getFloat(offset + 9), // balance
            cursor.getInt(offset + 10), // typeid
            cursor.isNull(offset + 11) ? null : new java.util.Date(cursor.getLong(offset + 11)), // transTime
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // cardSNO
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // workNo
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // workName
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // userId
            cursor.getInt(offset + 16), // qrType
            cursor.getInt(offset + 17), // systemId
            cursor.getInt(offset + 18), // uploadFlag
            cursor.isNull(offset + 19) ? null : new java.util.Date(cursor.getLong(offset + 19)), // uploadTime
            cursor.getLong(offset + 20) // paymentTotalId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PaymentRecordEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAuditNo(cursor.getInt(offset + 1));
        entity.setPosNo(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPostype(cursor.getInt(offset + 3));
        entity.setPayway(cursor.getInt(offset + 4));
        entity.setUserCode(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCardno(cursor.getInt(offset + 6));
        entity.setRemain(cursor.getFloat(offset + 7));
        entity.setAmount(cursor.getFloat(offset + 8));
        entity.setBalance(cursor.getFloat(offset + 9));
        entity.setTypeid(cursor.getInt(offset + 10));
        entity.setTransTime(cursor.isNull(offset + 11) ? null : new java.util.Date(cursor.getLong(offset + 11)));
        entity.setCardSNO(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setWorkNo(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setWorkName(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setUserId(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setQrType(cursor.getInt(offset + 16));
        entity.setSystemId(cursor.getInt(offset + 17));
        entity.setUploadFlag(cursor.getInt(offset + 18));
        entity.setUploadTime(cursor.isNull(offset + 19) ? null : new java.util.Date(cursor.getLong(offset + 19)));
        entity.setPaymentTotalId(cursor.getLong(offset + 20));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(PaymentRecordEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(PaymentRecordEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(PaymentRecordEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getPaymentTotalDao().getAllColumns());
            builder.append(" FROM PAYMENT_RECORD_ENTITY T");
            builder.append(" LEFT JOIN PAYMENT_TOTAL T0 ON T.\"PAYMENT_TOTAL_ID\"=T0.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected PaymentRecordEntity loadCurrentDeep(Cursor cursor, boolean lock) {
        PaymentRecordEntity entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        PaymentTotal total = loadCurrentOther(daoSession.getPaymentTotalDao(), cursor, offset);
         if(total != null) {
            entity.setTotal(total);
        }

        return entity;    
    }

    public PaymentRecordEntity loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<PaymentRecordEntity> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<PaymentRecordEntity> list = new ArrayList<PaymentRecordEntity>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<PaymentRecordEntity> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<PaymentRecordEntity> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
