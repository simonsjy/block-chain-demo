package com.simon.blockchain.transaction;

/**
 * Created by simon on 2018/2/9.
 */
public class TransactionInput {
    public String transactionOutputId;
    public TransactionOutput UTXO;

    public TransactionInput(String transactionOutputId){
        this.transactionOutputId = transactionOutputId;
    }
}
