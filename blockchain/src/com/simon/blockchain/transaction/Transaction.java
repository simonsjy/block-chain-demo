package com.simon.blockchain.transaction;

import com.simon.blockchain.util.CryptologyUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import com.simon.blockchain.Main;

/**
 * Created by simon on 2018/2/9.
 */
public class Transaction {
    //也是本次交易的hash值
    public String transactionId;

    public PublicKey sender;
    public PublicKey recipient;

    public float value;
    //数字签名保证两点：1.只有数字货币的拥有者才能使用。2.在交易被矿工挖出之前（共识前）交易不会被篡改。
    public byte[] signature;

    public List<TransactionInput> inputs = new ArrayList<>();
    public List<TransactionOutput> outputs = new ArrayList<>();

    //统计交易频次
    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value, List<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    public void generateSignature(PrivateKey privateKey) {
        //这里只对交易双方和value进行了签名，可以签名更多信息
        String data = CryptologyUtil.getStringFromKey(sender) + CryptologyUtil.getStringFromKey(recipient) +
                Float.toString(value);
        signature = CryptologyUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = CryptologyUtil.getStringFromKey(sender) + CryptologyUtil.getStringFromKey(recipient) +
                Float.toString(value);
        return CryptologyUtil.verifyECDSASig(sender,data,signature);
    }

    public boolean processTransaction(){

        if(!verifySignature()){
            System.out.println("Transaction Signature failed to verify");
            return false;
        }

        for(TransactionInput input:inputs){
            input.UTXO = Main.UTXOs.get(input.transactionOutputId);
        }

        if(getInputValue()< Main.minimumTransaction){
            System.out.println("Transaction Inputs too small: " + getInputValue());
            return false;
        }

        transactionId = calculateHash();

        float leftOver = getInputValue() - value;
        if(leftOver < 0.0){
            System.out.println("sum of Inputs is smaller than value, sum:" + getInputValue()+",value:"+value);
            return false;
        }

        outputs.add(new TransactionOutput(this.recipient,value,transactionId));
        outputs.add(new TransactionOutput(this.sender,leftOver,transactionId));

        //将所有output加入到hashmap
        for(TransactionOutput output:outputs){
            Main.UTXOs.put(output.id,output);
        }

        //将已经被使用了的input从hashmap中删除
        for(TransactionInput input:inputs){
            if(input.UTXO!=null){
                Main.UTXOs.remove(input.UTXO.id);
            }
        }

        return true;
    }

    public float getInputValue(){
        float total = 0;
        for(TransactionInput input:inputs ){
            if(input.UTXO == null){
                continue;
            }
            total += input.UTXO.value;
        }
        return total;
    }

    public float getOutputValue(){
        float total = 0;
        for(TransactionOutput output: outputs){
            total += output.value;
        }
        return total;
    }

    private String calculateHash() {
        sequence++;
        return CryptologyUtil.applySha256(
                CryptologyUtil.getStringFromKey(sender) +
                        CryptologyUtil.getStringFromKey(recipient) +
                        Float.toString(value) + sequence
        );
    }
}
