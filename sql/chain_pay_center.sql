/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50731
 Source Host           : localhost:3306
 Source Schema         : chain_pay_center

 Target Server Type    : MySQL
 Target Server Version : 50731
 File Encoding         : 65001

 Date: 08/01/2026 12:19:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bc_address_balances
-- ----------------------------
DROP TABLE IF EXISTS `bc_address_balances`;
CREATE TABLE `bc_address_balances` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `address_id` bigint(20) NOT NULL COMMENT '关联地址ID（bc_user_addresses.id）',
  `platform_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '平台ID',
  `chain_type` varchar(10) NOT NULL COMMENT '区块链类型（TRON=TRC，ETH=ERC 等）',
  `token_symbol` varchar(20) NOT NULL COMMENT '币种符号（TRX、USDT、USDC、ETH...）',
  `token_contract` varchar(128) DEFAULT NULL COMMENT '代币合约地址（主币为空）',
  `balance` decimal(36,18) DEFAULT '0.000000000000000000' COMMENT '币种余额',
  `balance_usdt_value` decimal(36,18) DEFAULT '0.000000000000000000' COMMENT '折算为USDT的价值',
  `total_balance` decimal(36,18) unsigned NOT NULL DEFAULT '0.000000000000000000' COMMENT '累计充值金额',
  `last_sync_time` datetime DEFAULT NULL COMMENT '最近同步时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '归集状态：0=待处理, 1=归集中 归集完改成0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_address_token` (`address_id`,`token_symbol`),
  KEY `uniq_platformId` (`platform_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地址余额表（币种级别）';

-- ----------------------------
-- Table structure for bc_address_pool
-- ----------------------------
DROP TABLE IF EXISTS `bc_address_pool`;
CREATE TABLE `bc_address_pool` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号ID',
  `chain_type` varchar(10) NOT NULL COMMENT '区块链类型（TRX，ETH 等）',
  `address` varchar(128) NOT NULL COMMENT '区块链地址',
  `private_key` varchar(255) NOT NULL COMMENT '私钥（由系统生成）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COMMENT='预生成地址临时表';

-- ----------------------------
-- Records of bc_address_pool
-- ----------------------------
BEGIN;
INSERT INTO `bc_address_pool` VALUES (7, 'TRX', 'TPVtM25zbWGRvKkBykUfbTpHxo9G3jJzL1', '7JuGKWRGtuEC25IQMIQhyLHaN91/nM2mWvJgQ+SqfO7Iwst4L8Z42de8/oFHmDII1yd11KylBHxf0LdHJqHonaGAd6p22kva');
INSERT INTO `bc_address_pool` VALUES (8, 'TRX', 'TW7DmKCTDkhvp73yfJAVdj2zbZWnw8ieJf', 'kUMiBBGxboJrM2ZgFESO9HZx90i0H6RnISuNqQd0RfAQ/QIPWkgJaTUf5wG7SKI6XBJCaW7u67DkvH0M1Gmoksa9m/58FOg6');
INSERT INTO `bc_address_pool` VALUES (9, 'TRX', 'TSshjq877EdEotTB3iQK5pvf35bsZfmmTm', 'l8MFbHymCuXBTQQRWGlZgmWT/FNTt8Zz48CzlV0NOFMmwjiQsZIdxFpCdJu6zz9b0TCEfbU7JPG6tDKw4nKc475l6XYKyCo5');
INSERT INTO `bc_address_pool` VALUES (10, 'TRX', 'TPVMFKwEbotM7uxTbLHeMNaYnpS3HZQqDj', 'h0yHq+qqSsKnJJtSkLugMl9TaMjFteLA1HKIB46BfCtD2u07orHGrQw0oISF/lT90G2Qzn2ihke7B2PmfryF3IY83OqB93gv');
INSERT INTO `bc_address_pool` VALUES (11, 'TRX', 'TXTAiNngcyXAThH4jPcQX9PXQmSDbeVbC8', 'yriGWluLsTSJiQjOuD0BEscfHiEt3o8CQ08MOciwCukAFKbWCcZrdcQjujI4WTDt3Kz42xrUFvfP5LLA28BeRMs439n6XC9g');
INSERT INTO `bc_address_pool` VALUES (12, 'TRX', 'TGmVoMEu7ANMmdyihy67sArNMTNUd2JVQG', 'p8o/Sbn0jkwW/yTSZUVAWFnSb01FMl2hkNWILBtRKe6khrdC2N4O2+Qu3JcGsvrjmFasg3gleDMwKJoSI/k2ChE0xZEyFxJP');
INSERT INTO `bc_address_pool` VALUES (13, 'TRX', 'TWoPPtDTQEXnCNghvHpLiKDoBYi4CecATA', '9OCE0MF7rZwiUyLWnNzes4HvoRsgjwBePL0XitA669DahIf6psWCii9jWH5hx1hiaTfgCmg2TiOfD6y011Ycav4dv6arxLAK');
INSERT INTO `bc_address_pool` VALUES (14, 'TRX', 'TWCfYiYywMwiD2tX2VkZ6jhtgJtypPsdaw', 'Tm9qvBYxpv5TPTQa9gk+l64SMS74eLqGToYwUDtIEwZGQus/M6yq7aKYN3VaOSKfl5aLM/7The9s7c5IJUJ/hoNrMF+NRMhz');
INSERT INTO `bc_address_pool` VALUES (15, 'TRX', 'TD25xhUzEheRV5yNykb7wNzKkwDVzX4iEc', 'm4GOqkuPWTGpfhjxxqaeybMXTVnx2zO2C2Nlci3RETouqA1GAXY3Q/f+k8sn7N5Gykmu6B/5OfZa+dAReTcjHts5h8gV1BY+');
INSERT INTO `bc_address_pool` VALUES (16, 'TRX', 'TRZy24LrsJTNgKKrs9xseeoZ9kYr2fV4n3', 'Tf4PeTFMWTT3Lmmmfyah/WMIhou1MIOQXvnzqk+RAhB8T7yoGGrYMRkM5mhEkA/mMfhcQZChte932X1/LdqeaJ6hJtPISanT');
INSERT INTO `bc_address_pool` VALUES (17, 'TRX', 'TWBriwmfR9znQtxrHxL2Av4ybjkwuthRmT', 'BZwiim9S4nOKbbIcLj8EA8TDXPRNrnGSLsbJ3FSHfk28muOYZaMVgp9VqSGBA9Xsuzw6vgTHkgZA1JBTUpfVo8lC4+xtkUSA');
INSERT INTO `bc_address_pool` VALUES (18, 'TRX', 'TF332va3tAGXfWKZNhHzeLgbN1WCYc2yX8', 'QTn1hnevREXlZnNKeAdC6i3fWvPSud8zSoHK1URorNCgnHOQqaQ1JvMi7w3chPTnbMmF/F7tdSleT1fclGNPzA9MMkoUQS2z');
INSERT INTO `bc_address_pool` VALUES (19, 'TRX', 'TEDnUf3j3gXUPn4QJyznVQjJh1NVteanod', 'sZhRtw/Zx1FUpa57vSwh+qSrWhu9WLleasjURnvfgU+tb2sBo9wHy9ns/+18XF12neV9oYTNTkIe0abvRUpZ0Ljj7HWK91N5');
INSERT INTO `bc_address_pool` VALUES (20, 'TRX', 'TNX4ytm15EnBimYTVfvpzezhjZEoAhhYqC', 'KSJybzdTNFqkS78pgXH2A2VdSin37gmSh2sYaDMoqFTkXCAZKX/86dL3Ab/0vuo/vr4Fr+ZcMGaSm60fj5vG8ADvEY1oFthR');
INSERT INTO `bc_address_pool` VALUES (21, 'TRX', 'TC4mbshqbZbghcmsodhxVxDs6zQZPKwtMB', 'qN7kuRFPVjXBMV5LNuHn4lUeuj5RBUTK0MRYHIhNY9+uzazvZXXsb7/BZns8JWE5k2tm+8xKF2j+hDcnOmPjsLFPwz2GukzN');
INSERT INTO `bc_address_pool` VALUES (22, 'TRX', 'TUNbugD5xefX7SrmbRZASGGAo1T7Yxtc7M', 'LqsrHUf7ymqCDFFt0blql6cnljSCLYV99WV1KJa+jUC8rRmZdIRh72wteEyWXm7UyjuR8G6kubJmJ4QTHt7SJJpzU974ziqV');
INSERT INTO `bc_address_pool` VALUES (23, 'TRX', 'TNnPnEJMaVhdMMMnBnZyHh6rRTN8ReFLLd', 'hH/NIwFUGH4u7QOyXzt2oTGS/JsestvJu1j7TVijqeaRcgWDjH37wikI7UiTia9a5wggdpw6PgwSShUThMO1VWJOyD2jpvaa');
INSERT INTO `bc_address_pool` VALUES (24, 'TRX', 'TCJmreVGqqvk7rtxuJWrAYP1i3m9nAiKRz', 'xU6SIcJ49nQMXXkdieu7eIj1bYK2Yr9xrGcBq3oVW89URmczc7gs3mzMlja5gmEl74PPnWy6wWRzt6HEt4T+cj22mHl9a3+e');
INSERT INTO `bc_address_pool` VALUES (25, 'TRX', 'TCwrzwRLN95pm9xT2EFmbf5V8LAgbcTFtV', 'fUjNiuh22O5ZdvPwT65K6gfGLi8ue7BdBZygbsyHL/dEuBZslgnSV5B4cnkPeEBTlJ36SsrR8iuefjzEvbs/CsoPTuKE8tmm');
INSERT INTO `bc_address_pool` VALUES (26, 'TRX', 'TJbEDpWYSAGxapUAVrjQUszHeEtz8NRVve', 'uZPakrEZjXFI1gmSMQYxNNQMaDwkUhP7LvqB2UOcelYkoszGodgfPFc4nVK4MulnAquPgdXWNQsICu9o9js38kaskDpS9vag');
INSERT INTO `bc_address_pool` VALUES (27, 'TRX', 'TDUPWFwmbsfB3scpaXxudbd8hs9rFJuvjs', '3RKjt+p1ENC+FbV6hI+VInlnhiYFXei5NbJJD8ejBddOXewuvynLpxYU8MSJLkgsrt5hFLocQYkNE7PU0PxP2QbyLIBn+EaI');
INSERT INTO `bc_address_pool` VALUES (28, 'TRX', 'TRfWWrAxUc2vZBVrJps5Sfggnq9hfvXs79', 'eYozDSRmEY8oxlvLota922Cl3roZuE2v6C03P/RKSV6n4RfiygxkSxfKD9JBzET9+pwRA2tQjlpsKJhbKAs2ebxU1lqzu8I0');
INSERT INTO `bc_address_pool` VALUES (29, 'TRX', 'TQSCzz9WBqmruEYuLwBEs84YXCWLfbTRA7', '/fMrulFPe3If1R0e09oUOjwRMMjhqSvtmQHCjRKWMwk5ouQ8quh8jo2wTmjsDy2ak0izBkKDISvcVbK16Dqht49QxjLUtSPE');
INSERT INTO `bc_address_pool` VALUES (30, 'TRX', 'TX7Vbpr6G8xM8JcgYwJ9d9ic7TWoWdGZJA', 'x9y1IId8+34URV4ZisuKkMSlgfUr36SdXTnG+7+7inbIwhl+DjiKyTOoL7FfPQigTPUvikFI4yaH/oRILZf/KG/VBmkmPAnU');
INSERT INTO `bc_address_pool` VALUES (37, 'ETH', '0x87be80fa6b403f24c977a888d90b784ffbcc611b', 'okIbxJeCJAm7w3YdBDE+/yo21qGHenQeG4VG1fx7jxObK0a5ZPVmXwmaOp0eJciucbO6g07Vz0ARTfx9PkDEjqP7jmwxl3mm');
INSERT INTO `bc_address_pool` VALUES (38, 'ETH', '0x17b690173a05ef8e56b1349cfa78b105781934fc', '1+PpI04lX2cgTOlo/6j1v7Gnsl01ErzDtaME634n8IT0R4XvGu0xacSMt4JGdYUuRZ94YC5xuLU6Uotr9m2t8C18K/e+Gnyq');
INSERT INTO `bc_address_pool` VALUES (39, 'ETH', '0xbbdfbb500e6e58021b0e3cd42b01889845a2713d', 'lit7+rEe5YYY/y2DR1QbKDgENWK8qW0Rws+WwOGz4T8Rk59DNUp1xx0V/KpMbjFYx1dMEf1qngWy2ZP8igx++IqriQjVYdZm');
INSERT INTO `bc_address_pool` VALUES (40, 'ETH', '0xb3188bc6a7dedcf02ea0128622dd058b76924464', 'i6T1ENaRA9qPa8ygsT/Pa3826C3GfrTBpRFaaw/D/w19GTYrq4U6XLfar61dAYgjNJE+JbokVswM84iPQuwMdrcbG0luZPli');
INSERT INTO `bc_address_pool` VALUES (41, 'ETH', '0x78fc0c74e7ad71a18fa81ac6901515553594aa46', 'aWMgVcQ3zQAElhYGpb26e5GyQItEsTxAQc+on6oJOD10iZDahejCH6WsgGxY9zE8pf+ekWCsnmwMrrzHSD3XeeGInqiJ250Y');
INSERT INTO `bc_address_pool` VALUES (42, 'ETH', '0x0ddb03b0685c3f2be2301561955b7b812e8f4053', 'JlZtL+Nhk7wVQGpJEt1sEnbea47vIPow0WKtkfbM+5PuZDc2gEtxDbXOPIORq/A91+EyKptdEmcBUWbIAbVkZ5Z8TSGlyXg3');
INSERT INTO `bc_address_pool` VALUES (43, 'ETH', '0x88ad80df6447d1f2a8e647152c93bcb3a615f1e6', 'ytQxDdYVC9SHLRWd5BWnfJjqAFcOgmEYLyOX0mDIh39tYduSqmRSPAtK+vVgTkUARzTR6XD4f6if3W3q5tXdcWSEA1TL2UE6');
INSERT INTO `bc_address_pool` VALUES (44, 'ETH', '0x3a92d3105c4598dd05a2019b7990017c7c05a8ab', 'oRBWSDtIcXymqlpTSCW4vd1bNyVrPFvq8eUevVZ8XhXEZ0j07MuQORaqoqz+l+2OlvFTV10fH0ZVX0BPU3truh0Xbtps50uA');
INSERT INTO `bc_address_pool` VALUES (45, 'ETH', '0x4406f647bb083adb58ff93e88c33b49f309078e8', 'Fk1IJDHX9yNhkgnO1b++3rg1dfJSNwlH3PMSA9Wz31Aqrwk3cx9oKnLp1GoNL92NcHw6ws3BedPZ7CWg0cg0YaGKO2uFX75D');
INSERT INTO `bc_address_pool` VALUES (46, 'ETH', '0x5fb02eccbf4291346adc91ed2bfcd4cc46fccd19', 'l/aEwIMjfK/wfrqWdUyswRlhpJQlud3aZPak3qX99FLxYQqrbITKNDwOBxr06neYuiRI7JOsOlYYDGQGNUMbca7XHhNkTB5D');
INSERT INTO `bc_address_pool` VALUES (47, 'ETH', '0x9f2b0ce4dd235432fdc15a878505864bfbef2eb0', 'ZlNpo4vM3cdc8UoE7ezswyNw1UPJgVgPbdWpXNUaYurjKXqaHyYRpuXLzcl3EVy+5ARZExFbJg2bbpOEIi4WmIDM064v+aGv');
INSERT INTO `bc_address_pool` VALUES (48, 'ETH', '0x364866ffb8fe17cc6e68cf925a795acfc1b6ec3d', '/Qg4JmpwwSmvUHG9Ld7rQ/0pmZMTeTBN2gSbWzi9GJVCI5yQlpA5n4t1PNnK+NAZdktluVlXuEMJ/aNzxVLfemeWGl22u1u/');
INSERT INTO `bc_address_pool` VALUES (49, 'ETH', '0xb224ef81e34ee5903ac5359ddafe16ad34d168a4', 'MMMUZaTTgRg7z3v+OlkGde8D3dGFDTknaqih4BxQ7Z5EFVHDtEK78LhmIQNpA4s8QGtYuVBm4R2QeY3EgSOA9t7k0fj4twsl');
INSERT INTO `bc_address_pool` VALUES (50, 'ETH', '0x8b089d60997443a50b1903abb2f8817a99783e3f', 'BgwhoBQP2HN7COXAuyq6zAehCPk4bgdDsUec+NEk8jsiCcsdYXe2uGmkhR2ePTtd768nXA1NgXzyr1EyTxz5U+yTkL/7XnN3');
INSERT INTO `bc_address_pool` VALUES (51, 'ETH', '0xcce88607fd215db4d64391be796953c5af9b0fbe', 'qdS/xKXtu76sl6z0S3/26GHDSfIu7Z9MEE/EWYEq9xv4w6KW8mD5a6wyHQmaAgphvC3xo93kkPtu1/EICId14ay+hTg6X1k7');
INSERT INTO `bc_address_pool` VALUES (52, 'ETH', '0x373b80f6c0db318b7a257bab4735e2b98d7e61e4', 'YrqFS7m1eTT8TPWxSk/0mQFz5UlnnEUgbpclgoJymsEWkysCXVkWyn+B56CaSdRZUi+3rSj1DYeaGZ4CMDMOs7Lj0GP+uEQk');
INSERT INTO `bc_address_pool` VALUES (53, 'ETH', '0x817448513a6e15a141f67a5030ba461367ef168a', 'rtSyr7deyZBJPTwbA9bZX3L/aeJVJwHst+tNQJ6sMn5Ay4jPozEh9PKbUW/d4jFY4nfx7BQw+PvSid8dBF3ErqjlDRxIHOvh');
INSERT INTO `bc_address_pool` VALUES (54, 'ETH', '0xf730d4ab9ac4e17d270936dc6db0cd5b8dc5f30c', 'SKL4BezjDPDFd2oFHDw9Bosl1XLh3bIO3d/j7utOayyVVvN2E33fgupsu8bN9pH/+GtVBTSeGZd/bbnMih6pIyg9/4CO5cQ5');
INSERT INTO `bc_address_pool` VALUES (55, 'ETH', '0x33bdc484b19477d0e879ee16024b3df6164b358f', 'zwV6amecUZ1vfGQsEhNxSdj1EHUyZTJYTrfEeI4VTfJ6rfR0amY9E+IlO1ZkX2fFQQZ3wnnlHQIwdDYKjYAFVsQXB3fXX9Fc');
INSERT INTO `bc_address_pool` VALUES (56, 'ETH', '0xde4bc4e0bf923a06248446681ca3b5204c286ccd', 'snWv5Zo5QsRQrNpu7CZdHCVi54H8lIvRjb8+IsjWojSfEuAU4vpp/mov1SdN3Ne3DXciL30EXumnE+frnlwdNVW7kJQqhT9r');
INSERT INTO `bc_address_pool` VALUES (57, 'ETH', '0x9569a48b730ab73a014b6030649a5603ea583899', 'aSKJBhCr9GPHzpZFtjOHupRsuxB3PfDe0OTpPXEIv3/15Y8ilWg4UHkKL7C7JHO1wYbkgCRHq/V6r2T+hbGB95IQeNTJKUty');
INSERT INTO `bc_address_pool` VALUES (58, 'ETH', '0x456a79cc09da96bf4268166ff0ec2ca822e50c6a', 'kzYsTZeR6sxuLPhojfE52B92WrBpnUhTott0XtYV+lxcG95HScpw1Nuw4seorllqYW0lCM+ErV25KjK9/PP6doqbksGY2YFF');
INSERT INTO `bc_address_pool` VALUES (59, 'ETH', '0x43d7551fa7868d2f58d484605350361502089d74', 'PAic/RoEq5F+zYdDLHVOA9KnGqm+GHvkfV49Nz1iptoFhH5jUWEoYYMUnZ893tl5GjxDQw5D9kGK8Cmi+1qFTXRJVqgqGShg');
INSERT INTO `bc_address_pool` VALUES (60, 'ETH', '0xae77be1960bc2a7d8be790a6dab171b7430ff123', 'Rs2FVEnogLSnUe786AWDqZZJ3C0ackqfHtSBQ1PczMrIEWd68tucwMkkLA3It2Myj0u3AYW5bLG7x2iy365Ln1val9fEO5+X');
COMMIT;

-- ----------------------------
-- Table structure for bc_api_keys
-- ----------------------------
DROP TABLE IF EXISTS `bc_api_keys`;
CREATE TABLE `bc_api_keys` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `chain_type` varchar(10) NOT NULL COMMENT '区块链类型（TRX、ETH、BSC 等）',
  `api_provider` varchar(50) NOT NULL COMMENT 'API提供商（TronGrid、Etherscan、Infura等）',
  `api_key` varchar(255) NOT NULL COMMENT 'API密钥',
  `api_url` varchar(255) DEFAULT NULL COMMENT 'API基础URL',
  `daily_limit` int(11) NOT NULL DEFAULT '1000' COMMENT '每日请求限制',
  `used_count` int(11) NOT NULL DEFAULT '0' COMMENT '今日已使用次数',
  `priority` int(11) DEFAULT '1' COMMENT '优先级（1-10，数字越小优先级越高）',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1禁用 2已达上限）',
  `last_used_time` datetime DEFAULT NULL COMMENT '最后使用时间',
  `reset_time` datetime DEFAULT NULL COMMENT '计数重置时间（每日凌晨）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_chain_type` (`chain_type`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='区块链API密钥管理表';

-- ----------------------------
-- Records of bc_api_keys
-- ----------------------------
BEGIN;
INSERT INTO `bc_api_keys` VALUES (1, 'TRX', 'TronGrid', '54b4af42-51ec-4057-aa43-61b176688e5b', 'https://api.trongrid.io', 80000, 0, 1, '0', '2025-11-13 00:00:00', '2025-11-13 00:00:00', NULL, '2025-11-23 18:13:22', 'TronGrid 主Key');
INSERT INTO `bc_api_keys` VALUES (2, 'TRX', 'TronGrid', 'c5c4b967-e506-4178-92e0-d412f0e4e3b0', 'https://api.trongrid.io', 80000, 0, 2, '0', NULL, NULL, NULL, '2025-11-23 18:13:45', 'TronGrid 备用Key');
INSERT INTO `bc_api_keys` VALUES (4, 'ETH', 'Infura', '9feff836ecef4500bc1d6531ff4158d6', 'https://mainnet.infura.io/v3/', 80000, 0, 1, '0', '2025-11-05 00:00:00', '2025-11-10 00:00:00', NULL, '2025-11-23 18:15:07', 'Infura Key');
INSERT INTO `bc_api_keys` VALUES (5, 'ETH', 'Infura', 'c02032d9c5df4fc8bfa0230aea9cae8f', 'https://mainnet.infura.io/v3/', 80000, 0, 2, '0', NULL, NULL, '2025-11-05 19:49:55', '2025-11-23 18:15:30', 'https://developer.metamask.io/');
INSERT INTO `bc_api_keys` VALUES (6, 'TRX', 'TronGrid', '0d02c838-1ba2-4ad7-821c-cc83ab35b59b', 'https://api.trongrid.io', 80000, 0, 3, '0', NULL, NULL, '2025-11-23 18:14:18', NULL, '');
INSERT INTO `bc_api_keys` VALUES (7, 'TRX', 'TronGrid', 'c8619c13-ee2f-4390-a8a0-62181ce6d359', 'https://api.trongrid.io', 80000, 0, 4, '0', NULL, NULL, '2025-11-23 18:14:43', NULL, '');
INSERT INTO `bc_api_keys` VALUES (8, 'ETH', 'Infura', '24b1ee50c37b4c0ab2de348a6b2853e6', 'https://mainnet.infura.io/v3/', 80000, 0, 3, '0', NULL, NULL, '2025-11-23 18:15:51', NULL, '');
INSERT INTO `bc_api_keys` VALUES (9, 'ETH', 'Infura', '6b2299147210488f84949af1d9c1ebf4', 'https://mainnet.infura.io/v3/', 80000, 0, 4, '0', NULL, NULL, '2025-11-23 18:16:20', NULL, '');
COMMIT;

-- ----------------------------
-- Table structure for bc_collection_config
-- ----------------------------
DROP TABLE IF EXISTS `bc_collection_config`;
CREATE TABLE `bc_collection_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `platform_id` bigint(20) NOT NULL COMMENT '关联平台ID（bc_platforms.id）',
  `chain_type` varchar(20) NOT NULL COMMENT '区块链类型（TRON、ETH、BSC等）',
  `target_address` varchar(128) NOT NULL COMMENT '归集目标地址（冷钱包地址）',
  `min_collect_amount` decimal(36,18) DEFAULT '0.000000000000000000' COMMENT '最小归集阈值（达到该金额才归集）',
  `enable_auto_collect` tinyint(1) DEFAULT '1' COMMENT '是否开启自动归集（1=是，0=否）',
  `token_scope` char(1) NOT NULL DEFAULT '1' COMMENT '归集范围：1=全部代币，2=主币，3=仅USDT',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_platform_chain` (`platform_id`,`chain_type`),
  KEY `idx_platform_id` (`platform_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='平台归集配置表';

-- ----------------------------
-- Records of bc_collection_config
-- ----------------------------
BEGIN;
INSERT INTO `bc_collection_config` VALUES (1, 6001, 'TRX', 'TQYTHCTbepdTiMzV5h41eJEYrS7orhm7EX', 10.000000000000000000, 1, '1', '', '', '2025-11-09 14:39:36', '2025-11-10 22:45:46');
INSERT INTO `bc_collection_config` VALUES (2, 6001, 'ETH', '0xdac17f958d2ee523a2206206994597c13d831ec6', 10.000000000000000000, 1, '1', '', '', '2025-11-10 22:53:50', NULL);
COMMIT;

-- ----------------------------
-- Table structure for bc_consolidation_detail
-- ----------------------------
DROP TABLE IF EXISTS `bc_consolidation_detail`;
CREATE TABLE `bc_consolidation_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_no` varchar(64) NOT NULL COMMENT '批次号',
  `platform_id` bigint(20) NOT NULL COMMENT '所属平台ID',
  `address_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '关联地址ID（bc_user_addresses.id）',
  `address_balance_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '关联地址ID（bc_user_address_balance.id）',
  `chain_type` varchar(20) NOT NULL COMMENT '链类型',
  `from_address` varchar(128) NOT NULL COMMENT '源地址（子钱包）',
  `to_address` varchar(128) NOT NULL COMMENT '目标地址（主钱包）',
  `token_symbol` varchar(20) DEFAULT NULL COMMENT '代币符号',
  `token_contract` varchar(128) DEFAULT NULL COMMENT '代币合约地址',
  `amount` decimal(36,18) DEFAULT '0.000000000000000000' COMMENT '归集金额',
  `gas_fee_sent` decimal(36,18) DEFAULT '0.000000000000000000' COMMENT '充值的Gas费用',
  `gas_fee_used` decimal(36,18) DEFAULT '0.000000000000000000' COMMENT '实际使用的Gas费用',
  `gas_tx_hash` varchar(128) DEFAULT NULL COMMENT '充Gas交易哈希',
  `gas_block_number` bigint(20) NOT NULL DEFAULT '0' COMMENT '手续费区块号',
  `gas_tx_status` char(1) DEFAULT '0' COMMENT 'Gas交易状态：0=未发送, 1=已发送, 2=已确认, 3=失败',
  `gas_send_time` int(11) DEFAULT NULL COMMENT '发送手续费时间',
  `tx_hash` varchar(128) DEFAULT NULL COMMENT '交易哈希',
  `tx_status` char(1) DEFAULT '0' COMMENT '交易状态：0=未发送, 1=已发送, 2=已确认, 3=失败',
  `block_number` bigint(20) NOT NULL DEFAULT '0' COMMENT '交易区块号',
  `confirm_count` int(11) DEFAULT '0' COMMENT '确认数',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '重试次数',
  `error_msg` varchar(512) DEFAULT NULL COMMENT '错误信息',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '整体状态：0=待处理, 1=归集中, 2=已归集,3=失败',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_platform_id` (`platform_id`),
  KEY `idx_chain_type` (`chain_type`),
  KEY `idx_status` (`status`),
  KEY `idx_seq_no` (`batch_no`) USING BTREE,
  KEY `idx_tx_status` (`gas_tx_status`,`tx_status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COMMENT='归集详情表';

-- ----------------------------
-- Table structure for bc_fee_source_addresses
-- ----------------------------
DROP TABLE IF EXISTS `bc_fee_source_addresses`;
CREATE TABLE `bc_fee_source_addresses` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `platform_id` bigint(20) NOT NULL COMMENT '关联平台ID',
  `chain_type` varchar(20) NOT NULL COMMENT '区块链类型（TRON、ETH、BSC等）',
  `fee_address` varchar(128) NOT NULL COMMENT '手续费提供地址（用于gas）',
  `private_key_encrypted` text NOT NULL COMMENT '加密后的私钥（仅服务器内部使用）',
  `last_used_time` datetime DEFAULT NULL COMMENT '上次使用时间',
  `balance` decimal(36,18) DEFAULT '0.000000000000000000' COMMENT '当前链主币余额（定时更新）',
  `status` char(1) DEFAULT '0' COMMENT '状态(0=正常 1=禁止)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_platform_chain` (`platform_id`,`chain_type`),
  KEY `idx_platform_id` (`platform_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='手续费来源地址表';

-- ----------------------------
-- Records of bc_fee_source_addresses
-- ----------------------------
BEGIN;
INSERT INTO `bc_fee_source_addresses` VALUES (1, 6001, 'TRX', 'TP6CqFPLbTdAuDxdhzoiFuBfM5WpMayFFM', 'rwpWrIGXObKjx+yXyDX2R4H02STM1kkcSL5NOiPvNBK4Zr3HYCUQJK6c/Wd4dlMZDBwe7gaLLX5cqHrbike/R4ILcVXD/Or8', NULL, 100.000000000000000000, '0', 'admin', '', '2025-11-10 23:37:32', NULL);
COMMIT;

-- ----------------------------
-- Table structure for bc_platforms
-- ----------------------------
DROP TABLE IF EXISTS `bc_platforms`;
CREATE TABLE `bc_platforms` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '平台ID',
  `platform_name` varchar(100) NOT NULL COMMENT '平台名称',
  `secret_key` varchar(255) NOT NULL COMMENT '平台签名密钥 SecretKey',
  `api_key` varchar(100) DEFAULT NULL COMMENT '平台访问Key',
  `callback_url` varchar(255) DEFAULT NULL COMMENT '充值回调URL',
  `withdraw_url` varchar(255) DEFAULT NULL COMMENT '提现回调URL',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1禁止）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6003 DEFAULT CHARSET=utf8mb4 COMMENT='下游平台';

-- ----------------------------
-- Records of bc_platforms
-- ----------------------------
BEGIN;
INSERT INTO `bc_platforms` VALUES (6001, '测试平台', 'F33ABehPffYtPlOK79c167462fdd7f37', 'PLAT-2XFLLKJX', 'http://localhost:890/api/test/callback', 'http://localhost:890/api/test/callback', '0', '', 'admin', '2025-10-27 16:15:44', '2025-11-13 16:16:35');
INSERT INTO `bc_platforms` VALUES (6002, '测试平台2', '8at93-s5-jSkEYiF03cef9367b2168ee', 'PLAT-JFZI2ZRV', '', NULL, '0', 'admin', '', '2025-11-09 14:36:52', NULL);
COMMIT;

-- ----------------------------
-- Table structure for bc_token_prices
-- ----------------------------
DROP TABLE IF EXISTS `bc_token_prices`;
CREATE TABLE `bc_token_prices` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `chain_type` varchar(10) NOT NULL COMMENT '区块链类型（TRX、ETH、BSC 等）',
  `token_symbol` varchar(20) NOT NULL COMMENT '币种符号（TRX、USDT、USDC、ETH 等）',
  `token_contract` varchar(128) DEFAULT NULL COMMENT '代币合约地址（主币为空）',
  `decimals` int(11) NOT NULL DEFAULT '18' COMMENT '代币精度',
  `price_in_usdt` decimal(36,18) DEFAULT '0.000000000000000000' COMMENT '1个该币折合多少USDT',
  `is_main_coin` tinyint(4) DEFAULT '0' COMMENT '是否主币（0否 1是）',
  `enabled` tinyint(4) DEFAULT '1' COMMENT '是否启用监听（0禁用 1启用）',
  `token_type` varchar(10) DEFAULT NULL COMMENT '代币类型（TRX主币/TRC20/ERC20等）',
  `last_update` datetime DEFAULT NULL COMMENT '最后更新时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_chain_token` (`chain_type`,`token_symbol`),
  KEY `idx_token_symbol` (`token_symbol`),
  KEY `idx_enabled` (`enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='币种配置与USDT汇率表';

-- ----------------------------
-- Records of bc_token_prices
-- ----------------------------
BEGIN;
INSERT INTO `bc_token_prices` VALUES (1, 'TRX', 'TRX', '', 6, 0.286500000000000000, 1, 1, 'TRX', '2025-11-05 20:15:51', NULL, '2025-10-29 17:33:00', 'TRON 主币');
INSERT INTO `bc_token_prices` VALUES (2, 'TRX', 'USDT', 'TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t', 6, 1.000000000000000000, 0, 1, 'TRC20', NULL, NULL, '2025-10-29 17:32:54', 'USDT-TRC20');
INSERT INTO `bc_token_prices` VALUES (3, 'TRX', 'USDC', 'TEkxiTehnzSmSe2XqrBj4w32RUN966rdz8', 6, 1.000000000000000000, 0, 1, 'TRC20', NULL, NULL, NULL, 'USDC-TRC20');
INSERT INTO `bc_token_prices` VALUES (4, 'ETH', 'ETH', '', 18, 3316.260000000000000000, 1, 1, 'ETH', '2025-11-05 20:15:51', NULL, '2025-10-29 17:33:06', 'Ethereum 主币');
INSERT INTO `bc_token_prices` VALUES (5, 'ETH', 'USDT', '0xdac17f958d2ee523a2206206994597c13d831ec7', 6, 1.000000000000000000, 0, 1, 'ERC20', NULL, NULL, NULL, 'USDT-ERC20');
INSERT INTO `bc_token_prices` VALUES (7, 'ETH', 'USDC', '0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48', 6, 1.000000000000000000, 0, 1, 'ERC20', NULL, '2025-11-05 17:32:18', '2025-11-05 17:33:41', 'USD Coin');
COMMIT;

-- ----------------------------
-- Table structure for bc_transactions
-- ----------------------------
DROP TABLE IF EXISTS `bc_transactions`;
CREATE TABLE `bc_transactions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '交易记录ID',
  `address_id` bigint(20) NOT NULL COMMENT '关联地址ID（bc_user_addresses.id）',
  `platform_id` bigint(20) NOT NULL COMMENT '所属平台ID',
  `tx_hash` varchar(128) NOT NULL COMMENT '区块链交易哈希',
  `chain_type` varchar(10) NOT NULL COMMENT '区块链类型（TRON=TRC，ETH=ERC 等）',
  `token_symbol` varchar(20) NOT NULL COMMENT '币种符号（TRX、USDT、USDC 等）',
  `token_contract` varchar(128) DEFAULT NULL COMMENT '代币合约地址（主币为空）',
  `amount` decimal(36,18) unsigned NOT NULL DEFAULT '0.000000000000000000' COMMENT '交易金额',
  `direction` char(1) NOT NULL DEFAULT '1' COMMENT '交易方向：1=充值，2=归集',
  `from_address` varchar(128) DEFAULT NULL COMMENT '发送方地址',
  `to_address` varchar(128) DEFAULT NULL COMMENT '接收方地址',
  `block_number` bigint(20) DEFAULT NULL COMMENT '所在区块号',
  `confirmed` char(1) NOT NULL DEFAULT '0' COMMENT '是否确认：0=未确认，1=已确认',
  `confirmations` int(11) DEFAULT '0' COMMENT '确认数（确认块数量）',
  `tx_status` char(1) DEFAULT '1' COMMENT '交易状态 （1待完成，2已完成，3失败）',
  `detected_time` datetime DEFAULT NULL COMMENT '监听到交易时间',
  `confirmed_time` datetime DEFAULT NULL COMMENT '确认完成时间',
  `callback_status` char(1) NOT NULL DEFAULT '1' COMMENT '回调状态(1待回调 2回调完成，3失败)',
  `callback_time` datetime DEFAULT NULL COMMENT '回调完成时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_tx_hash` (`tx_hash`,`address_id`),
  KEY `idx_address_id` (`address_id`),
  KEY `idx_platform_id` (`platform_id`),
  KEY `idx_confirmed` (`confirmed`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区块链交易记录表（充值/归集/提现）';

-- ----------------------------
-- Table structure for bc_user_addresses
-- ----------------------------
DROP TABLE IF EXISTS `bc_user_addresses`;
CREATE TABLE `bc_user_addresses` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `platform_id` bigint(20) NOT NULL COMMENT '所属平台ID',
  `user_id` varchar(64) NOT NULL COMMENT '平台侧的用户唯一ID',
  `chain_type` varchar(10) NOT NULL COMMENT '区块链类型（TRON=TRC，ETH=ERC 等）',
  `address` varchar(128) NOT NULL COMMENT '区块链地址',
  `private_key` varchar(255) DEFAULT NULL COMMENT '私钥（托管型由系统生成）',
  `last_sync_time` datetime DEFAULT NULL COMMENT '最近余额同步时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `address` (`address`),
  UNIQUE KEY `uniq_user_platform_chain` (`platform_id`,`user_id`,`chain_type`),
  KEY `idx_platform_id` (`platform_id`),
  KEY `idx_chain_type` (`chain_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- ----------------------------
-- Table structure for bc_withdraw_record
-- ----------------------------
DROP TABLE IF EXISTS `bc_withdraw_record`;
CREATE TABLE `bc_withdraw_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `platform_id` bigint(20) NOT NULL COMMENT '下游平台ID',
  `request_no` varchar(64) NOT NULL COMMENT '下游请求单号/业务单号',
  `chain_type` varchar(20) NOT NULL COMMENT '链类型，比如 ETH/TRX/BTC',
  `token_symbol` varchar(20) NOT NULL COMMENT '币种符号，例如 USDT, ETH',
  `token_contract` varchar(128) DEFAULT NULL COMMENT '代币合约地址（主币可为空）',
  `from_address` varchar(128) DEFAULT NULL COMMENT '发送地址',
  `to_address` varchar(128) NOT NULL COMMENT '提现地址',
  `amount` decimal(36,18) NOT NULL COMMENT '提现金额',
  `tx_hash` varchar(128) DEFAULT NULL COMMENT '链上交易哈希',
  `block_number` bigint(20) NOT NULL DEFAULT '0' COMMENT '区块号',
  `confirmed` char(1) NOT NULL DEFAULT '0' COMMENT '是否确认：0未确认 1已确认',
  `confirmations` int(11) DEFAULT '0' COMMENT '确认数',
  `tx_status` char(1) DEFAULT '0' COMMENT '交易状态：0待提交 1待完成 2已完成 3失败',
  `detected_time` datetime DEFAULT NULL COMMENT '监听到交易时间（广播后链上可查到）',
  `confirmed_time` datetime DEFAULT NULL COMMENT '确认完成时间（达到阈值确认数）',
  `callback_status` char(1) NOT NULL DEFAULT '1' COMMENT '回调状态：1待回调 2回调成功 3回调失败',
  `callback_time` datetime DEFAULT NULL COMMENT '回调完成时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_platform_req` (`platform_id`,`request_no`),
  KEY `idx_tx_hash` (`tx_hash`),
  KEY `idx_status` (`tx_status`,`callback_status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='区块链提现记录表';

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table` (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作 sub主子表操作）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `form_col_num` int(1) DEFAULT '1' COMMENT '表单布局（单列 双列 三列）',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COMMENT='代码生成业务表';

-- ----------------------------
-- Records of gen_table
-- ----------------------------
BEGIN;
INSERT INTO `gen_table` VALUES (1, 'bc_platforms', '下游平台', '', NULL, 'Platforms', 'crud', 'com.ruoyi.blockchain', 'platforms', 'platform', '下游平台', 'dc', 1, '0', '/', '{\"parentMenuId\":\"2000\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"链上支付中心\",\"treeCode\":\"\"}', 'admin', '2025-10-27 15:43:47', '', '2025-10-27 15:57:07', '');
INSERT INTO `gen_table` VALUES (2, 'bc_user_addresses', '用户地址表', 'bc_address_balances', 'address_id', 'UserAddresses', 'crud', 'com.ruoyi.blockchain', 'platforms', 'addresses', '用户地址', 'dc', 1, '0', '/', '{\"parentMenuId\":\"2000\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"链上支付中心\",\"treeCode\":\"\"}', 'admin', '2025-10-27 16:26:55', '', '2025-10-27 17:16:16', '');
INSERT INTO `gen_table` VALUES (3, 'bc_address_balances', '地址余额表（币种级别）', '', NULL, 'AddressBalances', 'crud', 'com.ruoyi.blockchain', 'platforms', 'balances', '地址余额（币种级别）', 'dc', 1, '0', '/', '{\"parentMenuId\":\"2000\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"链上支付中心\",\"treeCode\":\"\"}', 'admin', '2025-10-27 16:33:45', '', '2025-10-27 17:15:36', '');
INSERT INTO `gen_table` VALUES (4, 'bc_address_pool', '预生成地址临时表', '', NULL, 'AddressPool', 'crud', 'com.ruoyi.blockchain', 'platforms', 'pool', '临时地址', 'dc', 1, '0', '/', '{\"parentMenuId\":\"2000\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"链上支付中心\",\"treeCode\":\"\"}', 'admin', '2025-10-28 12:28:41', '', '2025-10-28 12:57:05', '');
INSERT INTO `gen_table` VALUES (5, 'bc_api_keys', '区块链API密钥管理表', '', NULL, 'BcApiKeys', 'crud', 'com.ruoyi.blockchain', 'config', 'apiKey', '区块API配置', 'dc', 1, '0', '/', '{\"parentMenuId\":\"2018\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"区块配置\",\"treeCode\":\"\"}', 'admin', '2025-10-29 13:47:02', '', '2025-10-29 14:13:05', '');
INSERT INTO `gen_table` VALUES (6, 'bc_token_prices', '币种配置与USDT汇率表', '', NULL, 'TokenPrices', 'crud', 'com.ruoyi.blockchain', 'config', 'tokens', '币种配置', 'dc', 1, '0', '/', '{\"parentMenuId\":\"2018\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"区块配置\",\"treeCode\":\"\"}', 'admin', '2025-10-29 13:47:02', '', '2025-10-29 14:13:42', '');
INSERT INTO `gen_table` VALUES (7, 'bc_transactions', '区块链交易记录表（充值/归集/提现）', '', NULL, 'BcTransactions', 'crud', 'com.ruoyi.blockchain', 'trade', 'recharge', '地址充值记录', 'dc', 1, '0', '/', '{\"parentMenuId\":\"2030\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"交易记录\",\"treeCode\":\"\"}', 'admin', '2025-10-30 19:44:13', '', '2025-10-30 20:00:15', '');
INSERT INTO `gen_table` VALUES (8, 'bc_collection_config', '平台归集配置表', '', NULL, 'BcCollectionConfig', 'crud', 'com.ruoyi.blockchain', 'collection', 'config', '归集配置', 'dc', 1, '0', '/', '{\"parentMenuId\":\"2037\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"地址配置\",\"treeCode\":\"\"}', 'admin', '2025-11-09 13:21:13', '', '2025-11-09 14:03:58', '');
INSERT INTO `gen_table` VALUES (9, 'bc_consolidation_detail', '归集详情表', '', NULL, 'BcConsolidationDetail', 'crud', 'com.ruoyi.blockchain', 'trade', 'collection', '归集记录', 'dc', 1, '0', '/', '{\"parentMenuId\":\"2030\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"交易记录\",\"treeCode\":\"\"}', 'admin', '2025-11-09 13:21:13', '', '2025-11-09 14:54:45', '');
INSERT INTO `gen_table` VALUES (10, 'bc_fee_source_addresses', '手续费/付款地址表', '', NULL, 'BcFeeSourceAddresses', 'crud', 'com.ruoyi.blockchain', 'collection', 'sourceAddress', '付款地址', 'dc', 1, '0', '/', '{\"parentMenuId\":\"2037\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"地址配置\",\"treeCode\":\"\"}', 'admin', '2025-11-09 13:21:13', '', '2025-11-09 13:34:34', '');
INSERT INTO `gen_table` VALUES (11, 'bc_withdraw_record', '区块链提现记录表', '', NULL, 'BcWithdrawRecord', 'crud', 'com.ruoyi.blockchain', 'trade', 'withdraw', '区块链提现记录', 'dc', 1, '0', '/', '{\"parentMenuId\":\"2030\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"交易记录\",\"treeCode\":\"\"}', 'admin', '2025-11-12 13:49:33', '', '2025-11-12 13:58:05', '');
COMMIT;

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column` (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint(20) DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) DEFAULT '' COMMENT '字典类型',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB AUTO_INCREMENT=150 DEFAULT CHARSET=utf8mb4 COMMENT='代码生成业务表字段';

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------
BEGIN;
INSERT INTO `gen_table_column` VALUES (1, 1, 'id', '平台ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2025-10-27 15:43:47', NULL, '2025-10-27 15:57:07');
INSERT INTO `gen_table_column` VALUES (2, 1, 'platform_name', '平台名称', 'varchar(100)', 'String', 'platformName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 2, 'admin', '2025-10-27 15:43:47', NULL, '2025-10-27 15:57:07');
INSERT INTO `gen_table_column` VALUES (3, 1, 'secret_key', '签名SecretKey', 'varchar(255)', 'String', 'secretKey', '0', '0', '1', '1', '1', '1', NULL, 'EQ', 'input', '', 3, 'admin', '2025-10-27 15:43:47', NULL, '2025-10-27 15:57:07');
INSERT INTO `gen_table_column` VALUES (4, 1, 'api_key', 'apiKey', 'varchar(100)', 'String', 'apiKey', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 4, 'admin', '2025-10-27 15:43:47', NULL, '2025-10-27 15:57:07');
INSERT INTO `gen_table_column` VALUES (5, 1, 'callback_url', '回调URL', 'varchar(255)', 'String', 'callbackUrl', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 5, 'admin', '2025-10-27 15:43:47', NULL, '2025-10-27 15:57:07');
INSERT INTO `gen_table_column` VALUES (6, 1, 'status', '状态', 'char(1)', 'String', 'status', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', 'sys_normal_disable', 6, 'admin', '2025-10-27 15:43:47', NULL, '2025-10-27 15:57:07');
INSERT INTO `gen_table_column` VALUES (7, 1, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', NULL, '1', NULL, '1', NULL, 'EQ', 'input', '', 7, 'admin', '2025-10-27 15:43:47', NULL, '2025-10-27 15:57:07');
INSERT INTO `gen_table_column` VALUES (8, 1, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'input', '', 8, 'admin', '2025-10-27 15:43:47', NULL, '2025-10-27 15:57:07');
INSERT INTO `gen_table_column` VALUES (9, 1, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, '1', NULL, 'EQ', 'datetime', '', 9, 'admin', '2025-10-27 15:43:47', NULL, '2025-10-27 15:57:07');
INSERT INTO `gen_table_column` VALUES (10, 1, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 10, 'admin', '2025-10-27 15:43:47', NULL, '2025-10-27 15:57:07');
INSERT INTO `gen_table_column` VALUES (11, 2, 'id', '地址ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2025-10-27 16:26:55', NULL, '2025-10-27 17:16:16');
INSERT INTO `gen_table_column` VALUES (12, 2, 'platform_id', '所属平台ID', 'bigint(20)', 'Long', 'platformId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2025-10-27 16:26:55', NULL, '2025-10-27 17:16:16');
INSERT INTO `gen_table_column` VALUES (13, 2, 'user_id', '用户ID', 'varchar(64)', 'String', 'userId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2025-10-27 16:26:55', NULL, '2025-10-27 17:16:16');
INSERT INTO `gen_table_column` VALUES (14, 2, 'chain_type', '区块链', 'varchar(10)', 'String', 'chainType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'bc_chain_type', 4, 'admin', '2025-10-27 16:26:55', NULL, '2025-10-27 17:16:16');
INSERT INTO `gen_table_column` VALUES (15, 2, 'address', '区块链地址', 'varchar(128)', 'String', 'address', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2025-10-27 16:26:55', NULL, '2025-10-27 17:16:16');
INSERT INTO `gen_table_column` VALUES (16, 2, 'private_key', '私钥', 'varchar(255)', 'String', 'privateKey', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 6, 'admin', '2025-10-27 16:26:55', NULL, '2025-10-27 17:16:16');
INSERT INTO `gen_table_column` VALUES (17, 2, 'last_sync_time', '最近同步时间', 'datetime', 'Date', 'lastSyncTime', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'datetime', '', 7, 'admin', '2025-10-27 16:26:55', NULL, '2025-10-27 17:16:16');
INSERT INTO `gen_table_column` VALUES (18, 2, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 8, 'admin', '2025-10-27 16:26:55', NULL, '2025-10-27 17:16:16');
INSERT INTO `gen_table_column` VALUES (19, 2, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 9, 'admin', '2025-10-27 16:26:55', NULL, '2025-10-27 17:16:16');
INSERT INTO `gen_table_column` VALUES (20, 3, 'id', 'ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2025-10-27 16:33:45', NULL, '2025-10-27 17:15:36');
INSERT INTO `gen_table_column` VALUES (21, 3, 'address_id', '关联地址ID', 'bigint(20)', 'Long', 'addressId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2025-10-27 16:33:45', NULL, '2025-10-27 17:15:36');
INSERT INTO `gen_table_column` VALUES (22, 3, 'chain_type', '区块链', 'varchar(10)', 'String', 'chainType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'bc_chain_type', 3, 'admin', '2025-10-27 16:33:45', NULL, '2025-10-27 17:15:36');
INSERT INTO `gen_table_column` VALUES (23, 3, 'token_symbol', '币种符号', 'varchar(20)', 'String', 'tokenSymbol', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'admin', '2025-10-27 16:33:45', NULL, '2025-10-27 17:15:36');
INSERT INTO `gen_table_column` VALUES (24, 3, 'token_contract', '合约地址（主币为空）', 'varchar(128)', 'String', 'tokenContract', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 5, 'admin', '2025-10-27 16:33:45', NULL, '2025-10-27 17:15:36');
INSERT INTO `gen_table_column` VALUES (25, 3, 'balance', '币种数量', 'decimal(36,18)', 'BigDecimal', 'balance', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2025-10-27 16:33:45', NULL, '2025-10-27 17:15:36');
INSERT INTO `gen_table_column` VALUES (26, 3, 'balance_usdt_value', 'USDT价值', 'decimal(36,18)', 'BigDecimal', 'balanceUsdtValue', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2025-10-27 16:33:45', NULL, '2025-10-27 17:15:36');
INSERT INTO `gen_table_column` VALUES (27, 3, 'last_sync_time', '同步时间', 'datetime', 'Date', 'lastSyncTime', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'datetime', '', 8, 'admin', '2025-10-27 16:33:45', NULL, '2025-10-27 17:15:36');
INSERT INTO `gen_table_column` VALUES (28, 3, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 9, 'admin', '2025-10-27 16:33:45', NULL, '2025-10-27 17:15:36');
INSERT INTO `gen_table_column` VALUES (29, 3, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 10, 'admin', '2025-10-27 16:33:45', NULL, '2025-10-27 17:15:36');
INSERT INTO `gen_table_column` VALUES (30, 4, 'id', '编号ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, '1', NULL, 'EQ', 'input', '', 1, 'admin', '2025-10-28 12:28:41', NULL, '2025-10-28 12:57:05');
INSERT INTO `gen_table_column` VALUES (31, 4, 'chain_type', '区块链', 'varchar(10)', 'String', 'chainType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'bc_chain_type', 2, 'admin', '2025-10-28 12:28:41', NULL, '2025-10-28 12:57:05');
INSERT INTO `gen_table_column` VALUES (32, 4, 'address', '区块链地址', 'varchar(128)', 'String', 'address', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2025-10-28 12:28:41', NULL, '2025-10-28 12:57:05');
INSERT INTO `gen_table_column` VALUES (33, 4, 'private_key', '私钥(加密后)', 'varchar(255)', 'String', 'privateKey', '0', '0', '1', '1', '1', NULL, NULL, 'EQ', 'input', '', 4, 'admin', '2025-10-28 12:28:41', NULL, '2025-10-28 12:57:05');
INSERT INTO `gen_table_column` VALUES (34, 5, 'id', '记录ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:05');
INSERT INTO `gen_table_column` VALUES (35, 5, 'chain_type', '区块链类型', 'varchar(10)', 'String', 'chainType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'bc_chain_type', 2, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:05');
INSERT INTO `gen_table_column` VALUES (36, 5, 'api_provider', 'API提供商', 'varchar(50)', 'String', 'apiProvider', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', 'bc_api_provider', 3, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:05');
INSERT INTO `gen_table_column` VALUES (37, 5, 'api_key', 'API密钥', 'varchar(255)', 'String', 'apiKey', '0', '0', '1', '1', '1', NULL, NULL, 'EQ', 'input', '', 4, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:05');
INSERT INTO `gen_table_column` VALUES (38, 5, 'api_url', 'API基础URL', 'varchar(255)', 'String', 'apiUrl', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 5, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:05');
INSERT INTO `gen_table_column` VALUES (39, 5, 'daily_limit', '每日请求限制', 'int(11)', 'Long', 'dailyLimit', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'input', '', 6, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:05');
INSERT INTO `gen_table_column` VALUES (40, 5, 'used_count', '今日已用次数', 'int(11)', 'Long', 'usedCount', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'input', '', 7, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:05');
INSERT INTO `gen_table_column` VALUES (41, 5, 'priority', '优先级（1-10，数字越小优先级越高）', 'int(11)', 'Long', 'priority', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'input', '', 8, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:05');
INSERT INTO `gen_table_column` VALUES (42, 5, 'status', '状态', 'char(1)', 'String', 'status', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'radio', 'bc_api_key_status', 9, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:05');
INSERT INTO `gen_table_column` VALUES (43, 5, 'last_used_time', '最后使用时间', 'datetime', 'Date', 'lastUsedTime', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'datetime', '', 10, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:05');
INSERT INTO `gen_table_column` VALUES (44, 5, 'reset_time', '计数重置时间（每日凌晨）', 'datetime', 'Date', 'resetTime', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'datetime', '', 11, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:05');
INSERT INTO `gen_table_column` VALUES (45, 5, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 12, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:05');
INSERT INTO `gen_table_column` VALUES (46, 5, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 13, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:05');
INSERT INTO `gen_table_column` VALUES (47, 5, 'remark', '备注', 'varchar(500)', 'String', 'remark', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'textarea', '', 14, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:05');
INSERT INTO `gen_table_column` VALUES (48, 6, 'id', '记录ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:42');
INSERT INTO `gen_table_column` VALUES (49, 6, 'chain_type', '区块链类型', 'varchar(10)', 'String', 'chainType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'bc_chain_type', 2, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:42');
INSERT INTO `gen_table_column` VALUES (50, 6, 'token_symbol', '币种符号', 'varchar(20)', 'String', 'tokenSymbol', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'bc_token_symbol', 3, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:42');
INSERT INTO `gen_table_column` VALUES (51, 6, 'token_contract', '代币合约地址（主币为空）', 'varchar(128)', 'String', 'tokenContract', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 4, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:42');
INSERT INTO `gen_table_column` VALUES (52, 6, 'decimals', '代币精度', 'int(11)', 'Long', 'decimals', '0', '0', '1', '1', '1', '1', NULL, 'EQ', 'input', '', 5, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:42');
INSERT INTO `gen_table_column` VALUES (53, 6, 'price_in_usdt', 'USDT汇率', 'decimal(36,18)', 'BigDecimal', 'priceInUsdt', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:42');
INSERT INTO `gen_table_column` VALUES (54, 6, 'is_main_coin', '是否主币', 'tinyint(4)', 'Integer', 'isMainCoin', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', 'bc_yes_no', 7, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:42');
INSERT INTO `gen_table_column` VALUES (55, 6, 'enabled', '是否启用监听（0禁用 1启用）', 'tinyint(4)', 'Integer', 'enabled', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', 'bc_disable_enable', 8, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:42');
INSERT INTO `gen_table_column` VALUES (56, 6, 'token_type', '代币类型', 'varchar(10)', 'String', 'tokenType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'bc_token_type', 9, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:42');
INSERT INTO `gen_table_column` VALUES (57, 6, 'last_update', '最后更新时间', 'datetime', 'Date', 'lastUpdate', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'datetime', '', 10, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:42');
INSERT INTO `gen_table_column` VALUES (58, 6, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 11, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:42');
INSERT INTO `gen_table_column` VALUES (59, 6, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 12, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:42');
INSERT INTO `gen_table_column` VALUES (60, 6, 'remark', '备注', 'varchar(500)', 'String', 'remark', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'textarea', '', 13, 'admin', '2025-10-29 13:47:02', NULL, '2025-10-29 14:13:42');
INSERT INTO `gen_table_column` VALUES (61, 7, 'id', '记录ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2025-10-30 19:44:13', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (62, 7, 'address_id', '关联地址ID（bc_user_addresses.id）', 'bigint(20)', 'Long', 'addressId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2025-10-30 19:44:13', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (63, 7, 'platform_id', '所属平台ID', 'bigint(20)', 'Long', 'platformId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2025-10-30 19:44:13', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (64, 7, 'tx_hash', '交易哈希', 'varchar(128)', 'String', 'txHash', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (65, 7, 'chain_type', '区块链类型', 'varchar(10)', 'String', 'chainType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'bc_chain_type', 5, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (66, 7, 'token_symbol', '币种符号', 'varchar(20)', 'String', 'tokenSymbol', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', 'bc_token_symbol', 6, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (67, 7, 'token_contract', '代币合约地址（主币为空）', 'varchar(128)', 'String', 'tokenContract', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 7, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (68, 7, 'amount', '交易金额', 'decimal(36,18) unsigned', 'BigDecimal', 'amount', '0', '0', '1', '1', '1', '1', NULL, 'EQ', 'input', '', 8, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (69, 7, 'direction', '交易方向', 'char(1)', 'String', 'direction', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'bc_direction', 9, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (70, 7, 'from_address', '发送方地址', 'varchar(128)', 'String', 'fromAddress', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 10, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (71, 7, 'to_address', '接收方地址', 'varchar(128)', 'String', 'toAddress', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 11, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (72, 7, 'block_number', '区块号', 'bigint(20)', 'Long', 'blockNumber', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 12, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (73, 7, 'confirmed', '是否确认', 'char(1)', 'String', 'confirmed', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'bc_has_confirmed', 13, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (74, 7, 'confirmations', '确认块数量', 'int(11)', 'Long', 'confirmations', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'input', '', 14, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (75, 7, 'tx_status', '交易状态', 'char(1)', 'String', 'txStatus', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'radio', 'bc_tx_status', 15, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (76, 7, 'detected_time', '交易时间', 'datetime', 'Date', 'detectedTime', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'datetime', '', 16, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (77, 7, 'confirmed_time', '确认完成时间', 'datetime', 'Date', 'confirmedTime', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'datetime', '', 17, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (78, 7, 'callback_status', '回调状态', 'char(1)', 'String', 'callbackStatus', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', 'bc_callback_status', 18, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (79, 7, 'callback_time', '回调时间', 'datetime', 'Date', 'callbackTime', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'datetime', '', 19, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (80, 7, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 20, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (81, 7, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 21, 'admin', '2025-10-30 19:44:14', NULL, '2025-10-30 20:00:15');
INSERT INTO `gen_table_column` VALUES (82, 8, 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:03:58');
INSERT INTO `gen_table_column` VALUES (83, 8, 'platform_id', '平台ID', 'bigint(20)', 'Long', 'platformId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:03:58');
INSERT INTO `gen_table_column` VALUES (84, 8, 'chain_type', '区块链', 'varchar(20)', 'String', 'chainType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'bc_chain_type', 3, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:03:58');
INSERT INTO `gen_table_column` VALUES (85, 8, 'target_address', '归集地址(必须充值且需要预留usdt)', 'varchar(128)', 'String', 'targetAddress', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:03:58');
INSERT INTO `gen_table_column` VALUES (86, 8, 'min_collect_amount', '最小归集阈值', 'decimal(36,18)', 'BigDecimal', 'minCollectAmount', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:03:58');
INSERT INTO `gen_table_column` VALUES (87, 8, 'enable_auto_collect', '自动归集', 'tinyint(1)', 'Integer', 'enableAutoCollect', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'radio', 'bc_yes_no', 6, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:03:58');
INSERT INTO `gen_table_column` VALUES (88, 8, 'token_scope', '归集范围', 'char(1)', 'String', 'tokenScope', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'bc_token_scope', 7, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:03:58');
INSERT INTO `gen_table_column` VALUES (89, 8, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 8, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:03:58');
INSERT INTO `gen_table_column` VALUES (90, 8, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 9, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:03:58');
INSERT INTO `gen_table_column` VALUES (91, 8, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 10, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:03:58');
INSERT INTO `gen_table_column` VALUES (92, 8, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 11, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:03:58');
INSERT INTO `gen_table_column` VALUES (93, 9, 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (94, 9, 'batch_no', '批次号', 'varchar(64)', 'String', 'batchNo', '0', '0', '1', '1', '1', NULL, NULL, 'EQ', 'input', '', 2, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (95, 9, 'platform_id', '平台ID', 'bigint(20)', 'Long', 'platformId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (96, 9, 'address_id', '地址ID', 'bigint(20)', 'Long', 'addressId', '0', '0', '1', '1', '1', NULL, NULL, 'EQ', 'input', '', 4, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (97, 9, 'chain_type', '链类型', 'varchar(20)', 'String', 'chainType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'bc_chain_type', 5, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (98, 9, 'from_address', '源地址', 'varchar(128)', 'String', 'fromAddress', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (99, 9, 'to_address', '目标地址', 'varchar(128)', 'String', 'toAddress', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (100, 9, 'token_symbol', '代币符号', 'varchar(20)', 'String', 'tokenSymbol', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'input', '', 8, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (101, 9, 'token_contract', '合约地址', 'varchar(128)', 'String', 'tokenContract', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 9, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (102, 9, 'amount', '归集金额', 'decimal(36,18)', 'BigDecimal', 'amount', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'input', '', 10, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (103, 9, 'gas_fee_sent', 'Gas费用', 'decimal(36,18)', 'BigDecimal', 'gasFeeSent', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 11, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (104, 9, 'gas_fee_used', '使用的Gas', 'decimal(36,18)', 'BigDecimal', 'gasFeeUsed', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 12, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (105, 9, 'gas_tx_hash', 'Gas交易哈希', 'varchar(128)', 'String', 'gasTxHash', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 13, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (106, 9, 'gas_tx_status', 'Gas交易状态：0=未发送, 1=已发送, 2=已确认, 3=失败', 'char(1)', 'String', 'gasTxStatus', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'radio', 'bc_gather_tx_status', 14, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (107, 9, 'gas_send_time', '发送手续费时间', 'int(11)', 'Long', 'gasSendTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 15, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (108, 9, 'tx_hash', '交易哈希', 'varchar(128)', 'String', 'txHash', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 16, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (109, 9, 'tx_status', '交易状态', 'char(1)', 'String', 'txStatus', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'radio', 'bc_gather_tx_status', 17, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (110, 9, 'confirm_count', '确认数', 'int(11)', 'Long', 'confirmCount', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 18, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (111, 9, 'confirm_time', '确认时间', 'datetime', 'Date', 'confirmTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 19, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (112, 9, 'retry_count', '重试次数', 'int(11)', 'Long', 'retryCount', '0', '0', '1', '1', '1', NULL, NULL, 'EQ', 'input', '', 20, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (113, 9, 'error_msg', '错误信息', 'varchar(512)', 'String', 'errorMsg', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'textarea', '', 21, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (114, 9, 'status', '整体状态', 'char(1)', 'String', 'status', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', 'bc_gather_status', 22, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (115, 9, 'start_time', '开始时间', 'datetime', 'Date', 'startTime', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'datetime', '', 23, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (116, 9, 'complete_time', '完成时间', 'datetime', 'Date', 'completeTime', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'datetime', '', 24, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (117, 9, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 25, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (118, 9, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 26, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 14:54:45');
INSERT INTO `gen_table_column` VALUES (119, 10, 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 13:34:34');
INSERT INTO `gen_table_column` VALUES (120, 10, 'platform_id', '平台ID', 'bigint(20)', 'Long', 'platformId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 13:34:34');
INSERT INTO `gen_table_column` VALUES (121, 10, 'chain_type', '区块链', 'varchar(20)', 'String', 'chainType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'bc_chain_type', 3, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 13:34:34');
INSERT INTO `gen_table_column` VALUES (122, 10, 'fee_address', '出款地址', 'varchar(128)', 'String', 'feeAddress', '0', '0', '1', '1', '1', '1', NULL, 'EQ', 'input', '', 4, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 13:34:34');
INSERT INTO `gen_table_column` VALUES (123, 10, 'private_key_encrypted', '私钥(加密)', 'text', 'String', 'privateKeyEncrypted', '0', '0', '1', '1', '1', NULL, NULL, 'EQ', 'textarea', '', 5, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 13:34:34');
INSERT INTO `gen_table_column` VALUES (124, 10, 'last_used_time', '最后使用时间', 'datetime', 'Date', 'lastUsedTime', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'datetime', '', 6, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 13:34:34');
INSERT INTO `gen_table_column` VALUES (125, 10, 'balance', '主币余额', 'decimal(36,18)', 'BigDecimal', 'balance', '0', '0', NULL, '1', '1', '1', NULL, 'EQ', 'input', '', 7, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 13:34:34');
INSERT INTO `gen_table_column` VALUES (126, 10, 'status', '状态', 'char(1)', 'String', 'status', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'radio', 'sys_normal_disable', 8, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 13:34:34');
INSERT INTO `gen_table_column` VALUES (127, 10, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 9, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 13:34:34');
INSERT INTO `gen_table_column` VALUES (128, 10, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 10, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 13:34:34');
INSERT INTO `gen_table_column` VALUES (129, 10, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 11, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 13:34:34');
INSERT INTO `gen_table_column` VALUES (130, 10, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 12, 'admin', '2025-11-09 13:21:13', NULL, '2025-11-09 13:34:34');
INSERT INTO `gen_table_column` VALUES (131, 11, 'id', '编号', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (132, 11, 'platform_id', '平台ID', 'bigint(20)', 'Long', 'platformId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (133, 11, 'request_no', '下游单号', 'varchar(64)', 'String', 'requestNo', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (134, 11, 'chain_type', '区块链', 'varchar(20)', 'String', 'chainType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'bc_chain_type', 4, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (135, 11, 'token_symbol', '币种符号', 'varchar(20)', 'String', 'tokenSymbol', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', 'bc_token_symbol', 5, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (136, 11, 'token_contract', '合约地址', 'varchar(128)', 'String', 'tokenContract', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'input', '', 6, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (137, 11, 'from_address', '付款地址', 'varchar(128)', 'String', 'fromAddress', '0', '0', '1', '1', '1', NULL, NULL, 'EQ', 'input', '', 7, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (138, 11, 'to_address', '收款地址', 'varchar(128)', 'String', 'toAddress', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 8, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (139, 11, 'amount', '提现金额', 'decimal(36,18)', 'BigDecimal', 'amount', '0', '0', '1', '1', '1', '1', NULL, 'EQ', 'input', '', 9, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (140, 11, 'tx_hash', '交易哈希', 'varchar(128)', 'String', 'txHash', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 10, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (141, 11, 'confirmed', '确认状态', 'char(1)', 'String', 'confirmed', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', 'bc_has_confirmed', 11, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (142, 11, 'confirmations', '确认数', 'int(11)', 'Long', 'confirmations', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 12, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (143, 11, 'tx_status', '交易状态', 'char(1)', 'String', 'txStatus', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'radio', 'bc_tx_status', 13, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (144, 11, 'detected_time', '交易时间', 'datetime', 'Date', 'detectedTime', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'datetime', '', 14, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (145, 11, 'confirmed_time', '确认时间', 'datetime', 'Date', 'confirmedTime', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'datetime', '', 15, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (146, 11, 'callback_status', '回调状态', 'char(1)', 'String', 'callbackStatus', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', 'bc_callback_status', 16, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (147, 11, 'callback_time', '回调完成时间', 'datetime', 'Date', 'callbackTime', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'datetime', '', 17, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (148, 11, 'create_time', '创建日期', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 18, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
INSERT INTO `gen_table_column` VALUES (149, 11, 'update_time', '更新日期', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 19, 'admin', '2025-11-12 13:49:33', NULL, '2025-11-12 13:58:05');
COMMIT;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `config_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='参数配置表';

-- ----------------------------
-- Records of sys_config
-- ----------------------------
BEGIN;
INSERT INTO `sys_config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2025-10-27 11:21:37', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2025-10-27 11:21:37', '', NULL, '初始化密码 123456');
INSERT INTO `sys_config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2025-10-27 11:21:37', '', NULL, '深黑主题theme-dark，浅色主题theme-light，深蓝主题theme-blue');
INSERT INTO `sys_config` VALUES (4, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', '2025-10-27 11:21:37', '', NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (5, '用户管理-密码字符范围', 'sys.account.chrtype', '0', 'Y', 'admin', '2025-10-27 11:21:37', '', NULL, '默认任意字符范围，0任意（密码可以输入任意字符），1数字（密码只能为0-9数字），2英文字母（密码只能为a-z和A-Z字母），3字母和数字（密码必须包含字母，数字）,4字母数字和特殊字符（目前支持的特殊字符包括：~!@#$%^&*()-=_+）');
INSERT INTO `sys_config` VALUES (6, '用户管理-初始密码修改策略', 'sys.account.initPasswordModify', '1', 'Y', 'admin', '2025-10-27 11:21:37', '', NULL, '0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框');
INSERT INTO `sys_config` VALUES (7, '用户管理-账号密码更新周期', 'sys.account.passwordValidateDays', '0', 'Y', 'admin', '2025-10-27 11:21:37', '', NULL, '密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');
INSERT INTO `sys_config` VALUES (8, '主框架页-菜单导航显示风格', 'sys.index.menuStyle', 'default', 'Y', 'admin', '2025-10-27 11:21:37', '', NULL, '菜单导航显示风格（default为左侧导航菜单，topnav为顶部导航菜单）');
INSERT INTO `sys_config` VALUES (9, '主框架页-是否开启页脚', 'sys.index.footer', 'true', 'Y', 'admin', '2025-10-27 11:21:37', '', NULL, '是否开启底部页脚显示（true显示，false隐藏）');
INSERT INTO `sys_config` VALUES (10, '主框架页-是否开启页签', 'sys.index.tagsView', 'true', 'Y', 'admin', '2025-10-27 11:21:37', '', NULL, '是否开启菜单多页签显示（true显示，false隐藏）');
INSERT INTO `sys_config` VALUES (11, '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', '2025-10-27 11:21:37', '', NULL, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');
COMMIT;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
BEGIN;
INSERT INTO `sys_dept` VALUES (100, 0, '0', '总公司', 0, '管理员', '15888888888', 'admin@qq.com', '0', '0', 'admin', '2025-10-27 11:21:37', '', NULL);
INSERT INTO `sys_dept` VALUES (101, 100, '0,100', '深圳分公司', 1, '管理员', '15888888888', 'admin@qq.com', '0', '0', 'admin', '2025-10-27 11:21:37', '', NULL);
INSERT INTO `sys_dept` VALUES (102, 100, '0,100', '长沙分公司', 2, '管理员', '15888888888', 'admin@qq.com', '0', '0', 'admin', '2025-10-27 11:21:37', '', NULL);
INSERT INTO `sys_dept` VALUES (103, 101, '0,100,101', '研发部门', 1, '管理员', '15888888888', 'admin@qq.com', '0', '0', 'admin', '2025-10-27 11:21:37', '', NULL);
INSERT INTO `sys_dept` VALUES (104, 101, '0,100,101', '市场部门', 2, '管理员', '15888888888', 'admin@qq.com', '0', '0', 'admin', '2025-10-27 11:21:37', '', NULL);
INSERT INTO `sys_dept` VALUES (105, 101, '0,100,101', '测试部门', 3, '管理员', '15888888888', 'admin@qq.com', '0', '0', 'admin', '2025-10-27 11:21:37', '', NULL);
INSERT INTO `sys_dept` VALUES (106, 101, '0,100,101', '财务部门', 4, '管理员', '15888888888', 'admin@qq.com', '0', '0', 'admin', '2025-10-27 11:21:37', '', NULL);
INSERT INTO `sys_dept` VALUES (107, 101, '0,100,101', '运维部门', 5, '管理员', '15888888888', 'admin@qq.com', '0', '0', 'admin', '2025-10-27 11:21:37', '', NULL);
INSERT INTO `sys_dept` VALUES (108, 102, '0,100,102', '市场部门', 1, '管理员', '15888888888', 'admin@qq.com', '0', '0', 'admin', '2025-10-27 11:21:37', '', NULL);
INSERT INTO `sys_dept` VALUES (109, 102, '0,100,102', '财务部门', 2, '管理员', '15888888888', 'admin@qq.com', '0', '0', 'admin', '2025-10-27 11:21:37', '', NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(4) DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
BEGIN;
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '性别男');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '性别女');
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '性别未知');
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '显示菜单');
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '默认分组');
INSERT INTO `sys_dict_data` VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '系统分组');
INSERT INTO `sys_dict_data` VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '系统默认是');
INSERT INTO `sys_dict_data` VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '系统默认否');
INSERT INTO `sys_dict_data` VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '通知');
INSERT INTO `sys_dict_data` VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '公告');
INSERT INTO `sys_dict_data` VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '关闭状态');
INSERT INTO `sys_dict_data` VALUES (18, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '其他操作');
INSERT INTO `sys_dict_data` VALUES (19, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '新增操作');
INSERT INTO `sys_dict_data` VALUES (20, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '修改操作');
INSERT INTO `sys_dict_data` VALUES (21, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '删除操作');
INSERT INTO `sys_dict_data` VALUES (22, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '授权操作');
INSERT INTO `sys_dict_data` VALUES (23, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '导出操作');
INSERT INTO `sys_dict_data` VALUES (24, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '导入操作');
INSERT INTO `sys_dict_data` VALUES (25, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '强退操作');
INSERT INTO `sys_dict_data` VALUES (26, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '生成操作');
INSERT INTO `sys_dict_data` VALUES (27, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '清空操作');
INSERT INTO `sys_dict_data` VALUES (28, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (29, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (100, 1, 'TRON链', 'TRX', 'bc_chain_type', '', 'danger', 'Y', '0', 'admin', '2025-10-27 16:24:26', 'admin', '2025-10-27 17:11:17', '');
INSERT INTO `sys_dict_data` VALUES (101, 2, 'ethereum链', 'ETH', 'bc_chain_type', '', 'success', 'N', '0', 'admin', '2025-10-27 16:25:26', 'admin', '2025-10-27 16:25:54', '');
INSERT INTO `sys_dict_data` VALUES (105, 1, 'TronGrid', 'TronGrid', 'bc_api_provider', NULL, NULL, 'Y', '0', 'admin', '2025-10-29 13:43:52', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (106, 2, 'Etherscan', 'Etherscan', 'bc_api_provider', NULL, NULL, 'N', '0', 'admin', '2025-10-29 13:44:03', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (107, 3, 'Infura', 'Infura', 'bc_api_provider', NULL, NULL, 'N', '0', 'admin', '2025-10-29 13:44:19', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (108, 1, 'TRX主币', 'TRX', 'bc_token_type', NULL, NULL, 'Y', '0', 'admin', '2025-10-29 13:45:27', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (109, 2, 'ETH主币', 'ETH', 'bc_token_type', NULL, NULL, 'N', '0', 'admin', '2025-10-29 13:45:46', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (110, 3, 'TRX-TRC20', 'TRC20', 'bc_token_type', NULL, NULL, 'N', '0', 'admin', '2025-10-29 13:46:08', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (111, 4, 'ETH-ERC20', 'ERC20', 'bc_token_type', NULL, NULL, 'N', '0', 'admin', '2025-10-29 13:46:27', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (112, 1, 'USDC', 'USDC', 'bc_token_symbol', NULL, NULL, 'N', '0', 'admin', '2025-10-29 13:50:17', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (113, 2, 'USDT', 'USDT', 'bc_token_symbol', NULL, NULL, 'N', '0', 'admin', '2025-10-29 13:50:35', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (114, 3, 'ETH', 'ETH', 'bc_token_symbol', NULL, NULL, 'N', '0', 'admin', '2025-10-29 13:50:48', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (115, 4, 'TRX', 'TRX', 'bc_token_symbol', NULL, NULL, 'N', '0', 'admin', '2025-10-29 13:51:02', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (116, 1, '是', '1', 'bc_yes_no', NULL, 'success', 'N', '0', 'admin', '2025-10-29 13:53:17', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (117, 2, '否', '0', 'bc_yes_no', NULL, 'warning', 'Y', '0', 'admin', '2025-10-29 13:53:31', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (118, 1, '启用', '1', 'bc_disable_enable', NULL, 'success', 'Y', '0', 'admin', '2025-10-29 13:56:28', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (119, 2, '禁用', '0', 'bc_disable_enable', NULL, 'danger', 'N', '0', 'admin', '2025-10-29 13:56:44', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (120, 2, '禁用', '1', 'bc_api_key_status', '', 'warning', 'N', '0', 'admin', '2025-10-29 14:05:49', 'admin', '2025-10-29 14:07:37', '');
INSERT INTO `sys_dict_data` VALUES (121, 1, '正常', '0', 'bc_api_key_status', NULL, 'primary', 'Y', '0', 'admin', '2025-10-29 14:06:07', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (122, 3, '已达上限', '2', 'bc_api_key_status', NULL, 'danger', 'N', '0', 'admin', '2025-10-29 14:06:49', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (123, 1, '充值', '1', 'bc_direction', NULL, NULL, 'Y', '0', 'admin', '2025-10-30 19:37:33', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (124, 2, '归集', '2', 'bc_direction', NULL, NULL, 'N', '0', 'admin', '2025-10-30 19:37:50', '', NULL, '归集');
INSERT INTO `sys_dict_data` VALUES (125, 1, '未确认', '0', 'bc_has_confirmed', NULL, 'warning', 'Y', '0', 'admin', '2025-10-30 19:40:00', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (126, 2, '已确认', '1', 'bc_has_confirmed', NULL, 'success', 'N', '0', 'admin', '2025-10-30 19:40:16', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (127, 1, '待完成', '1', 'bc_tx_status', '', 'warning', 'N', '0', 'admin', '2025-10-30 19:41:13', 'admin', '2025-11-12 17:13:50', '已经在链上，等待区块确认');
INSERT INTO `sys_dict_data` VALUES (128, 2, '已完成', '2', 'bc_tx_status', NULL, 'primary', 'N', '0', 'admin', '2025-10-30 19:41:34', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (129, 3, '失败', '3', 'bc_tx_status', NULL, 'danger', 'N', '0', 'admin', '2025-10-30 19:41:52', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (130, 1, '待回调', '1', 'bc_callback_status', NULL, 'warning', 'Y', '0', 'admin', '2025-10-30 19:42:54', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (131, 2, '回调完成', '2', 'bc_callback_status', NULL, 'primary', 'N', '0', 'admin', '2025-10-30 19:43:17', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (132, 3, '失败', '3', 'bc_callback_status', NULL, 'danger', 'N', '0', 'admin', '2025-10-30 19:43:32', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (133, 1, '未发送', '0', 'bc_gather_tx_status', NULL, NULL, 'Y', '0', 'admin', '2025-11-09 13:09:12', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (134, 2, '已发送', '1', 'bc_gather_tx_status', NULL, NULL, 'N', '0', 'admin', '2025-11-09 13:09:26', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (135, 3, '已确认', '2', 'bc_gather_tx_status', NULL, NULL, 'N', '0', 'admin', '2025-11-09 13:09:45', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (136, 4, '失败', '3', 'bc_gather_tx_status', NULL, NULL, 'N', '0', 'admin', '2025-11-09 13:10:00', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (137, 1, '待处理', '0', 'bc_gather_status', NULL, NULL, 'Y', '0', 'admin', '2025-11-09 13:12:15', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (138, 2, '归集中', '1', 'bc_gather_status', NULL, NULL, 'N', '0', 'admin', '2025-11-09 13:12:31', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (139, 3, '已归集', '2', 'bc_gather_status', NULL, NULL, 'N', '0', 'admin', '2025-11-09 13:12:49', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (140, 4, '失败', '3', 'bc_gather_status', NULL, NULL, 'N', '0', 'admin', '2025-11-09 13:13:06', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (141, 1, '全部代币', '1', 'bc_token_scope', '', '', 'Y', '0', 'admin', '2025-11-09 13:16:07', 'admin', '2025-11-09 19:37:27', '');
INSERT INTO `sys_dict_data` VALUES (142, 2, '主币', '2', 'bc_token_scope', '', '', 'N', '0', 'admin', '2025-11-09 13:16:23', 'admin', '2025-11-09 13:16:52', '');
INSERT INTO `sys_dict_data` VALUES (143, 3, '仅USDT', '3', 'bc_token_scope', NULL, NULL, 'N', '0', 'admin', '2025-11-09 13:16:42', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (144, 0, '待提交', '0', 'bc_tx_status', NULL, 'default', 'Y', '0', 'admin', '2025-11-12 17:13:16', '', NULL, '等待提交到链上');
COMMIT;

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
BEGIN;
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '任务状态列表');
INSERT INTO `sys_dict_type` VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '任务分组列表');
INSERT INTO `sys_dict_type` VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '操作类型列表');
INSERT INTO `sys_dict_type` VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '登录状态列表');
INSERT INTO `sys_dict_type` VALUES (100, '区块链', 'bc_chain_type', '0', 'admin', '2025-10-27 16:23:45', 'admin', '2025-10-27 17:11:01', '区块链的简称。需要和该接口一致https://api.binance.com/api/v3/ticker/price');
INSERT INTO `sys_dict_type` VALUES (102, 'API提供商', 'bc_api_provider', '0', 'admin', '2025-10-29 13:42:44', 'admin', '2025-10-29 13:43:01', 'API提供商（TronGrid、Etherscan、Infura等）');
INSERT INTO `sys_dict_type` VALUES (103, '代币类型', 'bc_token_type', '0', 'admin', '2025-10-29 13:44:57', '', NULL, '代币类型（TRX主币/TRC20/ERC20等）');
INSERT INTO `sys_dict_type` VALUES (104, '币种符号', 'bc_token_symbol', '0', 'admin', '2025-10-29 13:50:05', '', NULL, 'TRX,USDT,ETH,USDC');
INSERT INTO `sys_dict_type` VALUES (105, '区块是否', 'bc_yes_no', '0', 'admin', '2025-10-29 13:52:30', 'admin', '2025-11-09 13:05:21', '是否（0否 1是）');
INSERT INTO `sys_dict_type` VALUES (106, '是否禁用', 'bc_disable_enable', '0', 'admin', '2025-10-29 13:56:07', '', NULL, '0禁用 1启用');
INSERT INTO `sys_dict_type` VALUES (107, '接口状态', 'bc_api_key_status', '0', 'admin', '2025-10-29 14:05:28', '', NULL, '状态（0正常 1禁用 2已达上限');
INSERT INTO `sys_dict_type` VALUES (108, '交易方向', 'bc_direction', '0', 'admin', '2025-10-30 19:37:09', '', NULL, '交易方向：1=充值，2=归集');
INSERT INTO `sys_dict_type` VALUES (109, '确认状态', 'bc_has_confirmed', '0', 'admin', '2025-10-30 19:39:36', 'admin', '2025-11-09 13:02:28', '充值确认状态：0=未确认，1=已确认');
INSERT INTO `sys_dict_type` VALUES (110, '交易状态', 'bc_tx_status', '0', 'admin', '2025-10-30 19:40:48', '', NULL, '交易状态 （1待完成，2已完成，3失败');
INSERT INTO `sys_dict_type` VALUES (111, '回调状态', 'bc_callback_status', '0', 'admin', '2025-10-30 19:42:26', '', NULL, '回调状态(1待回调 2回调完成，3失败)');
INSERT INTO `sys_dict_type` VALUES (112, '归集前置状态', 'bc_gather_tx_status', '0', 'admin', '2025-11-09 13:08:51', '', NULL, '交易状态：0=未发送, 1=已发送, 2=已确认, 3=失败');
INSERT INTO `sys_dict_type` VALUES (113, '归集状态', 'bc_gather_status', '0', 'admin', '2025-11-09 13:11:10', '', NULL, '归集的状态，状态：0=待处理, 1=归集中, 2=已归集,3=失败');
INSERT INTO `sys_dict_type` VALUES (114, '归集范围', 'bc_token_scope', '0', 'admin', '2025-11-09 13:15:25', 'admin', '2025-11-09 13:15:48', '归集范围：1=全部代币，2=主币，3=仅USDT');
COMMIT;

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job` (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8mb4 COMMENT='定时任务调度表';

-- ----------------------------
-- Records of sys_job
-- ----------------------------
BEGIN;
INSERT INTO `sys_job` VALUES (100, '生成地址', 'SYSTEM', 'createAddressTask.create(30)', '0 0/5 * * * ?', '1', '1', '1', 'admin', '2025-10-28 14:04:22', 'admin5566', '2025-11-23 18:19:39', '');
INSERT INTO `sys_job` VALUES (101, '更新代币价格', 'SYSTEM', 'tokenPriceUpdateTask.updatePrices', '0 0/7 * * * ?', '3', '1', '1', 'admin', '2025-10-29 16:57:49', 'admin5566', '2025-11-23 18:19:53', '');
INSERT INTO `sys_job` VALUES (102, 'TRX扫链区块', 'SYSTEM', 'scanTronChainTask.scanTronBlocks(50)', '0 0/1 * * * ?', '1', '1', '1', 'admin', '2025-10-30 14:38:17', 'admin', '2025-11-23 16:07:38', '');
INSERT INTO `sys_job` VALUES (103, 'trx充值确认交易', 'SYSTEM', 'scanTronChainTask.updatePending', '0/40 * * * * ?', '1', '1', '1', 'admin', '2025-10-31 18:11:51', 'admin5566', '2025-11-23 18:20:11', '');
INSERT INTO `sys_job` VALUES (104, '充值交易回调', 'DEFAULT', 'transactionCallbackTask.callBack', '0/20 * * * * ?', '1', '1', '1', 'admin', '2025-10-31 18:18:02', 'admin', '2025-11-22 15:41:20', '');
INSERT INTO `sys_job` VALUES (105, 'eth收款监控', 'SYSTEM', 'scanEthChainTask.scanTronBlocks(20)', '0 0/1 * * * ?', '1', '1', '1', 'admin', '2025-11-03 16:37:37', 'admin', '2025-11-23 16:32:33', '');
INSERT INTO `sys_job` VALUES (106, 'eth充值确认交易', 'SYSTEM', 'scanEthChainTask.updatePending', '0/50 * * * * ?', '1', '1', '1', 'admin', '2025-11-03 16:38:50', 'admin5566', '2025-11-23 18:20:49', '');
INSERT INTO `sys_job` VALUES (107, '处理需要归集的地址', 'SYSTEM', 'chainCollectionTask.queryAddress()', '0 0 3 * * ?', '1', '1', '1', 'admin', '2025-11-10 21:24:34', '', '2025-11-21 18:25:48', '每天凌晨3点执行一次');
INSERT INTO `sys_job` VALUES (108, '发送手续费', 'SYSTEM', 'chainCollectionTask.sendGas()', '0 10,45 3 * * ?', '1', '1', '1', 'admin', '2025-11-10 23:13:11', 'admin5566', '2025-11-23 18:21:47', '3:10 和 3:45 执行两次');
INSERT INTO `sys_job` VALUES (109, '确认手续费交易状态', 'SYSTEM', 'chainCollectionTask.checkGasStatus()', '0/40 0-59 3-4 * * ?', '1', '1', '1', 'admin', '2025-11-11 16:22:12', 'admin5566', '2025-11-23 18:22:02', '凌晨3点-5点。30s 执行一次');
INSERT INTO `sys_job` VALUES (110, '开始归集', 'SYSTEM', 'chainCollectionTask.collectAmount()', '0 0/10 3-6 * * ?', '1', '1', '1', 'admin', '2025-11-13 15:59:19', 'admin5566', '2025-11-23 18:22:56', '3点-6点。10分钟执行一次');
INSERT INTO `sys_job` VALUES (111, '检测归集状态', 'SYSTEM', 'chainCollectionTask.checkTxStatus()', '0/50 0-59 4-7 * * ?', '1', '1', '1', 'admin', '2025-11-13 16:00:16', 'admin5566', '2025-11-23 18:25:53', '');
INSERT INTO `sys_job` VALUES (112, '提交提现', 'SYSTEM', 'chainWithdrawTask.sendTx()', '0 0/2 * * * ?', '1', '1', '1', 'admin', '2025-11-13 16:01:00', 'admin5566', '2025-11-23 18:23:56', '');
INSERT INTO `sys_job` VALUES (113, '确认提现状态', 'SYSTEM', 'chainWithdrawTask.checkConfirmed()', '0/50 * * * * ?', '1', '1', '1', 'admin', '2025-11-13 16:01:36', 'admin5566', '2025-11-23 18:24:05', '');
INSERT INTO `sys_job` VALUES (114, '提现回调', 'SYSTEM', 'chainWithdrawTask.callBack()', '0/30 * * * * ?', '1', '1', '1', 'admin', '2025-11-13 16:02:10', '', NULL, '');
COMMIT;

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务调度日志表';

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `login_name` varchar(50) DEFAULT '' COMMENT '登录账号',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_sys_logininfor_s` (`status`),
  KEY `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4 COMMENT='系统访问记录';

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
BEGIN;
INSERT INTO `sys_logininfor` VALUES (100, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Mac OS X', '0', '退出成功', '2025-11-23 17:42:30');
INSERT INTO `sys_logininfor` VALUES (101, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Mac OS X', '1', '验证码错误', '2025-11-23 17:42:44');
INSERT INTO `sys_logininfor` VALUES (102, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Mac OS X', '1', '密码输入错误1次', '2025-11-23 17:42:47');
INSERT INTO `sys_logininfor` VALUES (103, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Mac OS X', '0', '登录成功', '2025-11-23 17:42:55');
INSERT INTO `sys_logininfor` VALUES (104, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Mac OS X', '0', '退出成功', '2025-11-23 17:44:05');
INSERT INTO `sys_logininfor` VALUES (105, 'admin5566', '127.0.0.1', '内网IP', 'Chrome 13', 'Mac OS X', '1', '密码输入错误1次', '2025-11-23 17:44:19');
INSERT INTO `sys_logininfor` VALUES (106, 'admin5566', '127.0.0.1', '内网IP', 'Chrome 13', 'Mac OS X', '1', '密码输入错误2次', '2025-11-23 17:50:10');
INSERT INTO `sys_logininfor` VALUES (107, 'admin5566', '127.0.0.1', '内网IP', 'Chrome 13', 'Mac OS X', '0', '登录成功', '2025-11-23 17:50:20');
COMMIT;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `url` varchar(200) DEFAULT '#' COMMENT '请求地址',
  `target` varchar(20) DEFAULT '' COMMENT '打开方式（menuItem页签 menuBlank新窗口）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `is_refresh` char(1) DEFAULT '1' COMMENT '是否刷新（0刷新 1不刷新）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2062 DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 10, '#', '', 'M', '0', '1', '', 'fa fa-gear', 'admin', '2025-10-27 11:21:37', '', NULL, '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 11, '#', '', 'M', '0', '1', '', 'fa fa-video-camera', 'admin', '2025-10-27 11:21:37', '', NULL, '系统监控目录');
INSERT INTO `sys_menu` VALUES (3, '系统工具', 0, 12, '#', '', 'M', '0', '1', '', 'fa fa-bars', 'admin', '2025-10-27 11:21:37', '', NULL, '系统工具目录');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, '/system/user', '', 'C', '0', '1', 'system:user:view', 'fa fa-user-o', 'admin', '2025-10-27 11:21:37', '', NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, '/system/role', '', 'C', '0', '1', 'system:role:view', 'fa fa-user-secret', 'admin', '2025-10-27 11:21:37', '', NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, '/system/menu', '', 'C', '0', '1', 'system:menu:view', 'fa fa-th-list', 'admin', '2025-10-27 11:21:37', '', NULL, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (103, '部门管理', 1, 4, '/system/dept', '', 'C', '0', '1', 'system:dept:view', 'fa fa-outdent', 'admin', '2025-10-27 11:21:37', '', NULL, '部门管理菜单');
INSERT INTO `sys_menu` VALUES (104, '岗位管理', 1, 5, '/system/post', '', 'C', '0', '1', 'system:post:view', 'fa fa-address-card-o', 'admin', '2025-10-27 11:21:37', '', NULL, '岗位管理菜单');
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, '/system/dict', '', 'C', '0', '1', 'system:dict:view', 'fa fa-bookmark-o', 'admin', '2025-10-27 11:21:37', '', NULL, '字典管理菜单');
INSERT INTO `sys_menu` VALUES (106, '参数设置', 1, 7, '/system/config', '', 'C', '0', '1', 'system:config:view', 'fa fa-sun-o', 'admin', '2025-10-27 11:21:37', '', NULL, '参数设置菜单');
INSERT INTO `sys_menu` VALUES (107, '通知公告', 1, 8, '/system/notice', '', 'C', '0', '1', 'system:notice:view', 'fa fa-bullhorn', 'admin', '2025-10-27 11:21:37', '', NULL, '通知公告菜单');
INSERT INTO `sys_menu` VALUES (108, '日志管理', 1, 9, '#', '', 'M', '0', '1', '', 'fa fa-pencil-square-o', 'admin', '2025-10-27 11:21:37', '', NULL, '日志管理菜单');
INSERT INTO `sys_menu` VALUES (109, '在线用户', 2, 1, '/monitor/online', '', 'C', '0', '1', 'monitor:online:view', 'fa fa-user-circle', 'admin', '2025-10-27 11:21:37', '', NULL, '在线用户菜单');
INSERT INTO `sys_menu` VALUES (110, '定时任务', 2, 2, '/monitor/job', '', 'C', '0', '1', 'monitor:job:view', 'fa fa-tasks', 'admin', '2025-10-27 11:21:37', '', NULL, '定时任务菜单');
INSERT INTO `sys_menu` VALUES (111, '数据监控', 2, 3, '/monitor/data', '', 'C', '0', '1', 'monitor:data:view', 'fa fa-bug', 'admin', '2025-10-27 11:21:37', '', NULL, '数据监控菜单');
INSERT INTO `sys_menu` VALUES (112, '服务监控', 2, 4, '/monitor/server', '', 'C', '0', '1', 'monitor:server:view', 'fa fa-server', 'admin', '2025-10-27 11:21:37', '', NULL, '服务监控菜单');
INSERT INTO `sys_menu` VALUES (113, '缓存监控', 2, 5, '/monitor/cache', '', 'C', '0', '1', 'monitor:cache:view', 'fa fa-cube', 'admin', '2025-10-27 11:21:37', '', NULL, '缓存监控菜单');
INSERT INTO `sys_menu` VALUES (114, '表单构建', 3, 1, '/tool/build', '', 'C', '0', '1', 'tool:build:view', 'fa fa-wpforms', 'admin', '2025-10-27 11:21:37', '', NULL, '表单构建菜单');
INSERT INTO `sys_menu` VALUES (115, '代码生成', 3, 2, '/tool/gen', '', 'C', '0', '1', 'tool:gen:view', 'fa fa-code', 'admin', '2025-10-27 11:21:37', '', NULL, '代码生成菜单');
INSERT INTO `sys_menu` VALUES (116, '系统接口', 3, 3, '/tool/swagger', '', 'C', '0', '1', 'tool:swagger:view', 'fa fa-gg', 'admin', '2025-10-27 11:21:37', '', NULL, '系统接口菜单');
INSERT INTO `sys_menu` VALUES (500, '操作日志', 108, 1, '/monitor/operlog', '', 'C', '0', '1', 'monitor:operlog:view', 'fa fa-address-book', 'admin', '2025-10-27 11:21:37', '', NULL, '操作日志菜单');
INSERT INTO `sys_menu` VALUES (501, '登录日志', 108, 2, '/monitor/logininfor', '', 'C', '0', '1', 'monitor:logininfor:view', 'fa fa-file-image-o', 'admin', '2025-10-27 11:21:37', '', NULL, '登录日志菜单');
INSERT INTO `sys_menu` VALUES (1000, '用户查询', 100, 1, '#', '', 'F', '0', '1', 'system:user:list', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1001, '用户新增', 100, 2, '#', '', 'F', '0', '1', 'system:user:add', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1002, '用户修改', 100, 3, '#', '', 'F', '0', '1', 'system:user:edit', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1003, '用户删除', 100, 4, '#', '', 'F', '0', '1', 'system:user:remove', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1004, '用户导出', 100, 5, '#', '', 'F', '0', '1', 'system:user:export', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1005, '用户导入', 100, 6, '#', '', 'F', '0', '1', 'system:user:import', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1006, '重置密码', 100, 7, '#', '', 'F', '0', '1', 'system:user:resetPwd', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1007, '角色查询', 101, 1, '#', '', 'F', '0', '1', 'system:role:list', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1008, '角色新增', 101, 2, '#', '', 'F', '0', '1', 'system:role:add', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1009, '角色修改', 101, 3, '#', '', 'F', '0', '1', 'system:role:edit', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1010, '角色删除', 101, 4, '#', '', 'F', '0', '1', 'system:role:remove', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1011, '角色导出', 101, 5, '#', '', 'F', '0', '1', 'system:role:export', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1012, '菜单查询', 102, 1, '#', '', 'F', '0', '1', 'system:menu:list', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1013, '菜单新增', 102, 2, '#', '', 'F', '0', '1', 'system:menu:add', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1014, '菜单修改', 102, 3, '#', '', 'F', '0', '1', 'system:menu:edit', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1015, '菜单删除', 102, 4, '#', '', 'F', '0', '1', 'system:menu:remove', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1016, '部门查询', 103, 1, '#', '', 'F', '0', '1', 'system:dept:list', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1017, '部门新增', 103, 2, '#', '', 'F', '0', '1', 'system:dept:add', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1018, '部门修改', 103, 3, '#', '', 'F', '0', '1', 'system:dept:edit', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1019, '部门删除', 103, 4, '#', '', 'F', '0', '1', 'system:dept:remove', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1020, '岗位查询', 104, 1, '#', '', 'F', '0', '1', 'system:post:list', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1021, '岗位新增', 104, 2, '#', '', 'F', '0', '1', 'system:post:add', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1022, '岗位修改', 104, 3, '#', '', 'F', '0', '1', 'system:post:edit', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1023, '岗位删除', 104, 4, '#', '', 'F', '0', '1', 'system:post:remove', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1024, '岗位导出', 104, 5, '#', '', 'F', '0', '1', 'system:post:export', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1025, '字典查询', 105, 1, '#', '', 'F', '0', '1', 'system:dict:list', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1026, '字典新增', 105, 2, '#', '', 'F', '0', '1', 'system:dict:add', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1027, '字典修改', 105, 3, '#', '', 'F', '0', '1', 'system:dict:edit', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1028, '字典删除', 105, 4, '#', '', 'F', '0', '1', 'system:dict:remove', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1029, '字典导出', 105, 5, '#', '', 'F', '0', '1', 'system:dict:export', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1030, '参数查询', 106, 1, '#', '', 'F', '0', '1', 'system:config:list', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1031, '参数新增', 106, 2, '#', '', 'F', '0', '1', 'system:config:add', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1032, '参数修改', 106, 3, '#', '', 'F', '0', '1', 'system:config:edit', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1033, '参数删除', 106, 4, '#', '', 'F', '0', '1', 'system:config:remove', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1034, '参数导出', 106, 5, '#', '', 'F', '0', '1', 'system:config:export', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1035, '公告查询', 107, 1, '#', '', 'F', '0', '1', 'system:notice:list', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1036, '公告新增', 107, 2, '#', '', 'F', '0', '1', 'system:notice:add', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1037, '公告修改', 107, 3, '#', '', 'F', '0', '1', 'system:notice:edit', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1038, '公告删除', 107, 4, '#', '', 'F', '0', '1', 'system:notice:remove', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1039, '操作查询', 500, 1, '#', '', 'F', '0', '1', 'monitor:operlog:list', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1040, '操作删除', 500, 2, '#', '', 'F', '0', '1', 'monitor:operlog:remove', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1041, '详细信息', 500, 3, '#', '', 'F', '0', '1', 'monitor:operlog:detail', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1042, '日志导出', 500, 4, '#', '', 'F', '0', '1', 'monitor:operlog:export', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1043, '登录查询', 501, 1, '#', '', 'F', '0', '1', 'monitor:logininfor:list', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1044, '登录删除', 501, 2, '#', '', 'F', '0', '1', 'monitor:logininfor:remove', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1045, '日志导出', 501, 3, '#', '', 'F', '0', '1', 'monitor:logininfor:export', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1046, '账户解锁', 501, 4, '#', '', 'F', '0', '1', 'monitor:logininfor:unlock', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1047, '在线查询', 109, 1, '#', '', 'F', '0', '1', 'monitor:online:list', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1048, '批量强退', 109, 2, '#', '', 'F', '0', '1', 'monitor:online:batchForceLogout', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1049, '单条强退', 109, 3, '#', '', 'F', '0', '1', 'monitor:online:forceLogout', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1050, '任务查询', 110, 1, '#', '', 'F', '0', '1', 'monitor:job:list', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1051, '任务新增', 110, 2, '#', '', 'F', '0', '1', 'monitor:job:add', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1052, '任务修改', 110, 3, '#', '', 'F', '0', '1', 'monitor:job:edit', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1053, '任务删除', 110, 4, '#', '', 'F', '0', '1', 'monitor:job:remove', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1054, '状态修改', 110, 5, '#', '', 'F', '0', '1', 'monitor:job:changeStatus', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1055, '任务详细', 110, 6, '#', '', 'F', '0', '1', 'monitor:job:detail', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1056, '任务导出', 110, 7, '#', '', 'F', '0', '1', 'monitor:job:export', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1057, '生成查询', 115, 1, '#', '', 'F', '0', '1', 'tool:gen:list', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1058, '生成修改', 115, 2, '#', '', 'F', '0', '1', 'tool:gen:edit', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1059, '生成删除', 115, 3, '#', '', 'F', '0', '1', 'tool:gen:remove', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1060, '预览代码', 115, 4, '#', '', 'F', '0', '1', 'tool:gen:preview', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1061, '生成代码', 115, 5, '#', '', 'F', '0', '1', 'tool:gen:code', '#', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2000, '链上支付中心', 0, 1, '#', 'menuItem', 'M', '0', '1', NULL, 'fa fa-cubes', 'admin', '2025-10-27 15:56:12', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2001, '下游平台', 2000, 1, '/platforms/platform', '', 'C', '0', '1', 'platforms:platform:view', '#', 'admin', '2025-10-27 15:59:02', '', NULL, '下游平台菜单');
INSERT INTO `sys_menu` VALUES (2002, '平台查询', 2001, 1, '#', '', 'F', '0', '1', 'platforms:platform:list', '#', 'admin', '2025-10-27 15:59:02', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2003, '平台新增', 2001, 2, '#', '', 'F', '0', '1', 'platforms:platform:add', '#', 'admin', '2025-10-27 15:59:02', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2004, '平台修改', 2001, 3, '#', '', 'F', '0', '1', 'platforms:platform:edit', '#', 'admin', '2025-10-27 15:59:02', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2005, '平台删除', 2001, 4, '#', '', 'F', '0', '1', 'platforms:platform:remove', '#', 'admin', '2025-10-27 15:59:02', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2006, '用户地址', 2000, 2, '/platforms/addresses', '', 'C', '0', '1', 'platforms:addresses:view', '#', 'admin', '2025-10-27 17:23:14', '', NULL, '用户地址菜单');
INSERT INTO `sys_menu` VALUES (2007, '查询', 2006, 1, '#', '', 'F', '0', '1', 'platforms:addresses:list', '#', 'admin', '2025-10-27 17:23:14', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2008, '新增', 2006, 2, '#', '', 'F', '0', '1', 'platforms:addresses:add', '#', 'admin', '2025-10-27 17:23:14', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2009, '修改', 2006, 3, '#', '', 'F', '0', '1', 'platforms:addresses:edit', '#', 'admin', '2025-10-27 17:23:14', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2010, '删除', 2006, 4, '#', '', 'F', '0', '1', 'platforms:addresses:remove', '#', 'admin', '2025-10-27 17:23:14', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2011, '导出', 2006, 5, '#', '', 'F', '0', '1', 'platforms:addresses:export', '#', 'admin', '2025-10-27 17:23:14', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2015, '临时地址', 2000, 4, '/platforms/pool', '', 'C', '0', '1', 'platforms:pool:view', '#', 'admin', '2025-10-28 12:59:31', '', NULL, '临时地址菜单');
INSERT INTO `sys_menu` VALUES (2016, '查询', 2015, 1, '#', '', 'F', '0', '1', 'platforms:pool:list', '#', 'admin', '2025-10-28 12:59:31', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2017, '删除', 2015, 2, '#', '', 'F', '0', '1', 'platforms:pool:remove', '#', 'admin', '2025-10-28 12:59:31', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2018, '区块配置', 0, 2, '#', 'menuItem', 'M', '0', '1', NULL, 'fa fa-cog', 'admin', '2025-10-29 13:40:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2019, '币种配置', 2018, 1, '/config/tokens', '', 'C', '0', '1', 'config:tokens:view', '#', 'admin', '2025-10-29 14:16:46', '', NULL, '币种配置菜单');
INSERT INTO `sys_menu` VALUES (2020, '币种配置查询', 2019, 1, '#', '', 'F', '0', '1', 'config:tokens:list', '#', 'admin', '2025-10-29 14:16:46', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2021, '币种配置新增', 2019, 2, '#', '', 'F', '0', '1', 'config:tokens:add', '#', 'admin', '2025-10-29 14:16:46', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2022, '币种配置修改', 2019, 3, '#', '', 'F', '0', '1', 'config:tokens:edit', '#', 'admin', '2025-10-29 14:16:46', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2023, '币种配置删除', 2019, 4, '#', '', 'F', '0', '1', 'config:tokens:remove', '#', 'admin', '2025-10-29 14:16:46', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2025, '区块API配置', 2018, 2, '/config/apiKey', '', 'C', '0', '1', 'config:apiKey:view', '#', 'admin', '2025-10-29 14:17:26', '', NULL, '区块API配置菜单');
INSERT INTO `sys_menu` VALUES (2026, '区块API配置查询', 2025, 1, '#', '', 'F', '0', '1', 'config:apiKey:list', '#', 'admin', '2025-10-29 14:17:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2027, '区块API配置新增', 2025, 2, '#', '', 'F', '0', '1', 'config:apiKey:add', '#', 'admin', '2025-10-29 14:17:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2028, '区块API配置修改', 2025, 3, '#', '', 'F', '0', '1', 'config:apiKey:edit', '#', 'admin', '2025-10-29 14:17:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2029, '区块API配置删除', 2025, 4, '#', '', 'F', '0', '1', 'config:apiKey:remove', '#', 'admin', '2025-10-29 14:17:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2030, '交易记录', 0, 4, '#', 'menuItem', 'M', '0', '1', NULL, 'fa fa-bank', 'admin', '2025-10-30 19:59:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2031, '充值记录', 2030, 1, '/trade/recharge', 'menuItem', 'C', '0', '1', 'trade:recharge:view', '#', 'admin', '2025-10-30 20:01:17', 'admin', '2025-11-12 13:48:49', '地址充值记录菜单');
INSERT INTO `sys_menu` VALUES (2032, '地址充值记录查询', 2031, 1, '#', '', 'F', '0', '1', 'trade:recharge:list', '#', 'admin', '2025-10-30 20:01:17', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2033, '地址充值记录新增', 2031, 2, '#', '', 'F', '0', '1', 'trade:recharge:add', '#', 'admin', '2025-10-30 20:01:17', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2034, '地址充值记录修改', 2031, 3, '#', '', 'F', '0', '1', 'trade:recharge:edit', '#', 'admin', '2025-10-30 20:01:17', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2035, '地址充值记录删除', 2031, 4, '#', '', 'F', '0', '1', 'trade:recharge:remove', '#', 'admin', '2025-10-30 20:01:17', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2036, '地址充值记录导出', 2031, 5, '#', '', 'F', '0', '1', 'trade:recharge:export', '#', 'admin', '2025-10-30 20:01:17', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2037, '地址配置', 0, 3, '#', 'menuItem', 'M', '0', '1', NULL, 'fa fa-address-book', 'admin', '2025-11-09 13:23:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2038, '归集记录', 2030, 2, '/trade/collection', '', 'C', '0', '1', 'trade:collection:view', '#', 'admin', '2025-11-09 13:40:21', '', NULL, '归集记录菜单');
INSERT INTO `sys_menu` VALUES (2039, '归集记录查询', 2038, 1, '#', '', 'F', '0', '1', 'trade:collection:list', '#', 'admin', '2025-11-09 13:40:21', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2040, '归集记录新增', 2038, 2, '#', '', 'F', '0', '1', 'trade:collection:add', '#', 'admin', '2025-11-09 13:40:21', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2041, '归集记录修改', 2038, 3, '#', '', 'F', '0', '1', 'trade:collection:edit', '#', 'admin', '2025-11-09 13:40:21', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2042, '归集记录删除', 2038, 4, '#', '', 'F', '0', '1', 'trade:collection:remove', '#', 'admin', '2025-11-09 13:40:21', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2043, '归集记录导出', 2038, 5, '#', '', 'F', '0', '1', 'trade:collection:export', '#', 'admin', '2025-11-09 13:40:21', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2044, '归集配置', 2037, 1, '/collection/config', '', 'C', '0', '1', 'collection:config:view', '#', 'admin', '2025-11-09 13:40:43', '', NULL, '归集配置菜单');
INSERT INTO `sys_menu` VALUES (2045, '归集配置查询', 2044, 1, '#', '', 'F', '0', '1', 'collection:config:list', '#', 'admin', '2025-11-09 13:40:44', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2046, '归集配置新增', 2044, 2, '#', '', 'F', '0', '1', 'collection:config:add', '#', 'admin', '2025-11-09 13:40:44', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2047, '归集配置修改', 2044, 3, '#', '', 'F', '0', '1', 'collection:config:edit', '#', 'admin', '2025-11-09 13:40:44', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2048, '归集配置删除', 2044, 4, '#', '', 'F', '0', '1', 'collection:config:remove', '#', 'admin', '2025-11-09 13:40:44', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2049, '归集配置导出', 2044, 5, '#', '', 'F', '0', '1', 'collection:config:export', '#', 'admin', '2025-11-09 13:40:44', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2050, '付款地址', 2037, 2, '/collection/sourceAddress', '', 'C', '0', '1', 'collection:sourceAddress:view', '#', 'admin', '2025-11-09 13:40:59', '', NULL, '付款地址菜单');
INSERT INTO `sys_menu` VALUES (2051, '付款地址查询', 2050, 1, '#', '', 'F', '0', '1', 'collection:sourceAddress:list', '#', 'admin', '2025-11-09 13:40:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2052, '付款地址新增', 2050, 2, '#', '', 'F', '0', '1', 'collection:sourceAddress:add', '#', 'admin', '2025-11-09 13:40:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2053, '付款地址修改', 2050, 3, '#', '', 'F', '0', '1', 'collection:sourceAddress:edit', '#', 'admin', '2025-11-09 13:40:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2054, '付款地址删除', 2050, 4, '#', '', 'F', '0', '1', 'collection:sourceAddress:remove', '#', 'admin', '2025-11-09 13:40:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2055, '付款地址导出', 2050, 5, '#', '', 'F', '0', '1', 'collection:sourceAddress:export', '#', 'admin', '2025-11-09 13:40:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2056, '提现记录', 2030, 3, '/trade/withdraw', '', 'C', '0', '1', 'trade:withdraw:view', '#', 'admin', '2025-11-12 14:07:29', '', NULL, '区块链提现记录菜单');
INSERT INTO `sys_menu` VALUES (2057, '查询', 2056, 1, '#', '', 'F', '0', '1', 'trade:withdraw:list', '#', 'admin', '2025-11-12 14:07:29', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2058, '新增', 2056, 2, '#', '', 'F', '0', '1', 'trade:withdraw:add', '#', 'admin', '2025-11-12 14:07:29', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2059, '修改', 2056, 3, '#', '', 'F', '0', '1', 'trade:withdraw:edit', '#', 'admin', '2025-11-12 14:07:29', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2060, '删除', 2056, 4, '#', '', 'F', '0', '1', 'trade:withdraw:remove', '#', 'admin', '2025-11-12 14:07:29', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2061, '导出', 2056, 5, '#', '', 'F', '0', '1', 'trade:withdraw:export', '#', 'admin', '2025-11-12 14:07:29', '', NULL, '');
COMMIT;

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `notice_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
BEGIN;
INSERT INTO `sys_notice` VALUES (1, '温馨提醒：2018-07-01 新版本发布啦', '2', 0xE696B0E78988E69CACE58685E5AEB9, '0', 'admin', '2025-10-27 11:21:38', '', NULL, '管理员');
INSERT INTO `sys_notice` VALUES (2, '维护通知：2018-07-01 系统凌晨维护', '1', 0xE7BBB4E68AA4E58685E5AEB9, '0', 'admin', '2025-10-27 11:21:38', '', NULL, '管理员');
COMMIT;

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int(1) DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int(1) DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint(20) DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_bt` (`business_type`),
  KEY `idx_sys_oper_log_s` (`status`),
  KEY `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录';

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='岗位信息表';

-- ----------------------------
-- Records of sys_post
-- ----------------------------
BEGIN;
INSERT INTO `sys_post` VALUES (1, 'ceo', '董事长', 1, '0', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_post` VALUES (2, 'se', '项目经理', 2, '0', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_post` VALUES (3, 'hr', '人力资源', 3, '0', 'admin', '2025-10-27 11:21:37', '', NULL, '');
INSERT INTO `sys_post` VALUES (4, 'user', '普通员工', 4, '0', 'admin', '2025-10-27 11:21:37', '', NULL, '');
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', '0', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '2', '0', '0', 'admin', '2025-10-27 11:21:37', '', NULL, '普通角色');
COMMIT;

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和部门关联表';

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_dept` VALUES (2, 100);
INSERT INTO `sys_role_dept` VALUES (2, 101);
INSERT INTO `sys_role_dept` VALUES (2, 105);
COMMIT;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_menu` VALUES (2, 1);
INSERT INTO `sys_role_menu` VALUES (2, 2);
INSERT INTO `sys_role_menu` VALUES (2, 3);
INSERT INTO `sys_role_menu` VALUES (2, 4);
INSERT INTO `sys_role_menu` VALUES (2, 100);
INSERT INTO `sys_role_menu` VALUES (2, 101);
INSERT INTO `sys_role_menu` VALUES (2, 102);
INSERT INTO `sys_role_menu` VALUES (2, 103);
INSERT INTO `sys_role_menu` VALUES (2, 104);
INSERT INTO `sys_role_menu` VALUES (2, 105);
INSERT INTO `sys_role_menu` VALUES (2, 106);
INSERT INTO `sys_role_menu` VALUES (2, 107);
INSERT INTO `sys_role_menu` VALUES (2, 108);
INSERT INTO `sys_role_menu` VALUES (2, 109);
INSERT INTO `sys_role_menu` VALUES (2, 110);
INSERT INTO `sys_role_menu` VALUES (2, 111);
INSERT INTO `sys_role_menu` VALUES (2, 112);
INSERT INTO `sys_role_menu` VALUES (2, 113);
INSERT INTO `sys_role_menu` VALUES (2, 114);
INSERT INTO `sys_role_menu` VALUES (2, 115);
INSERT INTO `sys_role_menu` VALUES (2, 116);
INSERT INTO `sys_role_menu` VALUES (2, 500);
INSERT INTO `sys_role_menu` VALUES (2, 501);
INSERT INTO `sys_role_menu` VALUES (2, 1000);
INSERT INTO `sys_role_menu` VALUES (2, 1001);
INSERT INTO `sys_role_menu` VALUES (2, 1002);
INSERT INTO `sys_role_menu` VALUES (2, 1003);
INSERT INTO `sys_role_menu` VALUES (2, 1004);
INSERT INTO `sys_role_menu` VALUES (2, 1005);
INSERT INTO `sys_role_menu` VALUES (2, 1006);
INSERT INTO `sys_role_menu` VALUES (2, 1007);
INSERT INTO `sys_role_menu` VALUES (2, 1008);
INSERT INTO `sys_role_menu` VALUES (2, 1009);
INSERT INTO `sys_role_menu` VALUES (2, 1010);
INSERT INTO `sys_role_menu` VALUES (2, 1011);
INSERT INTO `sys_role_menu` VALUES (2, 1012);
INSERT INTO `sys_role_menu` VALUES (2, 1013);
INSERT INTO `sys_role_menu` VALUES (2, 1014);
INSERT INTO `sys_role_menu` VALUES (2, 1015);
INSERT INTO `sys_role_menu` VALUES (2, 1016);
INSERT INTO `sys_role_menu` VALUES (2, 1017);
INSERT INTO `sys_role_menu` VALUES (2, 1018);
INSERT INTO `sys_role_menu` VALUES (2, 1019);
INSERT INTO `sys_role_menu` VALUES (2, 1020);
INSERT INTO `sys_role_menu` VALUES (2, 1021);
INSERT INTO `sys_role_menu` VALUES (2, 1022);
INSERT INTO `sys_role_menu` VALUES (2, 1023);
INSERT INTO `sys_role_menu` VALUES (2, 1024);
INSERT INTO `sys_role_menu` VALUES (2, 1025);
INSERT INTO `sys_role_menu` VALUES (2, 1026);
INSERT INTO `sys_role_menu` VALUES (2, 1027);
INSERT INTO `sys_role_menu` VALUES (2, 1028);
INSERT INTO `sys_role_menu` VALUES (2, 1029);
INSERT INTO `sys_role_menu` VALUES (2, 1030);
INSERT INTO `sys_role_menu` VALUES (2, 1031);
INSERT INTO `sys_role_menu` VALUES (2, 1032);
INSERT INTO `sys_role_menu` VALUES (2, 1033);
INSERT INTO `sys_role_menu` VALUES (2, 1034);
INSERT INTO `sys_role_menu` VALUES (2, 1035);
INSERT INTO `sys_role_menu` VALUES (2, 1036);
INSERT INTO `sys_role_menu` VALUES (2, 1037);
INSERT INTO `sys_role_menu` VALUES (2, 1038);
INSERT INTO `sys_role_menu` VALUES (2, 1039);
INSERT INTO `sys_role_menu` VALUES (2, 1040);
INSERT INTO `sys_role_menu` VALUES (2, 1041);
INSERT INTO `sys_role_menu` VALUES (2, 1042);
INSERT INTO `sys_role_menu` VALUES (2, 1043);
INSERT INTO `sys_role_menu` VALUES (2, 1044);
INSERT INTO `sys_role_menu` VALUES (2, 1045);
INSERT INTO `sys_role_menu` VALUES (2, 1046);
INSERT INTO `sys_role_menu` VALUES (2, 1047);
INSERT INTO `sys_role_menu` VALUES (2, 1048);
INSERT INTO `sys_role_menu` VALUES (2, 1049);
INSERT INTO `sys_role_menu` VALUES (2, 1050);
INSERT INTO `sys_role_menu` VALUES (2, 1051);
INSERT INTO `sys_role_menu` VALUES (2, 1052);
INSERT INTO `sys_role_menu` VALUES (2, 1053);
INSERT INTO `sys_role_menu` VALUES (2, 1054);
INSERT INTO `sys_role_menu` VALUES (2, 1055);
INSERT INTO `sys_role_menu` VALUES (2, 1056);
INSERT INTO `sys_role_menu` VALUES (2, 1057);
INSERT INTO `sys_role_menu` VALUES (2, 1058);
INSERT INTO `sys_role_menu` VALUES (2, 1059);
INSERT INTO `sys_role_menu` VALUES (2, 1060);
INSERT INTO `sys_role_menu` VALUES (2, 1061);
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `login_name` varchar(30) NOT NULL COMMENT '登录账号',
  `user_name` varchar(30) DEFAULT '' COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户 01注册用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像路径',
  `password` varchar(50) DEFAULT '' COMMENT '密码',
  `salt` varchar(20) DEFAULT '' COMMENT '盐加密',
  `status` char(1) DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` VALUES (1, 103, 'admin5566', '管理员', '00', 'admin@163.com', '15888888888', '1', '', '8e2ee44b2983320876c61b9b09f95c84', 'heydk', '0', '0', '127.0.0.1', '2025-11-23 17:50:20', NULL, 'admin', '2025-10-27 11:21:37', '', NULL, '管理员');
COMMIT;

-- ----------------------------
-- Table structure for sys_user_online
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_online`;
CREATE TABLE `sys_user_online` (
  `sessionId` varchar(50) NOT NULL DEFAULT '' COMMENT '用户会话id',
  `login_name` varchar(50) DEFAULT '' COMMENT '登录账号',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` varchar(10) DEFAULT '' COMMENT '在线状态on_line在线off_line离线',
  `start_timestamp` datetime DEFAULT NULL COMMENT 'session创建时间',
  `last_access_time` datetime DEFAULT NULL COMMENT 'session最后访问时间',
  `expire_time` int(5) DEFAULT '0' COMMENT '超时时间，单位为分钟',
  PRIMARY KEY (`sessionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='在线用户记录';

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与岗位关联表';

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_post` VALUES (1, 1);
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` VALUES (1, 1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
