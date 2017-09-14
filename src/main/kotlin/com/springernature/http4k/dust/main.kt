package com.springernature.http4k.dust

import java.io.StringWriter
import javax.script.ScriptEngineManager
import javax.script.SimpleBindings


data class Named(val name: String)

// For threading and Nashorn, see https://stackoverflow.com/a/30159424

typealias Template = (Any) -> String

class Dust(private val cacheTemplates: Boolean = true, private val loader: (String) -> String) {
    constructor(loader: (String) -> String): this(false, loader)
    
    private val js = ScriptEngineManager().apply {
        bindings = SimpleBindings(mapOf("loader" to loader))
    }.getEngineByName("nashorn")
    
    private val dust = loadDust()
    
    private fun loadDust(): Any {
        javaClass.getResourceAsStream("/dust-full-2.7.5.js").reader().use(js::eval)
        js.eval("dust.config.cache = ${cacheTemplates};")
        js.eval(
            //language=JavaScript
            """
            dust.onLoad = function(templateName, callback) {
                callback(null, loader.invoke(templateName));
            }
            """)
        
        return js["dust"] ?: throw IllegalStateException("could not initialise Dust")
    }
    
    operator fun get(templateName: String): Template =
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


