package com.example.application_template_jmvvm.ui.transaction;

import javax.inject.Inject;

import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.database.repository.TransactionRepository;
import com.example.application_template_jmvvm.MainActivity;

import java.util.List;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TransactionViewModel extends ViewModel{
    private MainActivity main;
    private TransactionRepository transactionRepository;

    @Inject
    public TransactionViewModel(MainActivity main, TransactionRepository transactionRepository) {
        this.main = main;
        this.transactionRepository = transactionRepository;
    }

    public void insertTransaction(TransactionEntity transaction) {
        transactionRepository.insertTransaction(transaction);
    }

    public List<TransactionEntity> getAllTransactions() {
        return transactionRepository.getAllTransactions();
    }

    public List<TransactionEntity> getTransactionsByRefNo(String refNo) {
        return transactionRepository.getTransactionsByRefNo(refNo);
    }

    public List<TransactionEntity> getTransactionsByCardNo(String cardNo) {
        return transactionRepository.getTransactionsByCardNo(cardNo);
    }

    public void setVoid(int gupSN, String date, String card_SID) {
        transactionRepository.setVoid(gupSN, date, card_SID);
    }

    public boolean isTransactionListEmpty() {
        return transactionRepository.isEmpty();
    }

    public void deleteAllTransactions() {
        transactionRepository.deleteAll();
    }

}