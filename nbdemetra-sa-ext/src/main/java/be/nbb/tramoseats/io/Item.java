/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package be.nbb.tramoseats.io;

/**
 *
 * @author Jean Palate
 */
public enum Item {

    RSA, ITER,
    MQ,
    // transform
    LAM, FCT,
    // model
    INIT,
    IMEAN, P, D, Q, BP, BD, BQ,
    PHI, BPHI, TH, BTH, JPR, JPS, JQR, JQS,
    // auto-modelling
    INIC, IDIF, CANCEL, UB1, UB2, TSIG, PC, PCR,
    // calendar
    ITRAD, IEAST, IDUR, pFTD,
    // outliers
    IATIP, AIO, VA, IMVX, INT1, INT2,
    // estimate
    TOL, UBP,
    // seats
    SEATS, NOADMISS, EPSPHI, RMOD, XL,
    // regression
    IREG, IUSER, NSER, ILONG,
    // intervention
    ISEQ, DELTA, DELTAS, ID1DS, REGEFF,
    // missing
    INTERP,
    // Demetra specific
    pos, type
}
