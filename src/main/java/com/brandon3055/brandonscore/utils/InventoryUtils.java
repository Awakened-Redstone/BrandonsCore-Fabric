package com.brandon3055.brandonscore.utils;


import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.lib.functions.TriPredicate;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Created by brandon3055 on 31/05/2016.
 */

public class InventoryUtils {

    public static boolean hasStack(ItemStack stack, IInventory inventory) {
        if (stack.isEmpty()) {
            return false;
        }

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack s = inventory.getItem(i);

            //TODO Do i still need to check damage?
            if (ItemStack.isSame(stack, s) && stack.getDamageValue() == s.getDamageValue() && s.getCount() >= stack.getCount()) {
                return true;
            }
        }

        return false;
    }

    public static boolean consumeStack(ItemStack stack, IInventory inventory) {
        if (stack.isEmpty()) {
            return false;
        }

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack s = inventory.getItem(i);
            if (s.isEmpty()) {
                continue;
            }

            //TODO Do i still need to check damage?
            if (ItemStack.isSame(stack, s) && stack.getDamageValue() == s.getDamageValue() && s.getCount() >= stack.getCount()) {
                s.shrink(stack.getCount());
                inventory.setChanged();
                return true;
            }
        }

        return false;
    }

    /**
     * Used to transfer an item between an inventory slot and the players hand. E.g. inserting or removing a dislocator from a receptacle.
     * If the player is holding an item that is valid for the slot it will be transferred to the slot.
     * If the slot is already occupied the stack in the slot will be transferred to the players main hand, failing that the players inventory
     * or if the players inventory is full the stack will be dropped at the players feet.
     * <p>
     * When inserting will first try to insert the item in the main hand and if that fails it will try the off hand.
     * Will not transfer partial stacks.
     */
    public static void handleHeldStackTransfer(int slot, IInventory inventory, PlayerEntity player) {
        if (player.level.isClientSide) {
            return;
        }

        if (!inventory.getItem(slot).isEmpty()) {
            if (player.getMainHandItem().isEmpty()) {
                player.setItemInHand(Hand.MAIN_HAND, inventory.getItem(slot));
            } else {
                givePlayerStack(player, inventory.getItem(slot));
            }
            inventory.setItem(slot, ItemStack.EMPTY);
        } else {
            DataUtils.forEach(Hand.values(), enumHand -> {
                ItemStack stack = player.getItemInHand(enumHand);
                if (!stack.isEmpty() && inventory.canPlaceItem(slot, stack) && inventory.getItem(slot).isEmpty()) {
                    inventory.setItem(slot, stack);
                    player.setItemInHand(enumHand, ItemStack.EMPTY);
                }
            });
        }
    }

    public static void handleHeldStackTransfer(int slot, IItemHandlerModifiable inventory, PlayerEntity player) {
        if (player.level.isClientSide) {
            return;
        }

        if (!inventory.getStackInSlot(slot).isEmpty()) {
            if (player.getMainHandItem().isEmpty()) {
                player.setItemInHand(Hand.MAIN_HAND, inventory.getStackInSlot(slot));
            } else {
                givePlayerStack(player, inventory.getStackInSlot(slot));
            }
            inventory.setStackInSlot(slot, ItemStack.EMPTY);
        } else {
            for (Hand hand : Hand.values()) {
                ItemStack stack = player.getItemInHand(hand);
                if (!stack.isEmpty() && inventory.isItemValid(slot, stack)) {
                    inventory.setStackInSlot(slot, stack);
                    player.setItemInHand(hand, ItemStack.EMPTY);
                    return;
                }
            }
        }
    }

    public static void consumeHeldItem(PlayerEntity player, ItemStack stack, Hand hand) {
        stack.shrink(1);
        player.setItemInHand(hand, stack.getCount() > 0 ? stack.copy() : ItemStack.EMPTY);
    }

    public static void givePlayerStack(PlayerEntity player, ItemStack stack) {
        if (player.level.isClientSide) {
            return;
        }
        player.inventory.add(stack);
        if (stack.getCount() > 0) {
            dropItemNoDelay(stack, player.level, Vector3.fromEntity(player));
        }
    }

    public static void dropItemNoDelay(ItemStack stack, World world, Vector3 dropLocation) {
        ItemEntity item = new ItemEntity(world, dropLocation.x, dropLocation.y, dropLocation.z, stack);
        item.setDeltaMovement(world.random.nextGaussian() * 0.05, world.random.nextGaussian() * 0.05 + 0.2F, world.random.nextGaussian() * 0.05);
        world.addFreshEntity(item);
        item.setPickUpDelay(0);
    }

    public static int findMatchingStack(IItemHandler itemHandler, TriPredicate<IItemHandler, ItemStack, Integer> predicate) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (predicate.test(itemHandler, itemHandler.getStackInSlot(i), i)) {
                return i;
            }
        }
        return -1;
    }
}
