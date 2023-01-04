/*
Copyright (c) 2015 - 2023 Michael Steinmötzger
All rights are reserved for this project, unless otherwise
stated in a license file.
*/

package dev.vanadium.coins.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Configuration {


    private MongoConnection mongoConnection;
    private int startingCoins;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MongoConnection {



        private String hostname;
        private String username;
        private String password;
        private String database;
    }
}
