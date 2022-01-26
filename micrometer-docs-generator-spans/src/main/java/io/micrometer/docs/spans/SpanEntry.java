/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.micrometer.docs.spans;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.micrometer.docs.commons.KeyValueEntry;
import io.micrometer.docs.commons.utils.Assert;
import io.micrometer.docs.commons.utils.StringUtils;

class SpanEntry implements Comparable<SpanEntry> {

    final String name;

    final String enclosingClass;

    final String enumName;

    final String description;

    final String prefix;

    final Collection<KeyValueEntry> tagKeys;

    final Collection<KeyValueEntry> additionalTagKeys;

    final Collection<KeyValueEntry> events;

    final Map.Entry<String, String> overridesDefaultSpanFrom;

    SpanEntry(String name, String enclosingClass, String enumName, String description, String prefix,
            Collection<KeyValueEntry> tagKeys, Collection<KeyValueEntry> additionalTagKeys, Collection<KeyValueEntry> events, Map.Entry<String, String> overridesDefaultSpanFrom) {
        Assert.hasText(name, "Span name must not be empty");
        Assert.hasText(description, "Span description must not be empty");
        this.name = name;
        this.enclosingClass = enclosingClass;
        this.enumName = enumName;
        this.description = description;
        this.prefix = prefix;
        this.tagKeys = tagKeys;
        this.additionalTagKeys = additionalTagKeys;
        this.events = events;
        this.overridesDefaultSpanFrom = overridesDefaultSpanFrom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SpanEntry spanEntry = (SpanEntry) o;
        return Objects.equals(name, spanEntry.name) && Objects.equals(enclosingClass, spanEntry.enclosingClass)
                && Objects.equals(enumName, spanEntry.enumName) && Objects.equals(description, spanEntry.description)
                && Objects.equals(additionalTagKeys, spanEntry.additionalTagKeys) && Objects.equals(tagKeys, spanEntry.tagKeys)
                && Objects.equals(events, spanEntry.events) && Objects.equals(overridesDefaultSpanFrom, spanEntry.overridesDefaultSpanFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, enclosingClass, enumName, description, additionalTagKeys, tagKeys, events, overridesDefaultSpanFrom);
    }

    @Override
    public int compareTo(SpanEntry o) {
        return enumName.compareTo(o.enumName);
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder()
                .append("==== ")
                .append(name())
                .append("\n\n> ").append(description).append("\n\n")
                .append("**Span name** `").append(name).append("`");
        if (name.contains("%s")) {
            text.append(" - since it contains `%s`, the name is dynamic and will be resolved at runtime.");
        }
        else {
            text.append(".");
        }
        text.append("\n\n").append("Fully qualified name of the enclosing class `").append(this.enclosingClass).append("`");
        if (StringUtils.hasText(prefix)) {
            text.append("\n\nIMPORTANT: All tags and event names must be prefixed with `").append(this.prefix).append("` prefix!");
        }
        if (!tagKeys.isEmpty()) {
            text.append("\n\n.Tag Keys\n|===\n|Name | Description\n").append(this.tagKeys.stream().map(KeyValueEntry::toString).collect(Collectors.joining("\n")))
                    .append("\n|===");
        }
        if (!events.isEmpty()) {
            text.append("\n\n.Event Values\n|===\n|Name | Description\n").append(this.events.stream().map(KeyValueEntry::toString).collect(Collectors.joining("\n")))
                    .append("\n|===");
        }
        return text.toString();
    }

    private String name() {
        String name = Arrays.stream(enumName.replace("_", " ").split(" ")).map(s -> StringUtils.capitalize(s.toLowerCase(Locale.ROOT))).collect(Collectors.joining(" "));
        if (!name.toLowerCase(Locale.ROOT).endsWith("span")) {
            return name + " Span";
        }
        return name;
    }
}
