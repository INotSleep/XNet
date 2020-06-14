package mcjty.xnet.modules.cables.client;

import com.google.common.base.Function;
import mcjty.lib.client.AbstractDynamicBakedModel;
import mcjty.xnet.XNet;
import mcjty.xnet.modules.cables.CableColor;
import mcjty.xnet.modules.cables.ConnectorType;
import mcjty.xnet.modules.cables.blocks.ConnectorBlock;
import mcjty.xnet.modules.cables.blocks.GenericCableBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static mcjty.xnet.modules.cables.ConnectorType.BLOCK;
import static mcjty.xnet.modules.cables.ConnectorType.CABLE;
import static mcjty.xnet.modules.cables.client.CablePatterns.SpriteIdx.*;

public class GenericCableBakedModel extends AbstractDynamicBakedModel {

    private TextureAtlasSprite spriteCable;

    public static class CableTextures {
        private TextureAtlasSprite spriteConnector;
        private TextureAtlasSprite spriteAdvancedConnector;
        private TextureAtlasSprite spriteNoneCable;
        private TextureAtlasSprite spriteNormalCable;
        private TextureAtlasSprite spriteEndCable;
        private TextureAtlasSprite spriteCornerCable;
        private TextureAtlasSprite spriteThreeCable;
        private TextureAtlasSprite spriteCrossCable;
    }

    private static CableTextures[] cableTextures = null;
    private static TextureAtlasSprite spriteSide;

    static {
        CablePatterns.PATTERNS.put(new CablePatterns.Pattern(false, false, false, false), new CablePatterns.QuadSetting(SPRITE_NONE, 0));
        CablePatterns.PATTERNS.put(new CablePatterns.Pattern(true, false, false, false), new CablePatterns.QuadSetting(SPRITE_END, 3));
        CablePatterns.PATTERNS.put(new CablePatterns.Pattern(false, true, false, false), new CablePatterns.QuadSetting(SPRITE_END, 0));
        CablePatterns.PATTERNS.put(new CablePatterns.Pattern(false, false, true, false), new CablePatterns.QuadSetting(SPRITE_END, 1));
        CablePatterns.PATTERNS.put(new CablePatterns.Pattern(false, false, false, true), new CablePatterns.QuadSetting(SPRITE_END, 2));
        CablePatterns.PATTERNS.put(new CablePatterns.Pattern(true, true, false, false), new CablePatterns.QuadSetting(SPRITE_CORNER, 0));
        CablePatterns.PATTERNS.put(new CablePatterns.Pattern(false, true, true, false), new CablePatterns.QuadSetting(SPRITE_CORNER, 1));
        CablePatterns.PATTERNS.put(new CablePatterns.Pattern(false, false, true, true), new CablePatterns.QuadSetting(SPRITE_CORNER, 2));
        CablePatterns.PATTERNS.put(new CablePatterns.Pattern(true, false, false, true), new CablePatterns.QuadSetting(SPRITE_CORNER, 3));
        CablePatterns.PATTERNS.put(new CablePatterns.Pattern(false, true, false, true), new CablePatterns.QuadSetting(SPRITE_STRAIGHT, 0));
        CablePatterns.PATTERNS.put(new CablePatterns.Pattern(true, false, true, false), new CablePatterns.QuadSetting(SPRITE_STRAIGHT, 1));
        CablePatterns.PATTERNS.put(new CablePatterns.Pattern(true, true, true, false), new CablePatterns.QuadSetting(SPRITE_THREE, 0));
        CablePatterns.PATTERNS.put(new CablePatterns.Pattern(false, true, true, true), new CablePatterns.QuadSetting(SPRITE_THREE, 1));
        CablePatterns.PATTERNS.put(new CablePatterns.Pattern(true, false, true, true), new CablePatterns.QuadSetting(SPRITE_THREE, 2));
        CablePatterns.PATTERNS.put(new CablePatterns.Pattern(true, true, false, true), new CablePatterns.QuadSetting(SPRITE_THREE, 3));
        CablePatterns.PATTERNS.put(new CablePatterns.Pattern(true, true, true, true), new CablePatterns.QuadSetting(SPRITE_CROSS, 0));
    }

