import com.u7.jthereum.*;
import com.u7.jthereum.annotations.*;
import com.u7.jthereum.types.*;
import com.u7.jthereum.wellKnownContracts.Messages;

import static com.u7.util.gg.cf;
import static com.u7.jthereum.ContractStaticImports.*;

public class Messenger implements ContractProxyHelper{

    private Mapping<Address,GrowableArray<Messenger.Message>> messageListsByAddress = new Mapping<Address,GrowableArray<Messenger.Message>>();

    public void sendMessage(final Address to, final Bytes messageBytes)
    {
        @Memory final Messenger.Message message = new Messenger.Message();

        message.messageBytes = messageBytes;
        GrowableArray<Messenger.Message> messages = new GrowableArray<Messenger.Message>();
        messages.add(message);

        messageListsByAddress.put(to, messages);
        // Emit the event

        //emitEvent(new Messenger.MessageSent(msg.sender, to, messageListsByAddress.get(to).size().asUint256()));

        // Copy message to the end of the list
        messageListsByAddress.get(to).add(message);
    }

    public void sendMessageAsReply(final Address to, final Bytes messageBytes, final Uint256 replyingToMyMessageID)
    {
        @Memory final Messenger.Message message = new Messenger.Message();

        message.messageBytes = messageBytes;

        // Emit the event
        emitEvent(new Messenger.MessageSent(msg.sender, to, messageListsByAddress.get(to).size().asUint256()));

        // Copy message to the end of the list
        messageListsByAddress.get(to).add(message);

        // Mark the original message with the replyID
        messageListsByAddress.get(msg.sender).get(replyingToMyMessageID).replyIDPlusOne = messageListsByAddress.get(to).size().asUint256();

    }

    public void markMessageAsRead(final Uint256 id)
    {
        messageListsByAddress.get(msg.sender).get(id).read = true;

        emitEvent(new Messenger.MessageMarkedAsRead(msg.sender, id));
    }

    // All @View methods below:

    // Message access methods

    @View
    public Uint256 getNMessages(final Address to)
    {
        return messageListsByAddress.get(to).size().asUint256();
    }

    @View
    public Bytes getMessage(final Address to, final Uint256 id)
    {
        return messageListsByAddress.get(to).get(id).messageBytes;
    }

    @View
    public Uint256 getMessageReplyID(final Address to, final Uint256 id)
    {
        return messageListsByAddress.get(to).get(id).replyIDPlusOne.subtract(1);
    }

    @View
    public boolean isMessageMarkedAsRead(final Address to, final Uint256 id)
    {
        return messageListsByAddress.get(to).get(id).read;
    }

    @EventClass static class MessageSent
    {
        @Indexed final Address from;
        @Indexed final Address to;
        final Uint256 messageID;

        public MessageSent(final Address from, final Address to, final Uint256 messageID)
        {
            this.from = from;
            this.to = to;
            this.messageID = messageID;
        }
    }

    @EventClass static class MessageMarkedAsRead {
        @Indexed
        final Address to;
        final Uint256 messageID;

        public MessageMarkedAsRead(final Address to, final Uint256 messageID) {
            this.to = to;
            this.messageID = messageID;
        }
    }
    static class Message
    {
        Bytes messageBytes;

        // True if the recipient explicitly marks the message as read
        boolean read;

        // It's 'plus one' to distinguish from 'zero', which means no reply.
        Uint256 replyIDPlusOne;
    }

    public static void main(String[] args){

        Messenger messenger = new Messenger();
        Address to = new Address();

        //My Public Key Address
        to.setValue("0xaece2df2dad113c76a322abe0473ff743ab100d89015f9f496294ee188d3ae86");

        //My Message
        Bytes message = new Bytes();
        message.setValue("Avatar Meher Baba says, You and I are not we but one.");

        messenger.sendMessage(to, message);

        //messenger.get
    }

}
