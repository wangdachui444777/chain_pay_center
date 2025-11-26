# 区块链多平台充值系统说明文档

## 一、项目概述

本项目是一个**多平台区块链充值系统**，支持多个第三方平台接入（如合作网站或应用），每个平台分配独立的密钥与地址池。  
主要功能包括：
- 动态分配充值地址（从地址池中取出）  
- 多链监听（当前支持 TRX、ETH，可扩展至 BSC、BTC 等）  
- 自动验证平台签名  
- 交易监听与充值归集  
- Redis 监听缓存与性能优化

项目基于 **RuoYi 框架（Spring Boot + MyBatis + Redis + Shiro）** 开发。

---

## 二、项目模块结构

| 模块 | 描述 |
|------|------|
| `bc_platforms` | 平台管理（平台ID、密钥、状态等） |
| `bc_address_pool` | 预生成地址临时表（待分配） |
| `bc_user_addresses` | 用户已分配地址表（正式表） |
| `bc_address_balances` | 地址余额表（币种级别）） |
| `bc_transactions` | 区块链充值记录表（监听后入账） |
| `bc_collection_config` | 平台归集配置表 |
| `bc_chain_listener` | 区块监听服务（定时任务或守护进程） |

---

## 三、主要数据表设计

### 1. 平台表 `bc_platforms`

```sql
CREATE TABLE `bc_platforms` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '平台ID',
                               `platform_name` varchar(100) NOT NULL COMMENT '平台名称',
                               `secret_key` varchar(255) NOT NULL COMMENT '平台签名密钥 SecretKey',
                               `api_key` varchar(100) DEFAULT NULL COMMENT '平台访问Key',
                               `callback_url` varchar(255) DEFAULT NULL COMMENT '充值回调URL',
                               `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1禁止）',
                               `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                               `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                               `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6002 DEFAULT CHARSET=utf8mb4 COMMENT='下游平台';
```
---

### 2. 地址池表 `bc_address_pool`

用于存储预生成的可用区块链地址（待分配）。

```sql
CREATE TABLE `bc_address_pool` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号ID',
  `chain_type` varchar(10) NOT NULL COMMENT '区块链类型（TRX，ETH 等）',
  `address` varchar(128) NOT NULL COMMENT '区块链地址',
  `private_key` varchar(255) NOT NULL COMMENT '私钥（由系统生成）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预生成地址临时表';
```

- 地址总量建议控制在每链 500～1000 条
- 取用时立即删除，防止重复分配
- 删除同时写入 Redis 缓存（监听用）

---

### 3. 用户地址表 `bc_user_addresses`

正式绑定用户地址的表。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | bigint | 主键ID |
| `platform_id` | bigint | 平台ID |
| `chain_type` | varchar(10) | 链类型（TRX、ETH） |
| `address` | varchar(128) | 用户充值地址 |
| `private_key` | varchar(255) | 加密后的私钥 |
| `create_time` | datetime | 生成时间 |

> 同时会在 Redis 中写入监听信息：
> - key: `{chainType}:{address}`
> - value: `userAddress.id`（仅记录 ID，节省内存）

---

## 四、接口逻辑流程

### `/api/user/getAddress`

用于第三方平台获取一个可用充值地址。

#### 请求参数：

| 参数 | 类型 | 说明 |
|------|------|------|
| `platformId` | Long | 平台 ID |
| `timestamp` | Long | 10位时间戳 |
| `sign` | String | 平台签名（MD5 大写） |
| `chainType` | String | 可选，TRX / ETH / 空（为空时随机从全部链取） |

#### 签名规则：

```
sign = MD5(platformId + timestamp + secretKey).toUpperCase()
```

#### 校验逻辑：
1. 验证平台合法性、状态；
2. 验证签名（时间戳 ±3秒）；
3. 若 `chainType` 为空，系统自动从字典 `bc_chain_type` 获取全部链；
4. 从 `bc_address_pool` 中取 1 条对应链的地址；
5. 插入 `bc_user_addresses`；
6. 同时写入 Redis 监听池；
7. 删除地址池对应记录；
8. 返回地址信息。

---

## 五、Redis 缓存结构设计

| Key 结构 | Value | 用途 |
|-----------|--------|------|
| `TRX:TPhHxxxxxx` | 用户地址ID | 区块监听时快速匹配充值地址 |
| `ETH:0x9dxxxxxx` | 用户地址ID | 同上 |
| `listener:latestBlock:TRX` | 区块高度 | 记录最后同步的区块 |

说明：
- 每个区块监听任务读取当前链的最新块；
- 匹配 `to_address` 是否在 Redis Key 中；
- 命中则记录交易入库；
- 同时触发归集或通知逻辑。

---

## 六、区块监听机制

### TRON 链监听
使用：
```
https://apilist.tronscanapi.com/api/transaction?sort=-timestamp&count=true&limit=50
```
- 获取最新 50 条交易；
- 解析 `toAddress`；
- 与 Redis 匹配；
- 命中则写入 `bc_transaction_log`；
- 监听支持多地址，无需单个轮询；
- 后续可扩展至归集模块。

> 优点：无须节点，全 API 化；  
> 缺点：API 频率受限，需轮询调度优化。

### ETH 链监听
使用 `Infura` / `Alchemy` WebSocket 监听，或扫描块区间：
```
/api?module=block&action=getblocknobytime&timestamp=xxx
```

---

## 七、注意事项与优化建议

1. **多平台支持**  
   - 每个平台独立 `secretKey`
   - 地址与交易隔离管理

2. **并发安全**  
   - 地址取出操作应加锁或使用 SQL 原子更新（防止重复分配）

3. **Redis 性能优化**  
   - 用 Hash 存储地址：`{chainType}:{address}` → `userAddressId`
   - 仅存 ID，减少内存占用

4. **归集逻辑（待实现）**  
   - 定时扫描有余额地址
   - 自动转至主钱包
   - 手续费由平台 A 地址提供（TRX）

5. **项目框架与依赖**  
   - Spring Boot（RuoYi 框架）
   - MyBatis-Plus
   - Redis
   - Jackson / Fastjson
   - Swagger + Knife4j 接口文档
   - lombok / hutool 工具包

---

## 八、后续工作（Claude 编码任务清单）

| 模块 | 目标 | 备注 |
|------|------|------|
| 地址接口 | 完善 `getAddress()` 控制器逻辑 | 签名验证 + Redis 写入 |
| 地址池服务 | 支持批量生成 / 导入地址 | 可用导入工具 |
| 区块监听器 | 实现 TRON / ETH 同步任务 | 记录、归集 |
| Redis 管理器 | 封装监听缓存 | 支持批量同步 |
| 平台安全 | 动态时间戳签名验证 | 过期拦截处理 |
| 前端管理页 | 地址池管理、平台管理 | RuoYi Vue 模块 |

---

## 九、扩展方向

- 支持 TRC20/ERC20 Token 监听  
- 统一归集服务（多币种）  
- 监控看板（实时充值统计）  
- Webhook 回调平台通知充值成功  

---

📘 **总结**
当前系统已完成：
- 数据结构与核心表设计；
- 地址获取接口原型；
- 平台签名验证逻辑；
- Redis 缓存结构；
- 监听逻辑方向；
- 多链支持框架。

接下来 Claude 需负责编码实现包括：
- 地址分配/删除逻辑；
- Redis 写入；
- 区块扫描与交易入账；
- 可选归集功能。
