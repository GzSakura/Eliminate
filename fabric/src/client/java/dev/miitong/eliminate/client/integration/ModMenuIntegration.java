package dev.miitong.eliminate.client.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            // 这里可以返回配置界面
            return null;
        };
    }
}