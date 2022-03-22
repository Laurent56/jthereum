/* Code is by Laurent Weichberger.
*/
import com.u7.jthereum.annotations.*;
import com.u7.jthereum.types.*;
import com.u7.jthereum.wellKnownInterfaces.*;

import static com.u7.util.gg.cf;
import static com.u7.jthereum.Jthereum.*;
import static com.u7.jthereum.ContractStaticImports.*;

public class BasicTokenContract implements ERC20 {

    public final String _name = "MyToken";
    public final String _symbol = "MYT";
    public final Uint8 _decimals = Uint8.valueOf(8);
    private final Uint256 _totalSupply = Uint256.valueOf(100_000_000_000_000_000L);

    // All of the balances
    final Mapping<Address, Uint256> balancesByAddress = new Mapping<>();

    public BasicTokenContract()
    {
        // set up total supply
        balancesByAddress.put(msg.sender, _totalSupply);
    }

    @View
    @Override
    public Uint256 balanceOf(Address who) {
        return balancesByAddress.get(who);
    }

    @Override
    public boolean transfer(Address to, Uint256 value) {

        //Make sure all is right in the world before proceeding:
        require(balancesByAddress.get(msg.sender).greaterThanOrEqual(value), "Not enough funds");
        require(!to.equals(msg.sender), "Don’t send tokens to yourself");

        // Compute the new balances
        final Uint256 newSenderBalance = balancesByAddress.get(msg.sender).subtract(value);

        final Uint256 newRecipientBalance = balancesByAddress.get(to).add(value);

        require(newRecipientBalance.greaterThanOrEqual(balancesByAddress.get(to))); //overflow check

        balancesByAddress.put(msg.sender, newSenderBalance);
        balancesByAddress.put(to, newRecipientBalance);

        // Log this transaction
        emitEvent(new Transfer(msg.sender, to, value));

        return true;
    }

    @Override
    public Uint256 allowance(Address owner, Address spender) {
        return null;
    }

    @Override
    public boolean transferFrom(Address from, Address to, Uint256 value) {
        return false;
    }

    @Override
    public boolean approve(Address spender, Uint256 value) {
        return false;
    }

    @View
    @Override
    public String name() {
        return _name;
    }

    @View
    @Override
    public String symbol() {
        return _symbol;
    }

    @Override
    public Uint8 decimals() {
        return _decimals;
    }

    @View
    @Override
    public Uint256 totalSupply() {
        return _totalSupply;
    }

    public static void main(final String[] args)
    {
        compileAndDeploy();
        String myAddress = getMyAddress();

        final BasicTokenContract btc = createProxy(BasicTokenContract.class);
        Address address = new Address(myAddress);

        p("My Old Balance before transfer starts: " + cf(btc.balanceOf(address)));

        // transfer some tokens
        btc.transfer(Address.ZERO, new Uint256(1000));

        p("My New Balance after transfer completes: " + cf(btc.balanceOf(address)));
    }

}
