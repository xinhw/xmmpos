package com.rankway.controller.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.rankway.controller.persistence.gen.DaoMaster;
import com.rankway.controller.persistence.gen.DaoSession;
import com.rankway.controller.persistence.gen.DishEntityDao;
import com.rankway.controller.persistence.gen.DishSubTypeEntityDao;
import com.rankway.controller.persistence.gen.DishTypeEntityDao;
import com.rankway.controller.persistence.gen.MessageDetailDao;
import com.rankway.controller.persistence.gen.PaymentItemEntityDao;
import com.rankway.controller.persistence.gen.PaymentRecordEntityDao;
import com.rankway.controller.persistence.gen.PaymentTotalDao;
import com.rankway.controller.persistence.gen.PersonInfoEntityDao;
import com.rankway.controller.persistence.gen.QrBlackListEntityDao;
import com.rankway.controller.persistence.gen.SemiEventEntityDao;
import com.rankway.controller.persistence.gen.UserInfoEntityDao;


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



    public DishEntityDao getDishEntityDao(){
        return mDaoSession.getDishEntityDao();
    }

    public DishTypeEntityDao getDishTypeEntityDao(){
        return mDaoSession.getDishTypeEntityDao();
    }

    public MessageDetailDao getMessageDetailDao(){
        return mDaoSession.getMessageDetailDao();
    }

    public PaymentItemEntityDao getPaymentItemEntityDao(){
        return mDaoSession.getPaymentItemEntityDao();
    }

    public PaymentRecordEntityDao getPaymentRecordEntityDao(){
        return mDaoSession.getPaymentRecordEntityDao();
    }

    public PaymentTotalDao getPaymentTotalDao(){
        return mDaoSession.getPaymentTotalDao();
    }

    public QrBlackListEntityDao getQrBlackListEntityDao(){
        return mDaoSession.getQrBlackListEntityDao();
    }

    public SemiEventEntityDao getSemiEventEntityDao() {
        return mDaoSession.getSemiEventEntityDao();
    }

    public UserInfoEntityDao getUserInfoEntityDao(){
        return mDaoSession.getUserInfoEntityDao();
    }

    public PersonInfoEntityDao getPersonInfoEntityDao(){
        return mDaoSession.getPersonInfoEntityDao();
    }

    public int getDatabaseVerion(){
        return DaoMaster.SCHEMA_VERSION;
    }

    public DishSubTypeEntityDao getDishSubTypeEntityDao(){
        return mDaoSession.getDishSubTypeEntityDao();
    }
}
