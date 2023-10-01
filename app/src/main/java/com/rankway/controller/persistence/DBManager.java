package com.rankway.controller.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.rankway.controller.persistence.entity.PaymentRecord;
import com.rankway.controller.persistence.gen.DaoMaster;
import com.rankway.controller.persistence.gen.DaoSession;
import com.rankway.controller.persistence.gen.DishDao;
import com.rankway.controller.persistence.gen.DishTypeDao;
import com.rankway.controller.persistence.gen.MessageDetailDao;
import com.rankway.controller.persistence.gen.PaymentRecordDao;
import com.rankway.controller.persistence.gen.SemiEventEntityDao;

import java.util.List;


public class DBManager {
    public static String DB_NAME = "wxjtpos.db";

    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    public static DBManager mDBManager;
    private DBHelper dbHelper;

    public static synchronized DBManager getInstance() {
        return mDBManager;
    }

    private DBManager(Context context) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public static void init(Context context) {

        if (mDBManager == null)
            mDBManager = new DBManager(context);
    }


    public MessageDetailDao getMessageDetailDao(){
        return mDaoSession.getMessageDetailDao();
    }

    public SemiEventEntityDao getSemiEventEntityDao() {
        return mDaoSession.getSemiEventEntityDao();
    }

    //
    public PaymentRecordDao getPaymentRecordDao(){
        return mDaoSession.getPaymentRecordDao();
    }
    public void savePaymentRecord(PaymentRecord record){
        getPaymentRecordDao().save(record);
    }
    public void deletePaymentRecord(PaymentRecord record){
        getPaymentRecordDao().delete(record);
    }
    public List<PaymentRecord> getAllPaymentRecords(){
        return getPaymentRecordDao().queryBuilder().list();
    }
    public void deleteInTxPaymentRecord(List<PaymentRecord> list){
        getPaymentRecordDao().deleteInTx(list);
    }

    public DishTypeDao getDishTypeDao(){
        return mDaoSession.getDishTypeDao();
    }

    public DishDao getDishDao(){
        return mDaoSession.getDishDao();
    }
}
