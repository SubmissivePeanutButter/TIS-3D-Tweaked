package li.cil.tis3d.common.inventory;

import li.cil.tis3d.common.Constants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Base implementation of an array based inventory.
 */
public class Inventory implements IInventory {
    private static final String TAG_ITEMS = "inventory";

    private final ITextComponent name;
    protected final ItemStack[] items;

    public Inventory(final ITextComponent name, final int size) {
        this.name = name;
        Arrays.fill(items = new ItemStack[size], ItemStack.EMPTY);
    }

    // --------------------------------------------------------------------- //

    public void readFromNBT(final NBTTagCompound nbt) {
        final NBTTagList itemList = nbt.getTagList(TAG_ITEMS, Constants.NBT.TAG_COMPOUND);
        final int count = Math.min(itemList.size(), items.length);
        for (int index = 0; index < count; index++) {
            items[index] = ItemStack.func_199557_a(itemList.getCompoundTagAt(index));
        }
    }

    public void writeToNBT(final NBTTagCompound nbt) {
        final NBTTagList itemList = new NBTTagList();
        for (final ItemStack stack : items) {
            final NBTTagCompound stackNbt = new NBTTagCompound();
            if (stack != null) {
                stack.writeToNBT(stackNbt);
            }
            itemList.add(stackNbt);
        }
        nbt.setTag(TAG_ITEMS, itemList);
    }

    // --------------------------------------------------------------------- //

    protected void onItemAdded(final int index) {
    }

    protected void onItemRemoved(final int index) {
    }

    // --------------------------------------------------------------------- //
    // IWorldNameable

    @Override
    public ITextComponent getName() {
        return name;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return getName();
    }

	@Nullable
	@Override
	public ITextComponent getCustomName() {
		return null;
	}

	// --------------------------------------------------------------------- //
    // IInventory

    @Override
    public int getSizeInventory() {
        return items.length;
    }

    public boolean isEmpty() {
        for (final ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getStackInSlot(final int index) {
        return items[index];
    }

    @Override
    public ItemStack decrStackSize(final int index, final int count) {
        if (items[index].getCount() <= count) {
            return removeStackFromSlot(index);
        } else {
            final ItemStack stack = items[index].splitStack(count);
            assert items[index].getCount() > 0;
            markDirty();
            return stack;
        }
    }

    @Override
    public ItemStack removeStackFromSlot(final int index) {
        final ItemStack stack = items[index];
        setInventorySlotContents(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setInventorySlotContents(final int index, final ItemStack stack) {
        if (items[index] == stack) {
            return;
        }

        if (!items[index].isEmpty()) {
            onItemRemoved(index);
        }

        items[index] = stack;

        if (!items[index].isEmpty()) {
            onItemAdded(index);
        }

        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUsableByPlayer(final EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(final EntityPlayer player) {
    }

    @Override
    public void closeInventory(final EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(final int index, final ItemStack stack) {
        return true;
    }

    @Override
    public int getField(final int id) {
        return 0;
    }

    @Override
    public void setField(final int id, final int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
    }
}
