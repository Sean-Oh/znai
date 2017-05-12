package api

import static com.twosigma.testing.webui.WebTestGroovyDsl.scenario
import static pages.Pages.*

scenario("""checks if an element or its value matches provided matcher""") {
    search.open()
    search.welcomeMessage.should == ~/welcome to \w+ search/
    search.welcomeMessage.shouldNot == ~/welcome to \w+ S.*/
    search.welcomeMessage.waitTo == ~/welcome to \w+ search/
    search.welcomeMessage.waitToNot == ~/welcome to \w+ S.*/
}
