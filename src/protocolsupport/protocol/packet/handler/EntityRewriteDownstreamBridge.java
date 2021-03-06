package protocolsupport.protocol.packet.handler;

import java.util.function.IntUnaryOperator;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.protocol.PacketWrapper;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.packet.entityrewrite.EntityRewrite;
import protocolsupport.protocol.packet.entityrewrite.EntityRewriteFactory;

public class EntityRewriteDownstreamBridge extends DownstreamBridge {

	protected final UserConnection con;
	protected final IntUnaryOperator rewritefunc;
	protected final EntityRewrite rewrite;
	public EntityRewriteDownstreamBridge(UserConnection con, ProtocolVersion version) {
		super(ProxyServer.getInstance(), con, con.getServer());
		this.con = con;
		this.rewritefunc = EntityRewriteFactory.createReplaceEntityIdFunc(con::getClientEntityId, con::getServerEntityId);
		this.rewrite = EntityRewriteFactory.getToClientRewrite(version);
	}

	@Override
	public void handle(PacketWrapper packet) throws Exception {
		rewrite.rewrite(packet.buf, rewritefunc);
		con.sendPacket(packet);
	}

}
