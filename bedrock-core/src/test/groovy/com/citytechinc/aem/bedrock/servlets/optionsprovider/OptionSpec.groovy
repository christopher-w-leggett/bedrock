/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.servlets.optionsprovider

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class OptionSpec extends Specification {

    def "sort options"() {
        setup:
        def options = Option.fromMap(map).sort(comparator)

        expect:
        options*.value == ["one", "two"]

        where:
        map                          | comparator
        ["two": "Two", "one": "One"] | Option.ALPHA
        ["two": "Two", "one": "one"] | Option.ALPHA_IGNORE_CASE
    }
}