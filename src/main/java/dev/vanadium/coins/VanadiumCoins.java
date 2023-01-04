/*
Copyright (c) 2015 - 2023 Michael Steinmötzger
All rights are reserved for this project, unless otherwise
stated in a license file.
*/

package dev.vanadium.coins;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.reactivestreams.client.MongoCollection;
import dev.vanadium.coins.commands.CoinsCommand;
import dev.vanadium.coins.config.Configuration;
import dev.vanadium.coins.event.PlayerEvents;
import dev.vanadium.coins.mongo.MongoManager;
import lombok.AccessLevel;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class VanadiumCoins extends JavaPlugin {

    @Getter
    private static String prefix = "§8» §b§lVanadium §7",
            noPerm = prefix + "§cYou don't have enough permissions to perform this action.";

    @Getter()
    private Gson gson;
    @Getter
    private Configuration configuration;
    @Getter
    private MongoManager mongoManager;
    @Getter
    private static VanadiumCoins instance;
    @Getter
    private MongoCollection<Document> collection;

    @Override
    public void onEnable() {
        instance = this;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        try {
            this.initConfig();
        } catch (IOException e) {
            System.out.println("Failed to load configuration:");
            throw new RuntimeException(e);
        }
        this.mongoManager = new MongoManager(
                this.configuration.getMongoConnection().getHostname(),
                this.configuration.getMongoConnection().getUsername(),
                this.configuration.getMongoConnection().getPassword(),
                this.configuration.getMongoConnection().getDatabase()
        );

        this.collection = VanadiumCoins.getInstance().getMongoManager().getDatabase().getCollection("coins");

        this.initCommands();
        this.initEvents();
    }

    public void initCommands() {
        this.getCommand("coins").setExecutor(new CoinsCommand());
        this.getCommand("coins").setTabCompleter(new CoinsCommand());
    }

    public void initEvents() {
        this.getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
    }

    public void initConfig() throws IOException {

        File file = new File("plugins/VanadiumCoins/config.json");

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            this.configuration = new Configuration(new Configuration.MongoConnection(
                    "hostname",
                    "username",
                    "password",
                    "VanadiumCoins"
            ), 1000);

            writer.write(this.gson.toJson(this.configuration));

            writer.close();
            return;
        }

        this.configuration = this.gson.fromJson(new BufferedReader(new FileReader(file)), Configuration.class);
    }

}
