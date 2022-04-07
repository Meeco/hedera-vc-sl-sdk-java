package com.hedera.hashgraph.identity.hfs.vc.sl.sdk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.BitSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StatusList {
    public final static int DEFAULT_LIST_BIT_SIZE = 100_032;

    private final BitSet bitset;
    /**
     * INFO: BitSet size has to divide by 64 otherwise java will increase the size to match this criteria
     */
    private final int size;

    public StatusList() {
        this.bitset = new BitSet(StatusList.DEFAULT_LIST_BIT_SIZE);
        this.size = this.bitset.size();
    }

    public StatusList(int number) {
        this.bitset = new BitSet(number);
        this.size = this.bitset.size();
    }

    public StatusList(BitSet bitset) {
        this.bitset = bitset;
        this.size = this.bitset.size();
    }

    public BitSet getBitset() {
        return bitset;
    }

    public int getSize() {
        return size;
    }

    public void setRevoked(int index, boolean revoked) {
        this.bitset.set(index, revoked);
    }

    public boolean isRevoked(int index) {
        return this.bitset.get(index);
    }

    public String encode() throws IOException {
        byte[] bitsetAsByteArray = this.bitset.toByteArray();
        byte[] byteArrayExtended = new byte[(int) Math.ceil(this.getSize() / 8)];
        System.arraycopy(bitsetAsByteArray, 0, byteArrayExtended, 0, bitsetAsByteArray.length);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(byteArrayExtended);
        gzip.close();

        return Base64.getUrlEncoder().encodeToString(bos.toByteArray());
    }

    public static StatusList decodeList(String encodedList) throws IOException {
        byte[] compressedBitset = Base64.getUrlDecoder().decode(encodedList);

        ByteArrayInputStream bos = new ByteArrayInputStream(compressedBitset);
        GZIPInputStream gis = new GZIPInputStream(bos);

        byte[] bitsetBytes = gis.readAllBytes();
        gis.close();

        BitSet resultBitset = new BitSet(bitsetBytes.length * 8);
        BitSet optimizedBitset = BitSet.valueOf(bitsetBytes);

        resultBitset.or(optimizedBitset);

        return new StatusList(resultBitset);
    }

}
