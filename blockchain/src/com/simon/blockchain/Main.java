package com.simon.blockchain;

import java.security.Security;
import java.util.*;

import com.google.gson.GsonBuilder;
import com.simon.blockchain.element.Block;
import com.simon.blockchain.element.Wallet;
import com.simon.blockchain.transaction.Transaction;
import com.simon.blockchain.transaction.TransactionInput;
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

    //创世交易
    public static Transaction genesisTransaction;


    public static void main(String args[]) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        //mineTest();
        //transactionTest();
        genesisTest();
    }

    //挖矿功能测试
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

        System.out.println("\nBlockChain is Valid: " + isBlockChainValid());
    }

    //简单的交易测试
    public static void transactionTest() {
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

    //构建区块链系统完整流程测试
    public static void genesisTest(){
        Wallet baseCoin = new Wallet();
        wallet1 = new Wallet();
        wallet2 = new Wallet();
        //构建创世区块
        Block genesis = genesis(baseCoin,wallet1);

        //创建几个交易和区块进行测试
        Block block1 = new Block(genesis.hash);
        System.out.println("\nWallet1's balance is: " + wallet1.getBalance());
        System.out.println("\nWallet1 is sending 4 to Wallet2...");
        block1.addTransaction(wallet1.sendFunds(wallet2.publicKey, 4f));
        addBlock(block1);
        System.out.println("\nWallet1's balance is: " + wallet1.getBalance());
        System.out.println("\nWallet2's balance is: " + wallet2.getBalance());

        Block block2 = new Block(block1.hash);
        System.out.println("\nWallet1 Attempting to send more funds 100 than it has...");
        block2.addTransaction(wallet1.sendFunds(wallet2.publicKey, 100f));
        addBlock(block2);
        System.out.println("\nWallet1's balance is: " + wallet1.getBalance());
        System.out.println("Wallet2's balance is: " + wallet2.getBalance());

        Block block3 = new Block(block2.hash);
        System.out.println("\nWallet2 is sending 2 to Wallet1...");
        block3.addTransaction(wallet2.sendFunds( wallet1.publicKey, 2));
        System.out.println("\nWallet1's balance is: " + wallet1.getBalance());
        System.out.println("Wallet2's balance is: " + wallet2.getBalance());

        isBlockChainValid();


    }

    public static Block genesis(Wallet baseCoin,Wallet wallet){

        //创世交易不需要任何input
        genesisTransaction = new Transaction(baseCoin.publicKey,wallet.publicKey,10f,null);
        genesisTransaction.generateSignature(baseCoin.privateKey);
        genesisTransaction.transactionId = "0";
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient,genesisTransaction.value,genesisTransaction.transactionId));
        UTXOs.put(genesisTransaction.outputs.get(0).id,genesisTransaction.outputs.get(0));

        //开始构建创世区块
        System.out.println("start Genesis block");
        Block genesisBlock = new Block("0");
        genesisBlock.addTransaction(genesisTransaction);
        addBlock(genesisBlock);
        return genesisBlock;
    }

    /**
     * 校验区块链中的信息是否有效
     * @return
     */
    public static Boolean isBlockChainValid() {
        Block currentBlock;
        Block previousBlock;
        Map<String,TransactionOutput> tempUTXOs = new HashMap<>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).id,genesisTransaction.outputs.get(0));

        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        for (int i = 1; i < blockChain.size(); i++) {
            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i - 1);

            //首先校验当前区块是否有效
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

            //校验当前区块中的每笔交易是否有效
            for(int j=0;j<currentBlock.transactions.size();j++){
                Transaction currentTransaction = currentBlock.transactions.get(j);

                if(!currentTransaction.verifySignature()){
                    System.out.println("Signature of "+ j +" transaction is invalid");
                    return false;
                }

                if(currentTransaction.getInputValue() != currentTransaction.getOutputValue()){
                    System.out.println("Input is not equals output in "+ j +" transaction");
                    return false;
                }

                //校验一笔交易中的每一个输入是否有效
                TransactionOutput tempOutput;
                for(TransactionInput input:currentTransaction.inputs){
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput == null){
                        System.out.println("output reference missed in "+ j +" transaction");
                        return false;
                    }

                    if(input.UTXO.value != tempOutput.value){
                        System.out.println("output value is invalid in "+ j +" transaction");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }
                //将每笔交易得到的输出加入到临时余额集合中。
                for(TransactionOutput output:currentTransaction.outputs){
                    tempUTXOs.put(output.id,output);
                }

                //校验当前交易的输出是否有效
                if(!Objects.equals(currentTransaction.outputs.get(0).recipient,currentTransaction.recipient)){
                    System.out.println("output for recipient is not invalid "+ j +" transaction");
                    return false;
                }
                //检验交易结余是否返回给sender
                if(!Objects.equals(currentTransaction.outputs.get(1).recipient,currentTransaction.sender)){
                    System.out.println("output for sender is not invalid "+ j +" transaction");
                    return false;
                }

            }
        }
        //校验通过
        System.out.println("validation finish!");
        return true;
    }

    private static void addBlock(Block block){
        block.mineBlock(difficulty);
        blockChain.add(block);
    }

}
