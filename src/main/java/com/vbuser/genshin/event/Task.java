package com.vbuser.genshin.event;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.*;

public class Task {

    private static String task = "";
    public static volatile String await = "";

    public static void setTask(String name,EntityPlayer player){
        if(!task.isEmpty()){
            try{
                reader.close();
            }catch (IOException e){
                throw new RuntimeException(e);
            }
            thread.stop();
            thread = null;
        }
        task = name;
        if(!name.isEmpty()){
            thread = new Thread(()->readScript(name,player));
            thread.start();
        }
    }

    public static void terminateTask(){
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        reader=null;
        thread.stop();
        thread = null;
        task = "";
    }

    private static BufferedReader reader;
    private static Thread thread;

    @SubscribeEvent
    public void tickEvent(TickEvent.WorldTickEvent event){
        if(event.phase != TickEvent.Phase.START) return;
        if(Minecraft.getMinecraft().world==null) return;

        String[] set = await.replaceAll("await ","").split(" ");
        if(set[0].equals("block_meta")){
            int x = Integer.parseInt(set[1].split(",")[0]);
            int y = Integer.parseInt(set[1].split(",")[1]);
            int z = Integer.parseInt(set[1].split(",")[2]);
            int meta = Integer.parseInt(set[2]);
            IBlockState state = event.world.getBlockState(new BlockPos(x,y,z));
            if(state.getBlock().getMetaFromState(state)==meta){
                await="";
            }
        }else if(set[0].equals("block")){
            int x = Integer.parseInt(set[1].split(",")[0]);
            int y = Integer.parseInt(set[1].split(",")[1]);
            int z = Integer.parseInt(set[1].split(",")[2]);
            Block block = Block.getBlockFromName(set[2]);
            if(event.world.getBlockState(new BlockPos(x,y,z)).getBlock().equals(block)){
                await="";
            }
        }
    }

    private static void execute(String command,EntityPlayer player){
        MinecraftServer server = player.getServer();
        if (server != null) {
            CommandHandler handler = (CommandHandler) server.getCommandManager();
            handler.executeCommand(player,command);
        }
    }

    public static void readScript(String name,EntityPlayer player){
        File worldDirectory =((EntityPlayerMP) player).getServerWorld().getSaveHandler().getWorldDirectory();
        File script = new File(worldDirectory, "\\tasks\\"+name+"\\script.txt");
        String path = script.getAbsolutePath();
        try {
            reader = new BufferedReader(new FileReader(path));
            while (true) {
                if (await.isEmpty()) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }

                    if (line.startsWith("@")) {
                        System.out.println(line.replaceAll("@", ""));
                    } else if (line.startsWith("await")) {
                        await = line;
                    } else {
                        execute(line, player);
                    }
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            terminateTask();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
