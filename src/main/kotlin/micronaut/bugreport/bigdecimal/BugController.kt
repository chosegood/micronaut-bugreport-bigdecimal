package micronaut.bugreport.bigdecimal

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post

@Controller("/bug")
class BugController {

    private lateinit var body: Map<String, Any?>

    @Post("/")
    fun post(@Body body: Map<String, Any?>) {
        this.body = body
    }

    @Get("/")
    fun get(): HttpResponse<out Any> {
        return HttpResponse.ok(body)
    }

}