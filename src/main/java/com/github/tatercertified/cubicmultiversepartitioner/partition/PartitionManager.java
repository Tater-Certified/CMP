package com.github.tatercertified.cubicmultiversepartitioner.partition;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.fantasy.Fantasy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartitionManager {
    private MinecraftServer server;
    private Fantasy fantasy;
    public long globalCounter;
    public NbtList partitions;
    private final List<String> nameToIDConverter = new ArrayList<>();

    /**
     * Initializes the PartitionManager
     * @param server1 MinecraftServer
     * @param partitions NbtList from PersistentState
     */
    public void init(MinecraftServer server1, NbtList partitions) {
        server = server1;
        fantasy = Fantasy.get(server);
        this.partitions = partitions;

        for (int i = 0; i < partitions.size(); i++) {
            NbtCompound partitionData = partitions.getCompound(i);
            nameToIDConverter.add(formatConversionData(partitionData));
        }
    }

    // TODO Find a better way to access Partitions
    /**
     * Attempts to very slowly and terribly find a Partition
     * @param nameAndID Formatted String
     */
    public void loadWorld(String nameAndID) {
        long id = unformatConversionData(nameAndID);
        NbtCompound partitionData = null;
        for (int i = 0; i < partitions.size(); i++) {
            NbtCompound data = partitions.getCompound(i);
            if (data.getLong("id") == id) {
                partitionData = data;
                break;
            }
        }
        if (partitions != null) {
            new PartitionWorld(server, fantasy, partitionData);
        }
    }

    /**
     * Creates a new PartitionWorld
     * @param name Name of Partition
     * @param seed Seed of Partition
     * @param size Size of WorldBorder
     * @param dimensions Dimension to include (1 = Overworld, 2 = Nether, 3 = End)
     * @param chunkGenerator ChunkGenerator for Dimensions
     */
    public void createWorld(String name, @Nullable Long seed, int size, int[] dimensions, @Nullable String chunkGenerator) {
        new PartitionWorld(server, fantasy, createBasicCompound(name, seed, size, dimensions, chunkGenerator));
    }

    /**
     * Creates a basic NbtCompound to generate a PartitionWorld
     * @param name Name of Partition
     * @param seed Seed of Partition
     * @param size Size of WorldBorder
     * @param dimensions Dimension to include (1 = Overworld, 2 = Nether, 3 = End)
     * @param chunkGenerator ChunkGenerator for Dimensions
     * @return Usable basic NbtCompound
     */
    private NbtCompound createBasicCompound(String name, @Nullable Long seed, int size, int[] dimensions, @Nullable String chunkGenerator) {
        NbtCompound compound = new NbtCompound();
        compound.putLong("id", updateCounter());
        compound.putString("name", name);
        compound.putLong("seed", Objects.requireNonNullElseGet(seed, () -> server.getOverworld().getRandom().nextLong()));
        compound.putInt("size", size);
        compound.putIntArray("dimensions", dimensions);
        compound.putString("generator", chunkGenerator);

        partitions.add(compound);
        nameToIDConverter.add(formatConversionData(compound));
        return compound;
    }

    /**
     * Formats Strings so that it contains the Partition's name and ID
     * @param partitionData PartitionWorld's NBT
     * @return Formatted String
     */
    private String formatConversionData(NbtCompound partitionData) {
        return partitionData.getString("name") + "_" + partitionData.getLong("id");
    }

    /**
     * Extracts the Partition's ID from the formatted string
     * @param formatted Formatted String
     * @return Long ID from String
     */
    private long unformatConversionData(String formatted) {
        Pattern pattern = Pattern.compile("_(?!.*_)");
        Matcher matcher = pattern.matcher(formatted);

        if (matcher.find()) {
            String numberStr = formatted.substring(matcher.end());
            return Long.getLong(numberStr);
        }
        return 0L;
    }

    /**
     * Updates the globalCounter by one, and then saves it
     * @return Output of globalCounter
     */
    private long updateCounter() {
        globalCounter++;
        CMPPersistentState state = CMPPersistentState.getServerState(server);
        state.counter = globalCounter;
        state.markDirty();
        return globalCounter;
    }
}
