package ui

import static com.twosigma.testing.webtau.WebTauGroovyDsl.*

scenario("filter by number") {
    open("/finders-and-filters")
    $("#menu ul li a").get(2).should == 'orders'
}
