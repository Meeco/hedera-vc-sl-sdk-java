
package com.hedera.hashgraph.identity.hfs.vc.sl.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class HfsVcSl {
    public static Hbar TRANSACTION_FEE = new Hbar(2);

    protected Client client;
    protected  PrivateKey vcStatusListOwnerPrivateKey;

    public HfsVcSl(Client client, PrivateKey vcStatusListOwnerPrivateKey) {
        this.client = client;
        this.vcStatusListOwnerPrivateKey = vcStatusListOwnerPrivateKey;
    }

    public FileId createRevocationListFile() throws PrecheckStatusException, TimeoutException, ReceiptStatusException, IOException {
        RevocationList vcStatusList = new RevocationList();
        String encodedVcStatusList = vcStatusList.encode();

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

    public RevocationList loadRevocationList(FileId vcStatusFileId) throws PrecheckStatusException, TimeoutException, IOException {
        FileContentsQuery query = new FileContentsQuery().setFileId(vcStatusFileId);
        ByteString encodedStatusList = query.execute(this.client);

        return RevocationList.decodeList(encodedStatusList.toStringUtf8());
    }

    public void revokeByIndex(FileId vcStatusListFileId, int vcStatusListIndex) throws Exception {
        this.updateStatus(vcStatusListFileId, vcStatusListIndex, VcSlStatus.REVOKED);
    }

    public void issueByIndex(FileId vcStatusListFileId, int vcStatusListIndex) throws Exception {
        this.updateStatus(vcStatusListFileId, vcStatusListIndex, VcSlStatus.ACTIVE);
    }

    public void suspendByIndex(FileId vcStatusListFileId, int vcStatusListIndex) throws Exception {
        this.updateStatus(vcStatusListFileId, vcStatusListIndex, VcSlStatus.SUSPENDED);
    }

    public void resumeByIndex(FileId vcStatusListFileId, int vcStatusListIndex) throws Exception {
        this.updateStatus(vcStatusListFileId, vcStatusListIndex, VcSlStatus.RESUMED);
    }

    public VcSlStatus resolveStatusByIndex(FileId vcStatusListFileId, int vcStatusListIndex) throws Exception {
        RevocationList list = this.loadRevocationList(vcStatusListFileId);

        if (!list.isRevoked(vcStatusListIndex) && !list.isRevoked(vcStatusListIndex + 1)) {
            return VcSlStatus.ACTIVE;
        } else if (!list.isRevoked(vcStatusListIndex) && list.isRevoked(vcStatusListIndex + 1)) {
            return VcSlStatus.RESUMED;
        } else if (list.isRevoked(vcStatusListIndex) && !list.isRevoked(vcStatusListIndex + 1)) {
            return VcSlStatus.SUSPENDED;
        } else if (list.isRevoked(vcStatusListIndex) && list.isRevoked(vcStatusListIndex + 1)) {
            return VcSlStatus.REVOKED;
        } else  {
            throw new Exception("Invalid status");
        }
    }

    /**
     * Private functions
     */

    private RevocationList updateStatus(FileId vcStatusListFileId, int vcStatusListIndex, VcSlStatus status) throws Exception {
        if (vcStatusListIndex % 2 != 0) {
            throw new Error("vcStatusListIndex must be Multiples of 2 OR 0. e.g. 0, 2, 4, 6, 8, 10, 12, 14");
        }

        RevocationList list = this.loadRevocationList(vcStatusListFileId);

        switch (status) {
            case ACTIVE:
                list.setRevoked(vcStatusListIndex, false);
                list.setRevoked(vcStatusListIndex + 1, false);
                break;
            case RESUMED:
                list.setRevoked(vcStatusListIndex, false);
                list.setRevoked(vcStatusListIndex + 1, true);
                break;
            case SUSPENDED:
                list.setRevoked(vcStatusListIndex, true);
                list.setRevoked(vcStatusListIndex + 1, false);
                break;
            case REVOKED:
                list.setRevoked(vcStatusListIndex, true);
                list.setRevoked(vcStatusListIndex + 1, true);
                break;
            default:
                throw new Exception("Invalid status");
        }

        FileUpdateTransaction transaction = new FileUpdateTransaction()
                .setFileId(vcStatusListFileId)
                .setContents(list.encode())
                .setMaxTransactionFee(HfsVcSl.TRANSACTION_FEE)
                .freezeWith(this.client);

        FileUpdateTransaction sigTx = transaction.sign(this.vcStatusListOwnerPrivateKey);
        TransactionResponse txResponse = sigTx.execute(this.client);
        txResponse.getReceipt(this.client);

        return list;
    }
}
