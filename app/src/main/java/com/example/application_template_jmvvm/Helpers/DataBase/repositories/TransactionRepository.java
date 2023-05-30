package com.example.application_template_jmvvm.Helpers.DataBase.repositories;

import com.example.application_template_jmvvm.Helpers.DataBase.daos.TransactionDao;
import com.example.application_template_jmvvm.Helpers.DataBase.entities.TransactionEntity;

import java.util.List;
import javax.inject.Inject;

public class TransactionRepository {

        private TransactionDao transactionDao;
        private List<TransactionEntity> allTransactions;

        @Inject
        public TransactionRepository(TransactionDao transactionDao) {
            this.transactionDao = transactionDao;
            this.allTransactions = transactionDao.getAllTransactions();
        }

        public List<TransactionEntity> getTransactionsByRefNo(String refNo) {
            return transactionDao.getTransactionsByRefNo(refNo);
        }

        public List<TransactionEntity> getTransactionsByCardNo(String cardNo) {
            return transactionDao.getTransactionsByCardNo(cardNo);
        }

        public void insertTransaction(TransactionEntity transaction) {
            transactionDao.insertTransaction(transaction);
        }

        public void setVoid(int gupSN, String date, String card_SID) {
            transactionDao.setVoid(gupSN, date, card_SID);
        }

        public boolean isEmpty() {
            return transactionDao.isEmpty() == 0;
        }

        public void deleteAll() {
            transactionDao.deleteAll();
        }
}
