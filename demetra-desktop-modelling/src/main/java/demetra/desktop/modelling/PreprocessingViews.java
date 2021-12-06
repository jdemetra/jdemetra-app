/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.modelling;


import demetra.util.Id;
import demetra.util.LinearId;

/**
 *
 * @author Jean Palate
 */
@lombok.experimental.UtilityClass
public class PreprocessingViews{

    public final String INPUT = "Input", SPEC = "Specifications", SERIES = "Series",
            MAIN = "Main results",
            PREPROCESSING = "Pre-processing",
            MODEL = "Model",
            DECOMPOSITION = "Decomposition",
            BENCHMARKING = "Benchmarking",
            DIAGNOSTICS = "Diagnostics";
    public final String PROCESSING = "Processing",
            FCASTS = "Forecasts",
            OSAMPLE = "Out-of-sample test",
            DETAILS = "Details",
            TABLE = "Table",
            STEPS = "Steps",
            PREADJUSTMENT = "Pre-adjustment series",
            ARIMA = "Arima",
            REGRESSORS = "Regressors",
            RESIDUALS = "Residuals",
            STATS = "Statistics",
            DISTRIBUTION = "Distribution",
            SPECTRAL = "Spectral analysis",
            REVISIONS = "Revisions analysis",
            SLIDINGSPANS = "Sliding spans",
            STABILITY = "Model stability",
            LIKELIHOOD = "Likelihood";
    public static final Id INPUT_SPEC = new LinearId(INPUT, SPEC),
            INPUT_SERIES = new LinearId(INPUT, SERIES), MODEL_SUMMARY = new LinearId(MODEL),
            MODEL_DET = new LinearId(MODEL, PREADJUSTMENT),
            MODEL_FCASTS = new LinearId(MODEL, FCASTS),
            MODEL_FCASTS_TABLE = new LinearId(MODEL, FCASTS, TABLE),
            MODEL_FCASTS_OUTOFSAMPLE = new LinearId(MODEL, FCASTS, OSAMPLE),
            MODEL_REGS = new LinearId(MODEL, REGRESSORS),
            MODEL_ARIMA = new LinearId(MODEL, ARIMA),
            MODEL_RES = new LinearId(MODEL, RESIDUALS),
            MODEL_RES_STATS = new LinearId(MODEL, RESIDUALS, STATS),
            MODEL_RES_DIST = new LinearId(MODEL, RESIDUALS, DISTRIBUTION),
            MODEL_RES_SPECTRUM = new LinearId(MODEL, RESIDUALS, SPECTRAL),
            MODEL_LIKELIHOOD = new LinearId(MODEL, LIKELIHOOD),
            PROCESSING_DETAILS = new LinearId(PROCESSING, DETAILS),
            PROCESSING_STEPS = new LinearId(PROCESSING, STEPS),
            PREPROCESSING_SUMMARY = new LinearId(PREPROCESSING),
            PREPROCESSING_FCASTS = new LinearId(PREPROCESSING, FCASTS),
            PREPROCESSING_FCASTS_OUTOFSAMPLE = new LinearId(PREPROCESSING, FCASTS, OSAMPLE),
            PREPROCESSING_DETAILS = new LinearId(PREPROCESSING, DETAILS),
            PREPROCESSING_REGS = new LinearId(PREPROCESSING, REGRESSORS),
            PREPROCESSING_ARIMA = new LinearId(PREPROCESSING, ARIMA),
            PREPROCESSING_DET = new LinearId(PREPROCESSING, PREADJUSTMENT),
            PREPROCESSING_RES = new LinearId(PREPROCESSING, RESIDUALS),
            PREPROCESSING_RES_STATS = new LinearId(PREPROCESSING, RESIDUALS, STATS),
            PREPROCESSING_RES_DIST = new LinearId(PREPROCESSING, RESIDUALS, DISTRIBUTION);


    public Id getPreferredView() {
        return MODEL_SUMMARY;
    }
}
