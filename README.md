# Econometric software for seasonal adjustment and other time series methods (JDemetra+)

[![Download](https://img.shields.io/github/release/jdemetra/jdemetra-app.svg)](https://github.com/jdemetra/jdemetra-app/releases/latest)
![GitHub All Releases](https://img.shields.io/github/downloads/jdemetra/jdemetra-app/total)
![GitHub Releases](https://img.shields.io/github/downloads/jdemetra/jdemetra-app/latest/total)

**JDemetra+ is a new tool for seasonal adjustment (SA)** developed by the National Bank of Belgium (NBB) in cooperation with the Deutsche Bundesbank and Eurostat in accordance with the Guidelines of the European Statistical System (ESS).

JDemetra+ implements the concepts and algorithms used in the two leading SA methods: [TRAMO/SEATS+](http://www.bde.es/bde/en/secciones/servicios/Profesionales/Programas_estadi/Programas_estad_d9fa7f3710fd821.html) and [X-12ARIMA/X-13ARIMA-SEATS](https://www.census.gov/srd/www/x13as/). Those methods have been re-engineered using an object-oriented approach that enables easier handling, extensions and modifications.

The program TRAMO-SEATS+ was developed by Gianluca Caporello and Agustin Maravall -with programming support from Domingo Perez and Roberto Lopez- at the Bank of Spain. It is based on the program TRAMO-SEATS, previously developed by Victor Gomez and Agustin Maravall.
The program X-13ARIMA-SEATS is a produced, distributed, and maintained by the US-Census Bureau


Besides seasonal adjustment, JDemetra+ bundles other time series models that are useful in the production or analysis of economic statistics, including for instance outlier detection, nowcasting, temporal disaggregation or benchmarking.

From a technical point of view, JDemetra+ is a collection of reusable and extensible Java components, which can be easily accessed through a rich graphical interface. The software is a free and open-source software (FOSS) developed under the [EUPL licence](http://ec.europa.eu/idabc/eupl.html).

JDemetra+ has been [officially recommended](https://ec.europa.eu/eurostat/cros/system/files/Jdemetra_%20release.pdf), since 2 February 2015, to the members of the ESS and the European System of Central Banks as software for seasonal and calendar adjustment of official statistics.

## Quickstart

JDemetra+ runs on multiple platforms such as Windows, macOS and Linux (Java SE 8 or later version  is required).

To install, download the [latest version](https://github.com/jdemetra/jdemetra-app/releases/latest), unzip it somewhere and launch the executable in `/bin` (more details in [Quick install guide](https://github.com/jdemetra/jdemetra-app/wiki/Quick-install-guide)).

The SACE has elaborated an extensive documentation, available through the [CROS - PORTAL](https://ec.europa.eu/eurostat/cros/content/seasonal-adjustment):

- [Quick start](https://ec.europa.eu/eurostat/cros/content/jdemetra-quick-start-0) is an introduction to the JDemetra+ functions for seasonal adjustment, including details regarding installation. 
- [User guide](https://ec.europa.eu/eurostat/cros/content/jdemetra-user-guide-0) includes step-by-step descriptions of how to perform a typical analysis of seasonal data and useful tips that facilitate replication of the results with the user’s own data and working instructions. 
- [Reference manual](https://ec.europa.eu/eurostat/cros/content/jdemetra-reference-manual-version-21_en)  covers all available JDemetra+ functionalities, options and functions available through the interface. 

The [technical docs](https://github.com/jdemetra/jdemetra-app/wiki) are available on the wiki.
