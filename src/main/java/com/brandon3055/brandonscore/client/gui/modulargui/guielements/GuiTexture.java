package com.brandon3055.brandonscore.client.gui.modulargui.guielements;

import codechicken.lib.render.buffer.TransformingVertexBuilder;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.api.render.GuiHelper;
import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.brandonscore.client.ResourceHelperBC;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.brandonscore.client.utils.GuiHelperOld;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

import java.util.function.Supplier;

/**
 * Created by brandon3055 on 1/10/2016.
 */
public class GuiTexture extends GuiElement<GuiTexture> {
    public int texU;
    public int texV;
    public int textSheetSizeX = 256;
    public int textSheetSizeY = 256;
    private int texUSize = 0;
    private int texVSize = 0;
    private boolean texSizeOverride = false;
    private Supplier<Integer> texXGetter = null;
    private Supplier<Integer> texYGetter = null;
    private float rotation = 0;
    private boolean flipX = false;
    private boolean flipY = false;

    public RenderMaterial material;
    public Supplier<RenderMaterial> materialSupplier;

    @Deprecated
    public ResourceLocation texture;
    @Deprecated
    public Supplier<ResourceLocation> textureSupplier;

    @Deprecated
    public GuiTexture(int xPos, int yPos, int textureX, int textureY, int xSize, int ySize, ResourceLocation texture) {
        super(xPos, yPos, xSize, ySize);
        this.texU = textureX;
        this.texV = textureY;
        this.texture = texture;
    }

    @Deprecated
    public GuiTexture(int textureX, int textureY, int xSize, int ySize, ResourceLocation texture) {
        super(0, 0, xSize, ySize);
        this.texU = textureX;
        this.texV = textureY;
        this.texture = texture;
    }

    @Deprecated
    public GuiTexture(int xSize, int ySize, ResourceLocation texture) {
        super(0, 0, xSize, ySize);
        this.texture = texture;
    }

    public GuiTexture(int xPos, int yPos, int xSize, int ySize, RenderMaterial material) {
        super(xPos, yPos, xSize, ySize);
        this.material = material;
    }

    public GuiTexture(int xPos, int yPos, int xSize, int ySize, Supplier<RenderMaterial> materialSupplier) {
        super(xPos, yPos, xSize, ySize);
        this.materialSupplier = materialSupplier;
    }

    public GuiTexture(int xSize, int ySize, RenderMaterial material) {
        super(0, 0, xSize, ySize);
        this.material = material;
    }

    public GuiTexture(int xSize, int ySize, Supplier<RenderMaterial> materialSupplier) {
        super(0, 0, xSize, ySize);
        this.materialSupplier = materialSupplier;
    }

    public GuiTexture(Supplier<RenderMaterial> materialSupplier) {
        super(0, 0);
        this.materialSupplier = materialSupplier;
    }

    @Override
    public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        if (preDrawCallback == null) {
            RenderSystem.color4f(1, 1, 1, 1);
        }

        RenderMaterial mat = getMaterial();
        if (mat != null) {
            IRenderTypeBuffer.Impl getter = RenderUtils.getTypeBuffer();
            if (rotation != 0 || flipX || flipY) {
                MatrixStack mStack = new MatrixStack();
                mStack.translate(xPos(), yPos(), 0);
                mStack.translate(xSize() / 2D, ySize() / 2D, 0);
                if (flipX || flipY) {
                    mStack.scale(flipX ? -1 : 1, flipY ? -1 : 1, 1);
                }
                if (rotation != 0) {
                    mStack.mulPose(new Quaternion(0, 0, rotation, true));
                }
                mStack.translate(-(xSize() / 2D), -(ySize() / 2D), 0);
                IVertexBuilder builder = new TransformingVertexBuilder(getter.getBuffer(mat.renderType(BCSprites::makeType)), mStack);
                drawSprite(builder, 0, 0, xSize(), ySize(), mat.sprite());
            } else {
                drawSprite(getter.getBuffer(mat.renderType(BCSprites::makeType)), xPos(), yPos(), xSize(), ySize(), mat.sprite());
            }
            getter.endBatch();
        } else {
            ResourceLocation texture = getTexture();
            if (texture != null) {
                bindTexture(texture);
                if (texSizeOverride) {
                    drawScaledCustomSizeModalRect(xPos(), yPos(), getTexU(), getTexV(), texUSize, texVSize, xSize(), ySize(), textSheetSizeX, textSheetSizeY);
                } else {
                    drawModalRectWithCustomSizedTexture(xPos(), yPos(), getTexU(), getTexV(), xSize(), ySize(), textSheetSizeX, textSheetSizeY);
                }
            }
        }

