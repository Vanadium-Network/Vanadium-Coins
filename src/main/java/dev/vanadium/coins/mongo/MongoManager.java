/*
Copyright (c) 2015 - 2023 Michael Steinmötzger
All rights are reserved for this project, unless otherwise
stated in a license file.
*/

package dev.vanadium.coins.mongo;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

public class MongoManager {

    @Getter
    MongoClient client;
    @Getter
    MongoDatabase database;

    public MongoManager(String hostname, String username, String password, String database) {
        this.client = MongoClients.create(String.format("mongodb://%s:%s@%s/%s", username, password, hostname, database));
        this.database = this.client.getDatabase(database);
    }


}