/*
Copyright (c) 2015 - 2023 Michael Steinmötzger
All rights are reserved for this project, unless otherwise
stated in a license file.
*/

package dev.vanadium.coins.api;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import dev.vanadium.coins.VanadiumCoins;
import dev.vanadium.coins.async.Callback;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.player.ICloudPlayer;
import eu.thesimplecloud.api.player.IOfflineCloudPlayer;
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class CoinPlayer {

    private UUID player;
    private int cachedCoins;
    private long cacheExpire;

    private CoinPlayer() {
    }

    private CoinPlayer(Player player) {
        this.player = player.getUniqueId();
        this.resetCache();
    }

    private CoinPlayer(UUID uuid) {
        this.player = uuid;
        this.resetCache();
    }

    public void getCoins(Consumer<Integer> consumer) {
        if (System.currentTimeMillis() < cacheExpire) {
            consumer.accept(cachedCoins);
            return;
        }
        VanadiumCoins.getInstance().getCollection().find(Filters.eq("player", player.toString())).first().subscribe(new Subscriber<Document>() {

            Document d;

            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(Document document) {
                d = document;


            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                if (d == null) {
                    create(() -> {
                        consumer.accept(VanadiumCoins.getInstance().getConfiguration().getStartingCoins());
                        cachedCoins = VanadiumCoins.getInstance().getConfiguration().getStartingCoins();
                    });
                } else {
                    consumer.accept(d.getInteger("coins"));
                    cachedCoins = d.getInteger("coins");
                }

                cacheExpire = System.currentTimeMillis() + 1000 * 10;
            }
        });
    }

    public void setCoins(int coins, Callback callback) {
        VanadiumCoins.getInstance().getCollection().updateOne(Filters.eq("player", player.toString()), new Document()
                .append("$set", new Document("coins", coins)), new UpdateOptions().upsert(true)).subscribe(new Subscriber<UpdateResult>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(UpdateResult updateResult) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                callback.call();
            }
        });
    }

    public void resetCache() {
        this.cacheExpire = -1;
        this.cachedCoins = -1;
    }

    private void create(Callback cb) {
        VanadiumCoins.getInstance().getCollection().insertOne(new Document()
                        .append("player", player.toString())
                        .append("coins", VanadiumCoins.getInstance().getConfiguration().getStartingCoins())).subscribe(new Subscriber<InsertOneResult>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(InsertOneResult insertOneResult) {

                System.out.println("cba");
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                System.out.println("abc");
                cb.call();
            }
        });
    }

    public static void getFromName(String name, Consumer<CoinPlayer> consumer) throws ExecutionException, InterruptedException {
        IOfflineCloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(name).get();

        if(cloudPlayer == null) {
            consumer.accept(null);
            return;
        }
        consumer.accept(new CoinPlayer(cloudPlayer.getUniqueId()));
    }

    public static CoinPlayer fromPlayer(Player player) {
        return new CoinPlayer(player);
    }

}
