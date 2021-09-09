package surf.pvp.practice.profile;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import surf.pvp.practice.kit.Kit;
import surf.pvp.practice.match.Match;
import surf.pvp.practice.profile.hotbar.HotbarItem;
import surf.pvp.practice.profile.loadout.CustomLoadOut;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class Profile {

    private final UUID uuid;

    private final List<CustomLoadOut> customLoadOutMap = new ArrayList<>();
    private final Map<Kit, Integer> eloMap = new HashMap<>();

    private int win, loss, xp, coins;

    private ProfileState profileState = ProfileState.LOBBY;
    private Match match;

    /**
     * Contructs a profile
     *
     * @param uuid uuid of the profile
     */

    public Profile(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the elo of a kit
     *
     * @param kit kit to get the elo of
     * @return {@link Integer}
     */

    public final int getElo(Kit kit) {
        return eloMap.get(kit);
    }

    /**
     * Gets all the loud outs of a kit
     *
     * @param kit kit to get the load outs of
     * @return {@link List<CustomLoadOut>}
     */

    public final List<CustomLoadOut> getLoudOut(Kit kit) {
        final List<CustomLoadOut> customLoadOuts = customLoadOutMap.stream().filter(loadOut -> loadOut.getKit().getName().equalsIgnoreCase(kit.getName())).collect(Collectors.toList());

        if (!customLoadOuts.isEmpty()) {
            customLoadOuts.add(0, new CustomLoadOut(kit, kit.getInventoryContents(), kit.getArmorContents()));
            return customLoadOuts;
        }

        return Collections.singletonList(new CustomLoadOut(kit, kit.getInventoryContents(), kit.getArmorContents()));
    }

    /**
     * Gives all books to the player
     *
     * @param player         player
     * @param customLoadOuts custom load outs
     */

    public final void giveBooks(Player player, List<CustomLoadOut> customLoadOuts) {
        customLoadOuts.forEach(customLoadOut -> {
            ItemStack stack = HotbarItem.BOOK.getItemStack();
            ItemMeta meta = stack.getItemMeta();

            meta.setDisplayName(meta.getDisplayName().replace("<number>", String.valueOf(customLoadOuts.indexOf(customLoadOut))));

            stack.setItemMeta(meta);

            player.getInventory().addItem(stack);
        });
    }

    /**
     * Creates a bson object and
     * stores data in it
     *
     * @return {@link Document}
     */

    public final Document toBson() {
        return new Document("_id", uuid.toString())
                .append("win", win)
                .append("loss", loss)
                .append("xp", xp)
                .append("coins", coins)
                .append("elo", eloMap.entrySet().stream().map(kitIntegerEntry -> kitIntegerEntry.getKey().getName() + ":" + kitIntegerEntry.getValue()).collect(Collectors.toList()))
                .append("loadout", customLoadOutMap.stream().map(CustomLoadOut::serialize).collect(Collectors.toList()));
    }

}