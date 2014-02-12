/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.inputmethod.latin.settings;

import android.content.res.Resources;

import com.android.inputmethod.keyboard.internal.KeySpecParser;
import com.android.inputmethod.keyboard.internal.MoreKeySpec;
import com.android.inputmethod.latin.Constants;
import com.android.inputmethod.latin.Dictionary;
import com.android.inputmethod.latin.R;
import com.android.inputmethod.latin.SuggestedWords;
import com.android.inputmethod.latin.SuggestedWords.SuggestedWordInfo;
import com.android.inputmethod.latin.utils.CollectionUtils;
import com.android.inputmethod.latin.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public final class SpacingAndPunctuations {
    private final int[] mSortedSymbolsPrecededBySpace;
    private final int[] mSortedSymbolsFollowedBySpace;
    private final int[] mSortedWordConnectors;
    public final int[] mSortedWordSeparators;
    public final SuggestedWords mSuggestPuncList;
    private final int mSentenceSeparator;
    public final String mSentenceSeparatorAndSpace;
    public final boolean mCurrentLanguageHasSpaces;
    public final boolean mUsesAmericanTypography;
    public final boolean mUsesGermanRules;

    public SpacingAndPunctuations(final Resources res) {
        // To be able to binary search the code point. See {@link #isUsuallyPrecededBySpace(int)}.
        mSortedSymbolsPrecededBySpace = StringUtils.toSortedCodePointArray(
                res.getString(R.string.symbols_preceded_by_space));
        // To be able to binary search the code point. See {@link #isUsuallyFollowedBySpace(int)}.
        mSortedSymbolsFollowedBySpace = StringUtils.toSortedCodePointArray(
                res.getString(R.string.symbols_followed_by_space));
        // To be able to binary search the code point. See {@link #isWordConnector(int)}.
        mSortedWordConnectors = StringUtils.toSortedCodePointArray(
                res.getString(R.string.symbols_word_connectors));
        mSortedWordSeparators = StringUtils.toSortedCodePointArray(
                res.getString(R.string.symbols_word_separators));
        final String[] suggestPuncsSpec = MoreKeySpec.splitKeySpecs(res.getString(
                R.string.suggested_punctuations));
        mSuggestPuncList = createSuggestPuncList(suggestPuncsSpec);
        mSentenceSeparator = res.getInteger(R.integer.sentence_separator);
        mSentenceSeparatorAndSpace = new String(new int[] {
                mSentenceSeparator, Constants.CODE_SPACE }, 0, 2);
        mCurrentLanguageHasSpaces = res.getBoolean(R.bool.current_language_has_spaces);
        final Locale locale = res.getConfiguration().locale;
        // Heuristic: we use American Typography rules because it's the most common rules for all
        // English variants. German rules (not "German typography") also have small gotchas.
        mUsesAmericanTypography = Locale.ENGLISH.getLanguage().equals(locale.getLanguage());
        mUsesGermanRules = Locale.GERMAN.getLanguage().equals(locale.getLanguage());
    }

    // Helper functions to create member values.
    private static SuggestedWords createSuggestPuncList(final String[] puncs) {
        final ArrayList<SuggestedWordInfo> puncList = CollectionUtils.newArrayList();
        if (puncs != null) {
            for (final String puncSpec : puncs) {
                // TODO: Stop using KeySpecParser.getLabel().
                // TODO: Punctuation suggestions should honor RTL languages.
                puncList.add(new SuggestedWordInfo(KeySpecParser.getLabel(puncSpec),
                        SuggestedWordInfo.MAX_SCORE, SuggestedWordInfo.KIND_HARDCODED,
                        Dictionary.DICTIONARY_HARDCODED,
                        SuggestedWordInfo.NOT_AN_INDEX /* indexOfTouchPointOfSecondWord */,
                        SuggestedWordInfo.NOT_A_CONFIDENCE /* autoCommitFirstWordConfidence */));
            }
        }
        return new SuggestedWords(puncList, null /* rawSuggestions */,
                false /* typedWordValid */,
                false /* hasAutoCorrectionCandidate */,
                true /* isPunctuationSuggestions */,
                false /* isObsoleteSuggestions */,
                false /* isPrediction */);
    }

    public boolean isWordSeparator(final int code) {
        return Arrays.binarySearch(mSortedWordSeparators, code) >= 0;
    }

    public boolean isWordConnector(final int code) {
        return Arrays.binarySearch(mSortedWordConnectors, code) >= 0;
    }

    public boolean isWordCodePoint(final int code) {
        return Character.isLetter(code) || isWordConnector(code);
    }

    public boolean isUsuallyPrecededBySpace(final int code) {
        return Arrays.binarySearch(mSortedSymbolsPrecededBySpace, code) >= 0;
    }

    public boolean isUsuallyFollowedBySpace(final int code) {
        return Arrays.binarySearch(mSortedSymbolsFollowedBySpace, code) >= 0;
    }

    public boolean isSentenceSeparator(final int code) {
        return code == mSentenceSeparator;
    }

    public String dump() {
        final StringBuilder sb = new StringBuilder();
        sb.append("mSortedSymbolsPrecededBySpace = ");
        sb.append("" + Arrays.toString(mSortedSymbolsPrecededBySpace));
        sb.append("\n   mSortedSymbolsFollowedBySpace = ");
        sb.append("" + Arrays.toString(mSortedSymbolsFollowedBySpace));
        sb.append("\n   mSortedWordConnectors = ");
        sb.append("" + Arrays.toString(mSortedWordConnectors));
        sb.append("\n   mSortedWordSeparators = ");
        sb.append("" + Arrays.toString(mSortedWordSeparators));
        sb.append("\n   mSuggestPuncList = ");
        sb.append("" + mSuggestPuncList);
        sb.append("\n   mSentenceSeparator = ");
        sb.append("" + mSentenceSeparator);
        sb.append("\n   mSentenceSeparatorAndSpace = ");
        sb.append("" + mSentenceSeparatorAndSpace);
        sb.append("\n   mCurrentLanguageHasSpaces = ");
        sb.append("" + mCurrentLanguageHasSpaces);
        sb.append("\n   mUsesAmericanTypography = ");
        sb.append("" + mUsesAmericanTypography);
        sb.append("\n   mUsesGermanRules = ");
        sb.append("" + mUsesGermanRules);
        return sb.toString();
    }
}
