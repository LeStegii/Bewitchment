package com.bewitchment.api.registry;

import com.bewitchment.Bewitchment;
import com.bewitchment.Util;
import com.bewitchment.api.capability.extendedplayer.ExtendedPlayer;
import com.bewitchment.common.curse.CurseReturnToSender;
import com.bewitchment.common.item.ItemTaglock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class Curse extends IForgeRegistryEntry.Impl<Curse> {
	final List<Ingredient> input;
	final boolean isLesser;

	public Curse (ResourceLocation name, List<Ingredient> input, boolean isLesser) {
		setRegistryName(name);
		this.input = input;
		this.isLesser = isLesser;
	}

	public final boolean matches(ItemStackHandler input) {
		return Util.areISListsEqual(this.input, input);
	}

	private boolean isValid(EntityPlayer player) {
		if (this.isLesser) {
			List<Curse> curses = player.getCapability(ExtendedPlayer.CAPABILITY, null).curses;
			for (Curse curs : curses) {
				if (curs instanceof CurseReturnToSender) {
					return false;
				}
			}
		}
		return true;
	}

	@Nullable
	public static EntityPlayer getPlayerFromTaglock(ItemStackHandler handler) {
		for(int i = 0; i < handler.getSlots(); i++) {
			ItemStack temp = handler.getStackInSlot(i);
			if (temp.getItem() instanceof ItemTaglock && temp.hasTagCompound() && temp.getTagCompound().hasKey("boundId")) {
				return Util.findPlayer(UUID.fromString(temp.getTagCompound().getString("boundId")));
			}
		}
		return null;
	}

	public boolean apply(@Nullable EntityPlayer player) {
		if(player != null && isValid(player)) {
			if (player.hasCapability(ExtendedPlayer.CAPABILITY, null)) {
				ExtendedPlayer ep = player.getCapability(ExtendedPlayer.CAPABILITY, null);
				ep.curses.add(this);
				return true;
			}
		}
		return false;
	}

	public abstract boolean doCurse(@Nullable EntityPlayer player);
}
