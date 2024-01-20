package com.vbuser.browser;

//E:\Desktop\Inventory\src\main\resources\assets\browser\cg\test.mp4

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.Utils;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExtractFrames {

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static boolean done,decodeDone;
    private static int currentIndex = 0,maxIndex = 0,minIndex = 0;
    public static final int limit = 100;
    public static Map<Integer,BufferedImage> imageList = new TreeMap<>();
    public static BufferedImage image;

    public static void main(String[] args){
        String inputFilename = "E:\\Desktop\\Inventory\\run\\cg\\test.mp4";
        IMediaReader mediaReader = ToolFactory.makeReader(inputFilename);
        mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
        executor.scheduleAtFixedRate(()->{
            if(!done){
                ImageMediaListener.printMinMaxKeysInRange(imageList);
                if(imageList.size()>0.4*limit){
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

        scheduler.scheduleAtFixedRate(()->{
            if(decodeDone && currentIndex<=maxIndex){
                currentIndex++;
                image = imageList.get(currentIndex);
                if(image==null) {
                    System.out.println("Err:Fail to load frame "+currentIndex);
                }
                if(done){
                    System.out.println("Max key:"+maxIndex+",Current key:"+currentIndex);
                    if(currentIndex==maxIndex){
                        System.out.println("Display finished");
                        scheduler.shutdownNow();
                    }
                }
            }
        },0,50,TimeUnit.MILLISECONDS);

        long start = System.currentTimeMillis();
        mediaReader.addListener(new ImageMediaListener());
        while (mediaReader.readPacket()==null){doNothing();}
        executor.shutdownNow();
        long end = System.currentTimeMillis();
        System.out.println("Successfully decode video in "+(end - start)/1000+"s");
        done=true;
        mediaReader.close();
    }

    public static void doNothing(){}

    static class ImageMediaListener extends MediaListenerAdapter {

        private static int frameCount = 0;

        public ImageMediaListener() {

        }

        @Override
        public void onVideoPicture(IVideoPictureEvent event) {
            if (imageList.size() < 100) {
                try {
                    IVideoPicture videoPicture = event.getPicture();

                    BufferedImage image = Utils.videoPictureToImage(videoPicture);
                    if(image==null){
                        System.out.println("Err:Xuggler error");
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