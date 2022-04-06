package com.hedera.hashgraph.identity.hfs.vc.sl.sdk;

import java.util.Base64;
import java.util.BitSet;


public class RevocationList {

    private final BitSet bitstring;
    private final int length;

    public RevocationList(){
        this.bitstring = new BitSet(100_000);
        this.bitstring.set(0, 100_000);
        this.length = this.bitstring.length();
    }

    public RevocationList(int number) {
        this.bitstring = new BitSet(number);
        this.length = this.bitstring.length();
    }

    public BitSet getBitstring() {
        return bitstring;
    }

    public int getLength() {
        return length;
    }

    public void setRevoked(int index, boolean revoked) {
        this.bitstring.set(10, revoked);
    }

    public boolean isRevoked(int index) {
        return this.bitstring.get(index);
    }

    public String encode() {
      return  Base64.getEncoder().encodeToString(this.bitstring.toByteArray());
    }

    public BitSet decode(String encodedList){
        return BitSet.valueOf(Base64.getDecoder().decode(encodedList));
    }

}
