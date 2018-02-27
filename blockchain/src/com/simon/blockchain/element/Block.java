package com.simon.blockchain.element;

import com.simon.blockchain.transaction.Transaction;
import com.simon.blockchain.util.CryptologyUtil;
import com.simon.blockchain.util.TreeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by simon on 2018/2/8.
 */
public class Block {
    //区块的hash，即数字签名
    public String hash;
    //前一个区块的hash
    public String previousHash;
    //hash根
    public String merkleRoot;
    //区块中的数据
    public String data;

    public List<Transaction> transactions = new ArrayList<>();
    //时间戳
    private long timeStamp;
    //随机数，使得hash的前difficulty位是0
    private int nonce;

    //block初始化的简单例子
    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    //block实际使用的构造函数
    public Block(String previousHash){
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash  = calculateHash();
    }

    public String calculateHash() {
        String calculatedHash = CryptologyUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
        return calculatedHash;
    }

    /**
     * 不停的重复计算hash，直至前difficulty位是0
     * @param difficulty
     */
    public void mineBlock(int difficulty){
        merkleRoot = TreeUtil.getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0','0');
        while (!hash.substring(0,difficulty).equals(target)){
            //可以用random值作为nonce进行尝试
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Mine successful! hash = " + hash);
    }

    public boolean addTransaction(Transaction transaction){
        if(Objects.isNull(transaction)){
            return false;
        }
        if(!Objects.equals(previousHash,"0")){
            if(!transaction.processTransaction()) {
                System.out.println("Transaction failed to process.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction successfully added to block");
        return true;
    }
}