    private static void initTextures() {
        if (cableTextures == null) {
            CableTextures[] tt = new CableTextures[CableColor.VALUES.length];
            for (CableColor color : CableColor.VALUES) {
                int i = color.ordinal();
                tt[i] = new CableTextures();
                tt[i].spriteConnector = getTexture(new ResourceLocation(XNet.MODID, "block/cable" + i + "/connector"));
                tt[i].spriteAdvancedConnector = getTexture(new ResourceLocation(XNet.MODID, "block/cable" + i + "/advanced_connector"));

                tt[i].spriteNormalCable = getTexture(new ResourceLocation(XNet.MODID, "block/cable" + i + "/normal_netcable"));
                tt[i].spriteNoneCable = getTexture(new ResourceLocation(XNet.MODID, "block/cable" + i + "/normal_none_netcable"));
                tt[i].spriteEndCable = getTexture(new ResourceLocation(XNet.MODID, "block/cable" + i + "/normal_end_netcable"));
                tt[i].spriteCornerCable = getTexture(new ResourceLocation(XNet.MODID, "block/cable" + i + "/normal_corner_netcable"));
                tt[i].spriteThreeCable = getTexture(new ResourceLocation(XNet.MODID, "block/cable" + i + "/normal_three_netcable"));
                tt[i].spriteCrossCable = getTexture(new ResourceLocation(XNet.MODID, "block/cable" + i + "/normal_cross_netcable"));
            }

            spriteSide = getTexture(new ResourceLocation(XNet.MODID, "block/connector_side"));
            cableTextures = tt;
        }
    }

    private static TextureAtlasSprite getSpriteNormal(CablePatterns.SpriteIdx idx, int index) {
        initTextures();
        CableTextures cableTexture = cableTextures[index];
        switch (idx) {
            case SPRITE_NONE:
                return cableTexture.spriteNoneCable;
            case SPRITE_END:
                return cableTexture.spriteEndCable;
            case SPRITE_STRAIGHT:
                return cableTexture.spriteNormalCable;
            case SPRITE_CORNER:
                return cableTexture.spriteCornerCable;
            case SPRITE_THREE:
                return cableTexture.spriteThreeCable;
            case SPRITE_CROSS:
                return cableTexture.spriteCrossCable;
        }
        return cableTexture.spriteNoneCable;
    }

