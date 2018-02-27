package com.simon.blockchain.util;

import com.simon.blockchain.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simon on 2018/2/20.
 */
public class TreeUtil {
    /**
     * 获取交易的merkle树的根节点，当一个区块中包含大量的transaction时，计算所有的hash值是不可取的
     * 所以使用merkel tree对全部hash值进行一个计算。
     * @param transactions
     * @return
     */
    public static String getMerkleRoot(List<Transaction> transactions){
        MerkleTree merkleTree = new MerkleTree(transactions);
        return merkleTree.buildTree();
    }
}
