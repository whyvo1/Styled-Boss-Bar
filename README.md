# Styled-Boss-Bar

Design your Styled Boss Bar!



# Manage Your Project

**build.gradle**

```
repositories {
	maven {
		name = "CurseMaven"
		url = "https://www.cursemaven.com"
		content {
			includeGroup "curse.maven"
		}
	}
}

dependencies {
	modApi "curse.maven:styled-boss-bar-api-1223966:${project.sbba_file_id}"
}
```

Visit [CurseMaven](https://www.cursemaven.com/) to find more information.



# How to use?

A `BossBarStyle` controls boss bar's render style.

`BossBarStyleApi` is used to register or find registered `BossBarStyle`s. All `BossBarStyle`s needs to be registerd with an `Identifier` before they can be rendered correctly.

A `BossBarStyleEntry` contains

A `StyledBossBar` holds a `BossBarStyle` to render.

### Simple Style

Use `SimpleBossBarStyle` which controls basic style like textures and locations.

### Custom Style

Create custom style by implementing `BossBarStyle` and its method `render(...)`.
