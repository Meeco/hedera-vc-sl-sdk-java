package com.hedera.hashgraph.identity.hfs.vc.sl.sdk;

public enum VcSlStatus {
    ACTIVE(0),
    RESUMED(1),
    SUSPENDED(2),
    REVOKED(3);

    private final int value;

    VcSlStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
