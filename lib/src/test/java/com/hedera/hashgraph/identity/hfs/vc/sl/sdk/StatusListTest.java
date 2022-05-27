package com.hedera.hashgraph.identity.hfs.vc.sl.sdk;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
public class StatusListTest {

    @Test
    @DisplayName("Creates new empty status list")
    void itCreatesEmptyStatusList() {
        StatusList list = new StatusList();
        assertEquals(StatusList.DEFAULT_LIST_BIT_SIZE, list.getSize());
    }

    @Test
    @DisplayName("Creates empty list of set size")
    void itCreatesEmptyStatusListOfProvidedSize() {
        StatusList list = new StatusList(128);
        assertEquals(128, list.getSize());
    }

    @Test
    @DisplayName("Creates list from bitset instance")
    void itCreatesStatusListFromProvidedBitSetInstance() {
        BitSet bitset = new BitSet(1024);
        StatusList list = new StatusList(bitset);
        assertEquals(1024, list.getSize());
        assertEquals(bitset, list.getBitset());
    }

    @Test
    @DisplayName("It sets bits to revoked status and later back")
    void itSetsBitsToRevokedStatus() {
        StatusList list = new StatusList();
        int[] indices = new int[]{11, 12, 65432, 65433, 99999};

        for (int index: indices) {
            list.setRevoked(index, true);
        }

        validateBitSetValues(list.getBitset(), indices);

        for (int index: indices) {
            list.setRevoked(index, false);
        }

        validateBitSetValues(list.getBitset(), new int[]{});
    }

    @Test
    @DisplayName("It sets bits to revoked and back. Uses API function to check the status")
    void itSetsBitsToRevokedStatusAndChecksItByUsingInstanceApi() {
        StatusList list = new StatusList();
        int[] indices = new int[]{11, 12, 65432, 65433, 99999};

        for (int index: indices) {
            list.setRevoked(index, true);
        }

        validateStatusListValues(list, indices);

        for (int index: indices) {
            list.setRevoked(index, false);
        }

        validateStatusListValues(list, new int[]{});
    }

    @Test
    @DisplayName("Encodes an empty list")
    void itEncodesAnEmptyList() throws IOException {
        StatusList list = new StatusList();
        String listEncoded = list.encode();
        StatusList decodedList = StatusList.decodeList(listEncoded);

        assertEquals(decodedList.getSize(), list.getSize());
        assertEquals(decodedList.getBitset(), list.getBitset());
    }

    @Test
    @DisplayName("Encodes a list with some bits set to true")
    void itEncodesAListWithBitsSetToTrue() throws IOException {
        StatusList list = new StatusList();
        int[] indices = new int[]{11, 12, 65432, 65433, 99999};
        for (int index: indices) {
            list.setRevoked(index, true);
        }
        String listEncoded = list.encode();
        StatusList decodedList = StatusList.decodeList(listEncoded);

        assertEquals(decodedList.getSize(), list.getSize());
        assertEquals(decodedList.getBitset(), list.getBitset());
    }

    @Test
    @DisplayName("Decodes an empty list")
    void itDecodesAnEmptyList() throws IOException {
        StatusList list = StatusList.decodeList("H4sIAAAAAAAAAO3BMQEAAADCoPVPbQwfoAAAAAAAAAAAAAAAAD4GQx1mVtgwAAA=");
        assertEquals(StatusList.DEFAULT_LIST_BIT_SIZE, list.getSize());
        validateStatusListValues(list, new int[]{});
    }

    @Test
    @DisplayName("Decodes list with some bits updated")
    void itDecodesListWithSomeBitsUpdated() throws IOException {
        StatusList list = StatusList.decodeList("H4sIAAAAAAAAAO3OsQkAMAgAQcElHDejB2ewEOGu-PqjAgAAAAAAAAC4J7cHAAAAAIZe5wNOuM282DAAAA==");
        assertEquals(StatusList.DEFAULT_LIST_BIT_SIZE, list.getSize());
        validateStatusListValues(list, new int[]{11, 12, 65432, 65433, 99999});
    }

    /**
     * Decoding Javascript generated lists and checking if state is the same
     */

