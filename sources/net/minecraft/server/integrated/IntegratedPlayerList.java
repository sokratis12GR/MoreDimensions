package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IntegratedPlayerList extends ServerConfigurationManager
{
    /** Holds the NBT data for the host player's save file, so this can be written to level.dat. */
    private NBTTagCompound hostPlayerData;
    private static final String __OBFID = "CL_00001128";

    public IntegratedPlayerList(IntegratedServer p_i1314_1_)
    {
        super(p_i1314_1_);
        this.setViewDistance(10);
    }

    /**
     * also stores the NBTTags if this is an intergratedPlayerList
     */
    protected void writePlayerData(EntityPlayerMP playerIn)
    {
        if (playerIn.getName().equals(this.getIntegratedServerInstance().getServerOwner()))
        {
            this.hostPlayerData = new NBTTagCompound();
            playerIn.writeToNBT(this.hostPlayerData);
        }

        super.writePlayerData(playerIn);
    }

    /**
     * checks ban-lists, then white-lists, then space for the server. Returns null on success, or an error message
     */
    public String allowUserToConnect(SocketAddress address, GameProfile profile)
    {
        return profile.getName().equalsIgnoreCase(this.getIntegratedServerInstance().getServerOwner()) && this.getPlayerByUsername(profile.getName()) != null ? "That name is already taken." : super.allowUserToConnect(address, profile);
    }

    public IntegratedServer getIntegratedServerInstance()
    {
        return (IntegratedServer)super.getServerInstance();
    }

    /**
     * On integrated servers, returns the host's player data to be written to level.dat.
     */
    public NBTTagCompound getHostPlayerData()
    {
        return this.hostPlayerData;
    }

    public MinecraftServer getServerInstance()
    {
        return this.getIntegratedServerInstance();
    }
}