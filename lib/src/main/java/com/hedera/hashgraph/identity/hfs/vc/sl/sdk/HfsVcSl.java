
package com.hedera.hashgraph.identity.hfs.vc.sl.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.*;

import java.util.concurrent.TimeoutException;

public class HfsVcSl {
    public static Hbar TRANSACTION_FEE = new Hbar(2);

    protected Client client;
    protected  PrivateKey vcStatusListOwnerPrivateKey;

    public HfsVcSl(Client client, PrivateKey vcStatusListOwnerPrivateKey) {
        this.client = client;
        this.vcStatusListOwnerPrivateKey = vcStatusListOwnerPrivateKey;
    }

    public FileId createRevocationListFile() throws PrecheckStatusException, TimeoutException, ReceiptStatusException {
        // TODO: Create status list instance and encode
        String encodedVcStatusList = "mockContent";
        FileCreateTransaction transaction = new FileCreateTransaction()
                .setKeys(this.vcStatusListOwnerPrivateKey.getPublicKey())
                .setContents(encodedVcStatusList)
                .setMaxTransactionFee(HfsVcSl.TRANSACTION_FEE)
                .freezeWith(this.client);

        FileCreateTransaction sigTx = transaction.sign(this.vcStatusListOwnerPrivateKey);
        TransactionResponse txResponse = sigTx.execute(this.client);
        TransactionReceipt txReceipt = txResponse.getReceipt(this.client);

        return txReceipt.fileId;
    }

    public ByteString loadRevocationList(FileId vcStatusFileId) throws PrecheckStatusException, TimeoutException {
        FileContentsQuery query = new FileContentsQuery().setFileId(vcStatusFileId);
        ByteString encodedStatusList = query.execute(this.client);
        // TODO: implement actual decoding
        ByteString decodedStatusList = encodedStatusList;

        return decodedStatusList;
    }

    /**
     * TODO
     */

    public void revokeByIndex(FileId vcStatusListFileId, int vcStatusListIndex) throws ReceiptStatusException, PrecheckStatusException, TimeoutException {
        this.updateStatus(vcStatusListFileId, vcStatusListIndex, VcSlStatus.REVOKED);
    }

    public void issueByIndex(FileId vcStatusListFileId, int vcStatusListIndex) throws ReceiptStatusException, PrecheckStatusException, TimeoutException {
        this.updateStatus(vcStatusListFileId, vcStatusListIndex, VcSlStatus.ACTIVE);
    }

    public void suspendByIndex(FileId vcStatusListFileId, int vcStatusListIndex) throws ReceiptStatusException, PrecheckStatusException, TimeoutException {
        this.updateStatus(vcStatusListFileId, vcStatusListIndex, VcSlStatus.SUSPENDED);
    }

    public void resumeByIndex(FileId vcStatusListFileId, int vcStatusListIndex) throws ReceiptStatusException, PrecheckStatusException, TimeoutException {
        this.updateStatus(vcStatusListFileId, vcStatusListIndex, VcSlStatus.RESUMED);
    }

    public VcSlStatus resolveStatusByIndex(FileId vcStatusListFileId, int vcStatusListIndex) throws Exception {
        /**
         * TODO: replace with a real implementation
         */
        ByteString vcStatusListDecoded = this.loadRevocationList(vcStatusListFileId);
        String[] parts = vcStatusListDecoded.toStringUtf8().split(" - ");

        switch (parts[1]) {
            case "ACTIVE":
                return VcSlStatus.ACTIVE;
            case "RESUMED":
                return VcSlStatus.RESUMED;
            case "SUSPENDED":
                return VcSlStatus.SUSPENDED;
            case "REVOKED":
                return VcSlStatus.REVOKED;
            default:
                throw new Exception("Invalid status");
        }
    }

    /**
     * Private functions TODO
     */

    private ByteString updateStatus(FileId vcStatusListFileId, int vcStatusListIndex, VcSlStatus status) throws PrecheckStatusException, TimeoutException, ReceiptStatusException {
        String vcStatusListEncoded = "updatedMockContent" + " - " + status.toString();

        FileUpdateTransaction transaction = new FileUpdateTransaction()
                .setFileId(vcStatusListFileId)
                .setContents(vcStatusListEncoded)
                .setMaxTransactionFee(HfsVcSl.TRANSACTION_FEE)
                .freezeWith(this.client);

        FileUpdateTransaction sigTx = transaction.sign(this.vcStatusListOwnerPrivateKey);
        TransactionResponse txResponse = sigTx.execute(this.client);
        txResponse.getReceipt(this.client);

        // TODO: should return a decoded list
        return ByteString.copyFromUtf8(vcStatusListEncoded);
    }
}
