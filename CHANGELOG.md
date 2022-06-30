# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
Prior to version 6.0.0, this project used MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

## [6.0.0-beta.2+1.19] - 2022.06.30
### Changed
- [Forge] Now specifically requires Forge 41.0.34+
### Fixed
- [Fabric] Fixed lack of recipes for items

## [6.0.0-beta.1+1.19] - 2022.06.30
### Added
- [Forge] Added `comforts:sleeping_bags` and `comforts:hammocks` item and block tags for Comforts sleeping bags and hammocks
### Changed
- Merged Forge and Fabric versions of the project together using the MultiLoader template
- Configuration system is now provided by SpectreLib
- Configuration file is now located in the root folder's `defaultconfigs` folder
- Changed to [Semantic Versioning](http://semver.org/spec/v2.0.0.html)
- Updated to Minecraft 1.19
- [Forge] Updated to Forge 41+
- [Fabric] Updated to Fabric API 0.55.2+
- [Fabric] Updated to Cardinal Components API 5.0.0+
### Fixed
- Fixed player arms rotating out of place when auto-using sleeping bags [#80](https://github.com/TheIllusiveC4/Comforts/issues/80)
### Removed
- Temporarily removed data generators
- [Fabric] Temporarily removed Mod Menu configuration screen

## [1.18.2-5.0.0.4] - 2022.03.08
### Fixed
- Fixed auto-using sleeping bags not working properly

## [1.18.2-5.0.0.3] - 2022.03.07
### Changed
- Updated `fr_fr.json` localization (thanks HollishKid!) [#75](https://github.com/TheIllusiveC4/Comforts/pull/75)
### Fixed
- Fixed players unlocking all Comforts recipes when gaining any item [#61](https://github.com/TheIllusiveC4/Comforts/issues/61)

## [1.18.1-5.0.0.2] - 2022.02.16
### Changed
- Attempting invalid placements for hammocks will now provide general feedback messages to the player about what went
wrong
- Rope and Nail can now be crafted with any item tagged `forge:rope`, with native support for Quark and Supplementaries
rope items

## [1.18-5.0.0.1] - 2021.12.05
### Changed
- Updated to Minecraft 1.18
- Updated to Forge 38+

## [1.17.1-5.0.0.1] - 2021.08.24
### Changed
- Optimized assets (thanks RDKRACZ!)

## [1.17.1-5.0.0.0] - 2021.08.03
### Changed
- Updated to Minecraft 1.17.1

## [1.16.5-4.0.1.2] - 2021.08.02
### Changed
- Updated Swedish translation (thanks GeorgeOrwell1!)

## [1.16.5-4.0.1.1] - 2021.06.21
### Added
- Insulated Sleeping Bags config option for Survive mod integration
  [#46](https://github.com/TheIllusiveC4/Comforts/issues/46)
### Changed
- Updated versioning to account for lost update
### Fixed
- Fixed broken Morpheus compatibility [#50](https://github.com/TheIllusiveC4/Comforts/issues/50)
  [#57](https://github.com/TheIllusiveC4/Comforts/issues/57)
  [#59](https://github.com/TheIllusiveC4/Comforts/issues/59)

## [1.16.5-4.0.0.4] - 2021.05.15
### Added
- Added Polish localization (thanks joker876!)
### Fixed
- Added failsafe for hammock placement [#53](https://github.com/TheIllusiveC4/Comforts/issues/53)

## [1.16.4-4.0.0.3] - 2020.12.19
### Added
- Added Swedish localization (thanks heubest!)

## [1.16.4-4.0.0.2] - 2020.11.09
### Changed
- Updated to Minecraft 1.16.4

## [1.16.3-4.0.0.1] - 2020.09.29
### Changed
- Updated to Minecraft 1.16.3

## [1.16.2-4.0.0.0] - 2020.08.23
### Changed
- Updated to Minecraft 1.16.2

## [1.16.1-3.0.0.1] - 2020.08.09
### Changed
- Hammock recipes now use Forge's string tag for the string item ingredient

## [1.16.1-3.0.0.0] - 2020.07.08
### Changed
- Ported to 1.16.1 Forge

## [1.15.2-2.0.0.4] - 2020.08.09
### Changed
- Hammock recipes now use Forge's string tag for the string item ingredient

## [1.15.2-2.0.0.3] - 2020.06.14
### Changed
- Updated Turkish translation (thanks Emirhangg!)

## [1.15.2-2.0.0.2] - 2020.05.02
### Changed
- Switched to using Forge event for hammock day-to-night manipulation

## [1.15.2-2.0.0.1] - 2020.04.08
### Fixed
- Fixed hammock rendering incorrectly when placed directly on top of blocks [#40](https://github.com/TheIllusiveC4/Comforts/issues/40)
- Fixed hammocks not breaking when blocks holding their supporting nails are destroyed [#40](https://github.com/TheIllusiveC4/Comforts/issues/40)

## [1.15.2-2.0.0.0] - 2020.02.15
### Changed
- Ported to 1.15.2 Forge

## [1.14.4-2.0.0.0-beta6] - 2020.02.12
### Fixed
- Fixed issue with Comforts items activating the Cosmetic Beds mod's recipes

## [1.14.4-2.0.0.0-beta5] - 2020.02.12
### Added
- Added waterlogging for sleeping bags and hammocks
- Added back localizations for German, Spanish, French, Korean, Russian, Turkish, and Chinese

## [1.14.4-2.0.0.0-beta4] - 2019.11.19
### Fixed
- Fixed Morpheus integration with hammocks
- Fixed Rope and Nail not dropping itself when harvested

## [1.14.4-2.0.0.0-beta3] - 2019.10.16
### Changed
- Ported to Forge 1.14.4

## [1.13.2-2.0.0.0-beta2] - 2019.08.11
### Fixed
- Fixed server-side crash [#30](https://github.com/TheIllusiveC4/Comforts/issues/30)

## [1.13.2-2.0.0.0-beta1] - 2019.05.09
### Added
- Comforts recipes will now unlock in the recipe book once the appropriate criteria are met
### Changed
- Updated to 1.13.2 Forge
- Hammock and sleeping bag textures updated
### Removed
- Leisure Hammocks config option
- Tough as Nails integration (mod does not exist on 1.13.x)

## [1.12.2-1.4.1.2] - 2019.04.30
### Changed
- Updated French translation (thanks Neerwan!)

## [1.12.2-1.4.1.1] - 2018.12.21
### Fixed
- Fixed potential NPE issues [#23](https://github.com/TheIllusiveC4/Comforts/issues/23)

## [1.12.2-1.4.1.0] - 2018.12.13
### Added
- Added Nighttime Hammocks config option, set to true to allow sleeping in hammocks at night
### Fixed
- Fixed a bug related to bed properties, which addresses an issue with Minecolonies
- Fixed a bug that allowed players to sleep in hammocks at night

## [1.12.2-1.4.0.1] - 2018.12.13
### Fixed
- Fixed some issues with bed properties which also addresses an error with trying to sleep when using Minecolonies colonies

## [1.12.2-1.4.0.0] - 2018.11.25
### Changed
- Requires Forge 14.23.5.2776 and above
### Removed
- Removed ASM patches

## [1.12.2-1.3.1.0] - 2018.10.25
### Added
- Added "Sleeping Bag Break Chance" config option - sets the chance that a sleeping bag will break after usage

## [1.12.2-1.3.0.0] - 2018.08.30
### Changed
- Requires Forge 14.23.4.2755 and above

## [1.12.2-1.2.0.1] - 2018.08.30
### Fixed
- Fixed hammocks setting the time to new day instead of night

## [1.12.2-1.2.0.0] - 2018.07.17
### Added
- Added Korean language translation (thank you SeolWha)
- Added license files to JAR
### Changed
- Refactored entire mod
- Old config files should be deleted to regenerate new ones that have been altered
- Automatically using sleeping bags must be directed on a block to activate (in contrast to before where you could just right click the air)
- License changed from GPLv3 to LGPLv3
- Sleeping during the day without a hammock (i.e. during a thunderstorm) now acts like the vanilla behavior instead of skipping to night
### Removed
- Removed unnecessary ASM patches
- Removed Auto-Pick Up Sleeping Bag config option and merged behavior with Auto-Use Sleeping Bag

## [1.12.2-1.1.3.0] - 2018.06.14
### Added
- Added Turkish localization (thanks Emirhangg)
- Added Mob filter registry to API

## [1.12.2-1.1.2.0] - 2018.06.10
### Added
- Added Creative tab for all Comforts items and blocks
### Fixed
- Fixed network error that was causing players to get stuck in sleeping bags on dedicated servers

## [1.12.2-1.1.1.1] - 2018.05.10
### Added
- Added Russian lang files (credit to Serj4ever57203)

## [1.12.2-1.1.1.0] - 2018.05.03
### Added
- Added new config option - Sleeping Bag Debuffs. This will add potion effects to players after sleeping in a sleeping bag. The format is "(potion registry name) (duration in seconds) (power)".

## [1.12.2-1.1.0.3] - 2018.04.04
### Changed
- Removed requirement on the Rope and Nail to be placed on logs or wooden planks - can be placed on most solid blocks now. A good rule of thumb is that if you can place a torch on the side of it, you can place the Rope and Nail also.
- Updated to Forge 14.23.2.2611
### Fixed
- Fixed the hammock and sleeping bag being categorized as solid blocks

## [1.12.2-1.1.0.2] - 2018.02.28
### Added
- Added French language files (credits to Xandoria)

## [1.12.2-1.1.0.1] - 2018.01.22
### Added
- Added OreDict support for wooden logs (credit to GlacieredPyro) and wooden planks, so now the Rope and Nail can be placed on modded wooden logs and planks for hammock builds
### Fixed
- Fixed issue with sleep handler not accounting for Forge sleep event results

## [1.12.2-1.1.0.0] - 2017.11.27
### Added
- Added Chinese localization (credit: DYColdWind)
- Added new config option: Leisure Hammocks. When set to true (default: false), players now sit in hammocks. Up to two players can sit in one hammock. LSHIFT to stop sitting. Players can still sleep in hammocks during the day by sneak-right-clicking with an empty hand with this option active.
### Changed
- New default config option for Well-Rested is false
### Fixed
- Fixed crash when attempting to place rope and nail in an invalid position next to valid blocks
- Fixed sleeping players rendering incorrectly to other players
- Fixed player not rendering correctly in first-person view during sleep
- Fixed Morpheus advancing time to the next day instead of night when using hammocks

## [1.12.2-1.0.0.2] - 2017.11.18
### Fixed
- Fixed NPE when respawning without having slept with SpongeForge loaded

## [1.12.2-1.0.0.1] - 2017.11.17
### Fixed
- Fixed crash when loading Morpheus with Comforts

## [1.12.2-1.0.0.0] - 2017.11.17
### Changed
- Updated hammock cloth item texture

## [1.12.2-0.0.0.8-rc2] - 2017.11.12
### Fixed
- Fixed crash on server start

## [1.12.2-0.0.0.8-rc1] - 2017.11.12
### Changed
- Updated textures for hammock model, hammock item, sleeping bag model, sleeping bag item
- Rope and Nail can now also be placed on wood planks

## [1.12.2-0.0.0.7] - 2017.10.23
### Changed
- Compatible with 1.12.1 again

## [1.12.2-0.0.0.6] - 2017.10.17
### Changed
- Sleeping bags, when auto-use and auto-pick up are enabled, will now attempt to return to the currently selected hotbar slot instead of the first empty inventory slot
- Changed license to GNU GPL v3

## [1.12.2-0.0.0.5] - 2017.10.13
### Added
- Added German translation
### Changed
- Updated mod to Forge 14.23.0.2491 MC 1.12.2 (no longer supporting 1.12 or 1.12.1 versions going forward)
### Fixed
- Fixed some model error warnings in the console

## [1.12.2-0.0.0.4] - 2017.10.06
### Changed
- When insulated sleeping bags is active, the warming effect requires less sleep overall (however it has a cap).
- Sleepy factor configuration option has a new max of 20 (if you need anything more than 20, it's probably best to just deactivate the well-rested feature entirely as 20 makes the wait time after even large amounts of sleep time almost negligible).

## [1.12.2-0.0.0.3] - 2017.09.29
### Added
- Added Well Rested configuration option - when true, it prevents going to sleep again for a configurable amount of time after sleeping
- Added Sleepy Factor configuration option - when Well Rested is true, determines how long players must wait (larger numbers here mean they can sleep sooner)
- Added Auto Use Sleeping Bag configuration option - when true, players can simply right click with their sleeping bag in hand and, provided proper conditions are met, they will automatically use the sleeping bag and go to sleep. They can still choose to place their sleeping bags by sneaking.
- Added Insulated Sleeping Bags configuration option (Tough as Nails compatibility feature) - when true, sleeping in sleeping bags will warm a player up depending on how long they sleep

## [1.12.2-0.0.0.2] - 2017.09.22
### Added
- Added hammocks that let you sleep during the day (does not set spawn)
### Changed
- Sleeping bags no longer count for the "Sweet dreams" advancement

## [1.12.1-0.0.0.1] - 2017.09.03
- Initial release
