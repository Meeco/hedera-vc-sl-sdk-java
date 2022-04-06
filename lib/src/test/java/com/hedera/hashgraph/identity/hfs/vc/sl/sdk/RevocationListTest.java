package com.hedera.hashgraph.identity.hfs.vc.sl.sdk;

import com.google.common.primitives.Longs;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.BitSet;
import java.util.zip.GZIPOutputStream;

@Tag("unit")
public class RevocationListTest {

    @Test
    void createList() throws IOException {
//       var rl = new RevocationList();
//        System.out.println("printing RL encoded");
//       System.out.println(rl.encode());

        var bitSet = new BitSet(100_000);
//        bitSet.set(0,2);
        var bitSetByteArray = bitSet.toByteArray();
        var bitSetByteArrayExtended = new byte[12500];
        System.arraycopy(bitSetByteArray, 0, bitSetByteArrayExtended, 0, bitSetByteArray.length);


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(bitSetByteArrayExtended);
        gzip.close();
        byte[] compressed = bos.toByteArray();
        bos.close();

        var result = Base64.getUrlEncoder().encodeToString(compressed);
        System.out.println(result);


    }


    @Test
    void test(){
        var result = Base64.getUrlDecoder().decode("H4sIAAAAAAAAA-3BMQEAAADCoPVPbQsvoAAAAAAAAAAAAAAAAP4GcwM92tQwAAA");
        System.out.println(Arrays.toString(result));
    }
}
