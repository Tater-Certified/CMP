package com.github.tatercertified.cmp.backend;

import net.minecraft.util.Identifier;

import java.util.List;

public class Partition {
    private Partition() {}

    static class PartitionBuilder {
        private final Identifier identifier;

        public PartitionBuilder(Identifier identifier) {
            this.identifier = identifier;
        }

        public PartitionBuilder addWorld() {

            return this;
        }
    }
}
