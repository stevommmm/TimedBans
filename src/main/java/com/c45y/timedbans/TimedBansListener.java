/*
 * The MIT License
 *
 * Copyright 2016 c45y.
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
package com.c45y.timedbans;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

/**
 *
 * @author c45y
 */
public class TimedBansListener implements Listener {
    private final TimedBans _plugin;
    
    public TimedBansListener(TimedBans plugin) {
        _plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String id = event.getUniqueId().toString();
        if (_plugin.config.PLAYER_BANS.containsKey(id)) {
            long now = System.currentTimeMillis() / 1000;
            long duration = _plugin.config.PLAYER_BANS.get(id);
            if (now < duration) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED); 
                event.setKickMessage(String.format("Your timeban still has %d days, %d hours, <%d minutes remaining", 
                    TimeUnit.SECONDS.toDays(duration - now),
                    TimeUnit.SECONDS.toHours(duration - now),
                    TimeUnit.SECONDS.toMinutes(duration - now)
                ));
                _plugin.getLogger().log(Level.INFO, String.format("Player %s denied login, waiting %d", id, duration - now));
            } else {
                _plugin.config.PLAYER_BANS.remove(id);
            }
        }
    }

}
