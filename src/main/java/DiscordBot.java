import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class DiscordBot {
    private static DiscordBot instance;
    private JDA jda;

    public DiscordBot(String token){
        instance = this;
        jda = JDABuilder.createDefault(token)
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setBulkDeleteSplittingEnabled(true)
                .setActivity(Activity.playing("Minecraft"))
                .setLargeThreshold(50)
                .addEventListeners(
                        new LCommands()
                )
                .build();

        jda.updateCommands().addCommands(
                Commands.slash("build", "Start building a base!")
                        .addOption(OptionType.INTEGER, "base-length", "The base length in chunks", true)
                        .addOption(OptionType.INTEGER, "wall-length", "The wall length in chunks", true)
                        .addOption(OptionType.STRING, "wall-block", "The block that the base will be made out of (obsidian or cobblestone)", true)
                        .addOption(OptionType.INTEGER, "border-offset-blocks", "The number of blocks to offset the border by", false)
                        .addOption(OptionType.BOOLEAN, "ocean-chunk", "Whether to generate an ocean chunk", false)
                        .addOption(OptionType.INTEGER, "ocean-chunk-offset", "The gap between the ocean chunk and the front wall in chunks", false)
                        .addOption(OptionType.BOOLEAN, "sand-walls", "Whether to generate sand walls", false)
                        .addOption(OptionType.BOOLEAN, "counter", "Whether to generate a counter", false)
                        .addOption(OptionType.INTEGER, "counter-length", "The length of the counter in blocks", false)
                        .addOption(OptionType.INTEGER, "counter-width", "The width of the counter in blocks", false)
                        .addOption(OptionType.INTEGER, "counter-wall-length", "The length of the counter walls in chunks", false)
        ).queue();
    }


    public static DiscordBot getInstance(){
        return instance;
    }

    public JDA getJDA(){
        return jda;
    }
}
