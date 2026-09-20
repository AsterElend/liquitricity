package aster.liquitricity.client;

import aster.liquitricity.registry.FluidChamberBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class FluidChamberBlockEntityRenderer implements BlockEntityRenderer<FluidChamberBlockEntity> {
    public FluidChamberBlockEntityRenderer(BlockEntityRendererFactory.Context ctx){
        //this could be a bit more clear, fabric
    }
    @Override
    public void render(FluidChamberBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        for (int i = 0; i < 4; i++) {
            ItemStack stack = entity.getInventory().getStack(i);
            if (stack.isEmpty()) continue;

            // 1. ALWAYS push before modifying the matrix
            matrices.push();

            translateBasedOnI(i, matrices);

            // 2. Scale items down
            matrices.scale(0.35f, 0.35f, 0.35f);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));

            // 3. Render the item
            MinecraftClient.getInstance().getItemRenderer().renderItem(
                    stack,
                    ModelTransformationMode.FIXED,
                    light,
                    overlay,
                    matrices,
                    vertexConsumers,
                    entity.getWorld(),
                    (int) entity.getPos().asLong() + i
            );

            // 4. Pop resets the matrix back for the next iteration
            matrices.pop();
        }
    }



    private void translateBasedOnI(int i, MatrixStack matrices){
        float one = 0.3125f;
        float two = 0.6875f;
        switch (i) {
            case 0 -> matrices.translate(one, one, one);
            case 1-> matrices.translate(two, one, one);
            case 2 -> matrices.translate(one, one, two);
            case 3 -> matrices.translate(two, one, two);
        }
    }
}

