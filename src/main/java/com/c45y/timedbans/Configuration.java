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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author c45y
 */
public class Configuration {
    private TimedBans _plugin;
    public Integer MAX_DURATION;
    public ConcurrentHashMap<String, Long> PLAYER_BANS;
    
    public Configuration(TimedBans plugin) {
        plugin.getConfig().addDefault("max_duration", 604800);
        
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.getConfig().options().copyDefaults(true);
            plugin.saveConfig();
        }
        _plugin = plugin;
    }
    
    public void load() {
        _plugin.reloadConfig();
        MAX_DURATION = _plugin.getConfig().getInt("max_duration", 604800);
//        PLAYER_BANS = new HashMap<String, Long>();
         
        try {
            File bansfile = new File(_plugin.getDataFolder(), "bans.ser");
            FileInputStream fis = new FileInputStream(bansfile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            PLAYER_BANS = (ConcurrentHashMap<String, Long>) ois.readObject();
            ois.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            PLAYER_BANS = new ConcurrentHashMap<String, Long>();
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void save() {
        _plugin.saveConfig();
        
        try {
            File bansfile = new File(_plugin.getDataFolder(), "bans.ser");
            FileOutputStream fos = new FileOutputStream(bansfile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(PLAYER_BANS);
            oos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
