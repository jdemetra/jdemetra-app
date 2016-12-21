#Econometric software for seasonal adjustment and other time series methods (JDemetra+)

[![Download](https://img.shields.io/github/release/jdemetra/jdemetra-app.svg)](https://github.com/jdemetra/jdemetra-app/releases/latest)

JDemetra+ is a new tool for seasonal adjustment (SA) developed by the National Bank of Belgium (NBB) in cooperation with the Deutsche Bundesbank and Eurostat in accordance with the Guidelines of the European Statistical System (ESS).

JDemetra+ implements the concepts and algorithms used in the two leading SA methods: [TRAMO/SEATS](http://www.bde.es/bde/en/secciones/servicios/Profesionales/Programas_estadi/Programas_estad_d9fa7f3710fd821.html) and [X-12ARIMA](https://www.census.gov/srd/www/x13as/). Those methods have been re-engineered using an object-oriented approach that enables easier handling, extensions and modifications.

Besides seasonal adjustment, JDemetra+ bundles other time series models that are useful in the production or analysis of economic statistics, including for instance outlier detection, nowcasting, temporal disaggregation or benchmarking.

From a technical point of view, JDemetra+ is a collection of reusable and extensible Java components, which can be easily accessed through a rich graphical interface. The software is a free and open-source software (FOSS) developed under the [EUPL licence](http://ec.europa.eu/idabc/eupl).

JDemetra+ has been [officially recommended](http://ec.europa.eu/eurostat/cros/system/files/Jdemetra_%20release.pdf), since 2 February 2015, to the members of the ESS and the European System of Central Banks as software for seasonal and calendar adjustment of official statistics.

##Quickstart

JDemetra+ runs on multiple platforms such as Windows, Mac OS X and Linux (Java SE 7 or later version  is required).

To install, download the [latest version](https://github.com/jdemetra/jdemetra-app/releases/latest), unzip it somewhere and launch the executable in `/bin`.

The SACE has elaborated an extensive documentation, available through the [CROS - PORTAL](http://ec.europa.eu/eurostat/cros/content/seasonal-adjustment):

- [Quick start](http://ec.europa.eu/eurostat/cros/content/jdemetra-quick-start-0) is an introduction to the JDemetra+ functions for seasonal adjustment, including details regarding installation. 
- [User guide](http://ec.europa.eu/eurostat/cros/content/jdemetra-user-guide-0) includes step-by-step descriptions of how to perform a typical analysis of seasonal data and useful tips that facilitate replication of the results with the user’s own data and working instructions. 
- [Reference manual](http://ec.europa.eu/eurostat/cros/content/jdemetra-reference-manual)  covers all available JDemetra+ functionalities, options and functions available through the interface. 

The [technical docs](https://github.com/jdemetra/jdemetra-app/wiki) are available on the wiki.
