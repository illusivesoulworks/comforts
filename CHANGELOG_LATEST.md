The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

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