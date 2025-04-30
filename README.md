# Jingle More Projectors Plugin

A Jingle plugin allows you to control more OBS projectors with customized configuration.

## How to use?

- Drag and drop this plugin file (.jar) into Jingle plugin folder and restart it.
- Go to Jingle -> Plugin -> More Projectors, you can see an add button for you to add an OBS projector.
- Enter the projector's name and click the add button.
- You successfully added a projector! Now you can edit or remove it for you like.

## Settings

### Name

- Just the projector's name, you can change it at any time.
- The projector's name must match the OBS projector but without prefix (like "Windowed Projector (Scene) - "), only its name.
- If you enable `Auto-open` below, the projector must be a **scene**.

### Auto-open

- `Open projector automatically` will open your OBS projector automatically.
- Go to OBS -> Tools -> Scripts -> '+' (plus icon), then add `.config/Jingle/more-projectors-plugin/more-projectors-obs-link.lua` script file to enable auto-open.

### Hotkey

- `Always activate` will activate projector at all time.
    - But this still controlled by states, see `Activate only when` below.
    - Hotkey relatives will be disabled when this is checked.
- `Set hotkey` let you set hotkey(s) to toggle the projector, just like Jingle -> Hotkey.
- `Ignore extra modifier keys` tells whether hotkey(s) should ignore modifier keys like (Ctrl, Alt, Shift).

### Activate only when

- `Instance states` defines which instance states can this projector activate.
    - **Waiting**: the instance is not opened.
    - **Title**: at the game title.
    - **In-World**: playing in a world.
    - **Wall**: walling.
- `In-world states` defines when the instance state is "inworld", which in-world states can this projector activate. This will be disabled when instance states not contain in-world.
    - **Unpaused**: when the game is unpaused, and no in-game screen opened.
    - **Paused**: when the game is paused (ESC, F3 + ESC).
    - **Game Screen Open**: when an in-game screen is opened (like crafting menu).
- When all states are selected, the projector can be activated at any time.
- When none are selected, the projector will never be activated.

### Geometry

- `Position` is the position of the projector, same with Jingle -> OBS.
- `Size` is the size of the projector, same with Jingle -> OBS.

### Others

- `Borderless` tells this plugin should this projector's window being borderless on found.
- `Top projector when active` will top the projector when it is activated.
- `Minimize projector when inactive` will minimize the projector when it is inactivated.

## Report

This plugin is not well-developed for now, and there must be many issues.

If you encounter any issue, feel free to report it under issues tab.

## Special Thanks

This project largely references [Jingle](https://github.com/DuncanRuns/Jingle) and [Jingle-CalcOverlay-Plugin](https://github.com/marin774/Jingle-CalcOverlay-Plugin).

As a Java newcomer, reading their source codes helped me a lot.