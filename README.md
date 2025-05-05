# Jingle More Projectors Plugin

[中文README](https://github.com/Naturean/Jingle-MoreProjectors-Plugin/blob/main/README_zh.md)

A Jingle plugin allows you to control more OBS projectors with customized configuration.

## How to use?

- Go to release page and download the newest jar file.
- Drag and drop this plugin file (.jar) into Jingle plugin folder and restart it.
- Go to Jingle -> Plugin -> More Projectors, you can see an add button for you to add an OBS projector.
- Click the add button.
- Enter the projector's name and click the add button on the add window.
- You successfully added a projector! Now you can edit or remove it for you like.

## Settings

### Enable

- Enable this projector when checked.
- If disabled, it just acts like removed.

### Name

- Just the projector's name, you can change it at any time.
- The projector's name must match the OBS projector but without prefix (like "Windowed Projector (Scene) - "), only its name.
- If you want to enable `Auto-open` below, the projector must be a **scene**.

### Auto-open

- `Open projector automatically` will open your OBS projector automatically.
- Go to OBS -> Tools -> Scripts -> '+' (plus icon), then add `.config/Jingle/more-projectors-plugin/more-projectors-obs-link.lua` script file to enable auto-open.

### Hotkeys

- `Always activate` will activate projector at all time.
    - But this still controlled by instance states, see `Activate only when` below.
    - `Manage hotkeys` will be disabled when this is checked.
- `Manage hotkeys` let you set single or multiple hotkeys to toggle the projector.
  - Click '+' button in hotkey column to add a new hotkey
  - Click hotkey button to set the hotkey, just like Jingle -> Hotkey.
  - `Ignore Modifier` tells whether hotkey(s) should ignore modifier keys like (Ctrl, Alt, Shift).

### Activate only when

- `Instance states` defines which instance states can this projector activate.
    - **Waiting**: the instance is not opened or not focused.
    - **Title**: at the game title.
    - **In-World**: playing in a world.
    - **Wall**: walling.
- `In-world states` defines when the instance state is "inworld", which in-world states can this projector activate. This will be disabled when instance states not contain in-world.
    - **Unpaused**: when the game is unpaused, and no in-game screen opened.
    - **Paused**: when the game is paused (ESC, F3 + ESC).
    - **Game Screen Open**: when an in-game screen is opened (like crafting menu).
- Default instance state is inworld, in-world states are unpaused and paused (Same condition with Jingle resizing).
- When all states are selected, the projector can be activated at any time.
- When none are selected, the projector will never be activated.

### Geometry

- `Position` is the position of the projector, same with Jingle -> OBS -> Position.
- `Size` is the size of the projector, same with Jingle -> OBS -> Size.
- The unit of both is pixels.

### Others

- `Borderless` tells this plugin should this projector's window being borderless on found.
- `Top projector when active` will top the projector when it is activated.
- `Minimize projector when inactive` will minimize the projector when it is inactivated.
- `Inactivate when different hotkeys are activated` will inactivate this projector when different hotkeys are activated.
  - For example, assume that `G` is the hotkey of this projector, we call it `G-Projector`. Now if we have another `H-Projector`, when you press `H` to activate it, `G-Projector` will be inactivated.
  - If you want to inactivate all projectors by pressing a hotkey, you can add an "empty projector". Name it any name that is different from the OBS scenes, and add a hotkey unused, then you can press it to inactivate all projectors. This is useful when you change resizing. For example, when changing thin bt to eye measuring, you set projectors on thin bt but no projector on eye measuring. To inactivate all projectors on thin bt, You can add an empty projector that has the same hotkeys as eye measuring.

## Feedback

This plugin is not well-developed for now, and there must be many issues.

If you encounter any issue, feel free to report it under issues tab.

## Special Thanks

This project largely references [Jingle](https://github.com/DuncanRuns/Jingle) and [Jingle-CalcOverlay-Plugin](https://github.com/marin774/Jingle-CalcOverlay-Plugin).

As a Java newcomer, reading their source codes helped me a lot.