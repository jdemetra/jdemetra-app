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

package ec.util.chart.impl;

import static ec.util.chart.ColorSchemeSupport.rgb;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Smart color scheme.
 *
 * @see
 * http://www.sapdesignguild.org/goodies/diagram_guidelines/color_palettes.html
 * @author Philippe Charles
 * @author Demortier Jeremy
 */
public class SmartColorScheme extends AbstractColorScheme {

    public static final String NAME = "Smart";
    // Smart colors.
    public static final int C00 = rgb(0, 0, 0); // 0
    public static final int C01 = rgb(255, 255, 255); // 1
    public static final int C02 = rgb(150, 81, 54); // 2
    public static final int C03 = rgb(0, 0, 0); // 3
    public static final int C04 = rgb(245, 244, 231); // 4
    public static final int C05 = rgb(222, 222, 200); // 5
    public static final int C06 = rgb(0, 0, 0); // 6
    public static final int C07 = rgb(255, 0, 255); // 7
    public static final int C08 = rgb(255, 248, 163); // 8
    public static final int C09 = rgb(250, 225, 107); // 9
    public static final int C10 = rgb(248, 215, 83); // 10
    public static final int C11 = rgb(243, 192, 28); // 11
    public static final int C12 = rgb(240, 180, 0); // 12
    public static final int C13 = rgb(245, 242, 226); // 13
    public static final int C14 = rgb(247, 179, 87); // 14
    public static final int C15 = rgb(79, 85, 106); // 15
    public static final int C16 = rgb(169, 204, 143); // 16
    public static final int C17 = rgb(130, 177, 106); // 17
    public static final int C18 = rgb(92, 151, 70); // 18
    public static final int C19 = rgb(61, 129, 40); // 19
    public static final int C20 = rgb(30, 108, 11); // 20
    public static final int C21 = rgb(245, 238, 217); // 21
    public static final int C22 = rgb(247, 181, 91); // 22
    public static final int C23 = rgb(163, 204, 75); // 23
    public static final int C24 = rgb(178, 200, 217); // 24
    public static final int C25 = rgb(119, 157, 191); // 25
    public static final int C26 = rgb(62, 117, 167); // 26
    public static final int C27 = rgb(32, 95, 154); // 27
    public static final int C28 = rgb(0, 72, 140); // 28
    public static final int C29 = rgb(245, 233, 208); // 29
    public static final int C30 = rgb(247, 186, 102); // 30
    public static final int C31 = rgb(255, 235, 106); // 31
    public static final int C32 = rgb(190, 163, 122); // 32
    public static final int C33 = rgb(144, 122, 82); // 33
    public static final int C34 = rgb(122, 101, 62); // 34
    public static final int C35 = rgb(99, 82, 43); // 35
    public static final int C36 = rgb(51, 38, 0); // 36
    public static final int C37 = rgb(245, 228, 195); // 37
    public static final int C38 = rgb(247, 190, 111); // 38
    public static final int C39 = rgb(255, 173, 136); // 39
    public static final int C40 = rgb(243, 170, 121); // 40
    public static final int C41 = rgb(235, 137, 83); // 41
    public static final int C42 = rgb(225, 102, 42); // 42
    public static final int C43 = rgb(220, 83, 19); // 43
    public static final int C44 = rgb(216, 64, 0); // 44
    public static final int C45 = rgb(246, 222, 171); // 45
    public static final int C46 = rgb(246, 196, 124); // 46
    public static final int C47 = rgb(77, 166, 25); // 47
    public static final int C48 = rgb(181, 181, 169); // 48
    public static final int C49 = rgb(138, 141, 130); // 49
    public static final int C50 = rgb(116, 121, 111); // 50
    public static final int C51 = rgb(93, 100, 90); // 51
    public static final int C52 = rgb(67, 76, 67); // 52
    public static final int C53 = rgb(246, 217, 171); // 53
    public static final int C54 = rgb(246, 202, 137); // 54
    public static final int C55 = rgb(255, 235, 0); // 55
    public static final int C56 = rgb(230, 165, 164); // 56
    public static final int C57 = rgb(214, 112, 123); // 57
    public static final int C58 = rgb(196, 56, 79); // 58
    public static final int C59 = rgb(188, 28, 57); // 59
    public static final int C60 = rgb(179, 0, 35); // 60
    public static final int C61 = rgb(246, 212, 161); // 61
    public static final int C62 = rgb(246, 207, 149); // 62
    public static final int C63 = rgb(230, 0, 0); // 63

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<Integer> getAreaColors() {
        return Arrays.asList(
                C08, C16, C24, C32, C40, C48, C56,
                C10, C18, C26, C34, C42, C50, C58,
                C12, C20, C28, C36, C44, C52, C60,
                C09, C17, C25, C33, C40, C48, C56,
                C11, C19, C27, C35, C43, C51, C59);
    }

    @Override
    public List<Integer> getLineColors() {
        return Arrays.asList(
                C12, C20, C28, C36, C44, C52, C60,
                C10, C18, C26, C34, C42, C50, C58,
                C08, C16, C24, C32, C40, C48, C56,
                C11, C19, C27, C35, C43, C51, C59,
                C09, C17, C25, C33, C40, C48, C56);
    }

    @Override
    public Map<KnownColor, Integer> getAreaKnownColors() {
        return knownColors(C24, C32, C48, C16, C40, C56, C08);
    }

    @Override
    public Map<KnownColor, Integer> getLineKnownColors() {
        return knownColors(C28, C36, C52, C20, C44, C60, C12);
    }

    @Override
    public int getBackColor() {
        return BasicColor.WHITE;
    }

    @Override
    public int getPlotColor() {
        return C04;
    }

    @Override
    public int getGridColor() {
        return BasicColor.LIGHT_GRAY;
    }
}
