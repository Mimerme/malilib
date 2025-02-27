package fi.dy.masa.malilib;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.HotkeyCategory;
import fi.dy.masa.malilib.input.HotkeyProvider;
import fi.dy.masa.malilib.util.ListUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class MaLiLibHotkeyProvider implements HotkeyProvider
{
    static final MaLiLibHotkeyProvider INSTANCE = new MaLiLibHotkeyProvider();

    private MaLiLibHotkeyProvider()
    {
    }

    @Override
    public List<? extends Hotkey> getAllHotkeys()
    {
        return ListUtils.getAppendedList(MaLiLibConfigs.Hotkeys.FUNCTIONAL_HOTKEYS,
                                         MaLiLibConfigs.Debug.HOTKEYS);
    }

    @Override
    public List<HotkeyCategory> getHotkeysByCategories()
    {
        ModInfo modInfo = MaLiLibReference.MOD_INFO;

        return ImmutableList.of(
                new HotkeyCategory(modInfo, "malilib.hotkeys.category.debug_hotkeys"  , MaLiLibConfigs.Debug.HOTKEYS),
                new HotkeyCategory(modInfo, "malilib.hotkeys.category.generic_hotkeys", MaLiLibConfigs.Hotkeys.FUNCTIONAL_HOTKEYS));
    }
}
