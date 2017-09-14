package com.springernature.dust

import org.junit.Assert.assertEquals
import org.junit.Test

class TemplateTest {
    data class Params(val x: Int, val y: Int, val z: Int)
    
    @Test
    fun `it loads and expands templates from files`() {
        val dust = Dust(loadFromFilesIn("src/test/resources/com/springernature/dust"))
        
        dust.withTemplates { templates ->
            val template = templates["test"]
            val expanded = template(Params(1,2,3))
            
            assertEquals("1 2 3", expanded)
        }
    }
    
    @Test
    fun `it loads and expands templates from resources relative to class`() {
        val dust = Dust(loadFromResourcesOf(javaClass))
        
        dust.withTemplates { templates ->
            val template = templates["test"]
            val expanded = template(Params(10,20,30))
            
            assertEquals("10 20 30", expanded)
        }
    }
    
    @Test
    fun `it loads and expands templates from resources relative to package`() {
        val dust = Dust(loadFromResourcesIn(javaClass.`package`))
        
        dust.withTemplates { templates ->
            val template = templates["test"]
            val expanded = template(Params(5,6,7))
            
            assertEquals("5 6 7", expanded)
        }
    }
}
