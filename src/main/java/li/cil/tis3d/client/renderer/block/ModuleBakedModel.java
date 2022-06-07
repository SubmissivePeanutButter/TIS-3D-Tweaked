package li.cil.tis3d.client.renderer.block;

import li.cil.tis3d.api.machine.Face;
import li.cil.tis3d.api.module.Module;
import li.cil.tis3d.api.module.traits.ModuleWithBakedModel;
import li.cil.tis3d.common.block.entity.CasingBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class ModuleBakedModel implements IDynamicBakedModel {
    private final BakedModel proxy;

    // --------------------------------------------------------------------- //

    ModuleBakedModel(final BakedModel proxy) {
        this.proxy = proxy;
    }

    // --------------------------------------------------------------------- //
    // IBakedModel

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable final BlockState state, @Nullable final Direction side, final Random random, final IModelData extraData) {
        final RenderType layer = MinecraftForgeClient.getRenderType();

        final CasingModules modules = extraData.getData(CasingModules.CASING_MODULES_PROPERTY);
        if (side != null) {
            if (modules != null) {
                final Face face = Face.fromDirection(side);
                final ModuleWithBakedModel module = modules.getModule(face);
                if (module != null && module.hasModel()) {
                    if (module.canRenderInLayer(layer)) {
                        return module.getQuads(state, side, random, modules.getModuleData(face));
                    } else {
                        return Collections.emptyList();
                    }
                }
            }

            if (layer != null && layer.equals(RenderType.solid())) {
                return proxy.getQuads(state, side, random, extraData);
            } else {
                return Collections.emptyList();
            }
        } else {
            final ArrayList<BakedQuad> quads = new ArrayList<>();

            if (modules != null) {
                for (final Face face : Face.VALUES) {
                    final ModuleWithBakedModel module = modules.getModule(face);
                    if (module != null && module.hasModel()) {
                        if (module.canRenderInLayer(layer)) {
                            quads.addAll(module.getQuads(state, null, random, modules.getModuleData(face)));
                        }
                    }
                }
            }

            if (layer != null && layer.equals(RenderType.solid())) {
                quads.addAll(proxy.getQuads(state, null, random, extraData));
            }

            return quads;
        }
    }

    @Override
    public boolean useAmbientOcclusion() {
        return proxy.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return proxy.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return proxy.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return proxy.isCustomRenderer();
    }

    @SuppressWarnings("deprecation")
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return proxy.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return proxy.getOverrides();
    }

    @Nonnull
    @Override
    public IModelData getModelData(final BlockAndTintGetter level, final BlockPos pos, final BlockState state, final IModelData tileData) {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof final CasingBlockEntity casing)) {
            return tileData;
        }

        final CasingModules data = new CasingModules();
        for (final Face face : Face.VALUES) {
            final Module module = casing.getModule(face);
            if (module instanceof final ModuleWithBakedModel moduleWithModel) {
                if (moduleWithModel.hasModel()) {
                    data.setModule(face, moduleWithModel, moduleWithModel.getModelData(level, pos, state, tileData));
                }
            }
        }

        if (!data.isEmpty()) {
            return new ModelDataMap.Builder()
                .withInitial(CasingModules.CASING_MODULES_PROPERTY, data)
                .build();
        }

        return tileData;
    }

    // --------------------------------------------------------------------- //

    private static final class CasingModules {
        public static final ModelProperty<CasingModules> CASING_MODULES_PROPERTY = new ModelProperty<>();

        private final ModuleWithBakedModel[] modules = new ModuleWithBakedModel[Face.VALUES.length];
        private final IModelData[] moduleData = new IModelData[Face.VALUES.length];

        public boolean isEmpty() {
            for (final ModuleWithBakedModel module : modules) {
                if (module != null) {
                    return false;
                }
            }

            return true;
        }

        public void setModule(final Face face, final ModuleWithBakedModel module, final IModelData data) {
            modules[face.ordinal()] = module;
            moduleData[face.ordinal()] = data;
        }

        @Nullable
        public ModuleWithBakedModel getModule(final Face face) {
            return modules[face.ordinal()];
        }

        public IModelData getModuleData(final Face face) {
            return moduleData[face.ordinal()];
        }
    }
}
