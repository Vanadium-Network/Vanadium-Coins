/*
Copyright (c) 2015 - 2023 Michael Steinmötzger
All rights are reserved for this project, unless otherwise
stated in a license file.
*/

package dev.vanadium.coins.commands;

import dev.vanadium.coins.VanadiumCoins;
import dev.vanadium.coins.api.CoinPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CoinsCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        CoinPlayer coinPlayer = (CoinPlayer) player.getMetadata("coinObject").get(0).value();
        if(args.length == 0) {
            coinPlayer.getCoins(coins -> {
                player.sendMessage(VanadiumCoins.getPrefix() + "Your coins: §5" + coins);
            });
            return false;
        }

        if(!player.hasPermission("vanadium.coins.manage")) {
            player.sendMessage(VanadiumCoins.getNoPerm());
            return true;
        }
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("get")) {
                String target = args[1];
                player.sendMessage(VanadiumCoins.getPrefix() + "Retrieving coins from database...");
                try {
                    CoinPlayer.getFromName(target, (cp) -> {
                        if(cp == null) {
                            player.sendMessage(VanadiumCoins.getPrefix() + "This player was §cnot §7found.");
                            return;
                        }

                        cp.getCoins((coins) -> {
                            player.sendMessage(VanadiumCoins.getPrefix() + "Coins from " + target + ": §5" + coins);
                        });
                    });
                } catch (ExecutionException | InterruptedException e) {
                    player.sendMessage(VanadiumCoins.getPrefix() + "This player was §cnot §7found.");
                }
            }
        }

        if(args.length == 3) {
            String target = args[1];
            int coins;
            try {
                coins = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(VanadiumCoins.getPrefix() + "Please provide a valid amount.");
                return true;
            }

            if(args[0].equalsIgnoreCase("set")) {
                try {
                    CoinPlayer.getFromName(target, cp -> {
                        cp.setCoins(coins, () -> {
                            player.sendMessage(VanadiumCoins.getPrefix() + "The amount of coins of §5" + target + " §7is now at §5" + coins);
                        });
                    });
                } catch (ExecutionException | InterruptedException e) {
                    player.sendMessage(VanadiumCoins.getPrefix() + "This player was §cnot §7found.");
                }
                return false;
            }

            if(args[0].equalsIgnoreCase("add")) {
                try {
                    CoinPlayer.getFromName(target, cp -> {
                        cp.getCoins(oldCoins -> {
                            cp.setCoins(oldCoins + coins, () -> {
                                player.sendMessage(VanadiumCoins.getPrefix() + "The amount of coins of §5" + target + " §7is now at §5" + (oldCoins + coins));
                            });
                        });
                    });
                } catch (ExecutionException | InterruptedException e) {
                    player.sendMessage(VanadiumCoins.getPrefix() + "This player was §cnot §7found.");
                }
                return false;
            }

            if(args[0].equalsIgnoreCase("remove")) {
                try {
                    CoinPlayer.getFromName(target, cp -> {
                        cp.getCoins(oldCoins -> {
                            cp.setCoins(oldCoins - coins, () -> {
                                player.sendMessage(VanadiumCoins.getPrefix() + "The amount of coins of §5" + target + " §7is now at §5" + (oldCoins - coins));
                            });
                        });
                    });
                } catch (ExecutionException | InterruptedException e) {
                    player.sendMessage(VanadiumCoins.getPrefix() + "This player was §cnot §7found.");
                }
                return false;
            }


        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();


        if(args.length == 1) {
            if(sender.hasPermission("vanadium.coins.manage")) {
                list.add("get");
                list.add("set");
                list.add("add");
                list.add("remove");
            }
        }

        if(args.length == 2) {
            if(sender.hasPermission("vanadium.coins.manage")) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getDisplayName).collect(Collectors.toList());
            }

        }




        return list;
    }
}
