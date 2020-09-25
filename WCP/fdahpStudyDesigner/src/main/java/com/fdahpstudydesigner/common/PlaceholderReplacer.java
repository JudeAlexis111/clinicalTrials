/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.common;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

public final class PlaceholderReplacer {

  private PlaceholderReplacer() {}

  private static Logger logger = Logger.getLogger(PlaceholderReplacer.class);

  public static String replaceNamedPlaceholders(
      final String textWithNamedPlaceholders, final Map<String, String> values) {

    PlaceholderResolver placeholderResolver =
        new PlaceholderResolver() {
          @Override
          public String resolvePlaceholder(String placeholderName) {
            String result = values.get(placeholderName);
            if (StringUtils.isEmpty(result)) {
              logger.error(
                  String.format(
                      "missing value for placeholder: '%s' in '%s'",
                      placeholderName, textWithNamedPlaceholders));
            }
            return result;
          }
        };

    PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}");
    String replacedTextResult =
        helper.replacePlaceholders(textWithNamedPlaceholders, placeholderResolver);

    helper = new PropertyPlaceholderHelper("{{", "}}");
    return helper.replacePlaceholders(replacedTextResult, placeholderResolver);
  }
}
