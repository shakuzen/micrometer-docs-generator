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

package io.micrometer.docs.metrics;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.TreeSet;
import java.util.regex.Pattern;

import io.micrometer.api.internal.logging.InternalLogger;
import io.micrometer.api.internal.logging.InternalLoggerFactory;

// TODO: Assert on prefixes
public class DocsFromSources {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DocsFromSources.class);

    private final File projectRoot;

    private final Pattern inclusionPattern;

    private final File outputDir;

    public DocsFromSources(File projectRoot, Pattern inclusionPattern, File outputDir) {
        this.projectRoot = projectRoot;
        this.inclusionPattern = inclusionPattern;
        this.outputDir = outputDir;
    }

    public static void main(String... args) {
        String projectRoot = args[0];
        String inclusionPattern = args[1];
        inclusionPattern = inclusionPattern.replace("/", File.separator);
        String output = args[2];
        new DocsFromSources(new File(projectRoot), Pattern.compile(inclusionPattern), new File(output)).generate();
    }

    public void generate() {
        Path path = this.projectRoot.toPath();
        logger.info("Inclusion pattern is [" + this.inclusionPattern + "]");
        Collection<SampleEntry> spanEntries = new TreeSet<>();
        FileVisitor<Path> fv = new SpanSearchingFileVisitor(this.inclusionPattern, spanEntries);
        try {
            Files.walkFileTree(path, fv);
            File file = new File(this.outputDir, "_samples.adoc");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            logger.info("Will create files under [" + file + "]");
            StringBuilder stringBuilder = new StringBuilder();
            Path output = file.toPath();
            logger.info("======================================");
            logger.info("Summary of sources analysis");
            logger.info("Found [" + spanEntries.size() + "] samples");
            logger.info(
                    "Found [" + spanEntries.stream().flatMap(e -> e.lowCardinalityTagKeys.stream()).distinct().count() + "] low cardinality tags");
            logger.info(
                    "Found [" + spanEntries.stream().flatMap(e -> e.highCardinalityTagKeys.stream()).distinct().count() + "] high cardinality tags");
            spanEntries.forEach(sampleEntry -> stringBuilder.append(sampleEntry.toString()).append("\n\n"));
            Files.write(output, stringBuilder.toString().getBytes());
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}