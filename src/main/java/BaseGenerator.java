import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BaseGenerator {
    private final int schematicLengthBlocks;

    private final int wallLengthChunks;
    private final int wallLengthBlocks;
    private final int baseLengthChunks;

    //optional parameters, default to 0 or false
    private final int borderOffsetBlocks;

    private final boolean sandWalls;

    private final boolean ocean;
    private final int oceanOffsetChunks;

    private final boolean counter;
    private final int counterLength;
    private final int counterWidth;
    private final int counterWallsLengthChunks;
    private final LBlock counterSlitsBlock;
    private final LBlock wallBlock;

    private ArrayList<Byte> blocks = new ArrayList<>();
    private ArrayList<Byte> data = new ArrayList<>();

    private byte[] blocks_array;
    private byte[] data_array;

    public byte[] getBlocks() {
        return blocks_array;
    }

    public byte[] getData() {
        return data_array;
    }

    public int getWidthLength(){
        return schematicLengthBlocks;
    }

    private BaseGenerator(BaseGeneratorBuilder builder) {
        this.wallLengthChunks = builder.wallsLengthChunks;
        this.wallLengthBlocks = builder.wallsLengthChunks * 16;
        this.baseLengthChunks = builder.baseLengthChunks;
        this.wallBlock = builder.wallBlock;

        this.schematicLengthBlocks = (builder.wallsLengthChunks * 16) + (builder.baseLengthChunks * 16) + builder.borderOffsetBlocks;

        this.ocean = builder.ocean;
        this.oceanOffsetChunks = builder.oceanOffsetChunks;

        this.counter = builder.counter;
        this.counterLength = builder.counterLength;
        this.counterWidth = builder.counterWidth;
        this.counterWallsLengthChunks = builder.counterWallsLengthChunks;
        this.counterSlitsBlock = builder.counterSlitsBlock;

        this.sandWalls = builder.sandWalls;
        this.borderOffsetBlocks = builder.borderOffsetBlocks;
    }


    public void createSchematic() throws IOException {
        int width = schematicLengthBlocks;
        int length = schematicLengthBlocks;
        int height = 1;

        System.out.println("WIDTH " +width);
        System.out.println("LENGTH " +length);

        blocks_array = new byte[width * height * length];
        data_array = new byte[width * height * length];

        //base building
        initialise();
        addBase(width, length);
        addWalls(width, length);
        addOutsideWalls(width, length);
        if (ocean) addOcean(width, length);
        if (counter) addCounter(width, length);

        copyArrays();
        saveSchematic(width, length, height);
    }

    private void saveSchematic(int width, int length, int height) throws IOException {
        LSchematic schematic = new LSchematic(blocks_array, data_array, (short) width, (short) length, (short) height);

        File file = new File("schematics\\baseschem.schematic");

        if(!file.exists()){
            if(!file.delete()) System.out.println("deletion error");
            if(!file.createNewFile()) System.out.println("file creation error");
        }
        schematic.save(file);
    }

    private void copyArrays(){
        for(int i = 0; i < blocks_array.length; i++){
            blocks_array[i] = blocks.get(i);
            data_array[i] = data.get(i);
        }
    }


    //======================================Base Building Functions======================================

    private void initialise() {
        for (int i = 0; i < blocks_array.length; i++) {
            blocks.add(LBlock.getIdFromBlockType(LBlock.NETHERRACK));
            //data is for a block's metadata, default to 0
            data.add((byte) 0);
        }
    }

    private void addBase(int width, int length){
        int height = 1;

        for (int i = 0; i < blocks.size(); i++) {
            int[] coordinates = LSchematic.getCoordinatesFromIndex(i, width, length, height);
            int x = coordinates[0];
            int y = coordinates[1];
            int z = coordinates[2];

            if(! (x >= borderOffsetBlocks)) continue;
            if(! (z >= borderOffsetBlocks)) continue;

            if(! (x < borderOffsetBlocks + baseLengthChunks * 16)) continue;
            if(! (z < borderOffsetBlocks + baseLengthChunks * 16)) continue;

            byte obsidian = LBlock.getIdFromBlockType(LBlock.OBSIDIAN);

            blocks.set(i, obsidian);
        }
    }


    private void addWalls(int width, int length){
        byte sand = LBlock.getIdFromBlockType(LBlock.SAND);

        int height = 1;

        //x walls
        for (int i = 0; i < blocks.size(); i++) {
            int[] coordinates = LSchematic.getCoordinatesFromIndex(i, width, length, height);
            int x = coordinates[0];
            int y = coordinates[1];
            int z = coordinates[2];

            //check if x greater than base
            if(! (x > borderOffsetBlocks + baseLengthChunks * 16)) continue;
            //check if z greater than border offset
            if(! (z >= borderOffsetBlocks - 1)) continue;
            //check if z smaller than base length + 1
            if(! (z <= borderOffsetBlocks + baseLengthChunks * 16 + 1)) continue;
            //check if x is even / uneven depending on border offset
            if((borderOffsetBlocks % 2 == 0 && x % 2 == 0) || (borderOffsetBlocks % 2 != 0 && x % 2 != 0)) continue;

            //sand walls :skull:

            int base = borderOffsetBlocks + baseLengthChunks * 16;

            if(!sandWalls){
                blocks.set(i, LBlock.getIdFromBlockType(wallBlock));
            }
            else {
                if (!((x - base + 1) % 16 == 0)) {
                    blocks.set(i, LBlock.getIdFromBlockType(wallBlock));
                } else {
                    blocks.set(i, sand);
                }
                //slits
                if (!counter) continue;
                if (z % 9 == 0 && x > (width - counterWallsLengthChunks * 16 - counterWidth + 2)) {
                    blocks.set(i, LBlock.getIdFromBlockType(counterSlitsBlock));
                }
            }
        }

        //z walls
        for (int i = 0; i < blocks.size(); i++) {
            int[] coordinates = LSchematic.getCoordinatesFromIndex(i, width, length, height);
            int x = coordinates[0];
            int y = coordinates[1];
            int z = coordinates[2];

            //check if z greater than base
            if (!(z > borderOffsetBlocks + baseLengthChunks * 16)) continue;
            //check if x greater than border offset
            if (!(x >= borderOffsetBlocks - 1)) continue;
            //check if x smaller than base length + 1
            if (!(x <= borderOffsetBlocks + baseLengthChunks * 16 + 1)) continue;
            //check if z is even / uneven depending on border offset
            if ((borderOffsetBlocks % 2 == 0 && z % 2 == 0) || (borderOffsetBlocks % 2 != 0 && z % 2 != 0)) continue;

            //sand walls :skull:
            int base = borderOffsetBlocks + baseLengthChunks * 16;

            if(!sandWalls){
                blocks.set(i, LBlock.getIdFromBlockType(wallBlock));
            }
            else {
                if (!((z - base + 1) % 16 == 0)) {
                    blocks.set(i, LBlock.getIdFromBlockType(wallBlock));
                } else {
                    blocks.set(i, sand);
                }
                //slits
                if (!counter) continue;
                if (x % 9 == 0 && z > (width - counterWallsLengthChunks * 16 - counterWidth + 2)) {
                    blocks.set(i, LBlock.getIdFromBlockType(counterSlitsBlock));
                }
            }
        }
    }


    private void addOcean(int width, int length){
        int height = 1;
        byte netherrack = LBlock.getIdFromBlockType(LBlock.NETHERRACK);

        for (int i = 0; i < blocks.size(); i++) {
            int[] coordinates = LSchematic.getCoordinatesFromIndex(i, width, length, height);
            int x = coordinates[0];
            int y = coordinates[1];
            int z = coordinates[2];

            //check if z greater than base
            if(! (z > borderOffsetBlocks + baseLengthChunks * 16)) continue;
            //check if x greater than border offset
            if(! (x >= borderOffsetBlocks - 1)) continue;
            //check if x smaller than base length + 1
            if(! (x <= borderOffsetBlocks + baseLengthChunks * 16 + 1)) continue;

            if((z < length - 16 * oceanOffsetChunks) && (z > length - 16 * oceanOffsetChunks - 16)) {
                blocks.set(i, netherrack);
            }
        }

        for (int i = 0; i < blocks.size(); i++) {
            int[] coordinates = LSchematic.getCoordinatesFromIndex(i, width, length, height);
            int x = coordinates[0];
            int y = coordinates[1];
            int z = coordinates[2];

            //check if z greater than base
            if(! (x > borderOffsetBlocks + baseLengthChunks * 16)) continue;
            //check if x greater than border offset
            if(! (z >= borderOffsetBlocks - 1)) continue;
            //check if x smaller than base length + 1
            if(! (z <= borderOffsetBlocks + baseLengthChunks * 16 + 1)) continue;

            if((x < length - 16 * oceanOffsetChunks) && (x > length - 16 * oceanOffsetChunks - 16)) {
                blocks.set(i, netherrack);
            }
        }
    }

    private void addCounter(int width, int length){
        int height = 1;
        byte obsidian = LBlock.getIdFromBlockType(LBlock.OBSIDIAN);

        //x counter
        for (int i = 0; i < blocks.size(); i++) {
            int[] coordinates = LSchematic.getCoordinatesFromIndex(i, width, length, height);
            int x = coordinates[0];
            int y = coordinates[1];
            int z = coordinates[2];

            if(! (x < width - counterWallsLengthChunks * 16)) continue;
            if(! (x >= width - (counterWallsLengthChunks * 16 + counterLength))) continue;

            if(! (z < length - (counterWallsLengthChunks * 16 + counterLength + 6))) continue;
            if(! (z >= length - (counterWallsLengthChunks * 16 + counterLength + 6 + counterWidth))) continue;

            blocks.set(i, obsidian);
        }

        //z counter
        for (int i = 0; i < blocks.size(); i++) {
            int[] coordinates = LSchematic.getCoordinatesFromIndex(i, width, length, height);
            int x = coordinates[0];
            int y = coordinates[1];
            int z = coordinates[2];

            if(! (z < length - counterWallsLengthChunks * 16)) continue;
            if(! (z >= length - (counterWallsLengthChunks * 16 + counterLength))) continue;

            if(! (x < width - (counterWallsLengthChunks * 16 + counterLength + 6))) continue;
            if(! (x >= width - (counterWallsLengthChunks * 16 + counterLength + 6 + counterWidth))) continue;

            blocks.set(i, obsidian);
        }

        addCounterFrontWalls(width, length);
        addCounterSideWalls(width, length);
    }

    private void addCounterFrontWalls(int width, int length){
        int height = 1;

        byte sand = LBlock.getIdFromBlockType(LBlock.SAND);

        //x front counter walls
        for (int i = 0; i < blocks.size(); i++) {
            int[] coordinates = LSchematic.getCoordinatesFromIndex(i, width, length, height);
            int x = coordinates[0];
            int y = coordinates[1];
            int z = coordinates[2];

            if(!(x < width + 1)) continue;
            if(!(x > width - counterWallsLengthChunks * 16)) continue;

            if(! (z < length - (counterWallsLengthChunks * 16 + counterLength + 4))) continue;
            if(! (z >= length - (counterWallsLengthChunks * 16 + counterLength + 6 + counterWidth + 2))) continue;

            int counter =  width - (counterWallsLengthChunks * 16 + 1);

            if(!((counter % 2 == 0 && x % 2 == 0) || (counter % 2 != 0 && x % 2 != 0))) continue;

            //sand walls :skull:

            if(!sandWalls){
                blocks.set(i, LBlock.getIdFromBlockType(wallBlock));
            }
            else {
                if (!((x - counter) % 16 == 0)) {
                    blocks.set(i, LBlock.getIdFromBlockType(wallBlock));
                } else {
                    blocks.set(i, sand);
                }
                if (z % 9 == 0){
                    blocks.set(i, LBlock.getIdFromBlockType(counterSlitsBlock));
                }
            }
        }

        //z front counter walls
        for (int i = 0; i < blocks.size(); i++) {
            int[] coordinates = LSchematic.getCoordinatesFromIndex(i, width, length, height);
            int x = coordinates[0];
            int y = coordinates[1];
            int z = coordinates[2];

            if(!(z < length + 1)) continue;
            if(!(z > length - counterWallsLengthChunks * 16)) continue;

            if(! (x < width - (counterWallsLengthChunks * 16 + counterLength + 4))) continue;
            if(! (x >= width - (counterWallsLengthChunks * 16 + counterLength + 6 + counterWidth + 2))) continue;

            int counter =  length - (counterWallsLengthChunks * 16 + 1);

            if(!((counter % 2 == 0 && z % 2 == 0) || (counter % 2 != 0 && z % 2 != 0))) continue;

            //sand walls :skull:

            if(!sandWalls){
                blocks.set(i, LBlock.getIdFromBlockType(wallBlock));
            }
            else {
                if (!((z - counter) % 16 == 0)) {
                    blocks.set(i, LBlock.getIdFromBlockType(wallBlock));
                } else {
                    blocks.set(i, sand);
                }
                if (x % 9 == 0){
                    blocks.set(i, LBlock.getIdFromBlockType(counterSlitsBlock));
                }
            }
        }
    }

    private void addCounterSideWalls(int width, int length) {
        int height = 1;

        byte sand = LBlock.getIdFromBlockType(LBlock.SAND);

        //x side counter walls
        for (int i = 0; i < blocks.size(); i++) {
            int[] coordinates = LSchematic.getCoordinatesFromIndex(i, width, length, height);
            int x = coordinates[0];
            int y = coordinates[1];
            int z = coordinates[2];

            if (!(x < width + 1)) continue;
            if (!(x > width - counterWallsLengthChunks * 16)) continue;

            if (!(z < length - (counterWallsLengthChunks * 16 - 2))) continue;
            if (!(z >= length - (counterWallsLengthChunks * 16 + counterLength + 2))) continue;

            int counter = width - (counterWallsLengthChunks * 16 + 1);

            if (!((counter % 2 == 0 && x % 2 == 0) || (counter % 2 != 0 && x % 2 != 0))) continue;


            if (!sandWalls) {
                blocks.set(i, LBlock.getIdFromBlockType(wallBlock));
            } else {
                if (!((x - counter) % 16 == 0)) {
                    blocks.set(i, LBlock.getIdFromBlockType(wallBlock));
                } else {
                    blocks.set(i, sand);
                }
                if (z % 9 == 0) {
                    blocks.set(i, LBlock.getIdFromBlockType(counterSlitsBlock));
                }
            }
        }

        //z side counter walls
        for (int i = 0; i < blocks.size(); i++) {
            int[] coordinates = LSchematic.getCoordinatesFromIndex(i, width, length, height);
            int x = coordinates[0];
            int y = coordinates[1];
            int z = coordinates[2];

            if (!(z < length + 1)) continue;
            if (!(z > length - counterWallsLengthChunks * 16)) continue;

            if (!(x < width - (counterWallsLengthChunks * 16 - 2))) continue;
            if (!(x >= width - (counterWallsLengthChunks * 16 + counterLength + 2))) continue;

            int counter = length - (counterWallsLengthChunks * 16 + 1);

            if (!((counter % 2 == 0 && z % 2 == 0) || (counter % 2 != 0 && z % 2 != 0))) continue;


            if (!sandWalls) {
                blocks.set(i, LBlock.getIdFromBlockType(wallBlock));
            } else {
                if (!((z - counter) % 16 == 0)) {
                    blocks.set(i, LBlock.getIdFromBlockType(wallBlock));
                } else {
                    blocks.set(i, sand);
                }
                if (x % 9 == 0) {
                    blocks.set(i, LBlock.getIdFromBlockType(counterSlitsBlock));
                }
            }
        }
    }

    private void addOutsideWalls(int width, int length){
        int height = 1;
        for (int i = 0; i < blocks.size(); i++) {
            int[] coordinates = LSchematic.getCoordinatesFromIndex(i, width, length, height);
            int x = coordinates[0];
            int y = coordinates[1];
            int z = coordinates[2];
            if(x == width-1 || z == length-1) blocks.set(i, LBlock.getIdFromBlockType(LBlock.SAND));
        }
    }


    //===========================================Builder Class============================================

    public static class BaseGeneratorBuilder {
        private final int baseLengthChunks;
        private int wallsLengthChunks;
        private final LBlock wallBlock;

        //Optional parameters
        private boolean sandWalls = false;

        private boolean ocean = false;
        private int oceanOffsetChunks = 1;

        private boolean counter = false;
        private int counterWidth = 20;
        private int counterLength = 30;
        private int counterWallsLengthChunks = 5;
        private LBlock counterSlitsBlock = LBlock.SAND;

        private int borderOffsetBlocks = 0;

        public BaseGeneratorBuilder(int baseLengthChunks, int wallsLengthChunks, LBlock wallBlock) {
            this.baseLengthChunks = baseLengthChunks;
            this.wallsLengthChunks = wallsLengthChunks;
            this.wallBlock = wallBlock;
        }

        public BaseGeneratorBuilder oceanChunk(boolean oceanChunk, int chunkOffset) {
            this.ocean = oceanChunk;
            this.oceanOffsetChunks = chunkOffset;
            this.wallsLengthChunks++;
            return this;
        }

        public BaseGeneratorBuilder sandWalls(boolean sandWalls) {
            this.sandWalls = sandWalls;
            return this;
        }

        public BaseGeneratorBuilder borderOffsetBlocks(int borderOffsetBlocks) {
            this.borderOffsetBlocks = borderOffsetBlocks;
            return this;
        }

        public BaseGeneratorBuilder counter(boolean counter, int counterLength, int counterWidth, int counterWallsLengthChunks, LBlock counterSlitsBlock){
            this.counter = counter;
            this.counterLength = counterLength;
            this.counterWidth = counterWidth;
            this.counterWallsLengthChunks = counterWallsLengthChunks;
            this.counterSlitsBlock = counterSlitsBlock;
            return this;
        }

        public BaseGenerator build() {
            return new BaseGenerator(this);
        }
    }
}

