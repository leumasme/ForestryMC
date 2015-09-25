/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.proxy;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.EntityExplodeFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;

import org.lwjgl.input.Keyboard;

import forestry.apiculture.render.TextureHabitatLocator;
import forestry.core.ForestryClient;
import forestry.core.TickHandlerCoreClient;
import forestry.core.entities.EntityFXHoneydust;
import forestry.core.entities.EntityFXIgnition;
import forestry.core.entities.EntityFXSnow;
import forestry.core.multiblock.MultiblockClientTickHandler;
import forestry.core.render.SpriteSheet;
import forestry.core.worldgen.WorldGenerator;

public class ProxyCommonClient extends ProxyCommon {

	@Override
	public void bindTexture(ResourceLocation location) {
		getClientInstance().getTextureManager().bindTexture(location);
	}

	@Override
	public void bindTexture(SpriteSheet spriteSheet) {
		bindTexture(spriteSheet.getLocation());
	}

	@Override
	public void registerTickHandlers(WorldGenerator worldGenerator) {
		super.registerTickHandlers(worldGenerator);

		FMLCommonHandler.instance().bus().register(new TickHandlerCoreClient());
		FMLCommonHandler.instance().bus().register(new MultiblockClientTickHandler());
	}

	@Override
	public IResourceManager getSelectedTexturePack(Minecraft minecraft) {
		return minecraft.getResourceManager();
	}

	@Override
	public void setHabitatLocatorCoordinates(Entity player, ChunkCoordinates coordinates) {
		TextureHabitatLocator.getInstance().setTargetCoordinates(coordinates);
	}

	@Override
	public File getForestryRoot() {
		return Minecraft.getMinecraft().mcDataDir;
	}

	@Override
	public World getRenderWorld() {
		return getClientInstance().theWorld;
	}

	@Override
	public int getByBlockModelId() {
		return ForestryClient.byBlockModelId;
	}

	@Override
	public boolean isOp(EntityPlayer player) {
		return false;
	}

	@Override
	public double getBlockReachDistance(EntityPlayer entityplayer) {
		if (entityplayer instanceof EntityPlayerSP) {
			return getClientInstance().playerController.getBlockReachDistance();
		} else {
			return 4f;
		}
	}

	@Override
	public boolean isShiftDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}

	@Override
	public String getItemStackDisplayName(Item item) {
		return item.getItemStackDisplayName(null);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return stack.getItem().getItemStackDisplayName(stack);
	}

	@Override
	public String getCurrentLanguage() {
		return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
	}

	@Override
	public String getDisplayName(ItemStack itemstack) {
		return itemstack.getItem().getItemStackDisplayName(itemstack);
	}

	@Override
	public void playSoundFX(World world, int x, int y, int z, Block block) {
		if (!world.isRemote) {
			super.playSoundFX(world, x, y, z, block);
		} else {
			playSoundFX(world, x, y, z, block.stepSound.getStepResourcePath(), block.stepSound.getVolume(), block.stepSound.getPitch());
		}
	}

	@Override
	public void playBlockBreakSoundFX(World world, int x, int y, int z, Block block) {
		if (!world.isRemote) {
			super.playSoundFX(world, x, y, z, block);
		} else {
			playSoundFX(world, x, y, z, block.stepSound.getBreakSound(), block.stepSound.getVolume() / 4, block.stepSound.getPitch());
		}
	}

	@Override
	public void playBlockPlaceSoundFX(World world, int x, int y, int z, Block block) {
		if (!world.isRemote) {
			super.playSoundFX(world, x, y, z, block);
		} else {
			playSoundFX(world, x, y, z, block.stepSound.getStepResourcePath(), block.stepSound.getVolume() / 4, block.stepSound.getPitch());
		}
	}

	@Override
	public void playSoundFX(World world, int x, int y, int z, String sound, float volume, float pitch) {
		world.playSound(x + 0.5, y + 0.5, z + 0.5, sound, volume, (1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f) * 0.7f, false);
	}

	@Override
	public void addEntitySwarmFX(World world, double d1, double d2, double d3) {
		if (!ProxyRenderClient.shouldSpawnParticle(world)) {
			return;
		}

		getClientInstance().effectRenderer.addEffect(new EntityFXHoneydust(world, d1, d2, d3, 0, 0, 0));
	}

	@Override
	public void addEntityExplodeFX(World world, double d1, double d2, double d3) {
		if (!ProxyRenderClient.shouldSpawnParticle(world)) {
			return;
		}

		getClientInstance().effectRenderer.addEffect(new EntityExplodeFX(world, d1, d2, d3, 0, 0, 0));
	}

	@Override
	public void addEntitySnowFX(World world, double d1, double d2, double d3) {
		if (!ProxyRenderClient.shouldSpawnParticle(world)) {
			return;
		}

		getClientInstance().effectRenderer.addEffect(new EntityFXSnow(world, d1 + world.rand.nextGaussian(), d2, d3 + world.rand.nextGaussian()));
	}

	@Override
	public void addEntityIgnitionFX(World world, double d1, double d2, double d3) {
		if (!ProxyRenderClient.shouldSpawnParticle(world)) {
			return;
		}

		getClientInstance().effectRenderer.addEffect(new EntityFXIgnition(world, d1, d2, d3));
	}

	@Override
	public void addEntityPotionFX(World world, double d1, double d2, double d3, int color) {
		if (!ProxyRenderClient.shouldSpawnParticle(world)) {
			return;
		}

		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;

		EntityFX entityfx = new EntitySpellParticleFX(world, d1, d2, d3, 0, 0, 0);
		entityfx.setRBGColorF(red, green, blue);

		getClientInstance().effectRenderer.addEffect(entityfx);
	}

	@Override
	public void addBlockDestroyEffects(World world, int xCoord, int yCoord, int zCoord, Block block, int i) {
		if (world.isRemote) {
			getClientInstance().effectRenderer.addBlockDestroyEffects(xCoord, yCoord, zCoord, block, i);
		} else {
			super.addBlockDestroyEffects(world, xCoord, yCoord, zCoord, block, i);
		}
	}

	@Override
	public void addBlockPlaceEffects(World world, int xCoord, int yCoord, int zCoord, Block block, int i) {
		if (world.isRemote) {
			playBlockPlaceSoundFX(world, xCoord, yCoord, zCoord, block);
		} else {
			super.addBlockPlaceEffects(world, xCoord, yCoord, zCoord, block, i);
		}
	}

	@Override
	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public EntityPlayer getPlayer(World world, GameProfile profile) {
		return super.getPlayer(world, profile);
	}
}
