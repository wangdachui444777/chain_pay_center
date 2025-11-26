package com.ruoyi.bc.domain;

public class BlockchainAddress {
    private final String chainType;
    private final String address;
    private final String privateKeyHex;

    public BlockchainAddress(String chainType, String address, String privateKeyHex) {
        this.chainType = chainType;
        this.address = address;
        this.privateKeyHex = privateKeyHex;
    }

    public String getChainType() { return chainType; }
    public String getAddress() { return address; }
    public String getPrivateKeyHex() { return privateKeyHex; }

    @Override
    public String toString() {
        return "BlockchainAddress{" +
                "chainType='" + chainType + '\'' +
                ", address='" + address + '\'' +
                ", privateKeyHex='" + privateKeyHex + '\'' +
                '}';
    }
}
