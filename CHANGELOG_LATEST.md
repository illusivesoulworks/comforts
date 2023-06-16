The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

This is a copy of the changelog for the most recent version. For the full version history, go [here](https://github.com/illusivesoulworks/comforts/blob/1.20.x/CHANGELOG.md).

## [6.3.0+1.20.1] - 2023.06.16
### Added
- Added `hammockUse` and `sleepingBagUse` configuration options to change the time of day when each type can be used
  [#86](https://github.com/illusivesoulworks/comforts/issues/86) [#107](https://github.com/illusivesoulworks/comforts/issues/107)
- Added in-game configuration GUI
- Added tooltips to sleeping bags, hammocks, and rope and nail items for more clarity
### Changed
- Updated to Minecraft 1.20.1
- Configuration options have been renamed with new updated comments
- If `autoUse` is enabled, sleeping bags will only be placed if sleeping is possible. If sleeping is not possible, the
  error message will be displayed without placing the sleeping bag down. Sleeping bags can still be placed at all times
  while crouching. [#110](https://github.com/illusivesoulworks/comforts/issues/110)
### Removed
- Replaced `nightHammocks` configuration option with new `hammockUse` configuration option
