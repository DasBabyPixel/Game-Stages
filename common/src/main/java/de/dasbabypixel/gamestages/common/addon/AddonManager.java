package de.dasbabypixel.gamestages.common.addon;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AddonManager<Addon extends de.dasbabypixel.gamestages.common.addon.Addon> {
    private static AddonManager<?> instance = null;
    private final Map<String, AddonEntry<Addon>> addonById = new HashMap<>();
    private final Map<String, Map<String, List<MessageListener<? super Addon>>>> messageListeners = new HashMap<>();
    private final List<Addon> addons = new ArrayList<>();

    protected boolean frozen = false;

    protected AddonManager() {
        if (instance != null) throw new IllegalStateException();
        instance = this;
    }

    public static AddonManager<?> instance() {
        return instance;
    }

    public List<Addon> addons() {
        if (!frozen) throw new UnsupportedOperationException();
        return addons;
    }

    protected void addAddon(String id, Addon addon) {
        if (frozen) throw new UnsupportedOperationException("Frozen");
        addonById.put(id, new AddonEntry<>(id, addon));
        this.addons.add(addon);
    }

    public void addMessageListener(String targetAddonId, String messageId, MessageListener<? super Addon> listener) {
        messageListeners
                .computeIfAbsent(targetAddonId, s -> new HashMap<>())
                .computeIfAbsent(messageId, s -> new ArrayList<>())
                .add(listener);
    }

    public <M> @Nullable M sendMessage(de.dasbabypixel.gamestages.common.addon.Addon origin, String originId, String messageId, Supplier<M> message) {
        return sendMessage(origin, originId, messageId, Supplier::get, message);
    }

    public <A1, M> @Nullable M sendMessage(de.dasbabypixel.gamestages.common.addon.Addon origin, String originId, String messageId, Send1Args<A1, M> message, A1 a1) {
        return sendMessage(origin, originId, messageId, Send1Args::create, message, a1);
    }

    @SuppressWarnings("unchecked")
    public <A1, A2, M> @Nullable M sendMessage(de.dasbabypixel.gamestages.common.addon.Addon origin, String originId, String messageId, Send2Args<A1, A2, M> message, A1 a1, A2 a2) {
        var byMsg = messageListeners.get(originId);
        if (byMsg == null) return null;
        var l = byMsg.get(messageId);
        var msg = message.create(a1, a2);
        for (var messageListener : l) {
            messageListener.handle((Addon) origin, msg);
        }
        return msg;
    }

    public interface Send1Args<A1, R> {
        R create(A1 arg1);
    }

    public interface Send2Args<A1, A2, R> {
        R create(A1 arg1, A2 arg2);
    }

    public record AddonEntry<A extends de.dasbabypixel.gamestages.common.addon.Addon>(String id, A addon) {
    }
}
