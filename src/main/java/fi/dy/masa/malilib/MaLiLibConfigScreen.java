package fi.dy.masa.malilib;

import java.util.ArrayList;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.util.ConfigUtils;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.BaseTabbedScreen;
import fi.dy.masa.malilib.gui.action.ActionListScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.malilib.gui.edit.CustomHotkeysListScreen;
import fi.dy.masa.malilib.gui.edit.CustomIconListScreen;
import fi.dy.masa.malilib.gui.edit.overlay.InfoRendererWidgetListScreen;
import fi.dy.masa.malilib.gui.tab.BaseScreenTab;
import fi.dy.masa.malilib.gui.tab.ScreenTab;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseInfoRendererWidgetEntryWidget;
import fi.dy.masa.malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import fi.dy.masa.malilib.overlay.widget.InfoRendererWidget;
import fi.dy.masa.malilib.util.data.ModInfo;

public class MaLiLibConfigScreen
{
    public static final ModInfo MOD_INFO = MaLiLibReference.MOD_INFO;

    public static final BaseConfigTab GENERIC           = new BaseConfigTab(MOD_INFO, "generic", 120, getGenericConfigs(),            MaLiLibConfigScreen::create);
    public static final BaseConfigTab HOTKEYS           = new BaseConfigTab(MOD_INFO, "hotkeys", 160, MaLiLibConfigs.Hotkeys.HOTKEYS, MaLiLibConfigScreen::create);
    public static final BaseConfigTab DEBUG             = new BaseConfigTab(MOD_INFO, "debug",   120, MaLiLibConfigs.Debug.OPTIONS,   MaLiLibConfigScreen::create);
    public static final BaseScreenTab ACTIONS           = new BaseScreenTab(MOD_INFO, "actions",                      (scr) -> scr instanceof ActionListScreen, ActionListScreen::createActionListScreen);
    public static final BaseScreenTab CSI               = new BaseScreenTab(MOD_INFO, "config_status_indicator.abbr", (scr) -> scr instanceof ConfigStatusIndicatorWidgetListScreen, MaLiLibConfigScreen::createConfigStatusIndicatorListScreen).setHoverText("malilib.hover.button.config_status_indicator_menu");
    public static final BaseScreenTab ICONS             = new BaseScreenTab(MOD_INFO, "custom_icons",                 (scr) -> scr instanceof CustomIconListScreen, CustomIconListScreen::openCustomIconListScreen).setHoverText("malilib.hover.button.custom_icons_menu");
    public static final BaseScreenTab CUSTOM_HOTKEYS    = new BaseScreenTab(MOD_INFO, "custom_hotkeys",               (scr) -> scr instanceof CustomHotkeysListScreen, MaLiLibConfigScreen::createCustomHotkeysEditScreen).setHoverText("malilib.hover.button.custom_hotkeys_menu");
    public static final BaseScreenTab INFO_RENDERERS    = new BaseScreenTab(MOD_INFO, "info_renderers",               (scr) -> scr instanceof AllInfoWidgetsListScreen, MaLiLibConfigScreen::createInfoRendererWidgetsListScreen).setHoverText("malilib.hover.button.info_renderers_menu");

    private static final ImmutableList<ConfigTab> CONFIG_TABS = ImmutableList.of(
            GENERIC,
            HOTKEYS,
            DEBUG
    );

    public static final ImmutableList<ScreenTab> ALL_TABS = ImmutableList.of(
            GENERIC,
            HOTKEYS,
            DEBUG,
            ACTIONS,
            CSI,
            ICONS,
            CUSTOM_HOTKEYS,
            INFO_RENDERERS
    );

    public static void open()
    {
        BaseScreen.openScreen(create());
    }

    public static BaseConfigScreen create()
    {
        // The parent screen should not be set here, to prevent infinite recursion via
        // the call to the parent's setWorldAndResolution -> initScreen -> switch tab -> etc.
        return new BaseConfigScreen(MOD_INFO, null, ALL_TABS, GENERIC, "malilib.title.screen.configs", MaLiLibReference.MOD_VERSION);
    }

    public static BaseConfigScreen create(@Nullable GuiScreen currentScreen)
    {
        // The parent screen should not be set here, to prevent infinite recursion via
        // the call to the parent's setWorldAndResolution -> initScreen -> switch tab -> etc.
        return new BaseConfigScreen(MOD_INFO, null, ALL_TABS, GENERIC, "malilib.title.screen.configs", MaLiLibReference.MOD_VERSION);
    }

    public static BaseTabbedScreen createConfigStatusIndicatorListScreen(@Nullable GuiScreen currentScreen)
    {
        return new ConfigStatusIndicatorWidgetListScreen();
    }

    public static BaseTabbedScreen createInfoRendererWidgetsListScreen(@Nullable GuiScreen currentScreen)
    {
        return new AllInfoWidgetsListScreen();
    }

    public static BaseTabbedScreen createCustomHotkeysEditScreen(@Nullable GuiScreen currentScreen)
    {
        return new CustomHotkeysListScreen();
    }

    public static ImmutableList<ConfigTab> getConfigTabs()
    {
        return CONFIG_TABS;
    }

    private static ImmutableList<ConfigInfo> getGenericConfigs()
    {
        ArrayList<ConfigInfo> list = new ArrayList<>(MaLiLibConfigs.Generic.OPTIONS);

        list.add(ConfigUtils.extractOptionsToExpandableGroup(list, MOD_INFO, "appearance",
                                                             MaLiLibConfigs.Generic.CONFIG_WIDGET_BACKGROUND,
                                                             MaLiLibConfigs.Generic.HOVERED_LIST_ENTRY_COLOR,
                                                             MaLiLibConfigs.Generic.HOVER_TEXT_MAX_WIDTH,
                                                             MaLiLibConfigs.Generic.MESSAGE_FADE_OUT_TIME,
                                                             MaLiLibConfigs.Generic.OPTION_LIST_CONFIG_USE_DROPDOWN,
                                                             MaLiLibConfigs.Generic.SELECTED_LIST_ENTRY_COLOR,
                                                             MaLiLibConfigs.Generic.SHOW_INTERNAL_CONFIG_NAME,
                                                             MaLiLibConfigs.Generic.SORT_CONFIGS_BY_NAME,
                                                             MaLiLibConfigs.Generic.SORT_EXTENSION_MOD_OPTIONS));
        ConfigUtils.sortConfigsByDisplayName(list);

        return ImmutableList.copyOf(list);
    }

    public static class ConfigStatusIndicatorWidgetListScreen extends InfoRendererWidgetListScreen<ConfigStatusIndicatorContainerWidget>
    {
        public ConfigStatusIndicatorWidgetListScreen()
        {
            super(InfoRendererWidgetListScreen.createSupplierFromInfoManagerForExactType(ConfigStatusIndicatorContainerWidget.class),
                  ConfigStatusIndicatorContainerWidget::new,
                  BaseInfoRendererWidgetEntryWidget::new);

            this.setTitle("malilib.title.screen.configs.config_status_indicator_configuration", MaLiLibReference.MOD_VERSION);
            this.canCreateNewWidgets = true;
        }
    }

    public static class AllInfoWidgetsListScreen extends InfoRendererWidgetListScreen<InfoRendererWidget>
    {
        public AllInfoWidgetsListScreen()
        {
            super(InfoRendererWidgetListScreen.createSupplierFromInfoManagerForSubtypes(InfoRendererWidget.class),
                  null,
                  BaseInfoRendererWidgetEntryWidget::new);

            this.setTitle("malilib.title.screen.configs.info_renderer_widgets", MaLiLibReference.MOD_VERSION);
        }
    }
}
