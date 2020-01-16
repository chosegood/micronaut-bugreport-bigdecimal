package micronaut.bugreport.bigdecimal

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import io.micronaut.context.event.BeanCreatedEvent
import io.micronaut.context.event.BeanCreatedEventListener
import io.micronaut.runtime.Micronaut
import javax.inject.Singleton

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("micronaut.bugreport.bigdecimal")
                .mainClass(Application.javaClass)
                .start()
    }

    @Singleton
    internal class ObjectMapperBeanEventListener : BeanCreatedEventListener<ObjectMapper> {
        override fun onCreated(event: BeanCreatedEvent<ObjectMapper>): ObjectMapper {
            val objectMapper = event.bean
            objectMapper.nodeFactory = JsonNodeFactory.withExactBigDecimals(true)
            return objectMapper
        }
    }

}