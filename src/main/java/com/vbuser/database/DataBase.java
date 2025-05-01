package com.vbuser.database;

import com.vbuser.database.network.Feedback;
import com.vbuser.database.network.Operation;
import com.vbuser.database.network.Storage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Random;

@Mod(modid = "database", name = "Database")
public class DataBase {
    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("database");
        network.registerMessage(Operation.Handle.class, Operation.class, 10, Side.SERVER);
        network.registerMessage(Feedback.Handle.class, Feedback.class, 11, Side.CLIENT);
    }

    public String execute(String command, EntityPlayerMP mp) throws InterruptedException {
        double mark = new Random().nextDouble();
        network.sendToServer(new Operation(mark,command,mp.getUniqueID()));
        while(!Storage.select.containsKey(mark)){
            Thread.sleep(10);
        }
        return Storage.select.remove(mark);
    }
}
