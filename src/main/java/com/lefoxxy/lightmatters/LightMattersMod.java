package com.lefoxxy.lightmatters;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.neoforged.fml.common.Mod;

@Mod(LightMattersMod.MODID)
public final class LightMattersMod {
    public static final String MODID = "lightmatters";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LightMattersMod() {
        LOGGER.info("Light Matters is loaded. Darkness now matters.");
    }
}
