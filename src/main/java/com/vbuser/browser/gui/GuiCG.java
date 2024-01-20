package com.vbuser.browser.gui;

import com.vbuser.browser.Browser;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GuiCG extends GuiScreen {
    private static ScheduledExecutorService executor;
    private static ScheduledExecutorService scheduler;
    private static ExecutorService thread;
    private static boolean done,decodeDone;
    private static int currentIndex,maxIndex,minIndex;
    public static final int limit = 100;
    public static Map<Integer,BufferedImage> imageList;
    private BufferedImage image;
    private int textureID;

    public GuiCG(){
        currentIndex=0;maxIndex=0;minIndex=0;
        imageList = new TreeMap<>();
        executor = Executors.newScheduledThreadPool(1);
        scheduler = Executors.newScheduledThreadPool(1);
        thread = Executors.newFixedThreadPool(1);
        thread.submit(this::display);
    }

    public void display(){
        String inputFilename =  Minecraft.getMinecraft().mcDataDir+"/cg/"+ Browser.video+".mp4";
        if(!(new File(inputFilename)).exists()){
            onGuiClosed();
            Minecraft.getMinecraft().displayGuiScreen(null);
            return;
        }
        IMediaReader mediaReader = ToolFactory.makeReader(inputFilename);
        mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
        executor.scheduleAtFixedRate(()->{
            if(!done){
                ImageMediaListener.printMinMaxKeysInRange(imageList);
                if(imageList.size()>0.6*limit){
                    decodeDone = true;
                }
                if(imageList.size()>0.8*limit){
                    ImageMediaListener.removeMinKeys(imageList);
                    if(decodeDone && currentIndex<=maxIndex){
                        if(currentIndex<minIndex){
                            currentIndex = minIndex;
                        }
                    }
                }
                System.out.println("Max key:"+maxIndex+",Min key:"+minIndex+",Current key:"+currentIndex);
            }
        },0,1, TimeUnit.SECONDS);

        long start = System.currentTimeMillis();
        mediaReader.addListener(new ImageMediaListener());
        while (mediaReader.readPacket()==null){doNothing();}
        executor.shutdownNow();
        long end = System.currentTimeMillis();
        System.out.println("Successfully decode video in "+(end - start)/1000+"s");
        done=true;
        mediaReader.close();
        thread.shutdownNow();
    }

    @Override
    public void updateScreen(){
        if(decodeDone && currentIndex<=maxIndex){
            currentIndex++;
            image = imageList.get(currentIndex);
            if(image==null) {
                System.out.println("Err:Fail to load frame "+currentIndex);
            }
            if (image!=null) {
                textureID = TextureUtil.uploadTextureImageAllocate(textureID, image, false, false);

                GlStateManager.bindTexture(textureID);
                GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
                GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
            }
            if(done){
                System.out.println("Max key:"+maxIndex+",Current key:"+currentIndex);
                if(currentIndex==maxIndex){
                    System.out.println("Display finished");
                    scheduler.shutdownNow();
                    mc.displayGuiScreen(null);
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        if (decodeDone && image != null) {
            int windowWidth = this.width;
            int windowHeight = this.height;
            float scaleX = (float) windowWidth / image.getWidth();
            float scaleY = (float) windowHeight / image.getHeight();
            float scale = Math.min(scaleX, scaleY);

            GlStateManager.pushMatrix();
            GlStateManager.translate((float) windowWidth / 2, (float) windowHeight / 2, 0.0F);
            GlStateManager.scale(scale, scale, 1.0F);
            GlStateManager.translate(-(float) image.getWidth() / 2, -(float) image.getHeight() / 2, 0.0F);

            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableDepth();
            GlStateManager.bindTexture(textureID);
            drawModalRectWithCustomSizedTexture(0, 0, 0, 0, image.getWidth(), image.getHeight(), image.getWidth(), image.getHeight());

            GlStateManager.enableDepth();
            GlStateManager.disableAlpha();
            GlStateManager.popMatrix();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    public static void doNothing(){}

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        scheduler.shutdownNow();
        executor.shutdownNow();
        thread.shutdownNow();
    }

    static class ImageMediaListener extends MediaListenerAdapter {
        private static int frameCount = 0;

        @Override
        public void onVideoPicture(IVideoPictureEvent event) {
            if (imageList.size() < limit) {
                try {
                    IVideoPicture videoPicture = event.getPicture();

                    BufferedImage image = Utils.videoPictureToImage(videoPicture);
                    if(image==null){
                        System.out.println("Err:Xuggler error.");
                    }

                    imageList.put(frameCount, image);
                    frameCount++;

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private static void removeMinKeys(Map<Integer, BufferedImage> treeMap) {
            Iterator<Map.Entry<Integer, BufferedImage>> iterator = treeMap.entrySet().iterator();
            for (int i = 0; i < 20 && iterator.hasNext(); i++) {
                iterator.next();
                iterator.remove();
            }
        }

        private static void printMinMaxKeysInRange(Map<Integer, BufferedImage> imageList) {
            int maxKey = Integer.MIN_VALUE;
            int minKey = Integer.MAX_VALUE;

            for (int key : imageList.keySet()) {
                if (key > maxKey) {
                    maxKey = key;
                }

                if (key < minKey) {
                    minKey = key;
                }
            }

            maxIndex = maxKey;
            minIndex = minKey;
        }
    }
}
