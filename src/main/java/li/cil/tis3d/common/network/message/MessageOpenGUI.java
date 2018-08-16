package li.cil.tis3d.common.network.message;

import li.cil.tis3d.common.gui.GuiHandlerCommon;
import pl.asie.protocharset.rift.network.SendNetwork;

public class MessageOpenGUI extends AbstractMessage {
	@SendNetwork
	public GuiHandlerCommon.GuiId id;

	public MessageOpenGUI() {

	}

	public MessageOpenGUI(GuiHandlerCommon.GuiId id) {
		this.id = id;
	}
}
