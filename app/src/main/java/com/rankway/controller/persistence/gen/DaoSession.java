package com.rankway.controller.persistence.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.rankway.controller.persistence.entity.DishEntity;
import com.rankway.controller.persistence.entity.DishSubTypeEntity;
import com.rankway.controller.persistence.entity.DishTypeEntity;
import com.rankway.controller.persistence.entity.MessageDetail;
import com.rankway.controller.persistence.entity.PaymentItemEntity;
import com.rankway.controller.persistence.entity.PaymentRecordEntity;
import com.rankway.controller.persistence.entity.PaymentTotal;
import com.rankway.controller.persistence.entity.PersonInfoEntity;
import com.rankway.controller.persistence.entity.QrBlackListEntity;
import com.rankway.controller.persistence.entity.SemiEventEntity;
import com.rankway.controller.persistence.entity.UserInfoEntity;

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

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig dishEntityDaoConfig;
    private final DaoConfig dishSubTypeEntityDaoConfig;
    private final DaoConfig dishTypeEntityDaoConfig;
    private final DaoConfig messageDetailDaoConfig;
    private final DaoConfig paymentItemEntityDaoConfig;
    private final DaoConfig paymentRecordEntityDaoConfig;
    private final DaoConfig paymentTotalDaoConfig;
    private final DaoConfig personInfoEntityDaoConfig;
    private final DaoConfig qrBlackListEntityDaoConfig;
    private final DaoConfig semiEventEntityDaoConfig;
    private final DaoConfig userInfoEntityDaoConfig;

    private final DishEntityDao dishEntityDao;
    private final DishSubTypeEntityDao dishSubTypeEntityDao;
    private final DishTypeEntityDao dishTypeEntityDao;
    private final MessageDetailDao messageDetailDao;
    private final PaymentItemEntityDao paymentItemEntityDao;
    private final PaymentRecordEntityDao paymentRecordEntityDao;
    private final PaymentTotalDao paymentTotalDao;
    private final PersonInfoEntityDao personInfoEntityDao;
    private final QrBlackListEntityDao qrBlackListEntityDao;
    private final SemiEventEntityDao semiEventEntityDao;
    private final UserInfoEntityDao userInfoEntityDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        dishEntityDaoConfig = daoConfigMap.get(DishEntityDao.class).clone();
        dishEntityDaoConfig.initIdentityScope(type);

        dishSubTypeEntityDaoConfig = daoConfigMap.get(DishSubTypeEntityDao.class).clone();
        dishSubTypeEntityDaoConfig.initIdentityScope(type);

        dishTypeEntityDaoConfig = daoConfigMap.get(DishTypeEntityDao.class).clone();
        dishTypeEntityDaoConfig.initIdentityScope(type);

        messageDetailDaoConfig = daoConfigMap.get(MessageDetailDao.class).clone();
        messageDetailDaoConfig.initIdentityScope(type);

        paymentItemEntityDaoConfig = daoConfigMap.get(PaymentItemEntityDao.class).clone();
        paymentItemEntityDaoConfig.initIdentityScope(type);

        paymentRecordEntityDaoConfig = daoConfigMap.get(PaymentRecordEntityDao.class).clone();
        paymentRecordEntityDaoConfig.initIdentityScope(type);

        paymentTotalDaoConfig = daoConfigMap.get(PaymentTotalDao.class).clone();
        paymentTotalDaoConfig.initIdentityScope(type);

        personInfoEntityDaoConfig = daoConfigMap.get(PersonInfoEntityDao.class).clone();
        personInfoEntityDaoConfig.initIdentityScope(type);

        qrBlackListEntityDaoConfig = daoConfigMap.get(QrBlackListEntityDao.class).clone();
        qrBlackListEntityDaoConfig.initIdentityScope(type);

        semiEventEntityDaoConfig = daoConfigMap.get(SemiEventEntityDao.class).clone();
        semiEventEntityDaoConfig.initIdentityScope(type);

        userInfoEntityDaoConfig = daoConfigMap.get(UserInfoEntityDao.class).clone();
        userInfoEntityDaoConfig.initIdentityScope(type);

        dishEntityDao = new DishEntityDao(dishEntityDaoConfig, this);
        dishSubTypeEntityDao = new DishSubTypeEntityDao(dishSubTypeEntityDaoConfig, this);
        dishTypeEntityDao = new DishTypeEntityDao(dishTypeEntityDaoConfig, this);
        messageDetailDao = new MessageDetailDao(messageDetailDaoConfig, this);
        paymentItemEntityDao = new PaymentItemEntityDao(paymentItemEntityDaoConfig, this);
        paymentRecordEntityDao = new PaymentRecordEntityDao(paymentRecordEntityDaoConfig, this);
        paymentTotalDao = new PaymentTotalDao(paymentTotalDaoConfig, this);
        personInfoEntityDao = new PersonInfoEntityDao(personInfoEntityDaoConfig, this);
        qrBlackListEntityDao = new QrBlackListEntityDao(qrBlackListEntityDaoConfig, this);
        semiEventEntityDao = new SemiEventEntityDao(semiEventEntityDaoConfig, this);
        userInfoEntityDao = new UserInfoEntityDao(userInfoEntityDaoConfig, this);

        registerDao(DishEntity.class, dishEntityDao);
        registerDao(DishSubTypeEntity.class, dishSubTypeEntityDao);
        registerDao(DishTypeEntity.class, dishTypeEntityDao);
        registerDao(MessageDetail.class, messageDetailDao);
        registerDao(PaymentItemEntity.class, paymentItemEntityDao);
        registerDao(PaymentRecordEntity.class, paymentRecordEntityDao);
        registerDao(PaymentTotal.class, paymentTotalDao);
        registerDao(PersonInfoEntity.class, personInfoEntityDao);
        registerDao(QrBlackListEntity.class, qrBlackListEntityDao);
        registerDao(SemiEventEntity.class, semiEventEntityDao);
        registerDao(UserInfoEntity.class, userInfoEntityDao);
    }
    
    public void clear() {
        dishEntityDaoConfig.clearIdentityScope();
        dishSubTypeEntityDaoConfig.clearIdentityScope();
        dishTypeEntityDaoConfig.clearIdentityScope();
        messageDetailDaoConfig.clearIdentityScope();
        paymentItemEntityDaoConfig.clearIdentityScope();
        paymentRecordEntityDaoConfig.clearIdentityScope();
        paymentTotalDaoConfig.clearIdentityScope();
        personInfoEntityDaoConfig.clearIdentityScope();
        qrBlackListEntityDaoConfig.clearIdentityScope();
        semiEventEntityDaoConfig.clearIdentityScope();
        userInfoEntityDaoConfig.clearIdentityScope();
    }

    public DishEntityDao getDishEntityDao() {
        return dishEntityDao;
    }

    public DishSubTypeEntityDao getDishSubTypeEntityDao() {
        return dishSubTypeEntityDao;
    }

    public DishTypeEntityDao getDishTypeEntityDao() {
        return dishTypeEntityDao;
    }

    public MessageDetailDao getMessageDetailDao() {
        return messageDetailDao;
    }

    public PaymentItemEntityDao getPaymentItemEntityDao() {
        return paymentItemEntityDao;
    }

    public PaymentRecordEntityDao getPaymentRecordEntityDao() {
        return paymentRecordEntityDao;
    }

    public PaymentTotalDao getPaymentTotalDao() {
        return paymentTotalDao;
    }

    public PersonInfoEntityDao getPersonInfoEntityDao() {
        return personInfoEntityDao;
    }

    public QrBlackListEntityDao getQrBlackListEntityDao() {
        return qrBlackListEntityDao;
    }

    public SemiEventEntityDao getSemiEventEntityDao() {
        return semiEventEntityDao;
    }

    public UserInfoEntityDao getUserInfoEntityDao() {
        return userInfoEntityDao;
    }

}
