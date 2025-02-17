[![Build](https://github.com/promcteam/proskillapi/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/promcteam/promccore/packages/1203744)
[![Build](https://github.com/promcteam/proskillapi/actions/workflows/release.yml/badge.svg?branch=main)](https://github.com/promcteam/promccore/packages/1203744)
[![Build](https://github.com/promcteam/proskillapi/actions/workflows/devbuild.yml/badge.svg?branch=dev)](https://github.com/promcteam/promccore/packages/1203744)

# ProSkillAPI-For-Dev
## Modification
```
[+] Added config 'Saving.volatile-storage' which disabled saving function for the function.
[+] Added PlayerAccountsLoadEvent and PlayerAccountsSaveEvent which allow user to make modification to the plugin saving feature (work even volatile-storage is true)
[/] Change PlayerData to interface, original PlayerData became PlayerDataImpl
```

---
# Original readme.md

Our fork is based on the original skillapi and the forked skillapi by Sentropic.

* Includes all premium features from the original premium version of Skillapi found on spigot.

## New dynamic editor

You'll need to use this editor in order for your classes and skills to be compatible with ProSkillAPI.

* Online Editor: https://promcteam.github.io/proskillapi/

## Downloads

You can download ProSkillAPI from our marketplace

* https://promcteam.com/resources/

## PROMCTEAM:

* Discord | https://discord.gg/6UzkTe6RvW

# Development

If you wish to use ProSkillAPI as a dependency in your projects, ProSkillAPI is available through Maven Central
or snapshots through Sonatype.

```xml
<repository>
    <id>sonatype</id>
    <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
</repository>

<dependency>
    <groupId>com.promcteam</groupId>
    <artifactId>proskillapi</artifactId>
    <version>1.3.0-R0.13-SNAPSHOT</version>
</dependency>
```
### A huge thanks to our contributors
<a href="https://github.com/promcteam/proskillapi/graphs/contributors">
<img src="https://contrib.rocks/image?repo=promcteam/proskillapi" />
</a>