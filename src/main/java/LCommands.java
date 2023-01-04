import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class LCommands extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event){
        if (event.getName().equals("build")){
            event.deferReply().queue();

            genBase(event);
        }
    }

    private void genBase(SlashCommandInteractionEvent event) {
        OptionMapping option1 = event.getOption("base-length");
        OptionMapping option2 = event.getOption("wall-length");
        OptionMapping option3 = event.getOption("wall-block");


        int baselength = event.getOption("base-length").getAsInt();
        int wallslength = event.getOption("wall-length").getAsInt();
        String wallsblock = event.getOption("wall-block").getAsString();

        int borderOffsetBlocks = 0;
        if(event.getOption("border-offset-blocks") != null){
            borderOffsetBlocks = event.getOption("border-offset-blocks").getAsInt();
        }

        boolean oceanChunk = false;
        if (event.getOption("ocean-chunk") != null) {
            oceanChunk = event.getOption("ocean-chunk").getAsBoolean();
        }

        int oceanChunkOffset = 1;
        if (event.getOption("ocean-chunk-offset") != null) {
            oceanChunkOffset = event.getOption("ocean-chunk-offset").getAsInt();
        }

        boolean sandWalls = true;
        if (event.getOption("sand-walls") != null) {
            sandWalls = event.getOption("sand-walls").getAsBoolean();
        }

        boolean counter = false;
        if (event.getOption("counter") != null) {
            counter = event.getOption("counter").getAsBoolean();
        }

        int counterLength = 30;
        if (event.getOption("counter-length") != null) {
            counterLength = event.getOption("counter-length").getAsInt();
        }

        int counterWidth = 22;
        if (event.getOption("counter-width") != null) {
            counterWidth = event.getOption("counter-width").getAsInt();
        }

        int counterWallLength = 5;
        if (event.getOption("counter-wall-length") != null) {
            counterWallLength = event.getOption("counter-wall-length").getAsInt();
        }


        BaseGenerator generator = new BaseGenerator.BaseGeneratorBuilder(baselength, wallslength, LBlock.getBlockTypeFromString(wallsblock))
                .borderOffsetBlocks(borderOffsetBlocks)
                .oceanChunk(oceanChunk, oceanChunkOffset)
                .counter(counter, counterLength, counterWidth, counterWallLength, LBlock.SAND)
                .sandWalls(sandWalls)
                .build();

        try {
            generator.createSchematic();
        } catch (IOException e) {
            event.getHook().sendMessage("An error occurred, Try again!").queue();
            e.printStackTrace();
        }

        String message = "Generating base: \n";

        message += "Base length: " + baselength + "\n";
        message += "Walls length: " + wallslength + "\n";
        message += "Block: " + wallsblock + "\n";

        if (event.getOption("border-offset-blocks") != null) {
            message += "Border offset blocks: " + borderOffsetBlocks + "\n";
        }

        if (event.getOption("ocean-chunk") != null) {
            message += "Ocean chunk: " + oceanChunk + "\n";
        }

        if (event.getOption("ocean-chunk-offset") != null) {
            message += "Ocean chunk offset: " + oceanChunkOffset + "\n";
        }

        if (event.getOption("sand-walls") != null) {
            message += "Sand walls: " + sandWalls + "\n";
        }

        if (event.getOption("counter") != null) {
            message += "Counter: " + counter + "\n";
        }

        if (event.getOption("counter-length") != null) {
            message += "Counter length: " + counterLength + "\n";
        }

        if (event.getOption("counter-width") != null) {
            message += "Counter width: " + counterWidth + "\n";
        }

        if (event.getOption("counter-wall-length") != null) {
            message += "Counter wall length: " + counterWallLength;
        }

        event.getHook().sendMessage(message).addFiles(FileUpload.fromData(new File("baseschem.schematic"))).queue();

    }
}
