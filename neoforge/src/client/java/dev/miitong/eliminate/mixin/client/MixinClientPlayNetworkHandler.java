package dev.miitong.eliminate.mixin.client;

import dev.miitong.eliminate.client.EliminateClient;
import dev.miitong.eliminate.config.EliminateConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String content, CallbackInfo ci) {
        if (content.equalsIgnoreCase(".debug")) {
            EliminateConfig config = EliminateConfig.getInstance();
            config.debugMode = !config.debugMode;
            EliminateConfig.save();
            
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                Text status = config.debugMode 
                    ? Text.literal("Eliminate Debug: ON").formatted(Formatting.GREEN)
                    : Text.literal("Eliminate Debug: OFF").formatted(Formatting.RED);
                client.player.sendMessage(status, false);
            }
            
            ci.cancel();
        }
    }
}
