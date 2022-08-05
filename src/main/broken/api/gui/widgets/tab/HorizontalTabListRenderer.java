package gregtech.api.gui.widgets.tab;

import gregtech.api.gui.resources.TextureArea;
import gregtech.api.gui.util.Position;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class HorizontalTabListRenderer extends TabListRenderer {

    private final HorizontalStartCorner startCorner;
    private final VerticalLocation verticalLocation;

    public HorizontalTabListRenderer(HorizontalStartCorner startCorner, VerticalLocation verticalLocation) {
        this.startCorner = startCorner;
        this.verticalLocation = verticalLocation;
    }

    @Override
    public void renderTabs(MatrixStack matrices, Position offset, List<ITabInfo> tabInfos, int guiWidth, int guiHeight, int selectedTabIndex) {
        boolean startLeft = startCorner == HorizontalStartCorner.LEFT;
        boolean isTopLine = verticalLocation == VerticalLocation.TOP;
        int tabYPosition = isTopLine ? (0 - TAB_HEIGHT + TAB_Y_OFFSET) : (guiHeight - TAB_Y_OFFSET);
        int currentXOffset = 0;

        for (int tabIndex = 0; tabIndex < tabInfos.size(); tabIndex++) {
            boolean isTabSelected = tabIndex == selectedTabIndex;
            boolean isTabFirst = tabIndex == 0;
            TextureArea tabTexture = getTabTexture(isTabSelected, isTabFirst, isTopLine, startLeft);

            int finalPosX = startLeft ? currentXOffset : (guiWidth - TAB_WIDTH - currentXOffset);
            tabInfos.get(tabIndex).renderTab(matrices, tabTexture, offset.x + finalPosX, offset.y + tabYPosition, TAB_WIDTH, TAB_HEIGHT, isTabSelected);
            currentXOffset += (TAB_WIDTH + SPACE_BETWEEN_TABS);
        }
    }

    private static TextureArea getTabTexture(boolean isTabSelected, boolean isTabFirst, boolean isTopLine, boolean startLeft) {
        if (isTopLine) {
            return TopTextures.getTabTexture(isTabFirst, startLeft, isTabSelected);
        } else return BottomTextures.getTabTexture(isTabFirst, startLeft, isTabSelected);
    }

    @Override
    public int[] getTabPos(int tabIndex, int guiWidth, int guiHeight) {
        boolean startLeft = startCorner == HorizontalStartCorner.LEFT;
        boolean isTopLine = verticalLocation == VerticalLocation.TOP;
        int tabYPosition = isTopLine ? (0 - TAB_HEIGHT + TAB_Y_OFFSET) : (guiHeight - TAB_Y_OFFSET);
        int tabXOffset = (TAB_WIDTH + SPACE_BETWEEN_TABS) * tabIndex;
        return new int[]{startLeft ? tabXOffset : (guiWidth - TAB_WIDTH - tabXOffset), tabYPosition, TAB_WIDTH, TAB_HEIGHT};
    }

    public enum HorizontalStartCorner {
        LEFT, RIGHT
    }

    public enum VerticalLocation {
        TOP, BOTTOM
    }

    private static final class TopTextures {

        private static final TextureArea startTabInactiveTexture = TABS_TOP_TEXTURE.getSubArea(0.0f, 0.0f, 1.0f / 3.0f, 0.5f);
        private static final TextureArea startTabActiveTexture = TABS_TOP_TEXTURE.getSubArea(0.0f, 0.5f, 1.0f / 3.0f, 0.5f);

        private static final TextureArea middleTabInactiveTexture = TABS_TOP_TEXTURE.getSubArea(1.0f / 3.0f, 0.0f, 1.0f / 3.0f, 0.5f);
        private static final TextureArea middleTabActiveTexture = TABS_TOP_TEXTURE.getSubArea(1.0f / 3.0f, 0.5f, 1.0f / 3.0f, 0.5f);

        private static final TextureArea endTabInactiveTexture = TABS_TOP_TEXTURE.getSubArea(2.0f / 3.0f, 0.0f, 1.0f / 3.0f, 0.5f);
        private static final TextureArea endTabActiveTexture = TABS_TOP_TEXTURE.getSubArea(2.0f / 3.0f, 0.5f, 1.0f / 3.0f, 0.5f);

        private static TextureArea getTabTexture(boolean isTabFirst, boolean startLeft, boolean isTabSelected) {
            return isTabFirst ? (startLeft ? (isTabSelected ? startTabActiveTexture : startTabInactiveTexture) :
                (isTabSelected ? endTabActiveTexture : endTabInactiveTexture)) :
                (isTabSelected ? middleTabActiveTexture : middleTabInactiveTexture);
        }
    }

    private static final class BottomTextures {

        private static final TextureArea startTabInactiveTexture = TABS_BOTTOM_TEXTURE.getSubArea(0.0f, 0.5f, 1.0f / 3.0f, 0.5f);
        private static final TextureArea startTabActiveTexture = TABS_BOTTOM_TEXTURE.getSubArea(0.0f, 0.0f, 1.0f / 3.0f, 0.5f);

        private static final TextureArea middleTabInactiveTexture = TABS_BOTTOM_TEXTURE.getSubArea(1.0f / 3.0f, 0.5f, 1.0f / 3.0f, 0.5f);
        private static final TextureArea middleTabActiveTexture = TABS_BOTTOM_TEXTURE.getSubArea(1.0f / 3.0f, 0.0f, 1.0f / 3.0f, 0.5f);

        private static final TextureArea endTabInactiveTexture = TABS_BOTTOM_TEXTURE.getSubArea(2.0f / 3.0f, 0.5f, 1.0f / 3.0f, 0.5f);
        private static final TextureArea endTabActiveTexture = TABS_BOTTOM_TEXTURE.getSubArea(2.0f / 3.0f, 0.0f, 1.0f / 3.0f, 0.5f);

        private static TextureArea getTabTexture(boolean isTabFirst, boolean startLeft, boolean isTabSelected) {
            return isTabFirst ? (startLeft ? (isTabSelected ? startTabActiveTexture : startTabInactiveTexture) :
                (isTabSelected ? endTabActiveTexture : endTabInactiveTexture)) :
                (isTabSelected ? middleTabActiveTexture : middleTabInactiveTexture);
        }
    }

}