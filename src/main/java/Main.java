import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        //createBase();
        startDiscord();
    }

    public static void startDiscord(){
        new DiscordBot("MTA1OTgxMzIwMDMzNTQ5MTEyMw.GWg0Fz.alPICLxFy15gvEi3gg7iDA0tc2mCZbukiLlF28");
    }

    public static void createBase(){
        BaseGenerator generator = new BaseGenerator.BaseGeneratorBuilder(3, 20, LBlock.getBlockTypeFromString("obsidian"))
                .borderOffsetBlocks(16)
                .oceanChunk(true, 1)
                .counter(true, 25, 12, 5, LBlock.SAND)
                .sandWalls(true)
                .build();

        try {
            generator.createSchematic();
            ImageGenerator.CreateImageFromByteArray(generator.getBlocks(), generator.getWidthLength(), generator.getWidthLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
