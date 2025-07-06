![alt text](https://tr7zw.github.io/uikit/banner/header_3d_skinlayers.png)

<p align="center" style="text-align: center;">
  <a href="https://discord.gg/caVV5eXekm"><img src="https://tr7zw.github.io/uikit/social_buttons_icon/Discord-Button-64.png" alt="Discord" style="margin: 5px 10px;"></a>
  <a href="https://github.com/tr7zw/3d-Skin-Layers"><img src="https://tr7zw.github.io/uikit/social_buttons_icon/Github-Button-64.png" alt="GitHub" style="margin: 5px 10px;"></a>
  <a href="https://modrinth.com/mod/3dskinlayers"><img src="https://tr7zw.github.io/uikit/social_buttons_icon/Modrinth-Button-64.png" alt="GitHub" style="margin: 5px 10px;"></a>
  <a href="https://www.curseforge.com/minecraft/mc-mods/skin-layers-3d"><img src="https://tr7zw.github.io/uikit/social_buttons_icon/Curseforge-Button-64.png" alt="GitHub" style="margin: 5px 10px;"></a>
  <a href="https://ko-fi.com/tr7zw"><img src="https://tr7zw.github.io/uikit/social_buttons_icon/Kofi-Button-64.png" alt="Ko-fi" style="margin: 5px 10px;"></a>
</p>

<br>![Divider](https://tr7zw.github.io/uikit/divider_faded/Divider_03.png)

<img src="https://tr7zw.github.io/uikit/headlines/large/About.png" alt="About" style="margin: 5px 10px;">

**Replaces the normally flat second layer of player skins with a fully 3D-modeled version.**  
The mod automatically switches back to vanilla-style 2D rendering when players are more than 12 blocks away, helping maintain high framerates. It also supports transparent elements - perfect for glasses, visors, or other cosmetic details!

Since this mod is purely visual and entirely client-side, **it doesn't need to be installed on servers** and works seamlessly in any environment.

<br>![Divider](https://tr7zw.github.io/uikit/divider_faded/Divider_03.png)

<br>![Features](https://tr7zw.github.io/uikit/headlines/large/Features.png)

Give your player skin the depth it deserves—with style and performance in mind.

### 3D Extruded Skin Layer

- Replaces the flat second skin layer with a full 3D model
- Adds visual depth to cosmetic details like jackets, sleeves, or hats
- Fully supports transparency for glasses, visors, etc.
- Also applies to player head items

### Performance-Aware Rendering

- Automatically switches to vanilla 2D rendering beyond 12 blocks
- Maintains FPS even in multiplayer or crowded areas
- Fully client-side and requires no server installation

<br>

[![Essential](https://tr7zw.github.io/uikit/banner/essential_1.png)](http://essential.gg)<br><br>
![Need a 24/7 Server? Check this out!](https://tr7zw.github.io/uikit/banner/shockbyte_divider.png)
[![Shockbyte](https://tr7zw.github.io/uikit/banner/shockbyte_small.png)](http://bit.ly/4bczSJY)

<br>![Divider](https://tr7zw.github.io/uikit/divider_faded/Divider_03.png)

<br>![Compatibility & Dependancys](https://tr7zw.github.io/uikit/headlines/medium/Compatibility%20&%20Dependancys.png)

|  Minecraft   |         Loader         |     Status      | Version  |                         Note                         |
|--------------|------------------------|-----------------|----------|------------------------------------------------------|
| 1.16.5+      | Fabric/Forge*/NeoForge | ✅ Supported     | Latest   |                                                      |
| *1.16.5      | Forge                  | ⚠️ Unsupported  | Outdated | Unsupported, until it gets support for JarInJar mods |
| 1.12.2/1.8.9 | Forge                  | ❌ Not supported | Outdated | Might get new updates at some point                  |

|            Mod            |       Status       |                                           Note                                            |
|---------------------------|--------------------|-------------------------------------------------------------------------------------------|
| Essential                 | ✅ Supported        |                                                                                           |
| Shoulder Surfing Reloaded | ✅ Supported        |                                                                                           |
| Player Animator           | ✅ Supported        | This means mods that use it like Better Combat and Emotecraft                             |
| CustomSkinLoader          | ⚠️ Partial support | Requires [this](https://modrinth.com/mod/skinlayers3d-customskinloader-bridge) helper mod |
| Lunar Client              | ❌ Not supported    | Will never get support!                                                                   |

<br>![Divider](https://tr7zw.github.io/uikit/divider_faded/Divider_03.png)

<br>![Screenshots / Media](https://tr7zw.github.io/uikit/headlines/medium/Screenshots%20Media.png)

![Stage View](https://raw.githubusercontent.com/tr7zw/3d-Skin-Layers/1.19/img/MC_NU_HEADER.png)

<br>![Divider](https://tr7zw.github.io/uikit/divider_faded/Divider_03.png)

<br>![Known Issues](https://tr7zw.github.io/uikit/headlines/medium/Known%20Issues.png)

- Because the outer layer is slightly larger than the player model, some skins may not align perfectly in 3D.
- ZigZag patterns around corners will join up. This down to the math of making it 3d, but it can be somewhat prevented by placing slightly transparent pixels into the holes.
- With shaders, some body part layer sides have weird shadows. I am trying to get this resolved.

<br>![Divider](https://tr7zw.github.io/uikit/divider_faded/Divider_03.png)

<br>![FAQ](https://tr7zw.github.io/uikit/headlines/medium/FAQ.png)

### Does this need to be installed on the server?

No. This is a fully client-side mod and does **not** need to be installed on the server.

### Does this work with HD Skins?

No. Since the pixels wouldn't have the correct shape, this is not possible.

### The layers stick through the modded armor

Some modded armor is closer to the body than vanilla. Turn down the 3d size in the mod settings till it fits.

### The layers are flying offset to the body

You are probably using a ETF/EMF resourcepack. Make sure you update it, fitting to the Minecraft version.

### Help the layers are gone! / The mod doesn't work!

A few things to check before asking for help:
- Check under Options > Skin Customization that you didn't accidentally disable your skin layers.
- Check in the mod settings, that the features are turned on.
- Does the mod show up in the modlist at all?
- The skin should be loaded via Mojang, other skin loaders might not be supported.
- Make sure your skin actually has things on the second layer. The mod can't magically invent layers.

### I'm having issues with my cracked version of Minecraft

Please buy the game, illegal copies of the game are not supported.

<br>![Divider](https://tr7zw.github.io/uikit/divider_faded/Divider_03.png)

<br>![Credits & license](https://tr7zw.github.io/uikit/headlines/medium/Credits%20&%20License.png)

👤 Thanks to the awesome translators and contributors on Github!
<a href="https://github.com/tr7zw/3d-Skin-Layers/graphs/contributors">
<img src="https://tr7zw.github.io/uikit/links/underlined/more_details.png" style="vertical-align: middle;" alt="Link">
</a> <br><br>
📄 License: tr7zw Protective License <br>
Feel free to use this mod in your Modrinth/Curseforge hosted modpacks without asking for permission. Do not redistribute the jar files anywhere else!
