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

import com.c45y.timedbans.command.TimedBansCommand;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author c45y
 */
public class TimedBans extends JavaPlugin {
    private TimedBansListener _listener;
    public Configuration config;
    
    public final String modPermission = "timedbans.staff";
    
    @Override
    public void onEnable() {
        /* Load our configuration to our global */
        config = new Configuration(this);
        config.load();
        
                
        /* Register our listener(s) */
        _listener = new TimedBansListener(this);
        getServer().getPluginManager().registerEvents(_listener, this);
        getCommand("tban").setExecutor(new TimedBansCommand(this));
        
        this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                config.save();
            }
        }, 20*60, 20*60);
        
        this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis() / 1000;
                for (String id: config.PLAYER_BANS.keySet()) {
                    long duration = config.PLAYER_BANS.get(id);
                    if (now > duration) {
                        config.PLAYER_BANS.remove(id);
                        getServer().getLogger().log(Level.INFO, "Removed time ban for player " + id);
                    }
                }
                config.save();
            }
        });
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        config.save();
    }
}
