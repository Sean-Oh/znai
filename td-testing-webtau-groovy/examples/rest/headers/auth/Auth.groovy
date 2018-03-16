package rest.headers.auth

import com.twosigma.console.ConsoleOutputs
import com.twosigma.testing.http.HttpRequestHeader

class Auth {
    static HttpRequestHeader authHeader(HttpRequestHeader original) {
        ConsoleOutputs.out('auth header injection point')
        return original.merge([Authorization: 'Bearer <token>'])
    }
}
