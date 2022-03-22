import com.u7.jthereum.annotations.*;
import com.u7.jthereum.types.*;
import static com.u7.util.gg.cf;
import static com.u7.jthereum.Jthereum.*;
import static com.u7.jthereum.ContractStaticImports.*;

public class BasicDataContract implements com.u7.jthereum.ContractProxyHelper {

    Address m_owner = null;

    private Mapping<String,String> data = new Mapping<>();

    public BasicDataContract(){

        m_owner = msg.sender;
    }

    @View
    public String getData(String key) {
        return data.get(key);
    }

    public void setData(String key, String value) {
        data.put(key, value);
    }

    @FallbackFunction
    @Payable
    public void fallbackFunction()
    {
    }

    @View
    public Uint getBalanceOfContract()
    {
        return getAddressOfCurrentContract().balance;
    }

    @View
    public Uint getBalanceOfOwner()
    {
        onlyOwner();

        return m_owner.balance;
    }

    @View
    public Uint getBalanceOfSender()
    {
        return msg.sender.balance;
    }

    @View
    public Address getAddressOfContract()
    {
        return getAddressOfCurrentContract();
    }

    @View
    public Address getAddressOfOwner()
    {
        return m_owner;
    }

    @View
    public Address getAddressOfSender()
    {
        return msg.sender;
    }

    @View
    private void onlyOwner()
    {
        require(isOwner(), "Only owner can do that!");
    }

    @View
    private boolean isOwner()
    {
        return msg.sender.equals(m_owner);
    }

    public static void main(final String[] args)
    {
        compileAndDeploy();

        final BasicDataContract bdc = createProxy(BasicDataContract.class);

        // Each call to set performs a transaction on the blockchain
        bdc.setData("breakfast", "protein shake");
        bdc.setData("lunch", "chana masala");
        bdc.setData("dinner", "shrimp scampi with linguini");

        // Calls to @View functions do not need transactions
        p("Value for 'breakfast': " + bdc.getData("breakfast"));
        p("Value for 'lunch': " + bdc.getData("lunch"));
        p("Value for 'dinner': " + bdc.getData("dinner"));

        //PART TWO:
        p("Address of Contract: " + bdc.getAddressOfContract());
        p("Address of Owner: " + bdc.getAddressOfOwner());
        p("Address of Sender: " + bdc.getAddressOfSender());

        p("Balance of Contract: " + cf(bdc.getBalanceOfContract()));
        p("Balance of Owner: " + cf(bdc.getBalanceOfOwner()));
        p("Balance of Sender: " + cf(bdc.getBalanceOfSender()));

        p("Blockchain name is: " + bdc.getBlockchainName());
    }
}
