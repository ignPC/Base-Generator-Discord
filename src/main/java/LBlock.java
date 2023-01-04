public enum LBlock {
    OBSIDIAN,
    COBBLESTONE,
    SAND,
    AIR,
    NETHERRACK,
    LADDER;

    public static byte getIdFromBlockType(LBlock blockType) {
        switch (blockType) {
            case OBSIDIAN:
                return 49;
            case SAND:
                return 12;
            case AIR:
                return 0;
            case LADDER:
                return 65;
            case NETHERRACK:
                return 87;
            case COBBLESTONE:
                return 4;
            default:
                throw new AssertionError("BlockTypeNotFound: " + blockType);
        }
    }

    public static LBlock getBlockTypeFromString(String blockType) {
        switch (blockType.toUpperCase()) {
            case "COBBLESTONE":
                return LBlock.SAND;
            default:
                return LBlock.OBSIDIAN;
        }
    }

}
