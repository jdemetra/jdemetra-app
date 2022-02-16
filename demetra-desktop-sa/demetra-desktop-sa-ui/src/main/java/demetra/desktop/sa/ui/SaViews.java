/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.sa.ui;

import demetra.desktop.TsDynamicProvider;
import demetra.information.BasicInformationExtractor;
import demetra.modelling.ModellingDictionary;
import demetra.modelling.SeriesInfo;
import demetra.sa.SaDictionaries;
import demetra.util.Id;
import demetra.util.LinearId;

/**
 *
 * @author PALATEJ
 */
@lombok.experimental.UtilityClass
public class SaViews {
    // main nodes

    public final String INPUT = "Input", SPEC = "Specifications", SERIES = "Series",
            MAIN = "Main results",
            PRELIMINARY = "Preliminary tests",
            PREPROCESSING = "Pre-processing",
            MODEL = "Model",
            DECOMPOSITION = "Decomposition",
            BENCHMARKING = "Benchmarking",
            DIAGNOSTICS = "Diagnostics",
            LIKELIHOOD = "Likelihood";
    // main results sub-nodes
    public final String CHART = "Chart", CHARTS = "Charts",
            TABLE = "Table",
            SI_RATIO = "S-I ratio",
            SA_TREND = "Sa, trend",
            C_S_I = "Cal., sea., irr.",
            LOG = "Processing log";
    public final String PROCESSING = "Processing",
            FCASTS = "Forecasts",
            OSAMPLE = "Out-of-sample test",
            DETAILS = "Details",
            SUMMARY = "Summary",
            STEPS = "Steps",
            PREADJUSTMENT = "Pre-adjustment series",
            ARIMA = "Arima",
            REGRESSORS = "Regressors",
            RESIDUALS = "Residuals",
            STATS = "Statistics",
            DISTRIBUTION = "Distribution";
    // diagnostics sub-nodes
    public final String SEASONALITY = "Seasonality tests",
            OSEASONALITY = "Original (transformed) series",
            RSEASONALITY = "Full residuals",
            LASTRSEASONALITY = "Residuals (last periods)",
            LSEASONALITY = "Linearized series",
            COMBINED = "Combined test",
            RESIDUAL = "Residual seasonality",
            TRANSFORMATION = "Transformation",
            SPECTRAL = "Spectral analysis",
            REVISIONS = "Revisions analysis",
            SLIDINGSPANS = "Sliding spans",
            STABILITY = "Model stability",
            IRREGULAR = "Irregular",
            SA_ST = "Sa series (stationary)",
            SASERIES = "SA series",
            SASERIES_CHANGES = "SA changes",
            LASTIRREGULAR = "Irregular (last periods)",
            LASTSASERIES = "SA series (last periods)",
            TREND = "Trend",
            SEASONAL = "Seasonal",
            TRADINGDAYS = "Trading days",
            SACHANGES = "SA changes",
            TRENDCHANGES = "Trend changes",
            REVISION = "Revisions history",
            EASTER = "Easter",
            MATRIX = "Matrix";
    public final Id INPUT_SPEC = new LinearId(INPUT, SPEC),
            INPUT_SERIES = new LinearId(INPUT, SERIES),
            MAIN_SUMMARY = new LinearId(MAIN),
            MAIN_CHART = new LinearId(MAIN, CHART),
            MAIN_CHARTS_LOW = new LinearId(MAIN, CHARTS, SA_TREND),
            MAIN_CHARTS_HIGH = new LinearId(MAIN, CHARTS, C_S_I),
            MAIN_TABLE = new LinearId(MAIN, TABLE),
            MAIN_SI = new LinearId(MAIN, SI_RATIO),
            MAIN_LOG = new LinearId(MAIN, LOG),
            PREPROCESSING_SUMMARY = new LinearId(PREPROCESSING),
            PREPROCESSING_FCASTS = new LinearId(PREPROCESSING, FCASTS),
            PREPROCESSING_FCASTS_TABLE = new LinearId(PREPROCESSING, FCASTS, TABLE),
            PREPROCESSING_FCASTS_OUTOFSAMPLE = new LinearId(PREPROCESSING, FCASTS, OSAMPLE),
            PREPROCESSING_DETAILS = new LinearId(PREPROCESSING, DETAILS),
            PREPROCESSING_REGS = new LinearId(PREPROCESSING, REGRESSORS),
            PREPROCESSING_ARIMA = new LinearId(PREPROCESSING, ARIMA),
            PREPROCESSING_DET = new LinearId(PREPROCESSING, PREADJUSTMENT),
            PREPROCESSING_RES = new LinearId(PREPROCESSING, RESIDUALS),
            PREPROCESSING_RES_STATS = new LinearId(PREPROCESSING, RESIDUALS, STATS),
            PREPROCESSING_RES_DIST = new LinearId(PREPROCESSING, RESIDUALS, DISTRIBUTION),
            PREPROCESSING_LIKELIHOOD = new LinearId(PREPROCESSING, LIKELIHOOD),
            DIAGNOSTICS_SUMMARY = new LinearId(DIAGNOSTICS),
            DIAGNOSTICS_SEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, COMBINED),
            DIAGNOSTICS_OSEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, OSEASONALITY),
            DIAGNOSTICS_LSEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, LSEASONALITY),
            DIAGNOSTICS_RSEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, RSEASONALITY),
            DIAGNOSTICS_SASEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, SASERIES),
            DIAGNOSTICS_LASTRSEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, LASTRSEASONALITY),
            DIAGNOSTICS_LASTSASEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, LASTSASERIES),
            DIAGNOSTICS_RESIDUALSEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, RESIDUAL),
            DIAGNOSTICS_ISEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, IRREGULAR),
            DIAGNOSTICS_LASTISEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, LASTIRREGULAR),
            DIAGNOSTICS_SPECTRUM_RES = new LinearId(DIAGNOSTICS, SPECTRAL, RESIDUALS),
            DIAGNOSTICS_SPECTRUM_I = new LinearId(DIAGNOSTICS, SPECTRAL, IRREGULAR),
            DIAGNOSTICS_SPECTRUM_SA = new LinearId(DIAGNOSTICS, SPECTRAL, SA_ST),
            DIAGNOSTICS_SLIDING_SUMMARY = new LinearId(DIAGNOSTICS, SLIDINGSPANS),
            DIAGNOSTICS_SLIDING_SEAS = new LinearId(DIAGNOSTICS, SLIDINGSPANS, SEASONAL),
            DIAGNOSTICS_SLIDING_TD = new LinearId(DIAGNOSTICS, SLIDINGSPANS, TRADINGDAYS),
            DIAGNOSTICS_SLIDING_SA = new LinearId(DIAGNOSTICS, SLIDINGSPANS, SACHANGES),
            DIAGNOSTICS_REVISION_SA = new LinearId(DIAGNOSTICS, REVISION, SASERIES),
            DIAGNOSTICS_REVISION_TREND = new LinearId(DIAGNOSTICS, REVISION, TREND),
            DIAGNOSTICS_REVISION_SA_CHANGES = new LinearId(DIAGNOSTICS, REVISION, SACHANGES),
            DIAGNOSTICS_REVISION_TREND_CHANGES = new LinearId(DIAGNOSTICS, REVISION, TRENDCHANGES),
            DIAGNOSTICS_STABILITY_TD = new LinearId(DIAGNOSTICS, STABILITY, TRADINGDAYS),
            DIAGNOSTICS_STABILITY_EASTER = new LinearId(DIAGNOSTICS, STABILITY, EASTER),
            DIAGNOSTICS_STABILITY_ARIMA = new LinearId(DIAGNOSTICS, STABILITY, ARIMA),
            DIAGNOSTICS_MATRIX = new LinearId(DIAGNOSTICS, MATRIX);
    


}
