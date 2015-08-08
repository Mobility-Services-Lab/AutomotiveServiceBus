/*
 * Copyright 2015 Technische Universität München
 *
 * Author:
 * David Soto Setzke
 *
 *
 * This file is part of the Automotive Service Bus v1.1 which was
 * forked from the research project Visio.M:
 *
 * 	 http://www.visiom-automobile.de/home/
 *
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * 	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package de.visiom.carpc.asb.integrationtests;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;

import java.io.File;

import org.ops4j.pax.exam.Option;

public final class OptionConfiguration {

    public static final Option[] getOptions() throws Exception {
        return new Option[] {
                karafDistributionConfiguration()
                        .frameworkUrl(
                                maven().groupId("org.apache.karaf")
                                        .artifactId("apache-karaf").type("zip")
                                        .version("3.0.0"))
                        .useDeployFolder(false).karafVersion("3.0.0")
                        .unpackDirectory(new File("target/paxexam/unpack/")),
                mavenBundle(maven().groupId("org.mockito")
                        .artifactId("mockito-all").versionAsInProject()),
                wrappedBundle(mavenBundle("junit", "junit-dep", "4.8.1"))
                        .exports("*;version=4.8.1"),

                features(
                        maven().groupId("de.visiom.carpc.asb")
                                .artifactId("features").classifier("features")
                                .versionAsInProject().type("xml"), "asb") };

    }
}