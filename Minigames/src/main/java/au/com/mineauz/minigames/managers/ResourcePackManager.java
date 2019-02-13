package au.com.mineauz.minigames.managers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.objects.ResourcePack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Created for the AddstarMC Project. Created by Narimm on 12/02/2019.
 */
public class  ResourcePackManager {

    private boolean enabled = true;

    public static Path getResourceDir() {
        return resourceDir;
    }

    final static Path resourceDir = Paths.get(Minigames.getPlugin().getDataFolder().toString(),"resources");

    private Map<String, ResourcePack> resources = new HashMap<>();

    public ResourcePackManager() {
        if(!Files.notExists(resourceDir))
            try {
                Path path = Files.createDirectories(resourceDir);
                if(Files.notExists(path)){
                    Minigames.log().severe("Cannot create a resource directory to house resources " +
                            "- they will be unavailable");
                    enabled = false;
                } else {
                    if(Files.exists(path))
                    enabled = true;
                    else {
                        enabled = false;
                        Minigames.log().severe("Cannot create a resource directory to house resources " +
                                "- they will be unavailable.");
                    }
                }

            }catch (IOException e){
                Minigames.log().severe("Cannot create a resource directory to house resources " +
                        "- they will be unavailable: Message" + e.getMessage() );
                enabled = false;
            }
    }

    private boolean loadEmptyPack() {
        try {
            URL u = new URL("https://github.com/AddstarMC/Minigames/raw/resourcepack/Minigames/src/main/resources/resourcepack/emptyResourcePack.zip");
            ResourcePack empty = new ResourcePack("empty",u);
            if(empty.isValid()){
                resources.put("empty",empty);
            }
            return true;
        }catch (MalformedURLException e){
            return false;
        }
    }
    
    public ResourcePack getResourcePack(String name){
        if(!enabled)return null;
        ResourcePack pack = resources.get(name);
        if(pack != null && pack.isValid()) return pack;
        else return null;
    }
    
    public ResourcePack addResourcePack(ResourcePack pack){
        if(!enabled)return null;
        return resources.put(pack.getName(),pack);
    };
    
    public boolean initialize(ConfigurationSection config){
        Set<String> keys =  config.getKeys(false);
        boolean emptyPresent = false;
        for(String key:keys){
            ConfigurationSection section = config.getConfigurationSection(key);
            if (key == "empty")emptyPresent = true;
            String url = section.getString("url");
                try {
                    URL u = new URL(url);
                    File local = new File(resourceDir.toFile(), key + ".resourcepack");
                    ResourcePack pack = new ResourcePack(key, u, local);
                    addResourcePack(pack);
                } catch (MalformedURLException e) {
                    Minigames.log().warning("Minigames Resource:" + key + " could not load see following error");
                    Minigames.log().warning(e.getMessage());
                    continue;
                }
            
        }
        if(!emptyPresent){
            if(!loadEmptyPack()){
                Minigames.log().warning("Minigames Resource Manager could not create the empty reset pack");
                enabled = false;
                return false;
            }
            ConfigurationSection sec = config.createSection("empty");
            sec.set("url","https://github.com/AddstarMC/Minigames/raw/resourcepack/Minigames/src/main/resources/resourcepack/emptyResourcePack.zip");
            Minigames.log().warning("You will need to update your minigames main config and replace the url for "
                   + "the empty resource pack.  Please see the wiki");
            enabled = true;
        }
        return true;
    }


}
