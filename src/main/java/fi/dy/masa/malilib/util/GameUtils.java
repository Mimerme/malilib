package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.RayTraceResult;

public class GameUtils
{
    public static Minecraft getClient()
    {
        return Minecraft.getMinecraft();
    }

    @Nullable
    public static WorldClient getClientWorld()
    {
        return getClient().world;
    }

    @Nullable
    public static EntityPlayerSP getClientPlayer()
    {
        return getClient().player;
    }

    @Nullable
    public static RayTraceResult getRayTrace()
    {
        return getClient().objectMouseOver;
    }

    public static boolean isCreativeMode()
    {
        EntityPlayerSP player = getClientPlayer();
        return player != null && player.capabilities.isCreativeMode;
    }

    public static int getRenderDistanceChunks()
    {
        return getClient().gameSettings.renderDistanceChunks;
    }

    public static boolean isSinglePlayer()
    {
        return getClient().isSingleplayer();
    }

    public static void scheduleToClientThread(Runnable task)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.isCallingFromMinecraftThread())
        {
            task.run();
        }
        else
        {
            mc.addScheduledTask(task);
        }
    }
}
