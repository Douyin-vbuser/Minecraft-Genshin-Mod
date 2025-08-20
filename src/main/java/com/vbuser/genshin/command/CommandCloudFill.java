package com.vbuser.genshin.command;

import com.vbuser.genshin.block.BlockCloud;
import com.vbuser.genshin.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class CommandCloudFill extends CommandBase {
    @Override
    public String getName() {
        return "cloudfill";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "cloudfill <x1> <y1> <z1> <x2> <y2> <z2> <baseDensity> [noiseSeed]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 7) {
            sender.sendMessage(new TextComponentString("Usage: /cloudfill <x1> <y1> <z1> <x2> <y2> <z2> <baseDensity> [noiseSeed]"));
            return;
        }

        BlockPos from = parseBlockPos(sender, args, 0, false);
        BlockPos to = parseBlockPos(sender, args, 3, false);

        BlockPos min = new BlockPos(
                Math.min(from.getX(), to.getX()),
                Math.min(from.getY(), to.getY()),
                Math.min(from.getZ(), to.getZ())
        );
        BlockPos max = new BlockPos(
                Math.max(from.getX(), to.getX()),
                Math.max(from.getY(), to.getY()),
                Math.max(from.getZ(), to.getZ())
        );

        int baseDensity = parseInt(args[6], 0, 15);

        long noiseSeed = new Random().nextLong();
        if (args.length > 7) {
            noiseSeed = Long.parseLong(args[7]);
        }

        World world = sender.getEntityWorld();
        final long finalNoiseSeed = noiseSeed;
        final int minY = min.getY();
        final int maxY = max.getY();

        CompletableFuture.runAsync(() -> {
            int totalBlocks = (max.getX() - min.getX() + 1) *
                    (max.getY() - min.getY() + 1) *
                    (max.getZ() - min.getZ() + 1);
            int processedBlocks = 0;
            int lastReportedProgress = 0;

            float minNoise = Float.MAX_VALUE;
            float maxNoise = Float.MIN_VALUE;

            for (int x = min.getX(); x <= max.getX(); x++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    for (int y = min.getY(); y <= max.getY(); y++) {
                        float noise = BlockCloud.getCloudDensityNoise(x, y, z, finalNoiseSeed, minY, maxY);

                        minNoise = Math.min(minNoise, noise);
                        maxNoise = Math.max(maxNoise, noise);

                        float threshold = 0.2f;
                        if (noise < threshold) {
                            processedBlocks++;
                            continue;
                        }

                        noise = (noise - threshold) / (1 - threshold);

                        noise = (float) Math.pow(noise, 1.5f);

                        int density = (int) (noise * baseDensity);
                        density = Math.max(1, Math.min(15, density));

                        IBlockState state = ModBlocks.CLOUD_BLOCK.getDefaultState()
                                .withProperty(BlockCloud.DENSITY, density);

                        final int finalX = x;
                        final int finalY = y;
                        final int finalZ = z;
                        final IBlockState finalState = state;

                        server.addScheduledTask(() -> world.setBlockState(new BlockPos(finalX, finalY, finalZ), finalState, 2));

                        processedBlocks++;
                        int progress = (int) ((float) processedBlocks / totalBlocks * 100);
                        if (progress > lastReportedProgress && progress % 5 == 0) {
                            lastReportedProgress = progress;
                            final int finalProgress = progress;
                            int finalProcessedBlocks = processedBlocks;
                            server.addScheduledTask(() -> sender.sendMessage(new TextComponentString(
                                    I18n.format("message.cf.process")+": " + finalProgress + "% (" + finalProcessedBlocks + "/" + totalBlocks + ")"
                            )));
                        }

                        if (processedBlocks % 1000 == 0) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
                }
            }

            final float finalMinNoise = minNoise;
            final float finalMaxNoise = maxNoise;
            int finalProcessedBlocks1 = processedBlocks;
            server.addScheduledTask(() -> {
                sender.sendMessage(new TextComponentString("Noise range: " + finalMinNoise + " to " + finalMaxNoise));
                sender.sendMessage(new TextComponentString(I18n.format("message.cf.result") + finalProcessedBlocks1));
            });
        });
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
}