    @Test
    @DisplayName("All bits set as revoked generated by javascript version of the lib")
    void itDecodesAndFindsAllBitsRevoked() throws IOException {
        StatusList list = StatusList.decodeList("H4sIAAAAAAAAA-3BMQEAAADCoP6pZwwfoAAAAAAAAAAAAAAAAD4G48MqJdgwAAA");
        assertEquals(StatusList.DEFAULT_LIST_BIT_SIZE, list.getSize());
        int[] indcides = new int[100032];

        for (int i = 0; i < 100032; i++) {
            indcides[i] = i;
        }

        validateStatusListValues(list, indcides);
    }

    private final Map<String, int[]> javascriptGeneratedTestSet = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("H4sIAAAAAAAAA-3XsQkAIBADwBcdwJEczdHVDb5T4Q7SpUibGseM6JE281UAHjBuDwAAgL-12wMAAADgO940AAAAkFR2Fj4KO1LYMAAA", new int[]{0, 1, 55, 76, 423, 12134, 35322, 66666, 100000}),
            new AbstractMap.SimpleEntry<>("H4sIAAAAAAAAA-3BMQ0AAAgDsCXz7wln4IKrbTPbAAAAAAAAAAAAAAAA_Dv_W3SW2DAAAA", new int[]{0, 1, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33}),
            new AbstractMap.SimpleEntry<>("H4sIAAAAAAAAA-3OsQ2AMAwEQAdRpMwIGYXRGJ2SbxBlJLir_mVLdqtbr9qjnpFrZNkiHznoWVrkmYPHIwDfNd5XFpirHwAAAAAAAAAAAADg7y4z3UJd2DAAAA", new int[]{0, 99, 122, 223, 324, 425, 526, 627, 728, 829, 930, 1031, 11132, 12333}),
            new AbstractMap.SimpleEntry<>("H4sIAAAAAAAAA-3OIQ6AQAwEwB5BnLwnwE8IL0PwaCQCQQ1BXgIzqptt0pa41YgxxS3N0XIY0rzk4qjz3tZ6hZKKKW89HgH4rva-0sHU-wEAAAAAAAAAAAAA_u4Ecv9U7dgwAAA", new int[]{0, 99, 122, 223, 324, 425, 526, 627, 628, 629, 630, 631, 635, 640, 645, 650, 655, 660, 665, 670, 675, 728, 829, 930, 1031, 11132, 12333}),
            new AbstractMap.SimpleEntry<>("H4sIAAAAAAAAA-3aMQ7DIAwFUGg7MHKE9CZRTsaQQ2esog71EmUiVNF7k79sARMDIqefktIrxBbqVGN4hHqOja2817qUb8ihMcWpw00A7quejwwwdVgzn48AAAAAcCfP0QcA6Oc_X_cBAAC4jt9QAP21_bL9AErnjtfYMAAA", new int[]{0, 99, 122, 223, 324, 425, 526, 627, 628, 629, 630, 631, 635, 640, 645, 650, 655, 660, 665, 670, 675, 728, 829, 930, 1031, 11132, 12333, 13000, 50000, 50001, 60012, 88888, 99999, 100000})
    );

    @Test
    @DisplayName("Successfuly decodes list generated by javascript version")
    void itDecodesListBuiltByJavascriptVersionOfTheLib() throws IOException {
        for (Map.Entry<String, int[]> entry : this.javascriptGeneratedTestSet.entrySet()) {
            StatusList list = StatusList.decodeList(entry.getKey());
            assertEquals(StatusList.DEFAULT_LIST_BIT_SIZE, list.getSize());
            validateStatusListValues(list, entry.getValue());
        }
    }

    /**
     * Helper functions
     */

    private void validateStatusListValues(StatusList list, int[] indicesSet) {
        for (int index = 0; index < list.getSize(); index++) {
            if (Arrays.binarySearch(indicesSet, index) > -1) {
                assertTrue(list.isRevoked(index));
            } else {
                assertFalse(list.isRevoked(index));
            }
        }
    }

    private void validateBitSetValues(BitSet bitset, int[] indicesSet) {
        for (int index = 0; index < bitset.size(); index++) {
            if (Arrays.binarySearch(indicesSet, index) > -1) {
                assertTrue(bitset.get(index));
            } else {
                assertFalse(bitset.get(index));
            }
        }
    }
}
