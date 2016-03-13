package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class S19PacketEntityStatus implements Packet
{
    private int field_149164_a;
    private byte field_149163_b;
    private static final String __OBFID = "CL_00001299";

    public S19PacketEntityStatus() {}

    public S19PacketEntityStatus(Entity p_i46335_1_, byte p_i46335_2_)
    {
        this.field_149164_a = p_i46335_1_.getEntityId();
        this.field_149163_b = p_i46335_2_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.field_149164_a = buf.readInt();
        this.field_149163_b = buf.readByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeInt(this.field_149164_a);
        buf.writeByte(this.field_149163_b);
    }

    public void func_180736_a(INetHandlerPlayClient p_180736_1_)
    {
        p_180736_1_.handleEntityStatus(this);
    }

    @SideOnly(Side.CLIENT)
    public Entity func_149161_a(World worldIn)
    {
        return worldIn.getEntityByID(this.field_149164_a);
    }

    @SideOnly(Side.CLIENT)
    public byte func_149160_c()
    {
        return this.field_149163_b;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.func_180736_a((INetHandlerPlayClient)handler);
    }
}