        super.renderElement(minecraft, mouseX, mouseY, partialTicks);
//        drawBorderedRect(xPos(), yPos(), xSize(), ySize(), 1, 0, 0xFFFFFF00);
    }

    public GuiTexture setRotation(float rotation) {
        this.rotation = rotation;
        return this;
    }

    public GuiTexture flipX() {
        this.flipX = true;
        return this;
    }

    public GuiTexture flipY() {
        this.flipY = true;
        return this;
    }

    /**
     * Set the x and y (u/v) position of the texture on the provided texture sheet.
     * By default the width and height if the texture will be set from the element width and height but
     * this can be overridden using {@link #setTexSizeOverride(int, int)}
     */
    @Deprecated
    public GuiTexture setTexturePos(int textureX, int textureY) {
        this.texU = textureX;
        this.texV = textureY;
        return this;
    }

    @Deprecated
    public GuiTexture setTexture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    @Deprecated
    public GuiTexture setTextureSupplier(Supplier<ResourceLocation> textureSupplier) {
        this.textureSupplier = textureSupplier;
        return this;
    }

    public GuiTexture setMaterial(RenderMaterial material) {
        this.material = material;
        return this;
    }

    public RenderMaterial getMaterial() {
        return materialSupplier == null ? material : materialSupplier.get();
    }

    public GuiTexture setMaterialSupplier(Supplier<RenderMaterial> materialSupplier) {
        this.materialSupplier = materialSupplier;
        return this;
    }

    @Deprecated
    public ResourceLocation getTexture() {
        return textureSupplier == null ? texture : textureSupplier.get();
    }

    @Deprecated
    public GuiTexture setTexture(String texture) {
        this.texture = ResourceHelperBC.getResourceRAW(texture);
        return this;
    }

    /**
     * Allows you to override the texture size. When set the texture will be scaled up or down as needed to fit
     * the bounds of this element.
     */
    @Deprecated
    public GuiTexture setTexSizeOverride(int texXOverride, int texYOverride) {
        this.texUSize = texXOverride;
        this.texVSize = texYOverride;
        texSizeOverride = true;
        return this;
    }

    /**
     * Set the size of the texture sheet being used. Default is the standard 256x256
     */
    @Deprecated
    public GuiTexture setTexSheetSize(int textureSizeX, int textureSizeY) {
        this.textSheetSizeX = textureSizeX;
        this.textSheetSizeY = textureSizeY;
        return this;
    }

    /**
     * @see #setTexSheetSize(int, int)
     */
    @Deprecated
    public GuiTexture setTexSheetSize(int textureSize) {
        return setTexSheetSize(textureSize, textureSize);
    }

    @Deprecated
    public GuiTexture setTexXGetter(Supplier<Integer> texXGetter) {
        this.texXGetter = texXGetter;
        return this;
    }

    @Deprecated
    public GuiTexture setTexYGetter(Supplier<Integer> texYGetter) {
        this.texYGetter = texYGetter;
        return this;
    }

    @Deprecated
    public int getTexU() {
        return texXGetter == null ? texU : texXGetter.get();
    }

    @Deprecated
    public int getTexV() {
        return texYGetter == null ? texV : texYGetter.get();
    }

    public static GuiTexture newDynamicTexture(int xSize, int ySize, Supplier<RenderMaterial> materialSupplier) {
        return newDynamicTexture(materialSupplier).setSize(xSize, ySize);
    }

    public static GuiTexture newDynamicTexture(Supplier<RenderMaterial> materialSupplier) {
        return new GuiTexture(null) {
            @Override
            public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
                IRenderTypeBuffer.Impl getter = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
                drawDynamicSprite(getter.getBuffer(BCSprites.GUI_TYPE), getMaterial().sprite(), xPos(), yPos(), xSize(), ySize(), getInsets().top, getInsets().left, getInsets().bottom, getInsets().right);
                getter.endBatch();

                for (GuiElement element : childElements) {
                    if (element.isEnabled()) {
                        element.renderElement(minecraft, mouseX, mouseY, partialTicks);
                    }
                }

            }
        }.setMaterialSupplier(materialSupplier).setInsets(4, 4, 4, 4);
    }

    public GuiTexture centerOnParent() {
        if (getParent() != null) {
            setXPos(getParent().xPos() + (getParent().xSize() / 2) - (xSize() / 2));
            setYPos(getParent().yPos() + (getParent().ySize() / 2) - (ySize() / 2));
        }
        return this;
    }
}
