
# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

## [2.2.4] - 2023-01-31

This the release of JDemetra+ v2.2.4.  
[Java SE 8 or later](https://adoptium.net/) version is required to run it.

The main changes of this release are some bug fixes in SA processing, the migration of libraries to Maven Central and the support of the Java 14+.

### Added

- ![OTHER] Add signing of binaries

### Changed

- ![OTHER] Migration to Maven-Central

### Removed

- ![UI] Remove JavaHelp workaround

### Fixed

- ![STAT] Correction in log/level (x13)
- ![STAT] Intermediate precision in X13 (ARMA)
- ![STAT] Uninitialized processing context
- ![STAT] Bug in Hannan-Rissanen
- ![IO] Fix parsing of locale
- ![IO] Fix issues with high memory usage in some OpenDocument files
- ![IO] Fix Zip64 issue in some Excel files
- ![UI] Fix colors on look&feel update
- ![UI] Fix TS export using invalid file name
- ![OTHER] Fix setups on JDK14+ due to pack200 removal

## [2.2.3] - 2020-07-07

This the release of JDemetra+ v2.2.3.  
[Java SE 8 or later](https://adoptium.net/) version is required to run it.

The main changes of this release are the addition of a new revision policy and the support of Java 11+.

### Added

- ![STAT] Add epsilon for standard deviation
- ![STAT] Add new revision policy (current [AO])
- ![IO] Add new open document engine (.ods files)
- ![OTHER] Add native support of Java 11+ thanks to NetBeans Platform 11
- ![OTHER] Add generation of checksum on binaries

### Changed

- ![STAT] Correction for the extreme value detection if the difference between the boundary and the value is below machine precision
- ![STAT] Correction in regression specs

### Fixed

- ![STAT] Fix error in TradingDaysSpec
- ![STAT] Fix for LogAdd for half yearly data with bias correction legacy
- ![STAT] Fix for AutoHenderson for halfyearly data
- ![STAT] Fix bug in automatic differencing (diagnostics)
- ![STAT] Fix bug in Tramo (automatic choice of trading days with holidays)
- ![STAT] Fix bugs with User-defined holidays
- ![IO] Fix parsing of numbers that use non-breaking spaces as grouping separator
- ![IO] Fix InvalidPathException
- ![IO] Fix Windows search scope
- ![IO] Fix SQL keyword list
- ![IO] Fix parsing of some dates in Excel ( .xlsx)
- ![UI] Fix drag & drop on Java11
- ![UI] Fix rendering on high dpi
- ![OTHER] Fix native library issues in some restricted environments

#### Some explanations on the new revision policy: current[AO]
The new revision policy fixes the model (including all the parameters) and handle any new observation as an additive outlier.
Those “pseudo outliers” are in fact introduced in the model as intervention variables (to avoid possible confusion).

It is easy to show that, in the case of model-based decompositions (canonical decomposition, structural model…), this solution is exactly the same as using forecasts (some small discrepancies can appear, due for instance to the bias correction in multiplicative models).
In X13, that approach is not exactly the same as the use of forecasts, due to the fact that the projected seasonal factors are not necessary coherent with the ARIMA forecasts. In most cases, the differences between the use of “Current[AO]” and “Current[forecasts]” are small.

The main advantages of the “Current[AO]” approach are:
- No modification of usual routines
- Usual diagnostics (+ estimation of the size of the AO)
- Possibility to fine tune the model (removal of some “AO”…)

The generated intervention variables are kept in the model (and perhaps re-estimated) when other revision policies are applied on subsequent periods, except in the case of the concurrent (=complete) policy; in that latter case they are removed from the model. Statisticians must then decide on the best handling of the series (use of automatic outliers detection, of ramps, of specific regression variables…)

## [2.2.2] - 2019-01-15

This the release of JDemetra+ v2.2.2.  
[Java SE 8 or later](https://adoptium.net/) version is required to run it.

### Added

- ![STAT] New bias correction (X11, pseudo-additive decomposition)
- ![STAT] X11 for half-yearly series

### Changed

- ![STAT] Enlarged matrix output
- ![IO] Improved error reporting
- ![IO] Improved file type detection of spreadsheets
- ![IO] Improved performance and memory consumption of big Excel files
- ![UI] Information of the input time series in the SaItemNode
- ![UI] Error reporting when message is null

### Fixed

- ![STAT] Bug in outliers detection (Tramo)
- ![STAT] Bug in the computation of the canonical decomposition (SEATS)
- ![IO] Overflow when dealing with unrealistic dimensions in Excel files
- ![UI] JDK11 compatibility
- ![UI] Windows constrain on ODBC plugin
- ![UI] Error reporting when message is null
- ![UI] Local refresh of arima parameters
- ![UI] Weak reference in calendars
- ![UI] NPE in spreadsheet output if data is missing
- ![OTHER] Various JDK11 issues
- ![OTHER] Missing API in v2.2.1
- ![OTHER] Deadlock in TsFactory

## [2.2.1] - 2018-01-16

## [2.2.0] - 2017-07-11

## [2.1.0] - 2016-04-04

## [2.0.0] - 2015-01-14

## [1.5.4] - 2014-12-18


[Unreleased]: https://github.com/jdemetra/jdemetra-app/compare/v2.2.4...HEAD
[2.2.4]: https://github.com/jdemetra/jdemetra-app/compare/v2.2.3...2.2.4
[2.2.3]: https://github.com/jdemetra/jdemetra-app/compare/v2.2.2...2.2.3
[2.2.2]: https://github.com/jdemetra/jdemetra-app/compare/v2.2.1...2.2.2
[2.2.1]: https://github.com/jdemetra/jdemetra-app/compare/v2.2.0...2.2.1
[2.2.0]: https://github.com/jdemetra/jdemetra-app/compare/v2.1.0...2.2.0
[2.1.0]: https://github.com/jdemetra/jdemetra-app/compare/v2.0.0...2.1.0
[2.0.0]: https://github.com/jdemetra/jdemetra-app/compare/v1.5.4...2.0.0
[1.5.4]: https://github.com/jdemetra/jdemetra-app/releases/tag/v1.5.4

[STAT]: https://img.shields.io/badge/-STAT-068C09
[OTHER]: https://img.shields.io/badge/-OTHER-e4e669
[IO]: https://img.shields.io/badge/-IO-F813F7
[UI]: https://img.shields.io/badge/-UI-5319E7
