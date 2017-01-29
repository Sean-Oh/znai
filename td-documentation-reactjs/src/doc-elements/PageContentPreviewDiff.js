class PageContentPreviewDiff {
    constructor(before, after) {
        this.before = before
        this.after = after
    }

    findFirstDifferentNode() {
        let btw = document.createTreeWalker(this.before);
        let atw = document.createTreeWalker(this.after);

        for (;;) {
            const bn = btw.nextNode()
            const an = atw.nextNode()

            if (bn === null || an === null) {
                return null
            }

            if (!an.className) {
                continue
            }

            const classes = an.className.split(' ')
            // section is not a content-block as it displays sometimes elements that dont fit specified width
            if (classes.indexOf("section") !== -1 || classes.indexOf('content-block') !== -1) {
                continue
            }

            if (!bn.isEqualNode(an)) {
                return an
            }
        }
    }
}

export default PageContentPreviewDiff