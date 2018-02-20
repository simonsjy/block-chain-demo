package com.simon.blockchain.element;

import com.simon.blockchain.Main;
import com.simon.blockchain.transaction.Transaction;
import com.simon.blockchain.transaction.TransactionInput;
import com.simon.blockchain.transaction.TransactionOutput;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by simon on 2018/2/8.
 */
public class Wallet {
    public PrivateKey privateKey;
    public PublicKey publicKey;

    //属于自己且没有被使用的TransactionOutput
    public Map<String,TransactionOutput> UTXOs = new HashMap<>();

    public Wallet(){
        generateKeyPair();
    }

    /**
     * 使用ECC（椭圆曲线算法）生成公私钥
     */
    public void generateKeyPair(){
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            keyPairGenerator.initialize(ecSpec,random);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 计算余额并其更新到本地存储
     * @return
     */
    public float getBalance(){
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: Main.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) {
                this.UTXOs.put(UTXO.id,UTXO);
                total += UTXO.value ;
            }
        }
        return total;
    }

    /**
     * 发起一笔转账
     * @param _recipient
     * @param value
     * @return
     */
    public Transaction sendFunds(PublicKey _recipient, float value) {
        if (getBalance() < value) {
            System.out.println();
        }
        List<TransactionInput> inputs = new ArrayList<>();

        float total = 0;

        for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if (total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);

        newTransaction.generateSignature(privateKey);

        for (TransactionInput input : inputs) {
            UTXOs.remove(input.transactionOutputId);
        }

        return newTransaction;
    }

}

