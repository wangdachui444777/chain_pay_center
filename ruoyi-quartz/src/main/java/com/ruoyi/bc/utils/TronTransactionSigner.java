package com.ruoyi.bc.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.trident.core.key.KeyPair;
import org.tron.trident.crypto.Hash;
import org.tron.trident.utils.Numeric;

/**
 * TRON 交易签名工具类（使用 Trident 0.3.0）
 * 避免直接使用 Protocol Buffer，解决版本冲突问题
 *
 * @author ruoyi
 */
public class TronTransactionSigner {

    private static final Logger log = LoggerFactory.getLogger(TronTransactionSigner.class);


}