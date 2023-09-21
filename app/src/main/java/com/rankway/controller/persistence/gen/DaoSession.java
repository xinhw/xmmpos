package com.rankway.controller.persistence.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.rankway.controller.persistence.entity.Dish;
import com.rankway.controller.persistence.entity.DishType;
import com.rankway.controller.persistence.entity.MessageDetail;
import com.rankway.controller.persistence.entity.PaymentItem;
import com.rankway.controller.persistence.entity.PaymentRecord;
import com.rankway.controller.persistence.entity.PaymentTotal;
import com.rankway.controller.persistence.entity.SemiEventEntity;

import com.rankway.controller.persistence.gen.DishDao;
import com.rankway.controller.persistence.gen.DishTypeDao;
import com.rankway.controller.persistence.gen.MessageDetailDao;
import com.rankway.controller.persistence.gen.PaymentItemDao;
import com.rankway.controller.persistence.gen.PaymentRecordDao;
import com.rankway.controller.persistence.gen.PaymentTotalDao;
import com.rankway.controller.persistence.gen.SemiEventEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig dishDaoConfig;
    private final DaoConfig dishTypeDaoConfig;
    private final DaoConfig messageDetailDaoConfig;
    private final DaoConfig paymentItemDaoConfig;
    private final DaoConfig paymentRecordDaoConfig;
    private final DaoConfig paymentTotalDaoConfig;
    private final DaoConfig semiEventEntityDaoConfig;

    private final DishDao dishDao;
    private final DishTypeDao dishTypeDao;
    private final MessageDetailDao messageDetailDao;
    private final PaymentItemDao paymentItemDao;
    private final PaymentRecordDao paymentRecordDao;
    private final PaymentTotalDao paymentTotalDao;
    private final SemiEventEntityDao semiEventEntityDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        dishDaoConfig = daoConfigMap.get(DishDao.class).clone();
        dishDaoConfig.initIdentityScope(type);

        dishTypeDaoConfig = daoConfigMap.get(DishTypeDao.class).clone();
        dishTypeDaoConfig.initIdentityScope(type);

        messageDetailDaoConfig = daoConfigMap.get(MessageDetailDao.class).clone();
        messageDetailDaoConfig.initIdentityScope(type);

        paymentItemDaoConfig = daoConfigMap.get(PaymentItemDao.class).clone();
        paymentItemDaoConfig.initIdentityScope(type);

        paymentRecordDaoConfig = daoConfigMap.get(PaymentRecordDao.class).clone();
        paymentRecordDaoConfig.initIdentityScope(type);

        paymentTotalDaoConfig = daoConfigMap.get(PaymentTotalDao.class).clone();
        paymentTotalDaoConfig.initIdentityScope(type);

        semiEventEntityDaoConfig = daoConfigMap.get(SemiEventEntityDao.class).clone();
        semiEventEntityDaoConfig.initIdentityScope(type);

        dishDao = new DishDao(dishDaoConfig, this);
        dishTypeDao = new DishTypeDao(dishTypeDaoConfig, this);
        messageDetailDao = new MessageDetailDao(messageDetailDaoConfig, this);
        paymentItemDao = new PaymentItemDao(paymentItemDaoConfig, this);
        paymentRecordDao = new PaymentRecordDao(paymentRecordDaoConfig, this);
        paymentTotalDao = new PaymentTotalDao(paymentTotalDaoConfig, this);
        semiEventEntityDao = new SemiEventEntityDao(semiEventEntityDaoConfig, this);

        registerDao(Dish.class, dishDao);
        registerDao(DishType.class, dishTypeDao);
        registerDao(MessageDetail.class, messageDetailDao);
        registerDao(PaymentItem.class, paymentItemDao);
        registerDao(PaymentRecord.class, paymentRecordDao);
        registerDao(PaymentTotal.class, paymentTotalDao);
        registerDao(SemiEventEntity.class, semiEventEntityDao);
    }
    
    public void clear() {
        dishDaoConfig.clearIdentityScope();
        dishTypeDaoConfig.clearIdentityScope();
        messageDetailDaoConfig.clearIdentityScope();
        paymentItemDaoConfig.clearIdentityScope();
        paymentRecordDaoConfig.clearIdentityScope();
        paymentTotalDaoConfig.clearIdentityScope();
        semiEventEntityDaoConfig.clearIdentityScope();
    }

    public DishDao getDishDao() {
        return dishDao;
    }

    public DishTypeDao getDishTypeDao() {
        return dishTypeDao;
    }

    public MessageDetailDao getMessageDetailDao() {
        return messageDetailDao;
    }

    public PaymentItemDao getPaymentItemDao() {
        return paymentItemDao;
    }

    public PaymentRecordDao getPaymentRecordDao() {
        return paymentRecordDao;
    }

    public PaymentTotalDao getPaymentTotalDao() {
        return paymentTotalDao;
    }

    public SemiEventEntityDao getSemiEventEntityDao() {
        return semiEventEntityDao;
    }

}
