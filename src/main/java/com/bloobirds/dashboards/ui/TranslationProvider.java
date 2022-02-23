package com.bloobirds.dashboards.ui;

import java.text.MessageFormat;
import java.util.*;

import org.springframework.stereotype.Component;

import com.vaadin.flow.i18n.I18NProvider;

/**
 * Simple implementation of {@link I18NProvider}.
 */
@Component
public class TranslationProvider implements I18NProvider {

    public static final String BUNDLE_PREFIX = "vaadin-i18n";

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(new Locale("en"), new Locale("es"));
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale);

        String value = bundle.getString(key);
        if (params.length > 0) {
            value = MessageFormat.format(value, params);
        }
        return value;
    }

}