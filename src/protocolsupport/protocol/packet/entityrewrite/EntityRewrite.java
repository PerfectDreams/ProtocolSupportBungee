package protocolsupport.protocol.packet.entityrewrite;

import java.util.function.IntUnaryOperator;

import io.netty.buffer.ByteBuf;
import protocolsupport.utils.netty.Allocator;

public abstract class EntityRewrite {

	protected final EntityRewriteCommand[][] rewritechains = new EntityRewriteCommand[256][];

	public void register(int packetId, EntityRewriteCommand... chain) {
		rewritechains[packetId] = chain.clone();
	}

	public void rewrite(ByteBuf buf, IntUnaryOperator rewritefunc) {
		buf.markReaderIndex();
		int packetId = readPacketId(buf);
		EntityRewriteCommand[] chain = rewritechains[packetId];
		if (chain == null) {
			buf.resetReaderIndex();
			return;
		}
		ByteBuf newbuf = Allocator.allocateBuffer();
		try {
			System.out.println("Rewriting packet ID: " + packetId);
			writePacketId(newbuf, packetId);
			for (EntityRewriteCommand command : chain) {
				System.out.println("In " + packetId + " we are using " + command.getClass().getSimpleName() + " rewritter!");
				command.rewrite(buf, newbuf, rewritefunc);
			}
			System.out.println("NewByteBuf Size: " + newbuf.capacity());
			System.out.println("Buf Size: " + buf.capacity());
			buf.clear();
			buf.writeBytes(newbuf);
		} finally {
			newbuf.release();
		}
	}

	protected abstract int readPacketId(ByteBuf from);

	protected abstract void writePacketId(ByteBuf to, int packetId);

}
