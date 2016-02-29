/*
 * The MIT License
 *
 * Copyright 2015 c45y.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.c45y.timedbans.command;

import com.c45y.timedbans.TimedBans;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author c45y
 */
public class TimedBansCommand implements CommandExecutor  {
    private final TimedBans _plugin;
    
    public TimedBansCommand(TimedBans plugin) {
        _plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {       
        if (!sender.hasPermission(_plugin.modPermission)) {
            sender.sendMessage(ChatColor.RED + "You cannot use this function");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Missing args. /tban <String:user> <Integer:duration> [reason]");
            return true;
        }
        
        OfflinePlayer player = _plugin.getServer().getOfflinePlayer(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Could not find the player, check spelling");
            return true;
        }
        
        int duration = Integer.valueOf(args[1]);
        if (duration > _plugin.config.MAX_DURATION) {
            sender.sendMessage(ChatColor.RED + "Duration longer than the max duration of " + _plugin.config.MAX_DURATION);
            return true;
        }
        
        String reason = "";
        for(int i = 2; i < args.length; i++) {
            reason += args[i] + " ";
        }
        
        if (duration <= 0) {
            _plugin.config.PLAYER_BANS.remove(player.getUniqueId().toString());
            sender.sendMessage(ChatColor.GREEN + "Player has been unbanned");
        } else {
            long now = System.currentTimeMillis() / 1000;
            _plugin.config.PLAYER_BANS.put(player.getUniqueId().toString(), now + duration);
            sender.sendMessage(ChatColor.GREEN + "Player has had ban duration set to " + duration);
            if (player.isOnline()) {
                ((Player) player).kickPlayer("You have been tempbanned. " + ChatColor.RED + reason);
            }
        }
        _plugin.getLogger().info(String.format("Time ban set for %s by %s with duration %d [%s]", player.getName(), sender.getName(), duration, reason));
        return true;
    }
    
}
