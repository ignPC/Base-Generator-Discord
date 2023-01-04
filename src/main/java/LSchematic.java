import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sk89q.jnbt.*;

public class LSchematic {

    private byte[] blocks;
    private byte[] data;
    private short width;
    private short length;
    private short height;

    public LSchematic(byte[] blocks, byte[] data, short width, short length, short height) {
        this.blocks = blocks;
        this.data = data;
        this.width = width;
        this.length = length;
        this.height = height;
    }


    public byte[] getBlocks()
    {
        return blocks;
    }


    public byte[] getData()
    {
        return data;
    }


    public short getWidth()
    {
        return width;
    }


    public short getLength()
    {
        return length;
    }


    public short getHeight()
    {
        return height;
    }

    public void save(File file) {
        try (NBTOutputStream nbtStream = new NBTOutputStream(new FileOutputStream(file))) {
            Map<String, Tag> schematic = new HashMap<String, Tag>();

            schematic.put("Width", new ShortTag(width));
            schematic.put("Height", new ShortTag(height));
            schematic.put("Length", new ShortTag(length));
            schematic.put("Materials", new StringTag("Alpha"));
            schematic.put("Blocks", new ByteArrayTag(blocks));
            schematic.put("Data", new ByteArrayTag(data));

            nbtStream.writeNamedTag("schematic", new CompoundTag(schematic));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static int getIndexFromCoordinates(int x, int y, int z, int width, int length, int height){
        return (x + y * width + z * width * length);
    }

    public static int[] getCoordinatesFromIndex(int index, int width, int length, int height){
        int x = index % width;
        int y = (index / width) % height;
        int z = index / (width * height);

        return new int[]{x, y, z};
    }
}

