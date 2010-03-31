/*
 * JCaptcha, the open source java framework for captcha definition and integration
 * Copyright (c)  2007 jcaptcha.net. All Rights Reserved.
 * See the LICENSE.txt file distributed with this package.
 */

package org.liveSense.service.captcha;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import com.octo.captcha.component.word.DefaultSizeSortedWordList;
import com.octo.captcha.component.word.DictionaryReader;
import com.octo.captcha.component.word.SizeSortedWordList;

/**
 * <p>Implementation of the DictionaryReader interface, uses a .properties file to retrieve words and return a
 * WordList.Constructed with the name of the properties file. It uses standard java mecanism for I18N</p>
 *
 * @author <a href="mailto:mga@octo.com">Mathieu Gandin</a>
 * @version 1.1
 */
public class BundleDictionary implements DictionaryReader {

    private String words;

    public BundleDictionary(String words) {
        this.words = words;
    }

    public SizeSortedWordList getWordList() {
        SizeSortedWordList list = generateWordList(Locale.getDefault(), words);
        return list;
    }

    public SizeSortedWordList getWordList(Locale locale) {
        SizeSortedWordList list = generateWordList(locale, words);
        return list;
    }

    protected SizeSortedWordList generateWordList(Locale locale, String words) {
        DefaultSizeSortedWordList list = new DefaultSizeSortedWordList(locale);
        StringTokenizer tokenizer = new StringTokenizer(words, ";");
        int count = tokenizer.countTokens();
        for (int i = 0; i < count; i++) {
            list.addWord(tokenizer.nextToken());
        }
        return list;
    }

}
