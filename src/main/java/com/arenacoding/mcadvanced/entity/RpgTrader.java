package com.arenacoding.mcadvanced.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Уникальный житель-торговец: своё меню обмена (новые торги за гемы).
 * Используйте предметы — откроется окно торговли.
 */
public class RpgTrader extends AbstractVillager {
    /** Одна позиция обмена: (предмет, количество) -> результат. */
    public record Entry(Item costItem, int costCount, ItemStack result, int maxUses, int xp) {}

    private final Component tradeTitle;
    private final List<Entry> trades;

    public RpgTrader(EntityType<? extends AbstractVillager> type, Level level,
                     Component tradeTitle, List<Entry> trades) {
        super(type, level);
        this.tradeTitle = tradeTitle;
        this.trades = trades;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected void updateTrades(ServerLevel level) {
        MerchantOffers offers = new MerchantOffers();
        for (Entry entry : this.trades) {
            offers.add(new MerchantOffer(new ItemCost(entry.costItem(), entry.costCount()),
                    entry.result().copy(), entry.maxUses(), entry.xp(), 0.05f));
        }
        this.overrideOffers(offers);
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        this.overrideXp(this.getVillagerXp() + 3);
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide()) {
            this.setTradingPlayer(player);
            this.openTradingScreen(player, this.tradeTitle, 1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        return null;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }
}
