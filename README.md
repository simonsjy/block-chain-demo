# block-chain-demo
以比特币白皮书为依据，简化模型，使用java实现的区块链模型。

## 1 面向人群
+ 对比特币的原理有基本的了解，熟悉java语言，希望在实践中进一步理解区块链技术
+ 比特币白皮书：https://bitcoin.org/bitcoin.pdf

## 2 实现功能
+ 密码学相关的工具类 -> CryptologyUtil
+ POW共识机制 -> Block
+ 交易过程 -> Transaction
+ 钱包基本功能 -> Wallet
+ 二叉树形式的简单Merkle Tree -> MerkleTree

## 3 demo使用方法
Main类中包含现有功能的测试方法，包括：
+ mineTest 用于测试挖矿功能（POW）
+ transactionTest 用于测试交易功能
+ genesisTest 完整的区块链测试

## 4 TODO
+ 网络通信功能
+ 缠绕链（侧链）功能
+ 钱包更丰富的功能

## 依赖jar包
+ bcprov-jdk15on-159.jar  https://downloads.bouncycastle.org/java/bcprov-jdk15on-159.jar
