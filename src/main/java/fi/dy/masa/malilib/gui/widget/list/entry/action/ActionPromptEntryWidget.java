package fi.dy.masa.malilib.gui.widget.list.entry.action;

import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseDataListEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StyledTextUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class ActionPromptEntryWidget extends BaseDataListEntryWidget<NamedAction>
{
    public ActionPromptEntryWidget(NamedAction data,
                                   DataListEntryWidgetData constructData)
    {
        super(data, constructData);

        this.getBorderRenderer().getHoverSettings().setBorderWidthAndColor(1, 0xFFF0B000);
        this.getBackgroundRenderer().getHoverSettings().setEnabled(false);

        StyledTextLine fullName = data.getColoredWidgetDisplayName();
        this.setText(StyledTextUtils.clampStyledTextToMaxWidth(fullName, this.getWidth() - 16, LeftRight.RIGHT, " ..."));

        this.getHoverInfoFactory().setTextLineProvider("action_info", data::getHoverInfo);
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        // Close the current screen first, in case the action opens another screen
        BaseScreen.openScreen(null);
        this.data.execute();
        return true;
    }
}
