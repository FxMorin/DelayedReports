package ca.fxco.delayedreports.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.message.ChatMessageSigner;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(ChatMessageSigner.class)
public class ChatMessageSignerMixin {

    private static Instant lastMessage = Instant.now();
    private static final long DELAY_MINUTES = (int)Duration.ofSeconds(270).toMillis(); //4.5m


    @Redirect(
            method = "create(Ljava/util/UUID;)Lnet/minecraft/network/message/ChatMessageSigner;",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/network/message/ChatMessageSigner"
            )
    )
    private static ChatMessageSigner onCreate(UUID sender, Instant instant, long salt) {
        Instant farTime = Instant.ofEpochMilli(instant.toEpochMilli() - DELAY_MINUTES);
        if (lastMessage.compareTo(farTime) <= 0) {
            farTime = lastMessage.plusMillis(Random.create().nextBetween(1,2));
            lastMessage = farTime;
        }
        return new ChatMessageSigner(sender, farTime, salt);
    }
}
