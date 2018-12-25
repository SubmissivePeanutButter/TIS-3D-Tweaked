package li.cil.tis3d.common.item;

import li.cil.tis3d.charset.PacketRegistry;
import li.cil.tis3d.client.gui.GuiHandlerClient;
import li.cil.tis3d.common.block.BlockCasing;
import li.cil.tis3d.common.init.Items;
import li.cil.tis3d.common.network.message.MessageModuleReadOnlyMemoryData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemModuleReadOnlyMemory extends ItemModule {
    private static final String TAG_DATA = "data";
    private static final byte[] EMPTY_DATA = new byte[0];

    public ItemModuleReadOnlyMemory(Item.Settings builder) {
        super(builder.stackSize(1));
    }

    // --------------------------------------------------------------------- //
    // Item

    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        if (world.isClient) {
            openForClient(player);
        } else {
            sendModuleMemory(player, hand);
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return BlockCasing.activate(context) ? ActionResult.SUCCESS : super.useOnBlock(context);
    }

    private static void openForClient(final PlayerEntity player) {
        Gui screen = GuiHandlerClient.getClientGuiElement(GuiHandlerClient.GuiId.MODULE_MEMORY, player.getEntityWorld(), player);
        if (screen != null) {
            MinecraftClient.getInstance().openGui(screen);
        }
    }

    private static void sendModuleMemory(final PlayerEntity player, final Hand hand) {
        if (!(player instanceof ServerPlayerEntity)) {
            return;
        }

        final ItemStack heldItem = player.getStackInHand(hand);
        if (!Items.isModuleReadOnlyMemory(heldItem)) {
            return;
        }

        MessageModuleReadOnlyMemoryData data = new MessageModuleReadOnlyMemoryData(ItemModuleReadOnlyMemory.loadFromStack(heldItem));
        ((ServerPlayerEntity) player).networkHandler.sendPacket(PacketRegistry.SERVER.wrap(data));
    }

    // --------------------------------------------------------------------- //

    /**
     * Load ROM data from the specified NBT tag.
     *
     * @param nbt the tag to load the data from.
     * @return the data loaded from the tag.
     */
    public static byte[] loadFromNBT(@Nullable final CompoundTag nbt) {
        if (nbt != null) {
            return nbt.getByteArray(TAG_DATA);
        }
        return EMPTY_DATA;
    }

    /**
     * Load ROM data from the specified item stack.
     *
     * @param stack the item stack to load the data from.
     * @return the data loaded from the stack.
     */
    public static byte[] loadFromStack(final ItemStack stack) {
        return loadFromNBT(stack.getTag());
    }

    /**
     * Save the specified ROM data to the specified item stack.
     *
     * @param stack the item stack to save the data to.
     * @param data  the data to save to the item stack.
     */
    public static void saveToStack(final ItemStack stack, final byte[] data) {
        CompoundTag nbt = stack.getTag();
        if (nbt == null) {
            stack.setTag(nbt = new CompoundTag());
        }
        nbt.putByteArray(TAG_DATA, data);
    }
}
