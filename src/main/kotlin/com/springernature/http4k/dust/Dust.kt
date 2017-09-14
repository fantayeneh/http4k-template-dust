package com.springernature.http4k.dust

import jdk.nashorn.api.scripting.JSObject
import org.apache.commons.pool2.BasePooledObjectFactory
import org.apache.commons.pool2.PooledObject
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.impl.GenericObjectPool
import java.io.StringWriter
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.SimpleBindings

// For threading and Nashorn, see https://stackoverflow.com/a/30159424

// Must only be used on one thread.
private class SingleThreadedDust(
    private val js: ScriptEngine,
    private val cacheTemplates: Boolean = true,
    private val notifyOnClosed: (SingleThreadedDust) -> Unit
) : Templates {
    
    private val dust = loadDust()
    
    private fun loadDust(): JSObject {
        javaClass.getResourceAsStream("/dust-full-2.7.5.js").reader().use(js::eval)
        js.eval(
            //language=JavaScript
            """
            dust.config.cache = ${cacheTemplates};
            dust.onLoad = function(templateName, callback) {
                callback(null, loader.invoke(templateName));
            }
            """)
        
        return js["dust"] as? JSObject ?: throw IllegalStateException("could not initialise Dust")
    }
    
    override fun close() {
        notifyOnClosed(this)
    }
    
    override fun get(templateName: String): Template =
        fun(params: Any) =
            expandTemplate(templateName, params)
    
    fun expandTemplate(templateName: String, params: Any): String {
        val writer = StringWriter()
        
        val bindings = SimpleBindings(mapOf(
            "dust" to dust,
            "templateName" to templateName,
            "templateParams" to params,
            "writer" to writer
        ))
        
        js.eval(
            //language=JavaScript
            """
            dust.render(templateName, templateParams, function(err, result) {
                if (err) {
                    throw new Error(err);
                } else {
                    writer.write(result, 0, result.length);
                }
            });
            """, bindings)
        
        return writer.toString()
    }
}


class Dust(private val cacheTemplates: Boolean, loader: TemplateLoader) {
    constructor(loader: TemplateLoader) : this(cacheTemplates = true, loader = loader)
    
    private val scriptEngineManager = ScriptEngineManager().apply {
        bindings = SimpleBindings(mapOf("loader" to loader))
    }
    
    private val pool = GenericObjectPool<SingleThreadedDust>(object : BasePooledObjectFactory<SingleThreadedDust>() {
        override fun create(): SingleThreadedDust {
            return SingleThreadedDust(scriptEngineManager.getEngineByName("nashorn"), cacheTemplates, { returnDustEngine(it) })
        }
        
        override fun wrap(obj: SingleThreadedDust): PooledObject<SingleThreadedDust> {
            return DefaultPooledObject(obj)
        }
    })
    
    private fun returnDustEngine(dustEngine: SingleThreadedDust) {
        pool.returnObject(dustEngine)
    }
    
    fun openTemplates(): Templates =
        pool.borrowObject()
}
