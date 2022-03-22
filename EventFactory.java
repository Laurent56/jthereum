import com.u7.jthereum.*;
import com.u7.jthereum.annotations.*;
import com.u7.jthereum.exampleContracts.SimpleEventDemo;
import com.u7.jthereum.support.*;
import com.u7.jthereum.types.*;

import static com.u7.jthereum.ContractStaticImports.emitEvent;
import static com.u7.jthereum.Jthereum.*;
import static com.u7.util.gg.*;
import static com.u7.util.gg.p;

public class EventFactory implements com.u7.jthereum.ContractProxyHelper {

    @EventClass
    static class SimpleIndexedString extends EventClassHelper {

        @Indexed
        final String eventString;

        public SimpleIndexedString(final String es){

            eventString = es;
        }

    }
    public void emitTest(final String message)
    {
        emitEvent(new SimpleIndexedString(message));
    }

    public static void main(String[] args){

        //compileAndDeploy(); test net fails with an Exception
        compileAndDeploy("test");

        // Get the proxy object
        final EventFactory ef = (EventFactory) createProxy();

        // Emit event
        ef.emitTest("Avatar Meher Baba");

        /* Next we need to read the events we have emitted */

        final GenericEventLogItem<SimpleIndexedString>[] events = getEventsFromBlockchain();

        for(final GenericEventLogItem<SimpleIndexedString> genericEvent : events)
        {
            final SimpleIndexedString event = genericEvent.getEventClassInstance();

            p("The EventClass String I got back from the Ethereum blockchain was: " + event.toString());
        }
    }
}
