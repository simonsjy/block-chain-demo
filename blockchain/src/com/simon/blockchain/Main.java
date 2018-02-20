package com.simon.blockchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.simon.blockchain.element.Block;
import com.simon.blockchain.element.Wallet;
import com.simon.blockchain.transaction.Transaction;
import com.simon.blockchain.transaction.TransactionOutput;
import com.simon.blockchain.util.CryptologyUtil;


/**
 * Created by simon on 2018/2/8.
 */
public class Main {

    public static List<Block> blockChain = new ArrayList<>();

    //为了提高交易效率，使用额外的数据记录输出信息。
    public static Map<String, TransactionOutput> UTXOs = new HashMap<>();

    //可以通过设置不同的值来测试挖矿时间（不要设置太大，否则后果自负^_^）
    public static final int difficulty = 4;

    //设置最小交易额
    public static final float minimumTransaction = 0.1f;

    public static Wallet wallet1;

    public static Wallet wallet2;

    public Transaction genesisTransaction;


    public static void main(String args[]) {
        transactionTest();
    }

    public static void mineTest() {
        long startTime = System.currentTimeMillis();

        blockChain.add(new Block("I am the first block", "0"));
        System.out.println("Start mine block 1... ");
        blockChain.get(0).mineBlock(difficulty);

        blockChain.add(new Block("I am the second block", blockChain.get(blockChain.size() - 1).hash));
        System.out.println("Start mine block 2... ");
        blockChain.get(1).mineBlock(difficulty);

        blockChain.add(new Block("I am the third block", blockChain.get(blockChain.size() - 1).hash));
        System.out.println("Start mine block 3... ");
        blockChain.get(2).mineBlock(difficulty);

        long endTime = System.currentTimeMillis();

        String blockChainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
        System.out.println("\nTotal time :" + (endTime - startTime) + "ms .The block chain is:");
        System.out.println(blockChainJson);

        System.out.println("\nBlockChain is Valid: " + isChainValid());
    }

    public static void transactionTest() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        wallet1 = new Wallet();
        wallet2 = new Wallet();

        System.out.println("Private and public keys:");
        System.out.println(CryptologyUtil.getStringFromKey(wallet1.privateKey));
        System.out.println();
        System.out.println(CryptologyUtil.getStringFromKey(wallet1.publicKey));

        Transaction transaction = new Transaction(wallet1.publicKey, wallet2.publicKey, 5, null);
        transaction.generateSignature(wallet1.privateKey);

        System.out.println("\nIs signature verified:" + transaction.verifySignature());

    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        for (int i = 1; i < blockChain.size(); i++) {
            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i - 1);

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }

            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Previous Hashes not equal");
                return false;
            }

            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block has not been mined");
                return false;
            }
        }
        return true;
    }

}
