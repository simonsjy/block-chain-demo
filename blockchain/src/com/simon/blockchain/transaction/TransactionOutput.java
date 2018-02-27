package com.simon.blockchain.transaction;

import com.simon.blockchain.util.CryptologyUtil;

import java.security.PublicKey;

/**
 * Created by simon on 2018/2/9.
 */
public class TransactionOutput {
    public String id;
    public PublicKey recipient;
    public float value;
    public String parentTransactionId;

    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = CryptologyUtil.applySha256(CryptologyUtil.getStringFromKey(recipient) +
                Float.toString(value) + parentTransactionId);

    }

    public boolean isMine(PublicKey publicKey) {
        return (publicKey == recipient);
    }

}
