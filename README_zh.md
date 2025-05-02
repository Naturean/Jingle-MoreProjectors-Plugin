# Jingle More Projectors Plugin（Jingle更多投影插件）

[English README](https://github.com/Naturean/Jingle-MoreProjectors-Plugin/blob/main/README.md)

一个能够通过自定义配置来控制OBS投影的Jingle插件。

## 如何使用？

- 前往Release页面下载最新版本的jar文件。
- 将插件jar文件拖放至Jingle的插件文件夹，然后重启Jingle。
- 在Jingle -> Plugin -> More Projectors页面，你会看到一个add（添加）按钮，用于添加OBS投影。
- 点击add按钮。
- 输入投影名字然后点击添加窗口的add按钮。
- 你成功添加了一个投影！现在你可以随意编辑（edit）或移除（remove）它。

## 设置

### Enable（启用）

- 勾选时启用该投影。
- 如果禁用，则与移除的表现相同。

### Name（名称）

- 就是投影的名称，你可以随时更改。
- 投影的名称必须与OBS投影相一致，但无需前缀（例如“窗口投影（场景） - ”），只需要其名称。
- 如果你想启用下面介绍的“自动开启”选项，那么投影必须是一个**场景**。

### 自动开启

- `Open projector automatically（自动开启投影）`会自动打开你的OBS投影。
- 点击OBS -> 工具 -> 脚本 -> '+' (加号标志)，然后添加`.config/Jingle/more-projectors-plugin/more-projectors-obs-link.lua`脚本文件以启用“自动开启”功能。

### 热键

- `Always activate（总是激活）`会一直激活投影。
    - 但这仍然受到实例状态的影响，详见后文的`Activate only when（仅在……时激活）`。
    - 当该选项启用时，热键相关功能会被禁用。
- `Set hotkey（设置热键）`可以让你设置热键以切换投影的激活状态，与Jingle的热键（Hotkey）类似。
- `Ignore extra modifier keys（忽略额外修饰键）`告诉热键是否应该忽略如Ctrl、Alt、Shift等修饰键。

### Activate only when（仅在……时激活）

- `Instance states（实例状态）`定义了投影能在什么实例状态下激活。
    - **Waiting（等待）**：实例未打开或未聚焦。
    - **Title（标题）**：在游戏的标题界面。
    - **In-World（世界内）**：正在一个世界内游玩。
    - **Wall（墙）**：正在多开墙的界面。
- `In-world states（世界内状态）`定义了当实例状态为“inworld（世界内）”时，投影能在世界内的什么状态下激活。
    - **Unpaused（未暂停）**：当游戏未暂停，且没有打开游戏内界面时。
    - **Paused（暂停）**：当游戏暂停时（ESC、F3加ESC）。
    - **Game Screen Open（游戏界面）**：当有游戏内界面打开时（例如合成界面）。
- 默认可激活时的实例状态为“inworld”，世界内状态为“unpaused”和“paused”（与Jingle的resizing条件相同）。
- 当所有状态都选中时，投影可在任何时候激活。
- 当没有选中任何状态时，投影无法被激活。

### Geometry（窗口几何）

- `Position（位置）`是指投影的位置，与Jingle -> OBS -> Position一致。
- `Size（大小）`是指投影的大小，与Jingle -> OBS -> Size一致。
- 两者的单位都是像素。

### 其他

- `Borderless（无边框）`告诉插件是否在找到投影窗口时进行无边框化。
- `Top projector when active（激活时置顶）`会在投影激活时将其置顶。
- `Minimize projector when inactive（停用时最小化）`会在投影停用时将其最小化。
- `Inactivate when different hotkeys are activated（当不同热键激活时停用）`会在不同热键激活时将该投影停用。
  - 举个例子，假设`G`是该投影的热键，简称为`G-投影`。现在如果我们有另一个`H-投影`，当你按`H`激活它时，`G-投影`将会被停用。
  - 如果你想通过按某一热键来停用所有投影，那么可以添加一个“空投影”。将其命名为与OBS场景不同的其他名字，然后添加一个未使用的热键，你就可以按热键以停用所有投影。当你在resize的时候会有所用。例如，当你从thin bt（找宝藏时的resize）切换到测眼时，假设你设置了thin bt时的投影但测眼时没有投影。想要停用thin bt时的投影，你可以设置一个与测眼热键一致的空投影。

## 反馈

目前该插件未开发完全，必然有许多的问题。

如果你遇到任何问题，请随时在Issues板块下报告。

## 特别感谢

该项目很大程度上参考了[Jingle](https://github.com/DuncanRuns/Jingle)和[Jingle-CalcOverlay-Plugin](https://github.com/marin774/Jingle-CalcOverlay-Plugin)。

作为一名Java萌新，阅读他们的源码给了我很多帮助。