    private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite, int rotation, float hilight) {
        switch (rotation) {
            case 0:
                return createQuad(v1, v2, v3, v4, sprite, hilight);
            case 1:
                return createQuad(v2, v3, v4, v1, sprite, hilight);
            case 2:
                return createQuad(v3, v4, v1, v2, sprite, hilight);
            case 3:
                return createQuad(v4, v1, v2, v3, sprite, hilight);
        }
        return createQuad(v1, v2, v3, v4, sprite, hilight);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        List<BakedQuad> quads = new ArrayList<>();
        RenderType layer = MinecraftForgeClient.getRenderLayer();

        if (side == null && (layer == null || layer.equals(RenderType.getSolid()))) {
            // Called with the blockstate from our block. Here we get the values of the six properties and pass that to
            // our baked model implementation.
            ConnectorType north = state.get(GenericCableBlock.NORTH);
            ConnectorType south = state.get(GenericCableBlock.SOUTH);
            ConnectorType west = state.get(GenericCableBlock.WEST);
            ConnectorType east = state.get(GenericCableBlock.EAST);
            ConnectorType up = state.get(GenericCableBlock.UP);
            ConnectorType down = state.get(GenericCableBlock.DOWN);
            CableColor cableColor = state.get(GenericCableBlock.COLOR);
            int index = cableColor.ordinal();

            initTextures();
            CableTextures ct = cableTextures[index];
            spriteCable = ct.spriteNormalCable;
            GenericCableBlock block = (GenericCableBlock) state.getBlock();
            TextureAtlasSprite spriteConnector;
            if (block.isAdvancedConnector()) {
                spriteConnector = ct.spriteAdvancedConnector;
            } else {
                spriteConnector = ct.spriteConnector;
            }
            Function<CablePatterns.SpriteIdx, TextureAtlasSprite> getSprite = idx -> getSpriteNormal(idx, index);
            float hilight = 1.0f;
            if (block instanceof ConnectorBlock) {
                if (north != BLOCK && south != BLOCK && west != BLOCK && east != BLOCK && up != BLOCK && down != BLOCK) {
                    hilight = 0.5f; // To make connectors with no actual connections visible
                }
            }

            double o = .4;      // Thickness of the cable. .0 would be full block, .5 is infinitely thin.
            double p = .1;      // Thickness of the connector as it is put on the connecting block
            double q = .2;      // The wideness of the connector

            // For each side we either cap it off if there is no similar block adjacent on that side
            // or else we extend so that we touch the adjacent block:

            if (up == CABLE) {
                quads.add(createQuad(v(1 - o, 1, o), v(1 - o, 1, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1 - o, o), spriteCable, hilight));
                quads.add(createQuad(v(o, 1, 1 - o), v(o, 1, o), v(o, 1 - o, o), v(o, 1 - o, 1 - o), spriteCable, hilight));
                quads.add(createQuad(v(o, 1, o), v(1 - o, 1, o), v(1 - o, 1 - o, o), v(o, 1 - o, o), spriteCable, hilight));
                quads.add(createQuad(v(o, 1 - o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1, 1 - o), v(o, 1, 1 - o), spriteCable, hilight));
            } else if (up == BLOCK) {
                quads.add(createQuad(v(1 - o, 1 - p, o), v(1 - o, 1 - p, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1 - o, o), spriteCable, hilight));
                quads.add(createQuad(v(o, 1 - p, 1 - o), v(o, 1 - p, o), v(o, 1 - o, o), v(o, 1 - o, 1 - o), spriteCable, hilight));
                quads.add(createQuad(v(o, 1 - p, o), v(1 - o, 1 - p, o), v(1 - o, 1 - o, o), v(o, 1 - o, o), spriteCable, hilight));
                quads.add(createQuad(v(o, 1 - o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1 - p, 1 - o), v(o, 1 - p, 1 - o), spriteCable, hilight));

                quads.add(createQuad(v(1 - q, 1 - p, q), v(1 - q, 1, q), v(1 - q, 1, 1 - q), v(1 - q, 1 - p, 1 - q), spriteSide, hilight));
                quads.add(createQuad(v(q, 1 - p, 1 - q), v(q, 1, 1 - q), v(q, 1, q), v(q, 1 - p, q), spriteSide, hilight));
                quads.add(createQuad(v(q, 1, q), v(1 - q, 1, q), v(1 - q, 1 - p, q), v(q, 1 - p, q), spriteSide, hilight));
                quads.add(createQuad(v(q, 1 - p, 1 - q), v(1 - q, 1 - p, 1 - q), v(1 - q, 1, 1 - q), v(q, 1, 1 - q), spriteSide, hilight));

                quads.add(createQuad(v(q, 1 - p, q), v(1 - q, 1 - p, q), v(1 - q, 1 - p, 1 - q), v(q, 1 - p, 1 - q), spriteConnector, hilight));
                quads.add(createQuad(v(q, 1, q), v(q, 1, 1 - q), v(1 - q, 1, 1 - q), v(1 - q, 1, q), spriteSide, hilight));
            } else {
                CablePatterns.QuadSetting pattern = CablePatterns.findPattern(west, south, east, north);
                quads.add(createQuad(v(o, 1 - o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1 - o, o), v(o, 1 - o, o), getSprite.apply(pattern.getSprite()), pattern.getRotation(), hilight));
            }

            if (down == CABLE) {
                quads.add(createQuad(v(1 - o, o, o), v(1 - o, o, 1 - o), v(1 - o, 0, 1 - o), v(1 - o, 0, o), spriteCable, hilight));
                quads.add(createQuad(v(o, o, 1 - o), v(o, o, o), v(o, 0, o), v(o, 0, 1 - o), spriteCable, hilight));
                quads.add(createQuad(v(o, o, o), v(1 - o, o, o), v(1 - o, 0, o), v(o, 0, o), spriteCable, hilight));
                quads.add(createQuad(v(o, 0, 1 - o), v(1 - o, 0, 1 - o), v(1 - o, o, 1 - o), v(o, o, 1 - o), spriteCable, hilight));
            } else if (down == BLOCK) {
                quads.add(createQuad(v(1 - o, o, o), v(1 - o, o, 1 - o), v(1 - o, p, 1 - o), v(1 - o, p, o), spriteCable, hilight));
                quads.add(createQuad(v(o, o, 1 - o), v(o, o, o), v(o, p, o), v(o, p, 1 - o), spriteCable, hilight));
                quads.add(createQuad(v(o, o, o), v(1 - o, o, o), v(1 - o, p, o), v(o, p, o), spriteCable, hilight));
                quads.add(createQuad(v(o, p, 1 - o), v(1 - o, p, 1 - o), v(1 - o, o, 1 - o), v(o, o, 1 - o), spriteCable, hilight));

                quads.add(createQuad(v(1 - q, 0, q), v(1 - q, p, q), v(1 - q, p, 1 - q), v(1 - q, 0, 1 - q), spriteSide, hilight));
                quads.add(createQuad(v(q, 0, 1 - q), v(q, p, 1 - q), v(q, p, q), v(q, 0, q), spriteSide, hilight));
                quads.add(createQuad(v(q, p, q), v(1 - q, p, q), v(1 - q, 0, q), v(q, 0, q), spriteSide, hilight));
                quads.add(createQuad(v(q, 0, 1 - q), v(1 - q, 0, 1 - q), v(1 - q, p, 1 - q), v(q, p, 1 - q), spriteSide, hilight));

                quads.add(createQuad(v(q, p, 1 - q), v(1 - q, p, 1 - q), v(1 - q, p, q), v(q, p, q), spriteConnector, hilight));
                quads.add(createQuad(v(q, 0, 1 - q), v(q, 0, q), v(1 - q, 0, q), v(1 - q, 0, 1 - q), spriteSide, hilight));
            } else {
                CablePatterns.QuadSetting pattern = CablePatterns.findPattern(west, north, east, south);
                quads.add(createQuad(v(o, o, o), v(1 - o, o, o), v(1 - o, o, 1 - o), v(o, o, 1 - o), getSprite.apply(pattern.getSprite()), pattern.getRotation(), hilight));
            }

            if (east == CABLE) {
                quads.add(createQuad(v(1, 1 - o, 1 - o), v(1, 1 - o, o), v(1 - o, 1 - o, o), v(1 - o, 1 - o, 1 - o), spriteCable, hilight));
                quads.add(createQuad(v(1, o, o), v(1, o, 1 - o), v(1 - o, o, 1 - o), v(1 - o, o, o), spriteCable, hilight));
                quads.add(createQuad(v(1, 1 - o, o), v(1, o, o), v(1 - o, o, o), v(1 - o, 1 - o, o), spriteCable, hilight));
                quads.add(createQuad(v(1, o, 1 - o), v(1, 1 - o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, o, 1 - o), spriteCable, hilight));
            } else if (east == BLOCK) {
                quads.add(createQuad(v(1 - p, 1 - o, 1 - o), v(1 - p, 1 - o, o), v(1 - o, 1 - o, o), v(1 - o, 1 - o, 1 - o), spriteCable, hilight));
                quads.add(createQuad(v(1 - p, o, o), v(1 - p, o, 1 - o), v(1 - o, o, 1 - o), v(1 - o, o, o), spriteCable, hilight));
                quads.add(createQuad(v(1 - p, 1 - o, o), v(1 - p, o, o), v(1 - o, o, o), v(1 - o, 1 - o, o), spriteCable, hilight));
                quads.add(createQuad(v(1 - p, o, 1 - o), v(1 - p, 1 - o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, o, 1 - o), spriteCable, hilight));

                quads.add(createQuad(v(1 - p, 1 - q, 1 - q), v(1, 1 - q, 1 - q), v(1, 1 - q, q), v(1 - p, 1 - q, q), spriteSide, hilight));
                quads.add(createQuad(v(1 - p, q, q), v(1, q, q), v(1, q, 1 - q), v(1 - p, q, 1 - q), spriteSide, hilight));
                quads.add(createQuad(v(1 - p, 1 - q, q), v(1, 1 - q, q), v(1, q, q), v(1 - p, q, q), spriteSide, hilight));
                quads.add(createQuad(v(1 - p, q, 1 - q), v(1, q, 1 - q), v(1, 1 - q, 1 - q), v(1 - p, 1 - q, 1 - q), spriteSide, hilight));

                quads.add(createQuad(v(1 - p, q, 1 - q), v(1 - p, 1 - q, 1 - q), v(1 - p, 1 - q, q), v(1 - p, q, q), spriteConnector, hilight));
                quads.add(createQuad(v(1, q, 1 - q), v(1, q, q), v(1, 1 - q, q), v(1, 1 - q, 1 - q), spriteSide, hilight));
            } else {
                CablePatterns.QuadSetting pattern = CablePatterns.findPattern(down, north, up, south);
                quads.add(createQuad(v(1 - o, o, o), v(1 - o, 1 - o, o), v(1 - o, 1 - o, 1 - o), v(1 - o, o, 1 - o), getSprite.apply(pattern.getSprite()), pattern.getRotation(), hilight));
            }

            if (west == CABLE) {
                quads.add(createQuad(v(o, 1 - o, 1 - o), v(o, 1 - o, o), v(0, 1 - o, o), v(0, 1 - o, 1 - o), spriteCable, hilight));
                quads.add(createQuad(v(o, o, o), v(o, o, 1 - o), v(0, o, 1 - o), v(0, o, o), spriteCable, hilight));
                quads.add(createQuad(v(o, 1 - o, o), v(o, o, o), v(0, o, o), v(0, 1 - o, o), spriteCable, hilight));
                quads.add(createQuad(v(o, o, 1 - o), v(o, 1 - o, 1 - o), v(0, 1 - o, 1 - o), v(0, o, 1 - o), spriteCable, hilight));
            } else if (west == BLOCK) {
                quads.add(createQuad(v(o, 1 - o, 1 - o), v(o, 1 - o, o), v(p, 1 - o, o), v(p, 1 - o, 1 - o), spriteCable, hilight));
                quads.add(createQuad(v(o, o, o), v(o, o, 1 - o), v(p, o, 1 - o), v(p, o, o), spriteCable, hilight));
                quads.add(createQuad(v(o, 1 - o, o), v(o, o, o), v(p, o, o), v(p, 1 - o, o), spriteCable, hilight));
                quads.add(createQuad(v(o, o, 1 - o), v(o, 1 - o, 1 - o), v(p, 1 - o, 1 - o), v(p, o, 1 - o), spriteCable, hilight));

                quads.add(createQuad(v(0, 1 - q, 1 - q), v(p, 1 - q, 1 - q), v(p, 1 - q, q), v(0, 1 - q, q), spriteSide, hilight));
                quads.add(createQuad(v(0, q, q), v(p, q, q), v(p, q, 1 - q), v(0, q, 1 - q), spriteSide, hilight));
                quads.add(createQuad(v(0, 1 - q, q), v(p, 1 - q, q), v(p, q, q), v(0, q, q), spriteSide, hilight));
                quads.add(createQuad(v(0, q, 1 - q), v(p, q, 1 - q), v(p, 1 - q, 1 - q), v(0, 1 - q, 1 - q), spriteSide, hilight));

                quads.add(createQuad(v(p, q, q), v(p, 1 - q, q), v(p, 1 - q, 1 - q), v(p, q, 1 - q), spriteConnector, hilight));
                quads.add(createQuad(v(0, q, q), v(0, q, 1 - q), v(0, 1 - q, 1 - q), v(0, 1 - q, q), spriteSide, hilight));
            } else {
                CablePatterns.QuadSetting pattern = CablePatterns.findPattern(down, south, up, north);
                quads.add(createQuad(v(o, o, 1 - o), v(o, 1 - o, 1 - o), v(o, 1 - o, o), v(o, o, o), getSprite.apply(pattern.getSprite()), pattern.getRotation(), hilight));
            }

            if (north == CABLE) {
                quads.add(createQuad(v(o, 1 - o, o), v(1 - o, 1 - o, o), v(1 - o, 1 - o, 0), v(o, 1 - o, 0), spriteCable, hilight));
                quads.add(createQuad(v(o, o, 0), v(1 - o, o, 0), v(1 - o, o, o), v(o, o, o), spriteCable, hilight));
                quads.add(createQuad(v(1 - o, o, 0), v(1 - o, 1 - o, 0), v(1 - o, 1 - o, o), v(1 - o, o, o), spriteCable, hilight));
                quads.add(createQuad(v(o, o, o), v(o, 1 - o, o), v(o, 1 - o, 0), v(o, o, 0), spriteCable, hilight));
            } else if (north == BLOCK) {
                quads.add(createQuad(v(o, 1 - o, o), v(1 - o, 1 - o, o), v(1 - o, 1 - o, p), v(o, 1 - o, p), spriteCable, hilight));
                quads.add(createQuad(v(o, o, p), v(1 - o, o, p), v(1 - o, o, o), v(o, o, o), spriteCable, hilight));
                quads.add(createQuad(v(1 - o, o, p), v(1 - o, 1 - o, p), v(1 - o, 1 - o, o), v(1 - o, o, o), spriteCable, hilight));
                quads.add(createQuad(v(o, o, o), v(o, 1 - o, o), v(o, 1 - o, p), v(o, o, p), spriteCable, hilight));

                quads.add(createQuad(v(q, 1 - q, p), v(1 - q, 1 - q, p), v(1 - q, 1 - q, 0), v(q, 1 - q, 0), spriteSide, hilight));
                quads.add(createQuad(v(q, q, 0), v(1 - q, q, 0), v(1 - q, q, p), v(q, q, p), spriteSide, hilight));
                quads.add(createQuad(v(1 - q, q, 0), v(1 - q, 1 - q, 0), v(1 - q, 1 - q, p), v(1 - q, q, p), spriteSide, hilight));
                quads.add(createQuad(v(q, q, p), v(q, 1 - q, p), v(q, 1 - q, 0), v(q, q, 0), spriteSide, hilight));

                quads.add(createQuad(v(q, q, p), v(1 - q, q, p), v(1 - q, 1 - q, p), v(q, 1 - q, p), spriteConnector, hilight));
                quads.add(createQuad(v(q, q, 0), v(q, 1 - q, 0), v(1 - q, 1 - q, 0), v(1 - q, q, 0), spriteSide, hilight));
            } else {
                CablePatterns.QuadSetting pattern = CablePatterns.findPattern(west, up, east, down);
                quads.add(createQuad(v(o, 1 - o, o), v(1 - o, 1 - o, o), v(1 - o, o, o), v(o, o, o), getSprite.apply(pattern.getSprite()), pattern.getRotation(), hilight));
            }

            if (south == CABLE) {
                quads.add(createQuad(v(o, 1 - o, 1), v(1 - o, 1 - o, 1), v(1 - o, 1 - o, 1 - o), v(o, 1 - o, 1 - o), spriteCable, hilight));
                quads.add(createQuad(v(o, o, 1 - o), v(1 - o, o, 1 - o), v(1 - o, o, 1), v(o, o, 1), spriteCable, hilight));
                quads.add(createQuad(v(1 - o, o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1 - o, 1), v(1 - o, o, 1), spriteCable, hilight));
                quads.add(createQuad(v(o, o, 1), v(o, 1 - o, 1), v(o, 1 - o, 1 - o), v(o, o, 1 - o), spriteCable, hilight));
            } else if (south == BLOCK) {
                quads.add(createQuad(v(o, 1 - o, 1 - p), v(1 - o, 1 - o, 1 - p), v(1 - o, 1 - o, 1 - o), v(o, 1 - o, 1 - o), spriteCable, hilight));
                quads.add(createQuad(v(o, o, 1 - o), v(1 - o, o, 1 - o), v(1 - o, o, 1 - p), v(o, o, 1 - p), spriteCable, hilight));
                quads.add(createQuad(v(1 - o, o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1 - o, 1 - p), v(1 - o, o, 1 - p), spriteCable, hilight));
                quads.add(createQuad(v(o, o, 1 - p), v(o, 1 - o, 1 - p), v(o, 1 - o, 1 - o), v(o, o, 1 - o), spriteCable, hilight));

                quads.add(createQuad(v(q, 1 - q, 1), v(1 - q, 1 - q, 1), v(1 - q, 1 - q, 1 - p), v(q, 1 - q, 1 - p), spriteSide, hilight));
                quads.add(createQuad(v(q, q, 1 - p), v(1 - q, q, 1 - p), v(1 - q, q, 1), v(q, q, 1), spriteSide, hilight));
                quads.add(createQuad(v(1 - q, q, 1 - p), v(1 - q, 1 - q, 1 - p), v(1 - q, 1 - q, 1), v(1 - q, q, 1), spriteSide, hilight));
                quads.add(createQuad(v(q, q, 1), v(q, 1 - q, 1), v(q, 1 - q, 1 - p), v(q, q, 1 - p), spriteSide, hilight));

                quads.add(createQuad(v(q, 1 - q, 1 - p), v(1 - q, 1 - q, 1 - p), v(1 - q, q, 1 - p), v(q, q, 1 - p), spriteConnector, hilight));
                quads.add(createQuad(v(q, 1 - q, 1), v(q, q, 1), v(1 - q, q, 1), v(1 - q, 1 - q, 1), spriteSide, hilight));
            } else {
                CablePatterns.QuadSetting pattern = CablePatterns.findPattern(west, down, east, up);
                quads.add(createQuad(v(o, o, 1 - o), v(1 - o, o, 1 - o), v(1 - o, 1 - o, 1 - o), v(o, 1 - o, 1 - o), getSprite.apply(pattern.getSprite()), pattern.getRotation(), hilight));
            }
        }

        BlockState facadeId = extraData.getData(GenericCableBlock.FACADEID);
        if (facadeId != null) {
            BlockState facadeState = facadeId.getBlockState();
            if (layer == null || RenderTypeLookup.canRenderInLayer(facadeState, layer)) { // always render in the null layer or the block-breaking textures don't show up
                IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(facadeState);
                try {
                    quads.addAll(model.getQuads(state, side, rand, null));
                } catch (Exception e) {
                }
            }
        }

        return quads;
    }


    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return spriteCable == null ? getTexture(new ResourceLocation("minecraft", "missingno")) : spriteCable;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }

}
