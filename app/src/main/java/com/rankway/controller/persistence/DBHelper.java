package com.rankway.controller.persistence;

import android.content.Context;

import com.rankway.controller.persistence.entity.SemiEventEntity;
import com.rankway.controller.persistence.gen.CardBlackListEntityDao;
import com.rankway.controller.persistence.gen.DaoMaster;
import com.rankway.controller.persistence.gen.DishDao;
import com.rankway.controller.persistence.gen.DishTypeDao;
import com.rankway.controller.persistence.gen.PaymentItemDao;
import com.rankway.controller.persistence.gen.PaymentRecordDao;
import com.rankway.controller.persistence.gen.PaymentTotalDao;
import com.rankway.controller.persistence.gen.QrBlackListEntityDao;
import com.rankway.controller.persistence.gen.UserInfoEntityDao;

import org.greenrobot.greendao.database.Database;

/**
 * @作者:Sommer
 * @时间:2018-11-01
 * @描述:数据库辅助类
 */
public class DBHelper extends DaoMaster.DevOpenHelper {
    public DBHelper(Context context) {
        super(context, DBManager.DB_NAME, null);
    }
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //   需要进行数据迁移更新的实体类 ，新增的不用加
        DBMigrationHelper.getInstance().migrate(db,
                CardBlackListEntityDao.class,

                DishDao.class,
                DishTypeDao.class,

                // MessageDetailDao.class,

                PaymentItemDao.class,
                PaymentRecordDao.class,
                PaymentTotalDao.class,

                QrBlackListEntityDao.class,

                SemiEventEntity.class,               //  事件列表

                UserInfoEntityDao.class
        );
    }
}
