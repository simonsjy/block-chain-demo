package com.simon.blockchain.transaction;

import com.simon.blockchain.util.CryptologyUtil;

import java.security.PublicKey;

/**
 * Created by simon on 2018/2/9.
 */
public class TransactionOutput {
    public String id;
    public PublicKey reciepient;
    public float value;
    public String parentTransactionId;

    public TransactionOutput(PublicKey reciepient,float value,String parentTransactionId){
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = CryptologyUtil.applySha256(CryptologyUtil.getStringFromKey(reciepient)+
                Float.toString(value)+parentTransactionId);

    }

    public boolean isMine(PublicKey publicKey){
        return (publicKey == reciepient);
    }

}
