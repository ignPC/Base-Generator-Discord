import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
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

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Base Schematic");
        embedBuilder.setDescription("Schematic created using BaseBuilder");

        embedBuilder.addField("Base length", String.valueOf(baselength), false);
        embedBuilder.addField("Walls length", String.valueOf(wallslength), false);
        embedBuilder.addField("Block", wallsblock, false);

        if (event.getOption("border-offset-blocks") != null) {
            embedBuilder.addField("Border offset blocks", String.valueOf(borderOffsetBlocks), false);
        }

        if (event.getOption("ocean-chunk-offset") != null) {
            embedBuilder.addField("Ocean chunk offset", String.valueOf(oceanChunkOffset), false);
        }

        if (event.getOption("sand-walls") != null) {
            embedBuilder.addField("Sand walls", String.valueOf(sandWalls), false);
        }

        if (event.getOption("counter-length") != null) {
            embedBuilder.addField("Counter length", String.valueOf(counterLength), false);
        }

        if (event.getOption("counter-width") != null) {
            embedBuilder.addField("Counter width", String.valueOf(counterWidth), false);
        }

        if (event.getOption("counter-wall-length") != null) {
            embedBuilder.addField("Counter wall length", String.valueOf(counterWallLength), false);
        }
        embedBuilder.setColor(Color.BLUE);

        MessageEmbed embed = embedBuilder.build();

        ImageGenerator.CreateImageFromByteArray(generator.getBlocks(), generator.getWidthLength(), generator.getWidthLength());

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        event.getHook().sendMessageEmbeds(embed).queue();
        event.getHook().sendFiles(FileUpload.fromData(new File("schematics\\baseschem.schematic")), FileUpload.fromData(new File("schematics\\image.png"))).queue();
    }
}
