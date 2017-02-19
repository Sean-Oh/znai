import React from 'react'

import SimpleCodeToken from './SimpleCodeToken.js'

const SimpleCodeSnippet = ({tokens}) => {
    return <div>
        <pre><code>{tokens.map((t, idx) => <SimpleCodeToken key={idx} token={t}/>)}</code></pre>
    </div>
}

export default SimpleCodeSnippet