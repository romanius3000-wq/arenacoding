package com.arenacoding.mcadvanced.world;

import com.arenacoding.mcadvanced.registry.ModBlocks;
import com.arenacoding.mcadvanced.util.ModWorlds;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Генератор Главной Комнаты: квадратный зал 88x88 с полом, стенами, куполом,
 * 12 порталами миров (по 3 на стену), алтарём боссов в центре и фонарями.
 */
public class HubRoomGenerator extends ChunkGenerator {
    public static final MapCodec<HubRoomGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Biome.CODEC.fieldOf("biome").forGetter(gen -> gen.biome)
            ).apply(instance, HubRoomGenerator::new));

    public static final int FLOOR_Y = 64;
    public static final int CEILING_Y = 79;
    public static final int HALF = 44; // комната от -44 до 44 по X/Z

    private final Holder<Biome> biome;

    public HubRoomGenerator(Holder<Biome> biome) {
        super(new FixedBiomeSource(biome));
        this.biome = biome;
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState random,
                                                        StructureManager structureManager, ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        BlockState stone = ModBlocks.HUB_STONE.defaultBlockState();
        BlockState bricks = ModBlocks.HUB_STONE_BRICKS.defaultBlockState();
        BlockState pillar = ModBlocks.HUB_PILLAR.defaultBlockState();
        BlockState lantern = ModBlocks.HUB_LANTERN.defaultBlockState();
        BlockState altar = ModBlocks.BOSS_ALTAR.defaultBlockState();

        for (int x = minX; x < minX + 16; x++) {
            for (int z = minZ; z < minZ + 16; z++) {
                if (Math.abs(x) > HALF || Math.abs(z) > HALF) {
                    continue;
                }
                // пол
                boolean border = Math.abs(x) == HALF || Math.abs(z) == HALF;
                boolean ring = Math.abs(x) == HALF - 1 || Math.abs(z) == HALF - 1;
                BlockState floor = border ? pillar : (ring ? pillar : (((x + z) & 1) == 0 ? bricks : stone));
                chunk.setBlockState(new BlockPos(x, FLOOR_Y, z), floor);

                // потолок
                BlockState ceil = ((x % 6 == 0) && (z % 6 == 0)) ? lantern : stone;
                chunk.setBlockState(new BlockPos(x, CEILING_Y, z), ceil);

                // стены
                if (border) {
                    boolean pillarCol = ((x + HALF) % 8 == 0) || ((z + HALF) % 8 == 0);
                    for (int y = FLOOR_Y + 1; y < CEILING_Y; y++) {
                        BlockState wall = pillarCol ? pillar : stone;
                        if (y == FLOOR_Y + 8 && ((x + HALF) % 4 == 0 || (z + HALF) % 4 == 0)) {
                            wall = lantern;
                        }
                        chunk.setBlockState(new BlockPos(x, y, z), wall);
                    }
                }
            }
        }

        // порталы миров: по 3 на каждой из 4 стен
        List<ModWorlds.WorldInfo> worlds = ModWorlds.WORLDS;
        for (int i = 0; i < worlds.size(); i++) {
            ModWorlds.WorldInfo world = worlds.get(i);
            int wall = i / 3;
            int slot = (i % 3) - 1; // -1, 0, 1
            int off = slot * 26;
            int px, pz;
            int dx = 0, dz = 0; // направление внутрь комнаты
            switch (wall) {
                case 0 -> { px = off; pz = -HALF; dz = 1; }
                case 1 -> { px = HALF; pz = off; dx = -1; }
                case 2 -> { px = off; pz = HALF; dz = -1; }
                default -> { px = -HALF; pz = off; dx = 1; }
            }
            if (inChunk(px, pz, chunkPos) || inChunk(px + dx, pz + dz, chunkPos)) {
                placePortal(chunk, world, px, pz, dx, dz);
            }
        }

        // центр: алтарь боссов + колонны
        if (inChunk(0, 0, chunkPos)) {
            chunk.setBlockState(new BlockPos(0, FLOOR_Y + 1, 0), altar);
        }
        for (int[] corner : new int[][]{{-5, -5}, {5, -5}, {-5, 5}, {5, 5}}) {
            int cx = corner[0], cz = corner[1];
            if (!inChunk(cx, cz, chunkPos)) {
                continue;
            }
            for (int y = FLOOR_Y + 1; y <= FLOOR_Y + 5; y++) {
                chunk.setBlockState(new BlockPos(cx, y, cz), pillar);
            }
            chunk.setBlockState(new BlockPos(cx, FLOOR_Y + 6, cz), lantern);
        }

        return CompletableFuture.completedFuture(chunk);
    }

    private void placePortal(ChunkAccess chunk, ModWorlds.WorldInfo world, int px, int pz, int dx, int dz) {
        // рамка из блока самоцвета в стене (3x3), площадка портала перед ней
        Block gemBlock = ModBlocks.gemBlock(world.gem());
        Block portal = ModBlocks.portalBlock(world);
        BlockState gem = gemBlock.defaultBlockState();
        for (int a = -1; a <= 1; a++) {
            for (int b = 0; b <= 2; b++) {
                int bx = px + (dx != 0 ? 0 : a) - dx;
                int bz = pz + (dz != 0 ? 0 : a) - dz;
                int by = FLOOR_Y + 2 + b;
                if (b == 1 && a == 0) {
                    chunk.setBlockState(new BlockPos(bx, by, bz),
                            ModBlocks.HUB_LANTERN.defaultBlockState());
                } else {
                    chunk.setBlockState(new BlockPos(bx, by, bz), gem);
                }
            }
        }
        // площадка-портал (встань — и телепортирует)
        chunk.setBlockState(new BlockPos(px + dx, FLOOR_Y, pz + dz), portal.defaultBlockState());
        // подсветка по бокам площадки
        chunk.setBlockState(new BlockPos(px + dx + (dz != 0 ? 1 : 0), FLOOR_Y + 1, pz + dz + (dx != 0 ? 1 : 0)),
                ModBlocks.HUB_LANTERN.defaultBlockState());
        chunk.setBlockState(new BlockPos(px + dx - (dz != 0 ? 1 : 0), FLOOR_Y + 1, pz + dz - (dx != 0 ? 1 : 0)),
                ModBlocks.HUB_LANTERN.defaultBlockState());
    }

    private static boolean inChunk(int x, int z, ChunkPos pos) {
        return x >= pos.getMinBlockX() && x < pos.getMaxBlockX() + 1
                && z >= pos.getMinBlockZ() && z < pos.getMaxBlockZ() + 1;
    }

    // ------------------------------------------------------------ trivial

    @Override
    public int getGenDepth() {
        return 256;
    }

    @Override
    public int getSeaLevel() {
        return 62;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return FLOOR_Y;
    }

    @Override
    public net.minecraft.world.level.NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState random) {
        return new net.minecraft.world.level.NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
    }

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState random,
                             net.minecraft.world.level.biome.BiomeManager biomeManager,
                             StructureManager structureManager, ChunkAccess chunk) {
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager,
                              RandomState random, ChunkAccess chunk) {
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
    }

    @Override
    public int getSpawnHeight(LevelHeightAccessor level) {
        return FLOOR_Y + 1;
    }
}
