package demo;

import com.google.common.base.Strings;
import com.hedera.hashgraph.identity.hfs.vc.sl.sdk.HfsVcSl;
import com.hedera.hashgraph.sdk.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

@Tag("demo")
public class DemoTest {

    private final Client client = Client.forTestnet();
    private PrivateKey VC_STATUS_LIST_OWNER_PRIVATE_KEY;
    private FileId VC_STATUS_LIST_FILE_ID;


    public DemoTest() {
        String CONFIG_FILE = "demo.config.properties";
        try (InputStream input = DemoTest.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            AccountId operatorId = AccountId.fromString(prop.getProperty("OPERATOR_ID"));
            PrivateKey operatorKey = PrivateKey.fromString(prop.getProperty("OPERATOR_KEY"));
            client.setOperator(operatorId, operatorKey);

            this.VC_STATUS_LIST_OWNER_PRIVATE_KEY = Strings.isNullOrEmpty(prop.getProperty("VC_STATUS_LIST_OWNER_PRIVATE_KEY"))
                    ? PrivateKey.generateED25519()
                    : PrivateKey.fromString(prop.getProperty("VC_STATUS_LIST_OWNER_PRIVATE_KEY"));

            if (!Strings.isNullOrEmpty(prop.getProperty("VC_STATUS_LIST_FILE_ID")))
                this.VC_STATUS_LIST_FILE_ID = FileId.fromString(prop.getProperty("VC_STATUS_LIST_FILE_ID"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @DisplayName("create vc status list")
    void create() throws IOException, ReceiptStatusException, PrecheckStatusException, TimeoutException {

        var hfsVc = new HfsVcSl(client, VC_STATUS_LIST_OWNER_PRIVATE_KEY);
        var VC_STATUS_LIST_FILE_ID = hfsVc.createStatusListFile();

        System.out.printf("VC_STATUS_LIST_FILE_ID: %s%n", VC_STATUS_LIST_FILE_ID.toString());
        System.out.printf("VC_STATUS_LIST_OWNER_PRIVATE_KEY: %s%n", VC_STATUS_LIST_OWNER_PRIVATE_KEY.toString());

        System.out.println(hfsVc.loadStatusList(VC_STATUS_LIST_FILE_ID).getBitset().toString());

    }

    @Test
    @DisplayName("resolve")
    void resolve() throws Exception {

        var hfsVc = new HfsVcSl(client, VC_STATUS_LIST_OWNER_PRIVATE_KEY);

        var resolveStatus = hfsVc.resolveStatusByIndex(this.VC_STATUS_LIST_FILE_ID, 0);
        var rl = hfsVc.loadStatusList(this.VC_STATUS_LIST_FILE_ID);

        System.out.println("==== status ====");
        System.out.println(resolveStatus);

        System.out.println("==== list ====");
        System.out.println(rl.getBitset().toString());

    }

    @Test
    @DisplayName("Revoke by index")
    void revokeByIndex() throws Exception {

        var statusIndex = 0;
        var hfsVc = new HfsVcSl(client, VC_STATUS_LIST_OWNER_PRIVATE_KEY);
        hfsVc.revokeByIndex(this.VC_STATUS_LIST_FILE_ID, statusIndex);

        System.out.printf("Revoked by index position %d%n", statusIndex);

        System.out.printf("status list %s", hfsVc.loadStatusList(VC_STATUS_LIST_FILE_ID).getBitset().toString());
    }

    @Test
    @DisplayName("Active by index")
    void activeByIndex() throws Exception {

        var statusIndex = 0;
        var hfsVc = new HfsVcSl(client, VC_STATUS_LIST_OWNER_PRIVATE_KEY);
        hfsVc.issueByIndex(this.VC_STATUS_LIST_FILE_ID, statusIndex);

        System.out.printf("Active by index position %d%n", statusIndex);

        System.out.println(hfsVc.loadStatusList(VC_STATUS_LIST_FILE_ID).getBitset().toString());

    }

    @Test
    @DisplayName("Suspend by index")
    void suspendByIndex() throws Exception {

        var statusIndex = 0;
        var hfsVc = new HfsVcSl(client, VC_STATUS_LIST_OWNER_PRIVATE_KEY);
        hfsVc.suspendByIndex(this.VC_STATUS_LIST_FILE_ID, statusIndex);

        System.out.printf("Suspend by index position %d%n", statusIndex);

        System.out.println(hfsVc.loadStatusList(VC_STATUS_LIST_FILE_ID).getBitset().toString());

    }

    @Test
    @DisplayName("Resume by index")
    void resumeByIndex() throws Exception {

        var statusIndex = 0;
        var hfsVc = new HfsVcSl(client, VC_STATUS_LIST_OWNER_PRIVATE_KEY);
        hfsVc.resumeByIndex(this.VC_STATUS_LIST_FILE_ID, statusIndex);

        System.out.printf("Resume by index position %d%n", statusIndex);

        System.out.println(hfsVc.loadStatusList(VC_STATUS_LIST_FILE_ID).getBitset().toString());

    }

}
